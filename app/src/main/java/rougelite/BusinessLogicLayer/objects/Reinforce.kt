package rougelite.BusinessLogicLayer.objects

import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.interfaces.ObjectInfo
import rougelite.BusinessLogicLayer.message.GameMessages

class Reinforce(val type: ReinforceType) : GameObject(), ObjectInfo {

    enum class ReinforceType(
        val russianReinforceName: String,
        val healthBooster: Int,
        val strengthBooster: Int,
        val dexterityBooster: Int
    ) {
        HELMET("Призрачный капюшон разбойника", 100, 0, 0),
        ARMOR("Доспех теневого стража", 200, 0, 0),
        CLOAK("Накидка теней", 0, 0, 15),
        SHIELD("Щит ночного покрова", 150, 0, 0),
        GLOVES("Перчатки тёмной силы", 0, 25, 0),
        BELT("Пояс усиления духа", 0, 25, 0),
        BOOTS("Бесшумные сапоги охотника", 0, 0, 15),
        AMULET("Амулет неуловимого призрака", 0, 0, 15),
        RING("Усиливающее кольцо Тьмы", 0, 25, 0)
    }

    val name: String = type.russianReinforceName
    val healthBooster: Int = type.healthBooster
    val strengthBooster: Int = type.strengthBooster
    val dexterityBooster: Int = type.dexterityBooster

    override fun reportObject(): String {
        return GameMessages.PICKUP_REINFORCE
    }

    fun applyBonuses(player: Player) {
        if (healthBooster > 0) {
            player.addHealthBonus(healthBooster)
        }
        if (strengthBooster > 0) {
            player.addStrengthBonus(strengthBooster)
        }
        if (dexterityBooster > 0) {
            player.addDexterityBonus(dexterityBooster)
        }
    }
}