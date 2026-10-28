package rougelite.DataAccessLayer

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.util.ArrayList
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.logic.GameEngine
import rougelite.BusinessLogicLayer.logic.MoveLogic
import rougelite.BusinessLogicLayer.mapGenerator.*
import rougelite.BusinessLogicLayer.objects.*
import rougelite.DataAccessLayer.save.*

object SaveLoadManager {

    private const val TAG = "SaveLoadManager"
    private const val SAVE_FILE_NAME = "savegame.json"
    private const val LEADERBOARD_FILE_NAME = "leaderboard.json"

    /** Папка приложения (context.filesDir/saves). Задаётся один раз при старте через [init]. */
    private var saveDir: File? = null

    /** Вызвать один раз до любых других методов: SaveLoadManager.init(context.filesDir). */
    fun init(filesDir: File) {
        saveDir = File(filesDir, "saves").also { it.mkdirs() }
    }

    private fun dir(): File =
        saveDir ?: throw IllegalStateException("SaveLoadManager.init(filesDir) не был вызван")

    private fun saveFile() = File(dir(), SAVE_FILE_NAME)

    private fun leaderboardFile() = File(dir(), LEADERBOARD_FILE_NAME)

    /** Пишем во временный файл и переименовываем: если приложение убьют на записи, старое сохранение не испортится. */
    private fun writeAtomically(target: File, text: String) {
        val tmp = File(target.parentFile, target.name + ".tmp")
        tmp.writeText(text)
        if (!tmp.renameTo(target)) {
            target.delete()
            if (!tmp.renameTo(target)) throw java.io.IOException("Не удалось записать ${target.name}")
        }
    }

    // ==================== СОХРАНЕНИЕ ====================
    fun saveGame(gameEngine: GameEngine, player: Player, level: GameLevel) {
        try {
            val save =
                SaveGame().apply {
                    // 1. Уровень подземелья
                    dungeonLevel = gameEngine.currentDungeonLevel

                    // 2. Игрок
                    this.player = savePlayer(player)
                    hands = saveHands(player)

                    // 3. Инвентарь
                    inventory = saveInventory(player)

                    // 4. Карман с камнями
                    pocket = savePocket(player)

                    // 5. Портал
                    portal = savePortal(level)

                    // 6. Уровень (комнаты, коридоры, предметы, враги, туман)
                    this.level = saveLevel(level)
                }

            // 7. Сохраняем в JSON
            val json = GsonBuilder().setPrettyPrinting().create().toJson(save)
            writeAtomically(saveFile(), json)

            Log.i(TAG, "Игра сохранена, уровень ${gameEngine.currentDungeonLevel}")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка сохранения", e)
        }
    }

    // ==================== СОХРАНЕНИЕ ИГРОКА ====================
    private fun savePlayer(player: Player): PlayerSave {
        return PlayerSave().apply {
            name = player.name
            health = player.health
            maxHealth = player.maxHealth
            strength = player.strength
            dexterity = player.dexterity
            experience = player.experience
            level = player.currentPlayerLevel
            position = PositionSave(player.levelX, player.levelY)
            facing = if (player.facing == MoveLogic.Direction.RIGHT) "RIGHT" else "LEFT"
        }
    }

    // ==================== СОХРАНЕНИЕ РУК ====================
    private fun saveHands(player: Player): HandsSave? {
        val equipped = player.equippedHands ?: return null
        return HandsSave().apply { type = equipped.type.name }
    }

    // ==================== СОХРАНЕНИЕ ИНВЕНТАРЯ ====================
    private fun saveInventory(player: Player): InventorySave {
        val backpack = player.backpack
        val inv = InventorySave()

        // Оружие
        inv.weapons =
            ArrayList(
                backpack.weapons.map { w ->
                    WeaponSave().apply {
                        type = w.type.name
                        currentAmmo = w.currentAmmo
                        maxAmmo = w.maxAmmo
                        name = w.name
                    }
                }
            )

        // Амуниция
        inv.ammo =
            ArrayList(
                backpack.ammo.map { a ->
                    AmmoSave().apply {
                        type = a.type.name
                        amount = a.amountAmmo
                    }
                }
            )

        // Зелья
        inv.potions =
            ArrayList(
                backpack.healthPotion.map { p -> PotionSave().apply { type = p.type.name } }
            )

        // Усиления
        inv.reinforces =
            ArrayList(
                backpack.reinforces.map { r ->
                    ReinforceSave().apply { type = r.type.name }
                }
            )

        // Сокровища
        inv.treasure = backpack.treasure

        // Экипированное оружие
        val equipped = backpack.equippedWeapon
        if (equipped != null) {
            inv.equippedWeaponType = equipped.type.name
        }

        return inv
    }

    // ==================== СОХРАНЕНИЕ КАРМАНА ====================
    private fun savePocket(player: Player): PocketSave {
        val ps = PocketSave()
        ps.stones = ArrayList(player.pocket.allStones.map { it.sourceRoomIndex })
        return ps
    }

    // ==================== СОХРАНЕНИЕ ПОРТАЛА ====================
    private fun savePortal(level: GameLevel): PortalSave? {
        val portal = level.portal ?: return null

        return PortalSave().apply {
            x = portal.x
            y = portal.y
            stonesInserted = ArrayList(portal.getInsertedStones())
            isOpen = portal.isOpen
        }
    }

    // ==================== СОХРАНЕНИЕ УРОВНЯ ====================
    private fun saveLevel(level: GameLevel): LevelSave {
        val ls = LevelSave()
        ls.dungeonLevel = level.dungeonLevel

        // 1. Комнаты
        val roomsList = ArrayList<RoomSave>()
        for (area in level.rooms) {
            val rs = RoomSave()

            when (area) {
                is Room -> {
                    rs.index = area.roomIndex
                    rs.x = area.x
                    rs.y = area.y
                    rs.height = area.height
                    rs.length = area.length
                    rs.tiles = area.tiles
                    rs.isCenterRoom = area.isCenterRoom

                    val stone = area.portalStone
                    rs.hasPortalStone = stone != null
                    if (stone != null) {
                        rs.portalStoneIndex = stone.sourceRoomIndex
                        rs.stoneX = stone.stoneX
                        rs.stoneY = stone.stoneY
                    } else {
                        rs.stoneX = -1
                        rs.stoneY = -1
                    }

                    val portal = area.portal
                    if (portal != null) {
                        rs.portalX = portal.x
                        rs.portalY = portal.y
                    } else {
                        rs.portalX = -1
                        rs.portalY = -1
                    }

                    roomsList.add(rs)
                }
                is LastRoom -> {
                    rs.index = area.roomIndex
                    rs.x = area.x
                    rs.y = area.y
                    rs.height = area.height
                    rs.length = area.length
                    rs.tiles = area.tiles
                    rs.isCenterRoom = area.isCenterRoom

                    val stone = area.portalStone
                    rs.hasPortalStone = stone != null
                    if (stone != null) {
                        rs.portalStoneIndex = stone.sourceRoomIndex
                    }

                    val portal = area.portal
                    if (portal != null) {
                        rs.portalX = portal.x
                        rs.portalY = portal.y
                    } else {
                        rs.portalX = -1
                        rs.portalY = -1
                    }

                    roomsList.add(rs)
                }
            }
        }
        ls.rooms = roomsList

        // 2. Коридоры
        ls.corridors =
            ArrayList(
                level.corridors.map { c ->
                    CorridorSave().apply {
                        fromRoom = c.fromRoom
                        toRoom = c.toRoom
                        path = c.path
                    }
                }
            )

        // 3. Предметы на полу
        val itemsList = ArrayList<ItemSave>()
        for (item in level.items) {
            val isSave =
                ItemSave().apply {
                    x = item.x
                    y = item.y
                }

            when (item) {
                is Weapon -> {
                    isSave.type = ItemSave.ItemType.WEAPON
                    isSave.subtype = item.type.name
                    isSave.currentAmmo = item.currentAmmo
                    isSave.varietyName = item.name
                }
                is Ammo -> {
                    isSave.type = ItemSave.ItemType.AMMO
                    isSave.subtype = item.type.name
                    isSave.amount = item.amountAmmo
                    isSave.varietyName = item.name
                }
                is HealthPotion -> {
                    isSave.type = ItemSave.ItemType.HEALTH_POTION
                    isSave.subtype = item.type.name
                    isSave.varietyName = item.name
                }
                is Reinforce -> {
                    isSave.type = ItemSave.ItemType.REINFORCE
                    isSave.subtype = item.type.name
                    isSave.varietyName = item.name
                }
                is DarkCrown -> {
                    isSave.type = ItemSave.ItemType.DARK_CROWN
                    isSave.varietyName = "Корона Тьмы"
                }
            }
            itemsList.add(isSave)
        }
        ls.itemsOnFloor = itemsList

        // 4. Враги
        ls.enemies =
            ArrayList(
                level.enemies.map { enemy ->
                    EnemySave().apply {
                        type = enemy.enemyType.name
                        x = enemy.x
                        y = enemy.y
                        this.level = enemy.level
                        health = enemy.health
                        maxHealth = enemy.maxHealth
                        isAlive = enemy.isAlive
                        isVisible = enemy.isVisible
                        isDisguised = enemy.isDisguised
                    }
                }
            )

        // 5. Туман войны (explorationMap)
        val levelMap = level.levelMap
        val height = levelMap.size
        val width = levelMap[0].size
        ls.explorationMap =
            Array(height) { y -> IntArray(width) { x -> level.getExploration(x, y).ordinal } }

        // 6. Карта уровня
        ls.levelMap = levelMap

        return ls
    }

    // ==================== ЗАГРУЗКА ====================
    @Throws(Exception::class)
    fun loadGame(): SaveGame {
        val json = saveFile().readText()
        return Gson().fromJson(json, SaveGame::class.java)
    }

    fun hasSave(): Boolean = saveFile().exists()

    fun deleteSave() {
        try {
            saveFile().delete()
            Log.i(TAG, "Файл сохранения удалён")
        } catch (e: Exception) {
            Log.e(TAG, "Не удалось удалить сохранение", e)
        }
    }

    // ==================== ТАБЛИЦА ЛИДЕРОВ ====================
    fun saveLeaderboard(recording: List<LeaderboardEntry>) {
        try {
            val save = LeaderBoardSave().apply { this.recording = ArrayList(recording) }
            val json = GsonBuilder().setPrettyPrinting().create().toJson(save)
            writeAtomically(leaderboardFile(), json)
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка сохранения таблицы лидеров", e)
        }
    }

    fun loadLeaderboard(): List<LeaderboardEntry> {
        return try {
            val file = leaderboardFile()
            if (!file.exists()) return ArrayList()
            val save = Gson().fromJson(file.readText(), LeaderBoardSave::class.java)
            save.recording ?: ArrayList()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки таблицы лидеров", e)
            ArrayList()
        }
    }
}