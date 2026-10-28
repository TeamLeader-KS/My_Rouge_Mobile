package com.example.my_rouge_mobile.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.my_rouge_mobile.input.GameAction
import com.example.my_rouge_mobile.ui.components.*
import com.example.my_rouge_mobile.ui.dialogs.*
import com.example.my_rouge_mobile.ui.theme.GameColors
import com.example.my_rouge_mobile.viewmodel.BackpackTab
import com.example.my_rouge_mobile.viewmodel.GameSession
import com.example.my_rouge_mobile.viewmodel.GameUiState
import com.example.my_rouge_mobile.viewmodel.Overlay

/**
 * Главный игровой экран.
 */
@Composable
fun GameScreen(
    state: GameUiState,
    session: GameSession?,
    onAction: (GameAction) -> Unit
) {
    BackHandler(enabled = state.overlay == Overlay.None) { onAction(GameAction.OpenMenu) }

    BoxWithConstraints(Modifier.fillMaxSize().background(GameColors.Background)) {
        // Оставляем только альбомный режим
        LandscapeLayout(state, session, onAction)
    }
}

@Composable
private fun LandscapeLayout(
    state: GameUiState,
    session: GameSession?,
    onAction: (GameAction) -> Unit
) {

    Box(Modifier.fillMaxSize().background(GameColors.Background)) {

        Column(Modifier.fillMaxSize()) {

            // 1. ВЕРХ: Сообщение (Ровно 30.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GameColors.Background)
                    .padding(top = 4.dp)
                    .height(30.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.message != null) {
                    MessageOverlay(state.message)
                }
            }

            // 2. СЕРЕДИНА: Карта (Занимает всё оставшееся место)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clipToBounds()
            ) {
                if (session != null) {
                    GameCanvas(session, state.tick, Modifier.fillMaxSize())
                }
            }

            // 3. НИЗ: HUD (Статистика)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(GameColors.Background)
            ) {
                GameStatistic(state.gameStatistic)
            }
        }

        // ── 2. КНОПКИ ПОВЕРХ ВСЕГО ──

        // СТРЕЛКИ (Слева)
        MovementArrows(
            onAction = onAction,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 35.dp, top = 50.dp) // Смещение по вашему вкусу
        )

        // КНОПКИ БОЯ И ПОРТАЛА (Справа, единый блок)
        RightActionPanel(
            onAction = onAction,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )

        // ── КНОПКИ МЕНЮ (СВЕРХУ) ──

        // Левый верхний угол: КАРМАН и РЮКЗАК
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val btnMod = Modifier.size(width = 90.dp, height = 34.dp)
            val bg = Color(0xFF1A1A1A)

            PadButton("КАРМАН", { onAction(GameAction.OpenPocket) }, btnMod, bg = bg, fontSize = 12)
            PadButton("РЮКЗАК", { onAction(GameAction.OpenBackpack(BackpackTab.Weapon)) }, btnMod, bg = bg, fontSize = 12)
        }

        // Правый верхний угол: МЕНЮ и ИНФО
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val btnMod = Modifier.size(width = 90.dp, height = 34.dp)
            val bg = Color(0xFF1A1A1A)

            PadButton("МЕНЮ", { onAction(GameAction.OpenMenu) }, btnMod, bg = bg, fontSize = 12)
            PadButton("ИНФО", { onAction(GameAction.OpenInformation) }, btnMod, bg = bg, fontSize = 12)
        }
    }
}

/**
 * Хост для всех всплывающих окон (Рюкзак, Меню, Карта и т.д.)
 */
@Composable
fun OverlayHost(
    state: GameUiState,
    session: GameSession?,
    onAction: (GameAction) -> Unit
) {
    val close = { onAction(GameAction.CloseOverlay) }

    when (val o = state.overlay) {
        Overlay.None -> Unit
        is Overlay.Backpack -> session?.let { s ->
            BackpackDialog(
                backpack = s.player.backpack,
                tab = o.tab,
                onSelectTab = { onAction(GameAction.SelectBackpackTab(it)) },
                onUseItem = { slot -> onAction(GameAction.UseItem(slot)) },
                onClose = close
            )
        }
        Overlay.Pocket -> session?.let { s -> PocketDialog(s.player.pocket, close) }
        Overlay.Information -> InformationDialog(close)
        Overlay.Menu -> ExitMenuDialog(
            onResume = close,
            onNewGame = { onAction(GameAction.NewGame) },
            onSaveAndExit = { onAction(GameAction.SaveAndExit) },
            onLeaderboard = { onAction(GameAction.OpenLeaderboard) }
        )
        Overlay.Leaderboard -> LeaderboardDialog(state.leaderboard, close)
    }
}