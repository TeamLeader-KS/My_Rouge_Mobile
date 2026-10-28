package com.example.my_rouge_mobile.input

import com.example.my_rouge_mobile.viewmodel.BackpackTab

enum class MoveDir { UP, DOWN, LEFT, RIGHT }

/**
 * Действия игрока. Замена PlayerCommands: кнопки ControlPad и диалоги
 * отправляют их во ViewModel, который превращает их в вызовы движков.
 */
sealed interface GameAction {
    // игровой экран
    data class Move(val dir: MoveDir) : GameAction
    data object Fire : GameAction
    data object ToggleWeapon : GameAction
    data object ToggleHands : GameAction
    data object UsePortal : GameAction
    data object UseStone : GameAction

    // окна
    data class OpenBackpack(val tab: BackpackTab = BackpackTab.Weapon) : GameAction
    data object OpenPocket : GameAction
    data object OpenInformation : GameAction
    data object OpenMenu : GameAction
    data object OpenLeaderboard : GameAction
    data object CloseOverlay : GameAction

    // рюкзак: вместо клавиш 1..9 и стрелок
    data class SelectBackpackTab(val tab: BackpackTab) : GameAction
    data class UseItem(val slot: Int) : GameAction // slot: 0..8

    // меню
    data object NewGame : GameAction
    data object SaveAndExit : GameAction

    // навигация
    data object ContinuePrologue : GameAction
    data class ConfirmName(val name: String) : GameAction
}