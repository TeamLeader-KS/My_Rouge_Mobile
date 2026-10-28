package rougelite.BusinessLogicLayer.characters.behavior

import rougelite.BusinessLogicLayer.characters.Enemy
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel
import rougelite.BusinessLogicLayer.message.GameMessages
import rougelite.BusinessLogicLayer.objects.DarkCrown
import rougelite.BusinessLogicLayer.objects.GameObject
import rougelite.BusinessLogicLayer.message.Messages
import kotlin.random.Random

class BossBehavior : EnemyBehavior {

    private var currentLevel: GameLevel? = null
    private var bossEnteredBattle: Boolean = false

    override fun getAgroRadius(): Array<BooleanArray> = AGRO_RADIUS

    override fun enemyMovePattern(
        enemy: Enemy,
        room: DrawRoomsMap,
        player: Player,
        level: GameLevel
    ) {
        this.currentLevel = level

        if (!bossEnteredBattle) {
            enemy.isVisible = false
        }

        if (isPlayerInAgroRadius(enemy, player, room)) {
            if (!enemy.isPlayerNoticed) {
                enemy.isPlayerNoticed = true
                Messages.show(GameMessages.BOSS_SCARES_YOU, 1500)
                bossEnteredBattle = true
                enemy.isVisible = true
            }
            if (attackOnPlayer(enemy, player)) {
                return
            }
        }

        val direction = if (!bossEnteredBattle) {
            MoveDirection.ENEMY_SKIP_MOVE
        } else {
            ACTIVE_DIRECTIONS.random()
        }

        enemy.enemyMove(enemy, room, direction, player)
    }

    override fun attackOnPlayer(enemy: Enemy, player: Player): Boolean {
        val result = enemy.attackPlayer(player)
        if (result) {
            bossEnteredBattle = true
            enemy.isVisible = true
        }
        return result
    }

    override fun receivingDamage(enemy: Enemy, damage: Int) {
        val healthBefore = enemy.health
        enemy.takeDamage(damage)

        bossEnteredBattle = true
        enemy.isVisible = true

        if (!enemy.isAlive) {
            // todo возможне вывод в сообщения ("☠️ Владыка Тьмы уничтожен!")
        }
    }

    override fun pursuit(enemy: Enemy, player: Player, level: GameLevel): String? {
        // хз
        return null
    }

    override fun dropGenerator(enemyType: Enemy): GameObject? {

        if (!enemyType.isAlive) {
            val type = DarkCrown.DarkCrownType.DARK_CROWN
            return DarkCrown(type).apply {
                setPosition(enemyType.x, enemyType.y)

            }
        }
        return null
    }

    // TODO сделать корону но с механикой портала! сообщение ты готов примерить корону правителя?

    companion object {
        private val AGRO_RADIUS: Array<BooleanArray> = arrayOf(
            booleanArrayOf(false, true, true, true, true, true, true, true, false),
            booleanArrayOf(true, true, true, true, true, true, true, true, true),
            booleanArrayOf(true, true, true, true, true, true, true, true, true),
            booleanArrayOf(true, true, true, true, true, true, true, true, true),
            booleanArrayOf(true, true, true, true, false, true, true, true, true),
            booleanArrayOf(true, true, true, true, true, true, true, true, true),
            booleanArrayOf(true, true, true, true, true, true, true, true, true),
            booleanArrayOf(true, true, true, true, true, true, true, true, true),
            booleanArrayOf(false, true, true, true, true, true, true, true, false)
        )

        private val ACTIVE_DIRECTIONS = arrayOf(
            MoveDirection.ENEMY_MOVE_UP,
            MoveDirection.ENEMY_MOVE_DOWN,
            MoveDirection.ENEMY_MOVE_LEFT,
            MoveDirection.ENEMY_MOVE_RIGHT,
            MoveDirection.ENEMY_MOVE_UP_LEFT,
            MoveDirection.ENEMY_MOVE_UP_RIGHT,
            MoveDirection.ENEMY_MOVE_DOWN_LEFT,
            MoveDirection.ENEMY_MOVE_DOWN_RIGHT,
            MoveDirection.ENEMY_MOVE_RANDOM,
            MoveDirection.BOSS_SCARES
        )
    }
}