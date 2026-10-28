package rougelite.BusinessLogicLayer.logic

import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.inventory.Backpack
import rougelite.BusinessLogicLayer.message.GameMessages
import rougelite.BusinessLogicLayer.objects.Ammo
import rougelite.BusinessLogicLayer.objects.GameObject
import rougelite.BusinessLogicLayer.objects.HealthPotion
import rougelite.BusinessLogicLayer.objects.Reinforce
import rougelite.BusinessLogicLayer.objects.Weapon

class InventoryEngine(private val player: Player) {

    val backpack: Backpack = player.backpack

    val equippedWeapon: Weapon?
        get() = backpack.equippedWeapon

    data class AddResult(
        val success: Boolean,
        val message: String
    )

    // ==================== ДОБАВЛЕНИЕ ПРЕДМЕТОВ ====================

    fun addItem(item: GameObject): AddResult = when (item) {
        is Weapon -> {
            if (backpack.hasWeapon(item.type)) {
                AddResult(false, GameMessages.BACKPACK_HAS_WEAPON)
            } else if (backpack.addWeapon(item)) {
                AddResult(true, GameMessages.BACKPACK_ADD_WEAPON)
            } else {
                AddResult(false, GameMessages.BACKPACK_FULL)
            }
        }

        is Ammo -> {
            if (backpack.addAmmo(item)) {
                AddResult(true, GameMessages.BACKPACK_ADD_AMMO)
            } else {
                AddResult(false, GameMessages.BACKPACK_FULL)
            }
        }

        is HealthPotion -> {
            if (backpack.addHealthPotion(item)) {
                AddResult(true, GameMessages.BACKPACK_ADD_POTION)
            } else {
                AddResult(false, GameMessages.BACKPACK_FULL)
            }
        }

        is Reinforce -> {
            if (backpack.hasReinforce(item.type)) {
                AddResult(false, GameMessages.BACKPACK_HAS_REINFORCE)
            } else if (backpack.addReinforce(item)) {
                applyReinforceBonuses(item)
                AddResult(true, GameMessages.BACKPACK_ADD_REINFORCE)
            } else {
                AddResult(false, GameMessages.BACKPACK_FULL)
            }
        }

        else -> AddResult(false, "Неизвестный предмет")
    }

    private fun applyReinforceBonuses(reinforce: Reinforce) {
        if (reinforce.healthBooster > 0) {
            player.addHealthBonus(reinforce.healthBooster)
        }
        if (reinforce.strengthBooster > 0) {
            player.addStrengthBonus(reinforce.strengthBooster)
        }
        if (reinforce.dexterityBooster > 0) {
            player.addDexterityBonus(reinforce.dexterityBooster)
        }
    }

    // ==================== ИСПОЛЬЗОВАНИЕ ПРЕДМЕТОВ ====================

    fun useWeapon(slot: Int): String? {
        if (slot in 0 until backpack.weaponsCount) {
            backpack.equipWeapon(slot)
            val weapon = backpack.weapons[slot]
            return GameMessages.formatEquipWeapon(weapon.name)
        }
        return null
    }

    fun equipFirstWeapon(): String {
        val weapons = backpack.weapons
        if (weapons.isNotEmpty()) {
            val firstWeapon = weapons[0]
            backpack.equipWeapon(0)
            return GameMessages.formatEquipWeapon(firstWeapon.name)
        }
        return GameMessages.NO_WEAPON
    }

    fun equipUnequipWeapon(): String? =
        if (backpack.equippedWeapon != null) unEquipWeapon() else equipLastWeapon()

    fun equipLastWeapon(): String {
        if (backpack.hasLastWeapon) {
            backpack.equipLastWeapon()
            val weapon = backpack.equippedWeapon ?: return equipFirstWeapon()
            return GameMessages.formatEquipWeapon(weapon.name)
        }
        return equipFirstWeapon()
    }

    fun unEquipWeapon(): String? {
        val current = backpack.equippedWeapon
        if (current != null) {
            backpack.unEquippedWeapon()
            return GameMessages.formatUnequipWeapon(current.name)
        }
        return null
    }

    fun useAmmo(slot: Int): String? {
        if (slot in 0 until backpack.ammoCount) {
            val ammo = backpack.ammo[slot]
            if (ammo.canUse(player)) {
                val result = ammo.useItem(player)
                backpack.removeAmmo(slot)
                return result
            } else {
                val equipped = player.backpack.equippedWeapon
                    ?: return GameMessages.NOT_EQUIP_WEAPON

                if (ammo.isBladedWeapon(equipped)) {
                    return GameMessages.BLADED_WEAPON
                }

                if (!ammo.isReloadableWeapon(equipped, ammo.type)) {
                    return GameMessages.IMPOSSIBLE_USE_AMMO
                }

                if (equipped.currentAmmo >= equipped.maxAmmo) {
                    return GameMessages.FULL_AMMO
                }
            }
        }
        return null
    }

    fun useHealthPotion(slot: Int): String? {
        if (slot in 0 until backpack.healthPotionCount) {
            val potion = backpack.healthPotion[slot]
            if (potion.canUse(player)) {
                val result = potion.useItem(player)
                backpack.removeHealthPotion(slot)
                return result
            }
            return GameMessages.FULL_HEALTH
        }
        return null
    }
}