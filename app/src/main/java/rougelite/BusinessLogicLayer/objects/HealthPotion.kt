package rougelite.BusinessLogicLayer.objects

import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.interfaces.ConsumableItem
import rougelite.BusinessLogicLayer.interfaces.ObjectInfo
import rougelite.BusinessLogicLayer.message.GameMessages
import kotlin.math.min

class HealthPotion(val type: HealthPotionType) : GameObject(), ObjectInfo, ConsumableItem {

    enum class HealthPotionType(
        val russianHealthPotionName: String,
        val healthPower: Int
    ) {
        LOW_HEALTH("Малое зелье здоровья", 50),
        AVERAGE_HEALTH("Среднее зелье здоровья", 100),
        BIG_HEALTH("Большое зелье здоровья", 200)
    }

    val name: String = type.russianHealthPotionName
    val healthPower: Int = type.healthPower

    override fun reportObject(): String {
        return GameMessages.PICKUP_POTION
    }

    override fun useItem(player: Player): String {
        return if (player.health < player.maxHealth) {
            val newHealth = min(player.health + healthPower, player.maxHealth)
            player.health = newHealth
            GameMessages.formatUseHealthPotion(name)
        } else {
            GameMessages.FULL_HEALTH
        }
    }

    override fun canUse(player: Player): Boolean {
        return player.health < player.maxHealth
    }
}