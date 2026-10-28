package rougelite.BusinessLogicLayer.objects

import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.interfaces.ConsumableItem
import rougelite.BusinessLogicLayer.interfaces.ObjectInfo
import rougelite.BusinessLogicLayer.message.GameMessages

class Ammo(val type: AmmoType) : GameObject(), ObjectInfo, ConsumableItem {

    enum class AmmoType(
        val russianAmmoName: String,
        val amountAmmo: Int
    ) {
        BOW_AMMUNITION("Колчан со стрелами", 35),
        SHOTGUN_AMMUNITION("Патроны для дробовика", 25),
        MINIGUN_AMMUNITION("Пулеметная лента", 100),
        LASER_AMMUNITION("Лазерная энергоячейка", 20),
        BLASTER_AMMUNITION("Бластерная энергоячейка", 20),
        WARP_ANNIHILATOR_AMMUNITION("Энергия Варпа", 1)
    }

    val name: String = type.russianAmmoName
    var amountAmmo: Int = type.amountAmmo

    fun setAmount(amount: Int) {
        this.amountAmmo = amount
    }

    override fun reportObject(): String {
        return GameMessages.PICKUP_AMMO
    }

    override fun useItem(player: Player): String {
        val equipped = player.backpack.equippedWeapon
            ?: return GameMessages.NOT_EQUIP_WEAPON

        if (isBladedWeapon(equipped)) {
            return GameMessages.BLADED_WEAPON
        }

        if (!isReloadableWeapon(equipped, type)) {
            return GameMessages.IMPOSSIBLE_USE_AMMO
        }

        if (equipped.currentAmmo >= equipped.maxAmmo) {
            return GameMessages.FULL_AMMO
        }

        equipped.addAmmo(amountAmmo)
        return String.format(GameMessages.USE_AMMO, equipped.name)
    }

    override fun canUse(player: Player): Boolean {
        val equipped = player.backpack.equippedWeapon ?: return false

        if (isBladedWeapon(equipped)) {
            return false
        }

        if (!isReloadableWeapon(equipped, type)) {
            return false
        }

        return equipped.currentAmmo < equipped.maxAmmo
    }

    fun isBladedWeapon(weapon: Weapon): Boolean {
        val weaponType = weapon.type
        return weaponType == Weapon.WeaponType.KNIFE ||
                weaponType == Weapon.WeaponType.SWORD ||
                weaponType == Weapon.WeaponType.WARHAMMER
    }

    fun isReloadableWeapon(weapon: Weapon, ammoType: AmmoType): Boolean {
        return when (weapon.type) {
            Weapon.WeaponType.BOW -> ammoType == AmmoType.BOW_AMMUNITION
            Weapon.WeaponType.SHOTGUN -> ammoType == AmmoType.SHOTGUN_AMMUNITION
            Weapon.WeaponType.MINIGUN -> ammoType == AmmoType.MINIGUN_AMMUNITION
            Weapon.WeaponType.LASER -> ammoType == AmmoType.LASER_AMMUNITION
            Weapon.WeaponType.BLASTER -> ammoType == AmmoType.BLASTER_AMMUNITION
            Weapon.WeaponType.WARP_ANNIHILATOR -> ammoType == AmmoType.WARP_ANNIHILATOR_AMMUNITION
            else -> false
        }
    }
}