package rougelite.DataAccessLayer.save

class PlayerSave {
    var name: String = ""
    var health: Int = 0
    var maxHealth: Int = 0
    var strength: Int = 0
    var dexterity: Int = 0
    var experience: Long = 0L
    var level: Int = 0
    var position: PositionSave? = null
    var facing: String? = null
}