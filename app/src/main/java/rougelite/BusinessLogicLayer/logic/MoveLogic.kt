package rougelite.BusinessLogicLayer.logic

import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel
import rougelite.BusinessLogicLayer.mapGenerator.Room
import rougelite.BusinessLogicLayer.objects.GameObject
import rougelite.BusinessLogicLayer.objects.PortalStone

class MoveLogic {

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    data class MoveResult(
        val isSuccess: Boolean,
        val isMoved: Boolean,
        val message: String? = null,
        val pickedItem: GameObject? = null,
        val pickedStone: PortalStone? = null,
        val newX: Int,
        val newY: Int
    ) {
        companion object {
            @JvmStatic
            fun invalid(): MoveResult = MoveResult(
                isSuccess = false,
                isMoved = false,
                newX = -1,
                newY = -1
            )

            @JvmStatic
            fun move(newX: Int, newY: Int): MoveResult = MoveResult(
                isSuccess = true,
                isMoved = true,
                newX = newX,
                newY = newY
            )

            @JvmStatic
            fun withPickup(newX: Int, newY: Int, item: GameObject): MoveResult = MoveResult(
                isSuccess = true,
                isMoved = true,
                pickedItem = item,
                newX = newX,
                newY = newY
            )

            @JvmStatic
            fun withStone(newX: Int, newY: Int, stone: PortalStone): MoveResult = MoveResult(
                isSuccess = true,
                isMoved = true,
                message = stone.reportObject(),
                pickedStone = stone,
                newX = newX,
                newY = newY
            )
        }
    }

    fun tryMove(player: Player, direction: Direction, level: GameLevel): MoveResult {
        var newX = player.levelX
        var newY = player.levelY

        when (direction) {
            Direction.UP -> newY--
            Direction.DOWN -> newY++
            Direction.LEFT -> newX--
            Direction.RIGHT -> newX++
        }

        val levelMap = level.levelMap

        if (newY !in levelMap.indices || newX !in levelMap[0].indices) {
            return MoveResult.invalid()
        }

        val tile = levelMap[newY][newX]

        if (tile == 0 || tile == 2 || tile == 3) {
            val hasEnemy = level.enemies.any { enemy ->
                enemy.isAlive && enemy.x == newX && enemy.y == newY
            }

            if (hasEnemy) {
                return MoveResult.invalid()
            }

            player.setLevelPosition(newX, newY)

            val area = level.getRoomAt(newY, newX)
            if (area is Room) {
                player.currentRoomIndex = area.roomIndex
                val localX = newX - area.x
                val localY = newY - area.y
                player.setRoomPosition(localX, localY)
            }

            val item = level.getItemAt(newX, newY)
            if (item != null) {
                return MoveResult.withPickup(newX, newY, item)
            }

            val stone = getStoneAtPosition(level, newX, newY)
            if (stone != null) {
                return MoveResult.withStone(newX, newY, stone)
            }

            return MoveResult.move(newX, newY)
        }

        return MoveResult.invalid()
    }

    private fun getStoneAtPosition(level: GameLevel, x: Int, y: Int): PortalStone? {
        val area = level.getRoomAt(y, x)
        if (area is Room) {
            val stone = area.portalStone
            if (stone != null && stone.stoneX == x && stone.stoneY == y) {
                area.portalStone = null
                return stone
            }
        }
        return null
    }
}