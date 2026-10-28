package com.example.my_rouge_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.my_rouge_mobile.input.GameAction
import com.example.my_rouge_mobile.ui.screens.GameOverScreen
import com.example.my_rouge_mobile.ui.screens.GameScreen
import com.example.my_rouge_mobile.ui.screens.OverlayHost
import com.example.my_rouge_mobile.ui.screens.PrologueScreen
import com.example.my_rouge_mobile.ui.screens.StartScreen
import com.example.my_rouge_mobile.ui.screens.VictoryScreen
import com.example.my_rouge_mobile.ui.theme.GameColors
import com.example.my_rouge_mobile.viewmodel.AppScreen
import com.example.my_rouge_mobile.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val vm: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                val state by vm.state.collectAsState()

                LaunchedEffect(state.exitRequested) {
                    if (state.exitRequested) finish()
                }

                Box(
                    Modifier
                        .fillMaxSize()
                        .background(GameColors.Background)
                        .systemBarsPadding()
                ) {
                    when (state.screen) {
                        AppScreen.Prologue -> PrologueScreen { vm.onAction(GameAction.ContinuePrologue) }
                        AppScreen.NameEntry -> StartScreen { name -> vm.onAction(GameAction.ConfirmName(name)) }
                        AppScreen.Playing -> GameScreen(state, vm.session, vm::onAction)
                        AppScreen.GameOver -> GameOverScreen(
                            heroName = state.heroName,
                            onNewGame = { vm.onAction(GameAction.NewGame) },
                            onLeaderboard = { vm.onAction(GameAction.OpenLeaderboard) }
                        )
                        AppScreen.Victory -> VictoryScreen(
                            heroName = state.heroName,
                            onNewGame = { vm.onAction(GameAction.NewGame) },
                            onLeaderboard = { vm.onAction(GameAction.OpenLeaderboard) }
                        )
                    }
                    OverlayHost(state, vm.session, vm::onAction)
                }
            }
        }
    }
}