package rougelite.BusinessLogicLayer.mapGenerator

import rougelite.BusinessLogicLayer.objects.Portal
import rougelite.BusinessLogicLayer.objects.PortalStone

class LastRoom(
    override val roomIndex: Int,
    override val isCenterRoom: Boolean
) : DrawRoomsMap {

    override var x: Int = 0
        private set
    override var y: Int = 0
        private set

    override val height: Int = LAST_HEIGHT
    override val length: Int = LAST_LENGTH

    override val tiles: Array<IntArray> = Array(LAST_HEIGHT) { i ->
        IntArray(LAST_LENGTH) { j ->
            if (i == 0 || i == LAST_HEIGHT - 1 || j == 0 || j == LAST_LENGTH - 1) 1 else 0
        }
    }

    override var portal: Portal? = null
    override var portalStone: PortalStone? = null

    fun setPosition(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    override fun getTileAt(x: Int, y: Int): Int {
        val localX = x - this.x
        val localY = y - this.y
        if (localY !in 0 until height || localX !in 0 until length) return -1
        return tiles[localY][localX]
    }

    fun generateNonCenterDoors() {}

    val playerPositionX: Int
        get() = x + (length / 2 - 10)

    val playerPositionY: Int
        get() = y + height / 2

    val bossPositionX: Int
        get() = x + (length / 2 + 10)

    val bossPositionY: Int
        get() = y + height / 2

    companion object {
        private const val LAST_HEIGHT = 11
        private const val LAST_LENGTH = 31
    }
}