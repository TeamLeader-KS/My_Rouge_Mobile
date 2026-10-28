package rougelite.DataAccessLayer.save

class ItemSave {
    var type: ItemType? = null

    var x: Int = 0
    var y: Int = 0

    var varietyName: String? = null
    var subtype: String = ""
    var amount: Int = 0
    var currentAmmo: Int = 0
    var name: String? = null

    enum class ItemType {
        WEAPON,
        AMMO,
        HEALTH_POTION,
        REINFORCE,
        DARK_CROWN
    }
}