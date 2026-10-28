package rougelite.DataAccessLayer.save

class LevelSave {
    var dungeonLevel: Int = 0
    var rooms: MutableList<RoomSave>? = null
    var corridors: MutableList<CorridorSave>? = null
    var itemsOnFloor: MutableList<ItemSave>? = null
    var enemies: MutableList<EnemySave>? = null
    var explorationMap: Array<IntArray>? = null
    var levelMap: Array<IntArray>? = null
}