package com.example.my_rouge_mobile.ui.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.my_rouge_mobile.ui.components.FramedPanel
import com.example.my_rouge_mobile.ui.components.GameButton

/** Игровое меню (бывший ExitWindow): вместо стрелок и Enter - три кнопки. */
@Composable
fun ExitMenuDialog(
    onResume: () -> Unit,
    onNewGame: () -> Unit,
    onSaveAndExit: () -> Unit,
    onLeaderboard: () -> Unit
) {
    Dialog(
        onDismissRequest = onResume, // кнопка «Назад» = Esc
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        FramedPanel(
            title = "=== МЕНЮ ===",
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            val full = Modifier.fillMaxWidth()
            GameButton("ПРОДОЛЖИТЬ", onResume, full)
            Spacer(Modifier.height(12.dp))
            GameButton("СОХРАНИТЬ И ВЫЙТИ", onSaveAndExit, full)
            Spacer(Modifier.height(12.dp))
            GameButton("ТАБЛИЦА ЛИДЕРОВ", onLeaderboard, full)
            Spacer(Modifier.height(12.dp))
            // деструктивное действие (удаляет сохранение) - отдельным цветом
            GameButton("НОВАЯ ИГРА", onNewGame, full,
                color = com.example.my_rouge_mobile.ui.theme.GameColors.Red)
        }
    }
}