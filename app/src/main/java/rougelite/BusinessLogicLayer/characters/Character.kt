package rougelite.BusinessLogicLayer.characters

import rougelite.BusinessLogicLayer.logic.ExperienceCalculator

abstract class Character(val name: String, health: Int, var strength: Int, var dexterity: Int) {
    var health: Int = health
        set(value) {
            field = value
        }

    var maxHealth: Int = health

    private val levelUpHP: Int = 25
    private val levelUpStr: Int = 5
    private val levelUpDext: Int = 5

    var experience: Long = 0L
    var currentPlayerLevel: Int = 1
    var type: String? = null
    var hasWeapon: Boolean = false

    var userName: String? = null

    var x: Int = 0
        private set
    var y: Int = 0
        private set

    val isAlive: Boolean
        get() = health > 0

    fun setPosition(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun levelUpHealth() {
        maxHealth += levelUpHP
        health = maxHealth
    }

    fun levelUpStrength() {
        strength += levelUpStr
    }

    fun levelUpDexterity() {
        dexterity += levelUpDext
    }

    fun addNewLevelForPlayer(exp: Long) {
        experience += exp
        val newLevel = ExperienceCalculator.getLevelByExp(experience)

        if (newLevel > currentPlayerLevel) {
            val levelUps = newLevel - currentPlayerLevel
            repeat(levelUps) {
                levelUpHealth()
                levelUpStrength()
                levelUpDexterity()
            }
            currentPlayerLevel = newLevel
        }
    }

    fun addHealthBonus(amount: Int) {
        maxHealth += amount
        health += amount
    }

    fun addStrengthBonus(amount: Int) {
        strength += amount
    }

    fun addDexterityBonus(amount: Int) {
        dexterity += amount
    }

    open fun takeDamage(damage: Int) {
        health = (health - damage).coerceAtLeast(0)

        if (this is Enemy) {
            isAggression = true
        }
    }

    fun takeDamageAndCheckDeath(damage: Int): Boolean {
        val healthBefore = health
        health -= damage
        return healthBefore > 0 && health <= 0
    }

}
