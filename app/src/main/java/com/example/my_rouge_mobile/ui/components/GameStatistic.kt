package com.example.my_rouge_mobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.my_rouge_mobile.ui.theme.GameColors
import com.example.my_rouge_mobile.viewmodel.GameStatisticState

@Composable
fun GameStatistic(gameStatistic: GameStatisticState, modifier: Modifier = Modifier) { // <-- Переименовали gameStatistic в GameStatistic
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Уровень персонажа / Опыт
        StatisticColumn(
            modifier = Modifier.weight(1f),
            topText = "Уровень персонажа: ${gameStatistic.playerLevel}",
            bottomText = "Опыт: ${gameStatistic.experience}"
        )

        // 2. Оружие / Амуниция
        StatisticColumn(
            modifier = Modifier.weight(1f),
            topText = "Оружие: ${gameStatistic.weaponName}",
            bottomText = "Амуниция: ${gameStatistic.ammoText}"
        )

        // 3. Пусто / Здоровье
        StatisticColumn(
            modifier = Modifier.weight(1f),
            topText = "",
            bottomText = "Здоровье: ${gameStatistic.health}/${gameStatistic.maxHealth}"
        )

        // 4. Сила / Ловкость
        StatisticColumn(
            modifier = Modifier.weight(1f),
            topText = "Сила: ${gameStatistic.strength}",
            bottomText = "Ловкость: ${gameStatistic.dexterity}"
        )

        // 5. Уровень подземелья / Сокровища
        StatisticColumn(
            modifier = Modifier.weight(1f),
            topText = "Уровень Подземелья: ${gameStatistic.dungeonLevel}",
            bottomText = "Сокровища: ${gameStatistic.treasure}"
        )
    }
}

@Composable
private fun StatisticColumn(
    modifier: Modifier = Modifier,
    topText: String,
    bottomText: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = topText,
            color = GameColors.Gold,
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = bottomText,
            color = GameColors.Gold,
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}