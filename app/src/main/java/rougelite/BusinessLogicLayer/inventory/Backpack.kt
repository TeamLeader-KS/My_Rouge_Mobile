package rougelite.BusinessLogicLayer.inventory

import rougelite.BusinessLogicLayer.objects.Ammo
import rougelite.BusinessLogicLayer.objects.HealthPotion
import rougelite.BusinessLogicLayer.objects.Reinforce
import rougelite.BusinessLogicLayer.objects.Weapon

class Backpack {

    private val _weapons: MutableList<Weapon> = mutableListOf()
    private val _ammo: MutableList<Ammo> = mutableListOf()
    private val _healthPotions: MutableList<HealthPotion> = mutableListOf()
    private val _reinforces: MutableList<Reinforce> = mutableListOf()

    val weapons: List<Weapon>
        get() = _weapons.toList()

    val ammo: List<Ammo>
        get() = _ammo.toList()

    val healthPotion: List<HealthPotion>
        get() = _healthPotions.toList()

    val reinforces: List<Reinforce>
        get() = _reinforces.toList()

    val weaponsCount: Int
        get() = _weapons.size

    val ammoCount: Int
        get() = _ammo.size

    val healthPotionCount: Int
        get() = _healthPotions.size

    val reinforcesCount: Int
        get() = _reinforces.size

    var treasure: Int = 0
        private set

    var equippedWeapon: Weapon? = null
        private set

    var lastEquippedWeapon: Weapon? = null
        private set

    val hasLastWeapon: Boolean
        get() = lastEquippedWeapon != null

    fun hasWeapon(type: Weapon.WeaponType): Boolean =
        _weapons.any { it.type == type }

    fun addWeapon(weapon: Weapon): Boolean {
        if (_weapons.size < MAX_ITEMS) {
            _weapons.add(weapon)
            return true
        }
        return false
    }

    fun equipWeapon(index: Int) {
        if (index in _weapons.indices) {
            equippedWeapon = _weapons[index]
        }
    }

    fun unEquipWeapon() {
        equippedWeapon = null
    }

    fun unEquippedWeapon() {
        equippedWeapon?.let {
            lastEquippedWeapon = it
        }
        equippedWeapon = null
    }

    fun equipLastWeapon() {
        lastEquippedWeapon?.let {
            equippedWeapon = it
        }
    }

    fun getWeaponIndex(weapon: Weapon): Int = _weapons.indexOf(weapon)

    fun addAmmo(ammoItem: Ammo): Boolean {
        if (_ammo.size < MAX_ITEMS) {
            _ammo.add(ammoItem)
            return true
        }
        return false
    }

    fun removeAmmo(index: Int): Boolean {
        if (index in _ammo.indices) {
            _ammo.removeAt(index)
            return true
        }
        return false
    }

    // ==================== ЗЕЛЬЯ ====================
    fun addHealthPotion(potion: HealthPotion): Boolean {
        if (_healthPotions.size < MAX_ITEMS) {
            _healthPotions.add(potion)
            return true
        }
        return false
    }

    fun removeHealthPotion(index: Int): Boolean {
        if (index in _healthPotions.indices) {
            _healthPotions.removeAt(index)
            return true
        }
        return false
    }

    fun hasReinforce(type: Reinforce.ReinforceType): Boolean =
        _reinforces.any { it.type == type }

    fun addReinforce(reinforce: Reinforce): Boolean {
        if (_reinforces.size < MAX_ITEMS) {
            _reinforces.add(reinforce)
            return true
        }
        return false
    }

    fun getReinforce(index: Int): Reinforce? =
        _reinforces.getOrNull(index)

    fun addTreasure(amount: Int) {
        treasure += amount
    }

    fun clear() {
        _weapons.clear()
        _ammo.clear()
        _healthPotions.clear()
        _reinforces.clear()
    }

    companion object {
        private const val MAX_ITEMS = 9
    }
}