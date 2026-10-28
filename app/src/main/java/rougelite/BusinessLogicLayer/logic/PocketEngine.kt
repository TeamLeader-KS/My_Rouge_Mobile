package rougelite.BusinessLogicLayer.logic

import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.inventory.Pocket
import rougelite.BusinessLogicLayer.message.GameMessages
import rougelite.BusinessLogicLayer.objects.Portal
import rougelite.BusinessLogicLayer.objects.PortalStone

class PocketEngine(private val player: Player) {

    val pocket: Pocket = player.pocket

    fun addStone(stone: PortalStone?): String? {
        if (stone == null) return null
        pocket.addStone(stone)
        return stone.reportObject()
    }

    fun useStoneOnPortal(portal: Portal?): String? {
        if (portal == null) return null

        val stone = pocket.stone ?: return null

        if (!stone.canUseOnPortal(portal)) {
            return null
        }

        val message = stone.insertStoneMessage
        stone.useOnPortal(player, portal)

        if (portal.isFullyActivated()) {
            portal.portalOpen(true)
            return GameMessages.ACTIVATED_PORTAL
        }

        return message
    }

    fun clearPocket() {
        pocket.clearPocket()
    }

    fun canEnterPortal(
            portal: Portal?,
            currentRoomIndex: Int,
            playerX: Int,
            playerY: Int
    ): Boolean {
        if (portal == null || !portal.isOpen) return false
        return currentRoomIndex == 4 && playerX == portal.x && playerY == portal.y
    }
}
