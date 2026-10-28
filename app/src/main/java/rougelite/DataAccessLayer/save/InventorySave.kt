package rougelite.DataAccessLayer.save

class InventorySave {
    var weapons: MutableList<WeaponSave>? = null
    var ammo: MutableList<AmmoSave>? = null
    var potions: MutableList<PotionSave>? = null
    var reinforces: MutableList<ReinforceSave>? = null
    var treasure: Int = 0
    var equippedWeaponType: String? = null
}