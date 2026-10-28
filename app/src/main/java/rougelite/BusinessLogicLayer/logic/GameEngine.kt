package rougelite.BusinessLogicLayer.logic

import rougelite.BusinessLogicLayer.characters.Enemy
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel
import rougelite.BusinessLogicLayer.mapGenerator.LastRoom
import rougelite.BusinessLogicLayer.message.GameMessages
import rougelite.BusinessLogicLayer.message.Messages
import rougelite.BusinessLogicLayer.objects.DarkCrown
import rougelite.BusinessLogicLayer.objects.GameObject

class GameEngine(val player: Player, startLevel: GameLevel) {
    var currentLevel: GameLevel = startLevel
    val inventoryEngine: InventoryEngine = InventoryEngine(player)
    val pocketEngine: PocketEngine = PocketEngine(player)
    var currentDungeonLevel: Int = startLevel.dungeonLevel
    private var crownPicked: Boolean = false

    // ==================== ПОРТАЛ ====================

    fun useStoneOnPortal(): String {
        if (player.currentRoomIndex != 4) {
            return GameMessages.IMPOSSIBLE_USE_PORTAL_STONE
        }
        return pocketEngine.useStoneOnPortal(currentLevel.portal) ?: ""
    }

    fun canEnterPortal(): Boolean =
        pocketEngine.canEnterPortal(
            currentLevel.portal,
            player.currentRoomIndex,
            player.levelX,
            player.levelY
        )

    // ==================== УРОВНИ ====================

    fun nextLevel(windowWidth: Int, windowHeight: Int): GameLevel {
        pocketEngine.clearPocket()

        val newLevel =
            GameLevel(currentDungeonLevel).apply {
                buildLevel(windowWidth, windowHeight)
                generateEnemies(currentDungeonLevel)
            }

        currentDungeonLevel++


        if (currentDungeonLevel == 21) {
            val area: DrawRoomsMap = newLevel.rooms[0]
            if (area is LastRoom) {
                val playerX = area.playerPositionX
                val playerY = area.playerPositionY
                player.setLevelPosition(playerX, playerY)
                player.setRoomPosition(playerX - area.x, playerY - area.y)
                player.currentRoomIndex = 0
            }
        } else {
            val centerRoom = newLevel.rooms[4]
            val centerX = centerRoom.x + centerRoom.length / 2
            val centerY = centerRoom.y + centerRoom.height / 2
            player.setLevelPosition(centerX, centerY)
            player.setRoomPosition(centerX - centerRoom.x, centerY - centerRoom.y)
            player.currentRoomIndex = 4
        }

        this.currentLevel = newLevel
        return newLevel
    }

    fun addExperienceFromEnemy(enemy: Enemy?) {
        if (enemy == null) {
            return
        }


        val experience = enemy.generatorExperience()


        player.addNewLevelForPlayer(experience.toLong())

        Messages.show(GameMessages.EXPERIENCE_UP, 1500)
    }

    fun addTreasuresFromEnemy(enemy: Enemy?) {
        if (enemy == null || enemy.isAlive) {
            return
        }
        val treasures = enemy.generatorTreasure()
        player.backpack.addTreasure(treasures)
        Messages.show(GameMessages.TREASURE_UP, 1500)
    }

    fun canPickUpCrown(): Boolean {
        val item: GameObject? = currentLevel.getItemAt(player.levelX, player.levelY)
        return item is DarkCrown
    }
}