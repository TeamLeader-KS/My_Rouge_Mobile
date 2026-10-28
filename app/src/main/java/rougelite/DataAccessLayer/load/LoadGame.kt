package rougelite.DataAccessLayer.load

import rougelite.BusinessLogicLayer.characters.Enemy
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.characters.behavior.*
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior
import rougelite.BusinessLogicLayer.inventory.Pocket
import rougelite.BusinessLogicLayer.logic.MoveLogic
import rougelite.BusinessLogicLayer.mapGenerator.*
import rougelite.BusinessLogicLayer.objects.*
import rougelite.DataAccessLayer.save.*
import rougelite.DataAccessLayer.save.ItemSave.ItemType

object LoadGame {

    // ==================== ВОССТАНОВЛЕНИЕ ИГРОКА ====================
    fun restorePlayer(playerSave: PlayerSave?): Player? {
        if (playerSave == null) return null

        val player =
                Player(
                                playerSave.name,
                                playerSave.maxHealth,
                                playerSave.strength,
                                playerSave.dexterity
                        )
                        .apply {
                            health = playerSave.health
                            experience = playerSave.experience
                            currentPlayerLevel = playerSave.level

                            playerSave.position?.let { pos ->
                                setLevelPosition(pos.x, pos.y)
                                setRoomPosition(pos.x, pos.y)
                            }

                            // ==================== ВОССТАНОВЛЕНИЕ НАПРАВЛЕНИЯ ИГРОКА
                            // ====================
                            playerSave.facing?.let { facing ->
                                this.facing =
                                        if (facing == "RIGHT") {
                                            MoveLogic.Direction.RIGHT
                                        } else {
                                            MoveLogic.Direction.LEFT
                                        }
                            }
                        }

        return player
    }

    // ==================== ВОССТАНОВЛЕНИЕ РУК ====================
    fun restoreHands(player: Player?, handsSave: HandsSave?) {
        if (handsSave == null || player == null) return
        val hiddenBackpack = player.hiddenBackpack
        for (hand in hiddenBackpack.allHands) {
            if (hand.type.name == handsSave.type) {
                hiddenBackpack.equippedHands = hand
                break
            }
        }
    }

    // ==================== ВОССТАНОВЛЕНИЕ ИНВЕНТАРЯ ====================
    fun restoreInventory(player: Player?, inventorySave: InventorySave?) {
        if (player == null || inventorySave == null) return

        val backpack = player.backpack

        // 1. Оружие
        inventorySave.weapons?.forEach { ws ->
            try {
                val type = Weapon.WeaponType.valueOf(ws.type)
                val weapon =
                        Weapon(type).apply {
                            currentAmmo = ws.currentAmmo
                            ws.name?.let { if (it.isNotEmpty()) name = it }
                        }
                backpack.addWeapon(weapon)

                if (inventorySave.equippedWeaponType == ws.type) {
                    val currentIndex = backpack.weapons.size - 1
                    backpack.equipWeapon(currentIndex)
                }
            } catch (e: IllegalArgumentException) {
            }
        }

        // 2. Амуниция
        inventorySave.ammo?.forEach { asSave ->
            try {
                val type = Ammo.AmmoType.valueOf(asSave.type)
                val ammo = Ammo(type).apply { setAmount(asSave.amount) }
                backpack.addAmmo(ammo)
            } catch (e: IllegalArgumentException) {

            }
        }

        // 3. Зелья
        inventorySave.potions?.forEach { ps ->
            try {
                val type = HealthPotion.HealthPotionType.valueOf(ps.type)
                val potion = HealthPotion(type)
                backpack.addHealthPotion(potion)
            } catch (e: IllegalArgumentException) {

            }
        }

        // 4. Усиления
        inventorySave.reinforces?.forEach { rs ->
            try {
                val type = Reinforce.ReinforceType.valueOf(rs.type)
                val reinforce = Reinforce(type)
                backpack.addReinforce(reinforce)
            } catch (e: IllegalArgumentException) {

            }
        }

        // 5. Сокровища
        backpack.addTreasure(inventorySave.treasure)
    }

    // ==================== ВОССТАНОВЛЕНИЕ КАРМАНА ====================
    fun restorePocket(pocketSave: PocketSave?, gameLevel: GameLevel): Pocket {
        val pocket = Pocket()
        pocketSave?.stones?.forEach { roomIndex ->
            for (area in gameLevel.rooms) {
                when (area) {
                    is Room -> {
                        if (area.roomIndex == roomIndex) {
                            pocket.addStone(PortalStone(roomIndex))
                            break
                        }
                    }
                    is LastRoom -> {
                        if (area.roomIndex == roomIndex) {
                            pocket.addStone(PortalStone(roomIndex))
                            break
                        }
                    }
                }
            }
        }
        return pocket
    }

    // ==================== ВОССТАНОВЛЕНИЕ ПОРТАЛА ====================
    fun restorePortal(portalSave: PortalSave?): Portal? {
        if (portalSave == null) return null
        val portal = Portal(portalSave.x, portalSave.y)
        portalSave.stonesInserted?.forEach { stoneIndex -> portal.insertStone(stoneIndex) }
        if (portalSave.isOpen) {
            portal.portalOpen(true)
        }
        return portal
    }

    // ==================== ВОССТАНОВЛЕНИЕ УРОВНЯ ====================
    fun restoreLevel(
            levelSave: LevelSave?,
            portal: Portal?,
            windowWidth: Int,
            windowHeight: Int
    ): GameLevel? {
        if (levelSave == null) return null

        val gameLevel = GameLevel(levelSave.dungeonLevel)

        if (portal != null) {
            gameLevel.portal = portal

        }

        // 1. Комнаты
        val restoredRooms = mutableListOf<DrawRoomsMap>()
        levelSave.rooms?.forEach { roomSave ->
            val isLastRoom = roomSave.height == 11 && roomSave.length == 31 && roomSave.index == 4

            val room: DrawRoomsMap =
                    if (isLastRoom) {
                        LastRoom(roomSave.index, roomSave.isCenterRoom).apply {
                            setPosition(roomSave.x, roomSave.y)

                        }
                    } else {
                        Room(roomSave.index, roomSave.isCenterRoom).apply {
                            setPosition(roomSave.x, roomSave.y)
                            height = roomSave.height
                            length = roomSave.length
                            roomSave.tiles?.let { tiles = it }
                        }
                    }

            if (roomSave.hasPortalStone) {
                val stone =
                        PortalStone(
                                roomSave.portalStoneIndex ?: roomSave.index,
                                roomSave.stoneX,
                                roomSave.stoneY
                        )
                when (room) {
                    is Room -> room.portalStone = stone
                    is LastRoom -> room.portalStone = stone
                }
            }

            if (roomSave.portalX != -1 && roomSave.portalY != -1) {
                if (portal != null) {
                    when (room) {
                        is Room -> room.portal = portal
                        is LastRoom -> room.portal = portal
                    }

                }
            }

            restoredRooms.add(room)
        }
        gameLevel.rooms.clear()
        gameLevel.rooms.addAll(restoredRooms)

        // 2. Коридоры
        val restoredCorridors = mutableListOf<Corridor>()
        levelSave.corridors?.forEach { corridorSave ->
            val corridor =
                    Corridor(
                            corridorSave.fromRoom,
                            corridorSave.toRoom,
                            corridorSave.path ?: emptyList()
                    )
            restoredCorridors.add(corridor)
        }
        gameLevel.corridors = restoredCorridors

        // 3. Предметы
        val restoredItems = mutableListOf<GameObject>()
        levelSave.itemsOnFloor?.forEach { itemSave ->
            var obj: GameObject? = null
            try {
                when (itemSave.type) {
                    ItemType.WEAPON -> {
                        val w =
                                Weapon(Weapon.WeaponType.valueOf(itemSave.subtype)).apply {
                                    setPosition(itemSave.x, itemSave.y)
                                    itemSave.varietyName?.let { name = it }
                                    currentAmmo = itemSave.currentAmmo
                                }
                        obj = w
                    }
                    ItemType.AMMO -> {
                        val a =
                                Ammo(Ammo.AmmoType.valueOf(itemSave.subtype)).apply {
                                    setAmount(itemSave.amount)
                                    setPosition(itemSave.x, itemSave.y)
                                }
                        obj = a
                    }
                    ItemType.HEALTH_POTION -> {
                        val p =
                                HealthPotion(
                                                HealthPotion.HealthPotionType.valueOf(
                                                        itemSave.subtype
                                                )
                                        )
                                        .apply { setPosition(itemSave.x, itemSave.y) }
                        obj = p
                    }
                    ItemType.REINFORCE -> {
                        val r =
                                Reinforce(Reinforce.ReinforceType.valueOf(itemSave.subtype)).apply {
                                    setPosition(itemSave.x, itemSave.y)
                                }
                        obj = r
                    }
                    ItemType.DARK_CROWN -> {
                        val crown =
                                DarkCrown(DarkCrown.DarkCrownType.DARK_CROWN).apply {
                                    setPosition(itemSave.x, itemSave.y)
                                }
                        obj = crown
                    }
                    else -> {}
                }
            } catch (e: Exception) {

            }
            if (obj != null) restoredItems.add(obj)
        }
        gameLevel.items = restoredItems

        // 4. Враги
        val restoredEnemies = mutableListOf<Enemy>()
        levelSave.enemies?.forEach { enemySave ->
            try {
                val type = Enemy.EnemyType.valueOf(enemySave.type)
                val behavior = createBehavior(type)
                val enemy =
                        Enemy(type, enemySave.x, enemySave.y, enemySave.level, behavior).apply {
                            health = enemySave.health
                            maxHealth = enemySave.maxHealth
                            isVisible = enemySave.isVisible
                            if (!enemySave.isAlive) health = 0
                        }
                restoredEnemies.add(enemy)
            } catch (e: Exception) {

            }
        }
        gameLevel.enemies = restoredEnemies

        // 5. Карты
        levelSave.levelMap?.let {
            gameLevel.levelMap = it

        }
        levelSave.explorationMap?.let { rawMap ->
            gameLevel.setExplorationMap(rawMap)

        }

        // 6. Решаем, нужно ли перестраивать уровень
        if (levelSave.levelMap == null) {

            gameLevel.buildLevel(windowWidth, windowHeight)
        } else {

        }

        return gameLevel
    }

    // ==================== СОЗДАНИЕ ПОВЕДЕНИЯ ====================
    private fun createBehavior(type: Enemy.EnemyType): EnemyBehavior {
        return when (type) {
            Enemy.EnemyType.ZOMBIE -> ZombieBehavior()
            Enemy.EnemyType.VAMPIRE -> VampireBehavior()
            Enemy.EnemyType.GHOST -> GhostBehavior()
            Enemy.EnemyType.OGRE -> OgreBehavior()
            Enemy.EnemyType.SNAKE_MAGE -> SnakeMageBehavior()
            Enemy.EnemyType.BOSS -> BossBehavior()
        }
    }
}
