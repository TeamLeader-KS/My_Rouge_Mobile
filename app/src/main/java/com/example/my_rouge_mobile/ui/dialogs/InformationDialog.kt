package com.example.my_rouge_mobile.ui.dialogs

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.my_rouge_mobile.ui.components.FramedPanel
import com.example.my_rouge_mobile.ui.components.GameButton
import com.example.my_rouge_mobile.ui.components.PanelText

/** Окно «Управление» (бывший Information). Тексты под сенсорные кнопки. */
@Composable
fun InformationDialog(onClose: () -> Unit) {
    val controls = listOf(
        "Крестовина слева - движение.",
        "«Огонь» - удар или выстрел.",
        "«Оружие» - достать/убрать оружие.",
        "«Руки» - рукопашный бой.",
        "«Портал» - войти в активированный портал.",
        "«Камень» - использовать камень портала.",
        "«Рюкзак» - открыть рюкзак; коснись слота, чтобы применить предмет.",
        "«Карман» - камни для портала.",
        "«Меню» - сохранение, новая игра, таблица лидеров."
    )

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        FramedPanel(
            title = "=== УПРАВЛЕНИЕ ===",
            modifier = Modifier.fillMaxWidth(0.92f).heightIn(max = 560.dp)
        ) {
            controls.forEach { PanelText(it) }
            Spacer(Modifier.height(12.dp))
            GameButton("ЗАКРЫТЬ", onClose)
        }
    }
}