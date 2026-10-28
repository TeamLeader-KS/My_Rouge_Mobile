package com.example.my_rouge_mobile.ui.dialogs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.my_rouge_mobile.ui.components.FramedPanel
import com.example.my_rouge_mobile.ui.components.GameButton
import com.example.my_rouge_mobile.ui.theme.GameColors
import rougelite.DataAccessLayer.save.LeaderboardEntry

/** Таблица лидеров (бывший Leaderboard.draw): топ-10 по сокровищам. */
@Composable
fun LeaderboardDialog(entries: List<LeaderboardEntry>, onClose: () -> Unit) {
    val top = entries.sortedByDescending { it.treasure }.take(10)

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        FramedPanel(
            title = "ТАБЛИЦА ЛИДЕРОВ",
            modifier = Modifier.fillMaxWidth(0.94f).heightIn(max = 620.dp)
        ) {
            Cells("№", "Игрок", "Ур.", "Сокровища", GameColors.GoldBright, bold = true)
            Spacer(Modifier.height(8.dp))

            for (i in 0 until 10) {
                val e = top.getOrNull(i)
                if (e != null) {
                    Cells((i + 1).toString(), e.playerName, e.level.toString(), e.treasure.toString(), Color.White)
                } else {
                    Cells("-", "-", "-", "-", Color.Gray)
                }
            }

            Spacer(Modifier.height(12.dp))
            GameButton("ЗАКРЫТЬ", onClose)
        }
    }
}

@Composable
private fun Cells(a: String, b: String, c: String, d: String, color: Color, bold: Boolean = false) {
    val weight = if (bold) FontWeight.Bold else FontWeight.Normal
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(a, color = color, fontWeight = weight, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.12f))
        Text(b, color = color, fontWeight = weight, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(0.4f))
        Text(c, color = color, fontWeight = weight, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.weight(0.15f))
        Text(d, color = color, fontWeight = weight, fontSize = 14.sp, textAlign = TextAlign.End, modifier = Modifier.weight(0.33f))
    }
}