package com.example.my_rouge_mobile.viewmodel

import rougelite.DataAccessLayer.save.LeaderboardEntry

enum class AppScreen { Prologue, NameEntry, Playing, GameOver, Victory }

enum class BackpackTab(val title: String) {
    Weapon("ОРУЖИЕ"),
    Ammo("АМУНИЦИЯ"),
    Potion("ЗЕЛЬЯ"),
    Reinforce("ПРЕДМЕТЫ")
}

/** Что открыто поверх экрана. Заменяет флаги inventoryOpen/pocketOpen/exitMenuOpen. */
sealed interface Overlay {
    data object None : Overlay
    data class Backpack(val tab: BackpackTab = BackpackTab.Weapon) : Overlay
    data object Pocket : Overlay
    data object Information : Overlay
    data object Menu : Overlay
    data object Leaderboard : Overlay
}

/** Данные верхней панели (бывший ShowStatistic). */
data class GameStatisticState(
    val playerLevel: Int = 1,
    val experience: Long = 0L,
    val strength: Int = 0,
    val health: Int = 0,
    val maxHealth: Int = 1,
    val dexterity: Int = 0,
    val treasure: Int = 0,
    val dungeonLevel: Int = 1,
    val weaponName: String = "Пустые руки",
    val ammoText: String = "n/a"
)

data class GameUiState(
    val screen: AppScreen = AppScreen.Prologue,
    val overlay: Overlay = Overlay.None,
    val heroName: String = "Unknown Hero",
    val message: String? = null,          // бывший Messages.getCurrent()
    val gameStatistic: GameStatisticState = GameStatisticState(),
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val exitRequested: Boolean = false,   // «Сохранить и выйти» -> Activity.finish()
    val backpackTab: BackpackTab = BackpackTab.Weapon, // <-- Запоминаем активную вкладку
    val tick: Long = 0L                   // +1 после каждого хода, по нему перерисовывается Canvas
)