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

/** Экран победы (бывший VictoryWindow). */
@Composable
fun VictoryScreen(
    heroName: String,
    onNewGame: () -> Unit,
    onLeaderboard: () -> Unit
) {
    val green = GameColors.GreenBright
    FramedPanel(title = "=== ЭПИЛОГ ===", frameColor = green, titleColor = green) {
        PanelText("Ты победил! Слава Герою $heroName!!!", green)
        PanelText("Темный Владыка пал и Корона Тьмы теперь твоя!", green)
        PanelText("Одень ее и стань нашим новым повелителем, Великий $heroName!", green)
        PanelText("Отныне твое имя навсегда останется на скрижалях истории!!!", green)
        PanelText("⚔ ⚔ ⚔ Владыка $heroName ⚔ ⚔ ⚔", green)

        Spacer(Modifier.height(24.dp))

        GameButton("НОВАЯ ИГРА", onNewGame, Modifier.fillMaxWidth(), color = green)
        Spacer(Modifier.height(12.dp))
        GameButton("ТАБЛИЦА ЛИДЕРОВ", onLeaderboard, Modifier.fillMaxWidth(), color = green)
    }
}