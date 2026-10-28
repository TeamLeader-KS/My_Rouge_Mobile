package rougelite.DataAccessLayer.save

/**
 * Бывший Leaderboard.LeaderboardEntry из PresentationLayer.
 * Перенесён в слой данных, т.к. старого presentation-слоя (Lanterna) больше нет.
 */
data class LeaderboardEntry(
    val playerName: String,
    val level: Int,
    val treasure: Int
)