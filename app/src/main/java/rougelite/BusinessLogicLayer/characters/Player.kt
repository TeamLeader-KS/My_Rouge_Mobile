package rougelite.BusinessLogicLayer.characters

import rougelite.BusinessLogicLayer.inventory.Backpack
import rougelite.BusinessLogicLayer.inventory.HiddenBackpack
import rougelite.BusinessLogicLayer.inventory.Pocket
import rougelite.BusinessLogicLayer.logic.MoveLogic.Direction
import rougelite.BusinessLogicLayer.objects.Hands
import rougelite.BusinessLogicLayer.objects.Weapon

class Player(name: String, health: Int, strength: Int, dexterity: Int) :
    Character(name, health, strength, dexterity) {

    var roomX: Int = 0
        private set
    var roomY: Int = 0
        private set

    var levelX: Int = 0
        private set
    var levelY: Int = 0
        private set

    val backpack: Backpack = Backpack()
    var pocket: Pocket = Pocket()
    val hiddenBackpack: HiddenBackpack = HiddenBackpack()

    var facing: Direction = Direction.RIGHT
    var currentRoomIndex: Int = 0

    val equippedHands: Hands?
        get() = hiddenBackpack.equippedHands

    val equippedWeapon: Weapon?
        get() = backpack.equippedWeapon

    fun setLevelPosition(x: Int, y: Int) {
        this.levelX = x
        this.levelY = y
    }

    fun setRoomPosition(x: Int, y: Int) {
        this.roomX = x
        this.roomY = y
    }

    fun switchHands(): HiddenBackpack.HandsChange = hiddenBackpack.switchHands()
}
