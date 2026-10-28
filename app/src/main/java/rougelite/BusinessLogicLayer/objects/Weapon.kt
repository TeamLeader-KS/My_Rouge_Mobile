package rougelite.BusinessLogicLayer.objects

import kotlin.math.min
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.interfaces.CanEquip
import rougelite.BusinessLogicLayer.interfaces.ObjectInfo
import rougelite.BusinessLogicLayer.message.GameMessages

class Weapon(val type: WeaponType) : GameObject(), ObjectInfo, CanEquip {

    enum class WeaponType(val russianWeaponName: String, val weaponPower: Int, val maxAmmo: Int) {
        KNIFE("Сумеречный кинжал", 25, 0),
        SWORD("Парные мечи Династии", 50, 0),
        WARHAMMER("Боевой молот войны", 100, 0),
        BOW("Лук Темных Эльфов", 25, 35),
        SHOTGUN("Дробовик", 50, 25),
        MINIGUN("Многоствольный пулемёт \"М134 Миниган\"", 10, 100),
        LASER("Лазерная винтовка \"Люциус\"", 550, 20),
        BLASTER("Ионный бластер", 350, 20),
        WARP_ANNIHILATOR("Варп Аннигилятор", 1000, 1)
    }

    var name: String = type.russianWeaponName
    override val weaponPower: Int = type.weaponPower
    var currentAmmo: Int = type.maxAmmo

    val maxAmmo: Int
        get() = type.maxAmmo

    fun addAmmo(amount: Int) {
        this.currentAmmo = min(this.currentAmmo + amount, type.maxAmmo)
    }

    fun fire() {
        if (currentAmmo > 0) {
            currentAmmo--
        }
    }

    override fun reportObject(): String {
        return GameMessages.PICKUP_WEAPON
    }

    override fun equip(player: Player) {
        // Экипировка через InventoryEngine
    }

    override fun unequip(player: Player) {
        // Снятие экипировки через InventoryEngine
    }
}
