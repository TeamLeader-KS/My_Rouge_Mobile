package com.example.my_rouge_mobile.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_rouge_mobile.input.GameAction
import com.example.my_rouge_mobile.input.MoveDir
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.logic.EnterUserName
import rougelite.BusinessLogicLayer.logic.GameEngine
import rougelite.BusinessLogicLayer.logic.MoveLogic
import rougelite.BusinessLogicLayer.logic.Settings
import rougelite.BusinessLogicLayer.logic.WeaponEngine
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel
import rougelite.BusinessLogicLayer.mapGenerator.LastRoom
import rougelite.BusinessLogicLayer.message.GameMessages
import rougelite.BusinessLogicLayer.message.Messages
import rougelite.BusinessLogicLayer.objects.DarkCrown
import rougelite.DataAccessLayer.SaveLoadManager
import rougelite.DataAccessLayer.load.LoadGame
import rougelite.DataAccessLayer.save.LeaderboardEntry

/**
 * Замена Main.kt + GameController.kt.
 *  - Main: загрузка сохранения / новая игра  -> loadSavedGame / startNewGame
 *  - GameController.start(): цикл while      -> onAction(): один вызов = один ход
 *  - Messages / флаги окон                   -> GameUiState
 */
class GameViewModel(app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    /** Живая модель игры. Canvas читает её напрямую, перерисовываясь по state.tick. */
    var session: GameSession? = null
        private set

    private var leaderboard: MutableList<LeaderboardEntry> = mutableListOf()
    private var messageJob: Job? = null

    init {
        // сообщения из бизнес-логики (Enemy, behaviors, движки) попадают в UI через этот слушатель
        Messages.listener = { text, ms -> showMessage(text, ms) }

        SaveLoadManager.init(app.filesDir)
        leaderboard = SaveLoadManager.loadLeaderboard().toMutableList()

        val loaded = if (SaveLoadManager.hasSave()) loadSavedGame() else null
        if (loaded != null) {
            startSession(loaded)
        } else {
            _state.update { it.copy(leaderboard = leaderboard.toList()) }
        }
    }

    // ═══════════════════════════ ВХОД ═══════════════════════════

    fun onAction(action: GameAction) {
        when (action) {
            GameAction.ContinuePrologue -> _state.update { it.copy(screen = AppScreen.NameEntry) }
            is GameAction.ConfirmName -> startNewGame(action.name.ifBlank { "Unknown Hero" })

            GameAction.NewGame -> {
                SaveLoadManager.deleteSave()
                session = null
                _state.update { it.copy(screen = AppScreen.NameEntry, overlay = Overlay.None, message = null) }
            }
            GameAction.SaveAndExit -> saveAndExit()

            // ── окна ──
//            is GameAction.OpenBackpack -> setOverlay(Overlay.Backpack(action.tab))
            is GameAction.OpenBackpack -> setOverlay(Overlay.Backpack(_state.value.backpackTab))
            GameAction.OpenPocket -> setOverlay(Overlay.Pocket)
            GameAction.OpenInformation -> setOverlay(Overlay.Information)
            GameAction.OpenMenu -> setOverlay(Overlay.Menu)
            GameAction.OpenLeaderboard -> setOverlay(Overlay.Leaderboard)
            GameAction.CloseOverlay -> setOverlay(Overlay.None)
//            is GameAction.SelectBackpackTab -> {
//                if (_state.value.overlay is Overlay.Backpack) setOverlay(Overlay.Backpack(action.tab))
//            }
            is GameAction.SelectBackpackTab -> {
                // Обновляем и вкладку в стейте, и текущее окно (если оно открыто)
                _state.update { it.copy(backpackTab = action.tab) }
                if (_state.value.overlay is Overlay.Backpack) {
                    setOverlay(Overlay.Backpack(action.tab))
                }
            }

            // ── ходы ──
            is GameAction.UseItem -> {
                val overlay = _state.value.overlay
                if (overlay is Overlay.Backpack) {
                    withTurn { s -> useItem(s, overlay.tab, action.slot) }
                    if (_state.value.screen == AppScreen.Playing) setOverlay(Overlay.None)
                }
            }
            is GameAction.Move -> withTurn { s -> handleMovement(s, action.dir) }
            GameAction.Fire -> withTurn { s -> s.weaponEngine.shoot() }
            GameAction.ToggleWeapon -> withTurn { s ->
                s.inventoryEngine.equipUnequipWeapon()?.let { showMessage(it, 1500) }
            }
            GameAction.ToggleHands -> withTurn { s ->
                s.handsEngine.switchHands()?.let { showMessage(it, 1500) }
            }
            GameAction.UseStone -> withTurn { s ->
                s.gameEngine.useStoneOnPortal().takeIf { it.isNotEmpty() }?.let { showMessage(it, 1500) }
            }
            GameAction.UsePortal -> withTurn { s ->
                if (s.gameEngine.canEnterPortal()) nextLevel(s)
            }
        }
    }

    private fun setOverlay(overlay: Overlay) {
        _state.update { it.copy(overlay = overlay) }
    }

    // ═══════════════════════════ ХОД МИРА ═══════════════════════════

    /** Выполняет действие игрока и, если игра продолжается, двигает мир (аналог одной итерации while). */
    private inline fun withTurn(block: (GameSession) -> Unit) {
        val s = session ?: return
        val st = _state.value
        if (st.screen != AppScreen.Playing) return
        // игровые действия допустимы только без открытых окон (UseItem проверяет окно сам)
        block(s)
        if (_state.value.screen != AppScreen.Playing) return
        advanceWorld(s)
    }

    private fun advanceWorld(s: GameSession) {
        s.weaponEngine.update()
        updateEnemies(s)

        // ВОТ ТУТ ПРОВЕРЯЕМ, НЕ ВЛЕТЕЛИ ЛИ ОНИ В ПУЛИ/ЛУЧИ ПОСЛЕ СВОЕГО ХОДА
        s.weaponEngine.checkEnemyCollisionsWithProjectiles()
        s.weaponEngine.checkEnemyCollisionsWithRays()

        revealFog(s)

        if (!s.player.isAlive) {
            finishRun(s, victory = false)
            return
        }
        publish(s)
    }

    private fun updateEnemies(s: GameSession) {
        val level = s.gameEngine.currentLevel
        val player = s.gameEngine.player
        for (enemy in level.enemies) {
            if (enemy.isAlive) {
                val room = level.getRoomAt(enemy.y, enemy.x)
                if (room != null) {
                    enemy.behavior.enemyMovePattern(enemy, room, player, level)
                }
            }
        }
    }

    /** Туман войны. Раньше это делалось внутри GameRender.render() - теперь после каждого хода. */
    private fun revealFog(s: GameSession) {
        val level = s.gameEngine.currentLevel
        val px = s.player.levelX
        val py = s.player.levelY
        level.openCellsInRadius(px, py, 2)
        val idx = level.rooms.indexOfFirst {
            px in it.x until (it.x + it.length) && py in it.y until (it.y + it.height)
        }
        if (idx != -1) level.openRoom(idx)
    }

    // ═══════════════════════════ ДВИЖЕНИЕ / ПРЕДМЕТЫ ═══════════════════════════

    private fun handleMovement(s: GameSession, dir: MoveDir) {
        val direction = when (dir) {
            MoveDir.UP -> MoveLogic.Direction.UP
            MoveDir.DOWN -> MoveLogic.Direction.DOWN
            MoveDir.LEFT -> MoveLogic.Direction.LEFT.also { s.weaponEngine.updateDirection(it) }
            MoveDir.RIGHT -> MoveLogic.Direction.RIGHT.also { s.weaponEngine.updateDirection(it) }
        }

        val moveResult = s.moveLogic.tryMove(s.gameEngine.player, direction, s.gameEngine.currentLevel)
        if (!moveResult.isSuccess) return

        val pickedItem = moveResult.pickedItem
        if (pickedItem != null) {
            if (pickedItem is DarkCrown) {
                finishRun(s, victory = true)
                return
            }
            val result = s.inventoryEngine.addItem(pickedItem)
            showMessage(result.message, 1500)
            if (result.success) s.gameEngine.currentLevel.removeItem(pickedItem)
        }

        val pickedStone = moveResult.pickedStone
        if (pickedStone != null) {
            s.pocketEngine.addStone(pickedStone)?.let { showMessage(it, 1500) }
        }

        if (s.gameEngine.canEnterPortal()) showMessage(GameMessages.ENTER_PORTAL, 1500)

        s.weaponEngine.clearRays()
        s.weaponEngine.clearWaves()
    }

    private fun useItem(s: GameSession, tab: BackpackTab, slot: Int) {
        val msg = when (tab) {
            BackpackTab.Weapon -> s.inventoryEngine.useWeapon(slot)
            BackpackTab.Ammo -> s.inventoryEngine.useAmmo(slot)
            BackpackTab.Potion -> s.inventoryEngine.useHealthPotion(slot)
            BackpackTab.Reinforce -> "Этот предмет даёт пассивный бонус"
        }
        if (msg != null) showMessage(msg, 1500)
    }

    private fun nextLevel(s: GameSession) {
        try {
            val newLevel = s.gameEngine.nextLevel(GameConfig.MAP_WIDTH, GameConfig.MAP_HEIGHT)
            newLevel.clearVisibility()
            s.weaponEngine.currentLevel = newLevel
            showMessage(GameMessages.PORTAL_NEXT_LEVEL, 3000)
        } catch (e: Exception) {
            Log.e(TAG, "nextLevel failed", e)
        }
    }

    // ═══════════════════════════ КОНЕЦ ЗАБЕГА / СОХРАНЕНИЕ ═══════════════════════════

    private fun finishRun(s: GameSession, victory: Boolean) {
        val p = s.player
        leaderboard.add(LeaderboardEntry(p.name, p.currentPlayerLevel, p.backpack.treasure))
        SaveLoadManager.saveLeaderboard(leaderboard)
        SaveLoadManager.deleteSave()
        _state.update {
            it.copy(
                screen = if (victory) AppScreen.Victory else AppScreen.GameOver,
                overlay = Overlay.None,
                message = null,
                leaderboard = leaderboard.toList()
            )
        }
    }

    private fun saveAndExit() {
        val s = session ?: return
        SaveLoadManager.saveGame(s.gameEngine, s.gameEngine.player, s.gameEngine.currentLevel)
        _state.update { it.copy(exitRequested = true) }
    }

    // ═══════════════════════════ СТАРТ (порт Main.kt) ═══════════════════════════

    private fun startNewGame(name: String) {
        EnterUserName.userName = name

        val dungeonLevel = Settings.gameLevel
        val level = GameLevel(dungeonLevel)
        level.buildLevel(GameConfig.MAP_WIDTH, GameConfig.MAP_HEIGHT)
        level.generateEnemies(dungeonLevel)

        val player = Player(name, Settings.startingHealth, Settings.startingStrength, Settings.startingDexterity)

        if (level.dungeonLevel == 21) {
            val area: DrawRoomsMap = level.rooms[0]
            if (area is LastRoom) player.setLevelPosition(area.playerPositionX, area.playerPositionY)
        } else {
            val area: DrawRoomsMap = level.rooms[4]
            player.setLevelPosition(area.x + area.length / 2, area.y + area.height / 2)
        }
        player.setRoomPosition(player.levelX, player.levelY)
        player.currentRoomIndex = 4

        val engine = GameEngine(player, level)
        val weapon = WeaponEngine(player, level, engine)
        startSession(GameSession(player, engine, weapon))
    }

    private fun loadSavedGame(): GameSession? {
        return try {
            val save = SaveLoadManager.loadGame()
            val dungeonLevel = save.dungeonLevel

            val player = LoadGame.restorePlayer(save.player)
            if (player == null) {
                SaveLoadManager.deleteSave()
                return null
            }
            save.hands?.let { LoadGame.restoreHands(player, it) }
            LoadGame.restoreInventory(player, save.inventory)

            val portal = save.portal?.let { LoadGame.restorePortal(it) }
            val level = LoadGame.restoreLevel(save.level, portal, GameConfig.MAP_WIDTH, GameConfig.MAP_HEIGHT)
                ?: GameLevel(dungeonLevel).also {
                    it.buildLevel(GameConfig.MAP_WIDTH, GameConfig.MAP_HEIGHT)
                    it.generateEnemies(dungeonLevel)
                }

            save.pocket?.let { player.pocket = LoadGame.restorePocket(it, level) }
            save.player?.position?.let {
                player.setLevelPosition(it.x, it.y)
                player.setRoomPosition(it.x, it.y)
            }

            val engine = GameEngine(player, level)
            engine.currentDungeonLevel = dungeonLevel
            val weapon = WeaponEngine(player, level, engine)
            weapon.currentLevel = level
            GameSession(player, engine, weapon)
        } catch (e: Exception) {
            Log.e(TAG, "load failed", e)
            SaveLoadManager.deleteSave()
            null
        }
    }

    private fun startSession(s: GameSession) {
        session = s
        revealFog(s)
        _state.update {
            it.copy(
                screen = AppScreen.Playing,
                overlay = Overlay.None,
                heroName = s.player.name,
                message = null,
                leaderboard = leaderboard.toList()
            )
        }
        publish(s)
    }

    // ═══════════════════════════ СОСТОЯНИЕ ДЛЯ UI ═══════════════════════════

    private fun publish(s: GameSession) {
        val p = s.player
        val weapon = p.backpack.equippedWeapon
        val hands = p.equippedHands

        // ── СЧИТАЕМ СУММАРНЫЕ ХАРАКТЕРИСТИКИ ──
        var totalStrength = p.strength
        var totalDexterity = p.dexterity

        if (weapon != null) {
            when (weapon.type) {
                // Ближний бой: прибавляет силу
                rougelite.BusinessLogicLayer.objects.Weapon.WeaponType.KNIFE,
                rougelite.BusinessLogicLayer.objects.Weapon.WeaponType.SWORD,
                rougelite.BusinessLogicLayer.objects.Weapon.WeaponType.WARHAMMER -> {
                    totalStrength += weapon.weaponPower
                }
                // Дальний бой (кроме лазера/бластера): прибавляет ловкость
                rougelite.BusinessLogicLayer.objects.Weapon.WeaponType.BOW,
                rougelite.BusinessLogicLayer.objects.Weapon.WeaponType.SHOTGUN,
                rougelite.BusinessLogicLayer.objects.Weapon.WeaponType.MINIGUN -> {
                    totalDexterity += weapon.weaponPower
                }
                // Лазер, бластер и аннигилятор не прибавляют ничего
                else -> {}
            }
        } else if (hands != null) {
            // Если в руках кулаки/когти, прибавляем их силу
            totalStrength += hands.handsPower
        }

        val gameStatistic = GameStatisticState(
            playerLevel = p.currentPlayerLevel,
            experience = p.experience.toLong(),
            strength = totalStrength,
            health = p.health,
            maxHealth = p.maxHealth,
            dexterity = totalDexterity,
            treasure = p.backpack.treasure,
            dungeonLevel = s.gameEngine.currentDungeonLevel,
            weaponName = weapon?.name ?: hands?.name ?: "Пустые руки",
            ammoText = if (weapon != null && weapon.maxAmmo > 0) "${weapon.currentAmmo}/${weapon.maxAmmo}" else "n/a"
        )
        _state.update { it.copy(gameStatistic = gameStatistic, tick = it.tick + 1) }
    }

    /** Замена Messages.show(text, ms): сообщение живёт ms миллисекунд. */
    private fun showMessage(text: String, durationMs: Long) {
        messageJob?.cancel()
        _state.update { it.copy(message = text) }
        messageJob = viewModelScope.launch {
            delay(durationMs)
            _state.update { it.copy(message = null) }
        }
    }

    private fun showMessage(text: String, durationMs: Int) = showMessage(text, durationMs.toLong())

    override fun onCleared() {
        Messages.listener = null
        super.onCleared()
    }

    private companion object {
        const val TAG = "GameViewModel"
    }
}