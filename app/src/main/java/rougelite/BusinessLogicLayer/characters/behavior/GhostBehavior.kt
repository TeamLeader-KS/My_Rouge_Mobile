package rougelite.BusinessLogicLayer.characters.behavior

import rougelite.BusinessLogicLayer.characters.Enemy
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior
import rougelite.BusinessLogicLayer.mapGenerator.DrawRoomsMap
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel
import rougelite.BusinessLogicLayer.message.GameMessages
import rougelite.BusinessLogicLayer.objects.Ammo
import rougelite.BusinessLogicLayer.objects.GameObject
import rougelite.BusinessLogicLayer.objects.Weapon
import rougelite.BusinessLogicLayer.message.Messages
import kotlin.random.Random

class GhostBehavior : EnemyBehavior {

    private var currentLevel: GameLevel? = null
    private var ghostEnteredBattle: Boolean = false

    override fun getAgroRadius(): Array<BooleanArray> = AGRO_RADIUS

    override fun enemyMovePattern(
        enemy: Enemy,
        room: DrawRoomsMap,
        player: Player,
        level: GameLevel
    ) {
        this.currentLevel = level

        if (!ghostEnteredBattle) {
            val chanceVisible = Random.nextInt(100)
            enemy.isVisible = chanceVisible >= 70
        }

        if (isPlayerInAgroRadius(enemy, player, room)) {
            if (!enemy.isPlayerNoticed) {
                enemy.isPlayerNoticed = true
                Messages.show(GameMessages.ENEMY_NOTICED_YOU, 1500)
                ghostEnteredBattle = true
                enemy.isVisible = true
            }

            if (attackOnPlayer(enemy, player)) {
                return
            }
        } else {
            enemy.isPlayerNoticed = false
        }

        val direction = MoveDirection.ENEMY_MOVE_RANDOM
        enemy.enemyMove(enemy, room, direction, player)
    }

    override fun pursuit(enemy: Enemy, player: Player, level: GameLevel): String? {
        // Призрак не преследует, а телепортируется
        return null
    }

    override fun attackOnPlayer(enemy: Enemy, player: Player): Boolean {
        val result = enemy.attackPlayer(player)
        if (result) {
            ghostEnteredBattle = true
            enemy.isVisible = true
        }
        return result
    }

    override fun receivingDamage(enemy: Enemy, damage: Int) {
        val healthBefore = enemy.health
        enemy.takeDamage(damage)


        ghostEnteredBattle = true
        enemy.isVisible = true

        if (!enemy.isAlive) {
            // todo ("☠️ Призрак уничтожен!")
        }
    }

    override fun dropGenerator(enemyType: Enemy): GameObject? {
        if (enemyType.isAlive) return null

        val chance = Random.nextInt(100)

        return when {
            chance < 35 && Random.nextBoolean() -> {
                val type = WEAPON_TYPES.random()
                Weapon(type).apply {
                    setPosition(enemyType.x, enemyType.y)
                }
            }
            chance in 36..69 && Random.nextBoolean() -> {
                val type = AMMO_TYPES.random()
                Ammo(type).apply {
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

        private val WEAPON_TYPES = arrayOf(
            Weapon.WeaponType.WARP_ANNIHILATOR
        )

        private val AMMO_TYPES = arrayOf(
            Ammo.AmmoType.WARP_ANNIHILATOR_AMMUNITION
        )
    }
}