package rougelite.BusinessLogicLayer.interfaces

import rougelite.BusinessLogicLayer.characters.Player

interface CanEquip {
    val weaponPower: Int

    fun equip(player: Player)
    fun unequip(player: Player)
}