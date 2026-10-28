package com.example.my_rouge_mobile.viewmodel

import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.logic.GameEngine
import rougelite.BusinessLogicLayer.logic.HandsEngine
import rougelite.BusinessLogicLayer.logic.InventoryEngine
import rougelite.BusinessLogicLayer.logic.MoveLogic
import rougelite.BusinessLogicLayer.logic.PocketEngine
import rougelite.BusinessLogicLayer.logic.WeaponEngine

/** Всё, что в старом GameController было полями: игрок, движки. Живёт, пока идёт забег. */
class GameSession(
    val player: Player,
    val gameEngine: GameEngine,
    val weaponEngine: WeaponEngine
) {
    val inventoryEngine = InventoryEngine(player)
    val pocketEngine = PocketEngine(player)
    val handsEngine = HandsEngine(player)
    val moveLogic = MoveLogic()
}