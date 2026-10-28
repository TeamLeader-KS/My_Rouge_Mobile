package com.example.my_rouge_mobile.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.my_rouge_mobile.ui.components.FramedPanel
import com.example.my_rouge_mobile.ui.components.GameButton
import com.example.my_rouge_mobile.ui.components.PanelText
import com.example.my_rouge_mobile.ui.theme.GameColors

/** Экран поражения (бывший GameOverWindow). */
@Composable
fun GameOverScreen(
    heroName: String,
    onNewGame: () -> Unit,
    onLeaderboard: () -> Unit
) {
    val red = GameColors.Red
    FramedPanel(title = "=== ПОРАЖЕНИЕ ===", frameColor = red, titleColor = red) {
        PanelText("Путешественник $heroName сражался храбро и погиб с честью!", red)
        PanelText("Мы запомним твоё имя, но не надолго.", red)
        PanelText("Если вообще запомним...", red)
        PanelText("✞ ✞ ✞", red)
        PanelText(
            "Нам остается надеяться, что однажды появится настоящий герой, " +
                    "способный закончить начатое...", red
        )

        Spacer(Modifier.height(24.dp))

        GameButton("НОВАЯ ИГРА", onNewGame, Modifier.fillMaxWidth(), color = red)
        Spacer(Modifier.height(12.dp))
        GameButton("ТАБЛИЦА ЛИДЕРОВ", onLeaderboard, Modifier.fillMaxWidth(), color = red)
    }
}