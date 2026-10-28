package rougelite.BusinessLogicLayer.characters

import kotlin.math.abs
import kotlin.random.Random
import rougelite.BusinessLogicLayer.characters.behavior.MoveDirection
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior
import rougelite.BusinessLogicLayer.logic.Settings
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap
import rougelite.BusinessLogicLayer.message.GameMessages
import rougelite.BusinessLogicLayer.message.Messages

class Enemy(val enemyType: EnemyType, x: Int, y: Int, val level: Int, val behavior: EnemyBehavior) :
    Character(
        name = enemyType.russianEnemyName,
        health = calculateHealth(enemyType, level),
        strength = calculateStrength(enemyType, level),
        dexterity = calculateDexterity(enemyType, level)
    ) {

    var facing: MoveDirection = MoveDirection.ENEMY_MOVE_LEFT
    var isVisible: Boolean = true
    var isAggression: Boolean = false
    var isPlayerNoticed: Boolean = false

    val isDisguised: Boolean
        get() = enemyType == EnemyType.BOSS && !isVisible

    init {
        setPosition(x, y)
    }

    enum class EnemyType(
        val russianEnemyName: String,
        val initialHealth: Int,
        val initialStrength: Int,
        val initialDexterity: Int
    ) {
        ZOMBIE("Зомби", 15, 5, 5),
        VAMPIRE("Вампир", 100, 10, 35),
        GHOST("Призрак", 10, 5, 15),
        OGRE("Огр", 200, 20, 10),
        SNAKE_MAGE("Змеиный маг", 150, 15, 50),
        BOSS("Владыка Тьмы", 5000, 50, 100)
    }

    // TODO настроить экспу
    fun generatorExperience(): Int = maxHealth * 2 + strength * 2 + dexterity * 2

    fun generatorTreasure(): Int = maxHealth + strength + dexterity

    fun enemyMove(enemy: Enemy, room: DrawRoomsMap, direction: MoveDirection, player: Player) {
        var newX = x
        var newY = y

        when (direction) {
            MoveDirection.ENEMY_MOVE_UP -> newY--
            MoveDirection.ENEMY_MOVE_DOWN -> newY++
            MoveDirection.ENEMY_MOVE_LEFT -> newX--
            MoveDirection.ENEMY_MOVE_RIGHT -> newX++
            MoveDirection.ENEMY_MOVE_UP_LEFT -> {
                newX--
                newY--
            }
            MoveDirection.ENEMY_MOVE_UP_RIGHT -> {
                newX++
                newY--
            }
            MoveDirection.ENEMY_MOVE_DOWN_LEFT -> {
                newX--
                newY++
            }
            MoveDirection.ENEMY_MOVE_DOWN_RIGHT -> {
                newX++
                newY++
            }
            MoveDirection.ENEMY_SKIP_MOVE -> return
            MoveDirection.ENEMY_MOVE_RANDOM -> {
                newX =
                    room.x +
                            DISTANCE_FROM_WALL +
                            Random.nextInt(room.length - DISTANCE_FROM_WALL * 2)
                newY =
                    room.y +
                            DISTANCE_FROM_WALL +
                            Random.nextInt(room.height - DISTANCE_FROM_WALL * 2)
            }
            MoveDirection.BOSS_SCARES -> {
                val scares = GameMessages.BOSS_SCARES.random()
                Messages.show(scares, 1500)
            }
        }

        if (newX == player.levelX && newY == player.levelY) {
            return
        }

        val isInsideRoom =
            newX >= room.x + DISTANCE_FROM_WALL &&
                    newX <= room.x + room.length - DISTANCE_FROM_WALL &&
                    newY >= room.y + DISTANCE_FROM_WALL &&
                    newY <= room.y + room.height - DISTANCE_FROM_WALL

        if (isInsideRoom) {
            if (direction != MoveDirection.ENEMY_SKIP_MOVE) {
                facing = direction
            }
            setPosition(newX, newY)
        }
    }

    fun attackPlayer(player: Player): Boolean {
        if (!isAlive || !player.isAlive) return false

        val dx = abs(x - player.levelX)
        val dy = abs(y - player.levelY)

        if (dx <= 1 && dy <= 1 && dx + dy > 0) {
            val damage = strength
            player.takeDamage(damage)
            val message = getAttackMessage()
            Messages.show(message, 1500)
            return true
        }
        return false
    }

    private fun getAttackMessage(): String =
        when (enemyType) {
            EnemyType.ZOMBIE -> GameMessages.ZOMBIE_ATTACKED_YOU
            EnemyType.VAMPIRE -> GameMessages.VAMPIRE_ATTACKED_YOU
            EnemyType.GHOST -> GameMessages.GHOST_ATTACKED_YOU
            EnemyType.OGRE -> GameMessages.OGRE_ATTACKED_YOU
            EnemyType.SNAKE_MAGE -> GameMessages.SNAKE_MAGE_ATTACKED_YOU
            EnemyType.BOSS -> GameMessages.BOSS_ATTACKED_YOU
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Enemy) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    companion object {
        private const val DISTANCE_FROM_WALL = 2

        fun calculateHealth(type: EnemyType, level: Int): Int =
            type.initialHealth + ((level - 1) * Settings.ENEMY_HEALTH_MULTIPLIER)

        fun calculateStrength(type: EnemyType, level: Int): Int =
            type.initialStrength + ((level - 1) + Settings.ENEMY_STRENGTH_MULTIPLIER)

        fun calculateDexterity(type: EnemyType, level: Int): Int =
            type.initialDexterity + ((level - 1) * Settings.ENEMY_DEXTERITY_MULTIPLIER)
    }
}