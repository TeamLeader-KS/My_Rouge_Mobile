package rougelite.BusinessLogicLayer.objects

import java.util.Random
import kotlin.math.max
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.interfaces.ObjectInfo
import rougelite.BusinessLogicLayer.interfaces.PortalStoneUsable
import rougelite.BusinessLogicLayer.message.GameMessages

class PortalStone(val sourceRoomIndex: Int, val stoneX: Int, val stoneY: Int) :
        GameObject(), ObjectInfo, PortalStoneUsable {

    val position: PositionOfStones = PositionOfStones.fromRoomIndex(sourceRoomIndex)

    // Конструктор для создания нового камня (генерирует случайные координаты внутри комнаты)
    constructor(
            sourceRoomIndex: Int,
            roomX: Int,
            roomY: Int,
            roomLength: Int,
            roomHeight: Int
    ) : this(
            sourceRoomIndex = sourceRoomIndex,
            stoneX = roomX + 1 + Random().nextInt(max(1, roomLength - 3)),
            stoneY = roomY + 1 + Random().nextInt(max(1, roomHeight - 3))
    )

    // Конструктор для камня в кармане (координаты не нужны)
    constructor(
            sourceRoomIndex: Int
    ) : this(sourceRoomIndex = sourceRoomIndex, stoneX = -1, stoneY = -1)

    override fun reportObject(): String {
        return GameMessages.PICKUP_PORTAL_STONE
    }

    val insertStoneMessage: String
        get() = "${position.russianName} установлен!"

    override fun useOnPortal(player: Player, portal: Portal): String? {
        if (!canUseOnPortal(portal)) {
            return null
        }
        portal.insertStone(sourceRoomIndex)
        player.pocket.removeStone()
        return insertStoneMessage
    }

    override fun canUseOnPortal(portal: Portal?): Boolean {
        return portal != null && !portal.hasStone(sourceRoomIndex)
    }
}
