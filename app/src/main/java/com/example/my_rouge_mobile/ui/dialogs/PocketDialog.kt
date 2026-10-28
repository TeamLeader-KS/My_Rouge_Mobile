package com.example.my_rouge_mobile.ui.dialogs

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.my_rouge_mobile.ui.components.FramedPanel
import com.example.my_rouge_mobile.ui.components.GameButton
import com.example.my_rouge_mobile.ui.theme.GameColors
import rougelite.BusinessLogicLayer.inventory.Pocket

/** Карман для камней (бывший PocketWindow): 8 слотов. */
@Composable
fun PocketDialog(pocket: Pocket, onClose: () -> Unit) {
    val stones = pocket.allStones

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        FramedPanel(
            title = "КАРМАН ДЛЯ КАМНЕЙ",
            modifier = Modifier.fillMaxWidth(0.94f).heightIn(max = 600.dp)
        ) {
            for (slot in 0 until 8) {
                val stone = stones.getOrNull(slot)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .border(1.dp, GameColors.Gold.copy(alpha = 0.4f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "${slot + 1}. ${stone?.position?.russianName ?: "(пусто)"}",
                        color = if (stone != null) GameColors.Gold else GameColors.Gold.copy(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            GameButton("ЗАКРЫТЬ", onClose)
        }
    }
}