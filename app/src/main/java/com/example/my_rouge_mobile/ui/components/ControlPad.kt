package com.example.my_rouge_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.my_rouge_mobile.input.GameAction
import com.example.my_rouge_mobile.input.MoveDir
import com.example.my_rouge_mobile.ui.theme.GameColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val PadBg = Color(0xFF1A1A1A)

/**
 * Базовая кнопка. Сделана public, чтобы использовать её для меню в GameScreen.kt
 */
@Composable
fun PadButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.size(56.dp),
    color: Color = GameColors.Gold,
    fontSize: Int = 12,
    repeat: Boolean = false,
    bg: Color = PadBg
) {
    val current by rememberUpdatedState(onClick)
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .background(bg)
            .border(2.dp, color)
            .pointerInput(repeat) {
                detectTapGestures(onPress = {
                    current()
                    val job = if (repeat) scope.launch {
                        delay(350)
                        while (true) {
                            current()
                            delay(130)
                        }
                    } else null
                    tryAwaitRelease()
                    job?.cancel()
                })
            },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = color, fontSize = fontSize.sp, fontWeight = FontWeight.Bold)
    }
}

// 1. ТОЛЬКО СТРЕЛКИ (D-pad)
@Composable
fun MovementArrows(onAction: (GameAction) -> Unit, modifier: Modifier = Modifier) {
    fun move(d: MoveDir) = { onAction(GameAction.Move(d)) }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        PadButton("▲", move(MoveDir.UP), fontSize = 20, repeat = true)
        Row {
            PadButton("◀", move(MoveDir.LEFT), fontSize = 20, repeat = true)
            Spacer(Modifier.size(56.dp))
            PadButton("▶", move(MoveDir.RIGHT), fontSize = 20, repeat = true)
        }
        PadButton("▼", move(MoveDir.DOWN), fontSize = 20, repeat = true)
    }
}

// 2. ОТДЕЛЬНЫЙ БЛОК: ОРУЖИЕ и РУКИ (плоские)
@Composable
fun WeaponAndHandsButtons(onAction: (GameAction) -> Unit, modifier: Modifier = Modifier) {
    val w = Modifier.size(width = 84.dp, height = 52.dp)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PadButton("ОРУЖИЕ", { onAction(GameAction.ToggleWeapon) }, w, fontSize = 12)
        PadButton("РУКИ", { onAction(GameAction.ToggleHands) }, w, fontSize = 12)
    }
}

// 3. ОТДЕЛЬНЫЙ БЛОК: только ОГОНЬ (плоская, задается извне)
@Composable
fun FireButton(onAction: (GameAction) -> Unit, modifier: Modifier = Modifier) {
    PadButton(
        label = "ОГОНЬ",
        onClick = { onAction(GameAction.Fire) },
        modifier = modifier.height(52.dp), // Высота 52, ширина задается снаружи
        color = GameColors.Red,
        fontSize = 18,
        repeat = true
    )
}

// 4. КНОПКИ ПОРТАЛА (ПОРТАЛ / КАМЕНЬ)
@Composable
fun PortalButtons(onAction: (GameAction) -> Unit, modifier: Modifier = Modifier) {
    val w = Modifier.size(width = 84.dp, height = 52.dp)
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        PadButton("ПОРТАЛ", { onAction(GameAction.UsePortal) }, w, color = Color(0xFF5C9DFF))
        PadButton("КАМЕНЬ", { onAction(GameAction.UseStone) }, w, color = Color(0xFF5C9DFF))
    }
}

// 5. ОБЩАЯ ПАНЕЛЬ СПРАВА (собирает всё в единый блок)
@Composable
fun RightActionPanel(onAction: (GameAction) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WeaponAndHandsButtons(onAction)
        // Ширина = 84 + 6 + 84 = 174.dp
        FireButton(onAction, Modifier.width(174.dp))
        PortalButtons(onAction)
    }
}