package rougelite.DataAccessLayer.save

class SaveGame {
    var dungeonLevel: Int = 0
    var player: PlayerSave? = null
    var inventory: InventorySave? = null
    var pocket: PocketSave? = null
    var portal: PortalSave? = null
    var level: LevelSave? = null
    var hands: HandsSave? = null
}