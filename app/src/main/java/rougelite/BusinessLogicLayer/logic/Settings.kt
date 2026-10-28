package rougelite.BusinessLogicLayer.logic

object Settings {

    // ===== ВЫБОР УРОВНЯ =====
    @JvmField
    var gameLevel: Int = 1

    // ===== МНОЖИТЕЛИ ВРАГОВ =====
    const val ENEMY_HEALTH_MULTIPLIER: Int = 20
    const val ENEMY_STRENGTH_MULTIPLIER: Int = 1
    const val ENEMY_DEXTERITY_MULTIPLIER: Int = 5

    // Настройка количества оружия
    var cauntWeapon: Int = 1
    var cauntHealthPotion: Int = 3
    var cauntAmmo: Int = 2
    var cauntReinforce: Int = 2

    // Стартовые характеристики игрока

    var startingHealth: Int = 100

    var startingStrength: Int = 0

    var startingDexterity: Int = 0

    // Стартовые характеристики рукопашного урона

    var startingFists: Int = 10

    var startingNails: Int = 15

}