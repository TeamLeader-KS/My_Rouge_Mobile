package rougelite.BusinessLogicLayer.inventory

import rougelite.BusinessLogicLayer.objects.Hands

class HiddenBackpack {

    private val handsList: List<Hands> = listOf(
        Hands(Hands.HandsType.FISTS),
        Hands(Hands.HandsType.NAILS)
    )

    var equippedHands: Hands? = null

    private var state: Int = 0
    private var nextHand: Int = 0

    val allHands: List<Hands>
        get() = handsList.toList()

    enum class HandsChange {
        EQUIP_FISTS,
        EQUIP_NAILS,
        UNEQUIP_FISTS,
        UNEQUIP_NAILS,
        NONE
    }

    fun switchHands(): HandsChange {
        if (handsList.isEmpty()) return HandsChange.NONE

        val currentEquipped = equippedHands
        return if (currentEquipped == null) {
            val selected = handsList[nextHand]
            equippedHands = selected
            val result = if (nextHand == 0) HandsChange.EQUIP_FISTS else HandsChange.EQUIP_NAILS
            nextHand = (nextHand + 1) % MAX_ITEMS
            result
        } else {
            val result = if (currentEquipped.type == Hands.HandsType.FISTS) {
                HandsChange.UNEQUIP_FISTS
            } else {
                HandsChange.UNEQUIP_NAILS
            }
            equippedHands = null
            result
        }
    }

    companion object {
        private const val MAX_ITEMS = 2
    }
}