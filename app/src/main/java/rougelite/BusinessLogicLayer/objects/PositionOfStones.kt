package rougelite.BusinessLogicLayer.objects

enum class PositionOfStones(val roomIndex: Int) {
    NONE(-1),
    UP_LEFT(0),
    UP_MIDDLE(1),
    UP_RIGHT(2),
    MIDDLE_LEFT(3),
    MIDDLE_CENTER(4), // «Корона Нокс» (Corona Nox)
    MIDDLE_RIGHT(5),
    DOWN_LEFT(6),
    DOWN_MIDDLE(7),
    DOWN_RIGHT(8);

    val russianName: String
        get() =
                when (this) {
                    UP_LEFT -> "верхний левый камень портала"
                    UP_MIDDLE -> "верхний центральный камень портала"
                    UP_RIGHT -> "верхний правый камень портала"
                    MIDDLE_LEFT -> "центральный левый камень портала"
                    MIDDLE_CENTER -> "АРТЕФАКТ Корона Ноктис"
                    MIDDLE_RIGHT -> "центральный правый камень портала"
                    DOWN_LEFT -> "нижний левый камень портала"
                    DOWN_MIDDLE -> "нижний центральный камень портала"
                    DOWN_RIGHT -> "нижний правый камень портала"
                    NONE -> "неизвестный предмет"
                }

    companion object {
        @JvmStatic
        fun fromRoomIndex(roomIndex: Int): PositionOfStones {
            return values().firstOrNull { it.roomIndex == roomIndex } ?: NONE
        }
    }
}
