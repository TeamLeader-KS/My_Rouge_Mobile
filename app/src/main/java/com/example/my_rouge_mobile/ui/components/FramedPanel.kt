package com.example.my_rouge_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.my_rouge_mobile.ui.theme.GameColors

/**
 * Общая «окно с рамкой» — замена копипасты fillBackground/drawFrame/drawTitle
 * из всех старых Window-объектов.
 */
@Composable
fun FramedPanel(
    title: String,
    modifier: Modifier = Modifier.fillMaxSize().padding(12.dp),
    frameColor: Color = GameColors.Gold,
    titleColor: Color = GameColors.GoldBright,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(GameColors.Background)
            .border(2.dp, frameColor)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = titleColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        content()
    }
}

/** Центрированная строка текста с отступом снизу. */
@Composable
fun PanelText(
    text: String,
    color: Color = GameColors.Gold,
    fontSize: TextUnit = 16.sp
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    )
}