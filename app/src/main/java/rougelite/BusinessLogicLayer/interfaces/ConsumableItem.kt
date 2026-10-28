package rougelite.BusinessLogicLayer.interfaces

import rougelite.BusinessLogicLayer.characters.Player

// Расходный материал
interface ConsumableItem {
    fun useItem(player: Player): String
    fun canUse(player: Player): Boolean
}