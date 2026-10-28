package com.example.my_rouge_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Экран пролога — показывает сюжетный текст перед началом игры.
 *
 * @param onContinue вызывается, когда игрок нажимает «Продолжить».
 *                   Здесь позже будет переход на экран ввода имени или сразу в игру.
 */
@Composable
fun PrologueScreen(onContinue: () -> Unit) {
    // Тёмный фон и жёлтый текст — как в старой терминальной версии.
    val backgroundColor = Color.Black
    val frameColor = Color(0xFFFFEB3B)      // жёлтый
    val titleColor = Color(0xFFFFFF00)      // ярко-жёлтый
    val textColor = Color(0xFFFFEB3B)       // жёлтый

    // Строки сюжета из старого PrologueWindow.
    // Пустая строка между абзацами заменена на Spacer ниже,
    // но её тоже можно оставить как Text(" ").
    val storyLines = listOf(
        "В этом путешествии по мрачным катакомбам Лабиринта Ужаса, тебе предстоит преодолеть множество испытаний.",
        "Магический портал поможет тебе проникнуть глубже, но войти в него будет не просто, очень не просто.",
        "Каждый камень необходимый для активации портала, охраняется легионом стражей подземелья.",
        "Бойся, путник! Ведь ты будешь один, а твоими врагами станут самые опасные твари потустороннего мира!",
        "С каждым новым шагом, враги будут становиться сильнее. Опаснее. Кровожаднее. Сможешь выжить?",
        "Следи за своей жизнью, ибо каждый здесь захочет ее отнять. И помни! Она у тебя одна!",
        "Удачи желать не буду! Она тебе не поможет..."
    )

    // Box — контейнер на весь экран. Внутри него рамка и колонка с контентом.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(12.dp)
            .border(2.dp, frameColor)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // verticalScroll — чтобы текст можно было прокрутить,
                // если он не помещается на экран телефона.
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Заголовок
            Text(
                text = "=== ПРОЛОГ ===",
                color = titleColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Сюжетные строки. Каждая — Text по центру, между ними отступ.
            storyLines.forEach { line ->
                Text(
                    text = line,
                    color = textColor,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка «Продолжить». Внизу, потому что Column растянут на весь экран,
            // но чтобы прижать её к низу, можно обернуть в Box с weight — см. ниже.
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = frameColor,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "ПРОДОЛЖИТЬ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}