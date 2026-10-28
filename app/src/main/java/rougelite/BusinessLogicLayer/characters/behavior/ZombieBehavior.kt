package rougelite.BusinessLogicLayer.characters.behavior

import rougelite.BusinessLogicLayer.characters.Enemy
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel
import rougelite.BusinessLogicLayer.message.GameMessages
import rougelite.BusinessLogicLayer.objects.GameObject
import rougelite.BusinessLogicLayer.objects.HealthPotion
import rougelite.BusinessLogicLayer.objects.Reinforce
import rougelite.BusinessLogicLayer.message.Messages
import java.util.ArrayDeque
import kotlin.math.abs
import kotlin.random.Random

class ZombieBehavior : EnemyBehavior {

    private var currentLevel: GameLevel? = null

    override fun getAgroRadius(): Array<BooleanArray> = AGRO_RADIUS

    override fun enemyMovePattern(
        enemy: Enemy,
        room: DrawRoomsMap,
        player: Player,
        level: GameLevel
    ) {
        this.currentLevel = level

        if (isPlayerInAgroRadius(enemy, player, room)) {
            if (!enemy.isPlayerNoticed) {
                enemy.isPlayerNoticed = true
                Messages.show(GameMessages.ENEMY_NOTICED_YOU, 1500)
            }

            if (attackOnPlayer(enemy, player)) {
                return
            }
        } else {
            enemy.isPlayerNoticed = false
        }

        val direction = IDLE_DIRECTIONS.random()
        enemy.enemyMove(enemy, room, direction, player)
    }

    override fun pursuit(enemy: Enemy, player: Player, level: GameLevel): String? {
        val startX = enemy.x
        val startY = enemy.y
        val targetX = player.levelX
        val targetY = player.levelY

        val map = level.levelMap
        val height = map.size
        val width = map[0].size

        val occupied = HashSet<String>()
        for (e in level.enemies) {
            if (e != enemy) occupied.add("${e.x},${e.y}")
        }
        for (item in level.items) {
            occupied.add("${item.x},${item.y}")
        }

        val visited = Array(height) { BooleanArray(width) }
        val parentX = Array(height) { IntArray(width) }
        val parentY = Array(height) { IntArray(width) }
        val parentDir = Array(height) { arrayOfNulls<MoveDirection>(width) }

        val queue = ArrayDeque<IntArray>()
        queue.add(intArrayOf(startX, startY))
        visited[startY][startX] = true

        val dx = intArrayOf(0, 0, -1, 1)
        val dy = intArrayOf(-1, 1, 0, 0)

        while (queue.isNotEmpty()) {
            val cur = queue.poll() ?: break
            var x = cur[0]
            var y = cur[1]

            val isAdjacentToPlayer = (abs(x - targetX) + abs(y - targetY) == 1)
            if (isAdjacentToPlayer) {
                if (map[y][x] != 1 && !occupied.contains("$x,$y")) {
                    while (parentX[y][x] != startX || parentY[y][x] != startY) {
                        val px = parentX[y][x]
                        val py = parentY[y][x]
                        x = px
                        y = py
                    }
                    return parentDir[y][x]?.name
                }
            }

            for (i in 0 until 4) {
                val nx = x + dx[i]
                val ny = y + dy[i]

                if (nx !in 0 until width || ny !in 0 until height) continue
                if (visited[ny][nx]) continue
                if (map[ny][nx] == 1) continue // Стена
                if (occupied.contains("$nx,$ny")) continue

                visited[ny][nx] = true
                parentX[ny][nx] = x
                parentY[ny][nx] = y
                parentDir[ny][nx] = CARDINAL_DIRECTIONS[i]
                queue.add(intArrayOf(nx, ny))
            }
        }

        return null
    }

    override fun attackOnPlayer(enemy: Enemy, player: Player): Boolean {
        return enemy.attackPlayer(player)
    }

    override fun receivingDamage(enemy: Enemy, damage: Int) {
        val healthBefore = enemy.health
        enemy.takeDamage(damage)
        if (!enemy.isAlive) {
            // todo ("☠️ Зомби уничтожен!")
        }
    }

    override fun dropGenerator(enemyType: Enemy): GameObject? {
        if (enemyType.isAlive) return null

        val chance = Random.nextInt(100)

        return when {
            chance < 55 && Random.nextBoolean() -> {
                HealthPotion(POTION_TYPES.random()).apply {
                    setPosition(enemyType.x, enemyType.y)
                }
            }
            chance in 91..99 -> {
                Reinforce(REINFORCE_TYPES.random()).apply {
                    setPosition(enemyType.x, enemyType.y)
                }
            }
            else -> null
        }
    }

    companion object {
        private val AGRO_RADIUS: Array<BooleanArray> = arrayOf(
            booleanArrayOf(false, true, true, true, false),
            booleanArrayOf(true, true, true, true, true),
            booleanArrayOf(true, true, false, true, true),
            booleanArrayOf(true, true, true, true, true),
            booleanArrayOf(false, true, true, true, false)
        )

        private val CARDINAL_DIRECTIONS = arrayOf(
            MoveDirection.ENEMY_MOVE_UP,
            MoveDirection.ENEMY_MOVE_DOWN,
            MoveDirection.ENEMY_MOVE_LEFT,
            MoveDirection.ENEMY_MOVE_RIGHT
        )

        private val IDLE_DIRECTIONS = arrayOf(
            MoveDirection.ENEMY_MOVE_UP,
            MoveDirection.ENEMY_MOVE_DOWN,
            MoveDirection.ENEMY_MOVE_LEFT,
            MoveDirection.ENEMY_MOVE_RIGHT,
            MoveDirection.ENEMY_SKIP_MOVE
        )

        private val POTION_TYPES = arrayOf(
            HealthPotion.HealthPotionType.LOW_HEALTH,
            HealthPotion.HealthPotionType.AVERAGE_HEALTH,
            HealthPotion.HealthPotionType.BIG_HEALTH
        )

        private val REINFORCE_TYPES = Reinforce.ReinforceType.values()
    }
}