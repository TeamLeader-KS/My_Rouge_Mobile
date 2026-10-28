package com.example.my_rouge_mobile.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.my_rouge_mobile.ui.render.Camera
import com.example.my_rouge_mobile.ui.render.RenderContext
import com.example.my_rouge_mobile.ui.render.drawEnemies
import com.example.my_rouge_mobile.ui.render.drawMap
import com.example.my_rouge_mobile.ui.render.drawPlayer
import com.example.my_rouge_mobile.ui.render.drawProjectiles
import com.example.my_rouge_mobile.ui.render.drawRays
import com.example.my_rouge_mobile.ui.render.drawWaves
import com.example.my_rouge_mobile.viewmodel.GameConfig
import com.example.my_rouge_mobile.viewmodel.GameSession
import androidx.compose.ui.res.imageResource
import com.example.my_rouge_mobile.R

/** Зоны поля, закрытые кнопками управления. Камера не даёт краю карты уехать под них. */
data class CanvasInsets(
    val left: Dp = 0.dp,
    val top: Dp = 0.dp,
    val right: Dp = 0.dp,
    val bottom: Dp = 0.dp
)

/**
 * Игровое поле (замена GameRender.render). Читает живую модель из session;
 * [tick] меняется после каждого хода и заставляет Canvas перерисоваться.
 */
@Composable
fun GameCanvas(
    session: GameSession,
    tick: Long,
    modifier: Modifier = Modifier,
    insets: CanvasInsets = CanvasInsets()
) {
    val measurer = rememberTextMeasurer(cacheSize = 128)

//    val playerImg = ImageBitmap.imageResource(id = R.drawable.sprite_player)

    Canvas(modifier = modifier.background(Color.Black).clipToBounds()) {
        @Suppress("UNUSED_VARIABLE") val redrawKey = tick

        val player = session.player
        val level = session.gameEngine.currentLevel

        val cam = Camera.centeredOn(
            player.levelX, player.levelY,
            level.levelMap.firstOrNull()?.size ?: GameConfig.MAP_WIDTH,
            level.levelMap.size.takeIf { it > 0 } ?: GameConfig.MAP_HEIGHT,
            size.width, size.height,
            GameConfig.TILE_DP.dp.toPx(),
            insets.left.toPx(), insets.top.toPx(), insets.right.toPx(), insets.bottom.toPx()
        )
        val ctx = RenderContext(
            cam = cam,
            measurer = measurer,
            baseStyle = TextStyle(fontSize = (cam.tilePx * 0.8f).toSp(), fontFamily = FontFamily.Monospace),

//            playerImage = playerImg
        )

        drawMap(ctx, level)
        drawWaves(ctx, session.weaponEngine, level, player)
        drawEnemies(ctx, level)
        drawProjectiles(ctx, session.weaponEngine)
        drawRays(ctx, session.weaponEngine)
        drawPlayer(ctx, player)
    }
}