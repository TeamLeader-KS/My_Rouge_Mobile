package rougelite.BusinessLogicLayer.objects

import rougelite.BusinessLogicLayer.logic.Settings
class Hands(val type: HandsType) {

    enum class HandsType(
        val russianHandsName: String,
        val handsPower: Int,
        val maxAmmo: Int
    ) {
        FISTS("Кулаки", Settings.startingFists, 0),
        NAILS("Зубы и Ногти", Settings.startingNails, 0);

        // Сохранено для обратной совместимости с Java-кодом
        fun getRussianWeaponName(): String = russianHandsName
    }

    val name: String = type.russianHandsName
    val handsPower: Int = type.handsPower
    val currentAmmo: Int = type.maxAmmo
}