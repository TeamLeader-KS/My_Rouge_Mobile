package rougelite.BusinessLogicLayer.mapGenerator

import rougelite.BusinessLogicLayer.objects.Portal
import rougelite.BusinessLogicLayer.objects.PortalStone

interface DrawRoomsMap {
    val x: Int
    val y: Int
    val height: Int
    val length: Int
    val tiles: Array<IntArray>
    val roomIndex: Int
    val isCenterRoom: Boolean

    var portal: Portal?
    var portalStone: PortalStone?

    fun getTileAt(x: Int, y: Int): Int
}