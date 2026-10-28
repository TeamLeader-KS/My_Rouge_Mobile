package rougelite.BusinessLogicLayer.logic

import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.inventory.HiddenBackpack
import rougelite.BusinessLogicLayer.message.GameMessages
import rougelite.BusinessLogicLayer.objects.Hands

class HandsEngine(
    private val player: Player
) {
    val equippedHands: Hands?
        get() = player.equippedHands

    fun switchHands(): String? = when (player.switchHands()) {
        HiddenBackpack.HandsChange.EQUIP_FISTS -> GameMessages.EQUIP_FISTS
        HiddenBackpack.HandsChange.EQUIP_NAILS -> GameMessages.EQUIP_NAILS
        HiddenBackpack.HandsChange.UNEQUIP_FISTS -> GameMessages.UNEQUIP_FISTS
        HiddenBackpack.HandsChange.UNEQUIP_NAILS -> GameMessages.UNEQUIP_NAILS
        HiddenBackpack.HandsChange.NONE -> null
    }
}