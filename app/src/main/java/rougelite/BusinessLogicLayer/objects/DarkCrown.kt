package rougelite.BusinessLogicLayer.objects

import rougelite.BusinessLogicLayer.interfaces.ObjectInfo
import rougelite.BusinessLogicLayer.message.GameMessages

class DarkCrown(val type: DarkCrownType) : GameObject(), ObjectInfo {

    enum class DarkCrownType(val russianDarkCrownName: String) {
        DARK_CROWN("Корона Тьмы")
    }

    val name: String = type.russianDarkCrownName

    override fun reportObject(): String {
        return GameMessages.PICKUP_CROWN
    }
}