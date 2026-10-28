package com.example.my_rouge_mobile.ui.render

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.my_rouge_mobile.mapping.Glyph
import com.example.my_rouge_mobile.mapping.GlyphShape
import com.example.my_rouge_mobile.mapping.SpriteMapping
import rougelite.BusinessLogicLayer.characters.Enemy
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.logic.MoveLogic.Direction
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel

/** Рисует символ по центру клетки (x, y) уровня. Общий помощник для всех рендереров. */
internal fun DrawScope.drawGlyph(ctx: RenderContext, glyph: Glyph, x: Int, y: Int) {
    if (!ctx.cam.isVisible(x, y)) return
    if (glyph.shape != GlyphShape.NONE) {
        drawShapeGlyph(ctx.cam, glyph, x, y)
        return
    }

//    // Если есть картинка — рисуем её и выходим
//    if (glyph.image != null) {
//        val tile = ctx.cam.tilePx.toInt()
//        drawImage(
//            image = glyph.image,
//            dstOffset = IntOffset(
//                ctx.cam.screenX(x).toInt(),
//                ctx.cam.screenY(y).toInt()
//            ),
//            dstSize = IntSize(tile, tile)
//        )
//        return
//    }
    val layout = ctx.measurer.measure(glyph.text, ctx.baseStyle.copy(color = glyph.color))
    val tile = ctx.cam.tilePx
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(
            ctx.cam.screenX(x) + (tile - layout.size.width) / 2f,
            ctx.cam.screenY(y) + (tile - layout.size.height) / 2f
        )
    )
}

/**
 * Фигуры вместо эмодзи-символов (сердце, молния, череп): рисуются путями и красятся любым цветом.
 * Координаты фигур заданы в долях клетки (0..1).
 */
private fun DrawScope.drawShapeGlyph(ctx: Camera, glyph: Glyph, x: Int, y: Int) {
    val tile = ctx.tilePx
    val size = tile * 0.72f
    val ox = ctx.screenX(x) + (tile - size) / 2f
    val oy = ctx.screenY(y) + (tile - size) / 2f
    fun px(u: Float) = ox + u * size
    fun py(v: Float) = oy + v * size

    when (glyph.shape) {
        GlyphShape.HEART -> {
            val p = Path().apply {
                moveTo(px(0.5f), py(0.92f))
                cubicTo(px(0.0f), py(0.58f), px(0.02f), py(0.12f), px(0.28f), py(0.12f))
                cubicTo(px(0.42f), py(0.12f), px(0.5f), py(0.22f), px(0.5f), py(0.30f))
                cubicTo(px(0.5f), py(0.22f), px(0.58f), py(0.12f), px(0.72f), py(0.12f))
                cubicTo(px(0.98f), py(0.12f), px(1.0f), py(0.58f), px(0.5f), py(0.92f))
                close()
            }
            drawPath(p, glyph.color)
        }
        GlyphShape.BOLT -> {
            val p = Path().apply {
                moveTo(px(0.64f), py(0.0f))
                lineTo(px(0.20f), py(0.56f))
                lineTo(px(0.46f), py(0.56f))
                lineTo(px(0.34f), py(1.0f))
                lineTo(px(0.82f), py(0.40f))
                lineTo(px(0.55f), py(0.40f))
                close()
            }
            drawPath(p, glyph.color)
        }
        GlyphShape.SKULL -> {
            drawCircle(glyph.color, radius = size * 0.34f, center = Offset(px(0.5f), py(0.40f)))
            drawRoundRect(
                color = glyph.color,
                topLeft = Offset(px(0.32f), py(0.60f)),
                size = Size(size * 0.36f, size * 0.30f),
                cornerRadius = CornerRadius(size * 0.06f)
            )
            val eye = Color(0xFF0A1517)
            drawCircle(eye, radius = size * 0.08f, center = Offset(px(0.38f), py(0.42f)))
            drawCircle(eye, radius = size * 0.08f, center = Offset(px(0.62f), py(0.42f)))
        }
        GlyphShape.NONE -> Unit
    }
}

/** Бывший EnemyRender.drawEnemies. */
internal fun DrawScope.drawEnemies(ctx: RenderContext, level: GameLevel) {
    for (enemy in level.enemies) {
        if (!enemy.isAlive || !ctx.cam.isVisible(enemy.x, enemy.y)) continue
        if (enemy.enemyType == Enemy.EnemyType.BOSS && !enemy.isVisible) {
            drawGlyph(ctx, SpriteMapping.CROWN, enemy.x, enemy.y)
        } else if (enemy.isVisible && level.isExplored(enemy.x, enemy.y)) {
            drawGlyph(ctx, SpriteMapping.enemy(enemy.enemyType), enemy.x, enemy.y)
        }
    }
}

/** Бывший WeaponRender.drawPlayer: игрок + оружие (или руки) рядом, по направлению взгляда. */
internal fun DrawScope.drawPlayer(ctx: RenderContext, player: Player) {
    val px = player.levelX
    val py = player.levelY
    drawGlyph(ctx, SpriteMapping.PLAYER, px, py)
    // Берем стандартный Glyph для игрока, но подставляем картинку из контекста
//    val playerGlyph = SpriteMapping.PLAYER.copy(image = ctx.playerImage)
//    drawGlyph(ctx, playerGlyph, px, py)

    val facing = player.facing
    val itemX = when (facing) {
        Direction.LEFT -> px - 1
        Direction.RIGHT -> px + 1
        else -> px
    }
    val weapon = player.backpack.equippedWeapon
    val hands = player.equippedHands
    if (weapon != null) {
        drawGlyph(ctx, SpriteMapping.weaponInHand(weapon.type, facing == Direction.LEFT), itemX, py)
    } else if (hands != null) {
        drawGlyph(ctx, SpriteMapping.hands(hands.type), itemX, py)
    }
}