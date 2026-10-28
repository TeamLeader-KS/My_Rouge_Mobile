package rougelite.BusinessLogicLayer.interfaces

import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.objects.Portal

interface PortalStoneUsable {
    fun useOnPortal(player: Player, portal: Portal): String?
    fun canUseOnPortal(portal: Portal?): Boolean
}