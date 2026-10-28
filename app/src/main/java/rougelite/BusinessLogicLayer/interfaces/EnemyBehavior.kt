package rougelite.BusinessLogicLayer.interfaces

import rougelite.BusinessLogicLayer.characters.Enemy
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel
import rougelite.BusinessLogicLayer.objects.GameObject
import kotlin.math.abs

interface EnemyBehavior {
    fun enemyMovePattern(enemy: Enemy, room: DrawRoomsMap, player: Player, level: GameLevel)
    fun getAgroRadius(): Array<BooleanArray>

    fun pursuit(enemy: Enemy, player: Player, level: GameLevel): String?
    fun attackOnPlayer(enemy: Enemy, player: Player): Boolean
    fun receivingDamage(enemy: Enemy, damage: Int)

    fun dropGenerator(enemyType: Enemy): GameObject?

    fun isPlayerInAgroRadius(enemy: Enemy, player: Player, room: DrawRoomsMap): Boolean {
        val radius = getAgroRadius()
        val height = radius.size
        val width = radius[0].size
        val centerX = width / 2
        val centerY = height / 2

        val differenceX = player.levelX - enemy.x
        val differenceY = player.levelY - enemy.y
        val radiusX = differenceX + centerX
        val radiusY = differenceY + centerY

        if (radiusX !in 0 until width || radiusY !in 0 until height) {
            return false
        }
        if (!radius[radiusY][radiusX]) {
            return false
        }

        return hasLineOfSight(enemy, player, room)
    }

    fun hasLineOfSight(enemy: Enemy, player: Player, room: DrawRoomsMap): Boolean {
        val x1 = enemy.x
        val y1 = enemy.y
        val x2 = player.levelX
        val y2 = player.levelY

        val dx = abs(x2 - x1)
        val dy = abs(y2 - y1)
        val sx = if (x1 < x2) 1 else -1
        val sy = if (y1 < y2) 1 else -1
        var err = dx - dy

        var currentX = x1
        var currentY = y1

        while (true) {
            if (currentX == x2 && currentY == y2) {
                return true
            }
            if (currentX != x1 || currentY != y1) {
                val tile = room.getTileAt(currentX, currentY)
                if (tile == 1) {
                    return false
                }
            }
            val e2 = 2 * err
            if (e2 > -dy) {
                err -= dy
                currentX += sx
            }
            if (e2 < dx) {
                err += dx
                currentY += sy
            }
            if (currentX < room.x || currentX >= room.x + room.length ||
                currentY < room.y || currentY >= room.y + room.height
            ) {
                return false
            }
        }
    }
}