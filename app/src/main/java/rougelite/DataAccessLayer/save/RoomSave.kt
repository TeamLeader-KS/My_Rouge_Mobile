package rougelite.DataAccessLayer.save

class RoomSave {
    var index: Int = 0
    var x: Int = 0
    var y: Int = 0
    var height: Int = 0
    var length: Int = 0
    var tiles: Array<IntArray>? = null
    var isCenterRoom: Boolean = false
    var portalStoneIndex: Int? = null
    var portalX: Int = 0
    var portalY: Int = 0
    var hasPortalStone: Boolean = false
    var stoneX: Int = 0
    var stoneY: Int = 0
}