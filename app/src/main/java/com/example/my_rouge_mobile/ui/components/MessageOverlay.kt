package com.example.my_rouge_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.my_rouge_mobile.ui.theme.GameColors

/** Игровое сообщение сверху поля (бывшие Messages + MessageOutput.showGameMessage). */
@Composable
fun MessageOverlay(message: String?, modifier: Modifier = Modifier) {
    if (message == null) return
    Text(
        text = message,
        color = GameColors.GoldBright,
        fontSize = 12.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
            .background(Color(0xCC000000))
            .padding(horizontal = 2.dp, vertical = 2.dp)
    )
}