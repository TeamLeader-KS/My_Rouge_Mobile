package rougelite.BusinessLogicLayer.inventory

import rougelite.BusinessLogicLayer.objects.PortalStone

class Pocket {

    private val stones: MutableList<PortalStone> = mutableListOf()

    val stone: PortalStone?
        get() = stones.firstOrNull()

    val hasStone: Boolean
        get() = stones.isNotEmpty()

    val stoneCount: Int
        get() = stones.size

    val allStones: List<PortalStone>
        get() = stones.toList()

    fun addStone(stone: PortalStone) {
        stones.add(stone)
    }

    fun removeStone() {
        if (stones.isNotEmpty()) {
            stones.removeAt(0)
        }
    }

    fun clearPocket() {
        stones.clear()
    }

    companion object {
        private const val MAX_ITEMS = 8
    }
}