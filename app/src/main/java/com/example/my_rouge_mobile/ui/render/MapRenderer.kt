package com.example.my_rouge_mobile.ui.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.my_rouge_mobile.mapping.SpriteMapping
import rougelite.BusinessLogicLayer.mapGenerator.DoorCoordinates
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel
import rougelite.BusinessLogicLayer.objects.Portal

internal fun DrawScope.fillTile(cam: Camera, x: Int, y: Int, color: Color, inset: Float = 0f) {
    val s = cam.tilePx
    drawRect(
        color = color,
        topLeft = Offset(cam.screenX(x) + inset, cam.screenY(y) + inset),
        size = Size(s - 2 * inset, s - 2 * inset)
    )
}

private fun DrawScope.dotTile(cam: Camera, x: Int, y: Int, color: Color) {
    val s = cam.tilePx
    drawCircle(
        color = color,
        radius = s * 0.07f,
        center = Offset(cam.screenX(x) + s / 2f, cam.screenY(y) + s / 2f)
    )
}

/**
 * Бывшие GameRender.drawRoomWithFog / drawLastRoomWithFog / drawCorridorsWithFog / drawPortal /
 * drawPortalStonesWithFog / drawWeapon... / drawCrown...
 * Туман войны: неисследованные клетки не рисуются (остаются чёрными).
 */
internal fun DrawScope.drawMap(ctx: RenderContext, level: GameLevel) {
    val cam = ctx.cam
    val noFog = level.dungeonLevel == 21 // последняя комната видна целиком

    // ── комнаты ──
    for (room in level.rooms) {
        for (row in 0 until room.height) {
            for (col in 0 until room.length) {
                val x = room.x + col
                val y = room.y + row
                if (!cam.isVisible(x, y)) continue
                if (!noFog && !level.isExplored(x, y)) continue
                when (room.tiles[row][col]) {
                    1 -> fillTile(cam, x, y, SpriteMapping.WALL_COLOR, inset = 1f)
                    2 -> fillTile(cam, x, y, SpriteMapping.DOOR_COLOR, inset = cam.tilePx * 0.15f)
                    else -> {
                        fillTile(cam, x, y, SpriteMapping.FLOOR_BG)
                        dotTile(cam, x, y, SpriteMapping.FLOOR_COLOR.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }

    // ── коридоры (двери пропускаем: они уже нарисованы как часть комнаты) ──
    val doors = HashSet<Long>()
    for (d in DoorCoordinates.getDoors()) doors.add(d.x.toLong() shl 32 or d.y.toLong())
    for (corridor in level.corridors) {
        for (point in corridor.path) {
            val x = point[0]
            val y = point[1]
            if (!cam.isVisible(x, y) || !level.isExplored(x, y)) continue
            if (doors.contains(x.toLong() shl 32 or y.toLong())) continue
            fillTile(cam, x, y, SpriteMapping.CORRIDOR_COLOR.copy(alpha = 0.22f))
        }
    }

    // ── портал ──
    val portal = level.portal
    if (portal != null && level.isExplored(portal.x, portal.y)) drawPortal(ctx, portal)

    // ── камни портала в комнатах ──
    for (room in level.rooms) {
        val stone = room.portalStone ?: continue
        if (level.isExplored(stone.stoneX, stone.stoneY)) {
            drawGlyph(ctx, SpriteMapping.PORTAL_STONE, stone.stoneX, stone.stoneY)
        }
    }

    // ── предметы на полу ──
    for (item in level.items) {
        if (!level.isExplored(item.x, item.y)) continue
        SpriteMapping.item(item)?.let { drawGlyph(ctx, it, item.x, item.y) }
    }
}

private fun DrawScope.drawPortal(ctx: RenderContext, portal: Portal) {
    val mapping = arrayOf(intArrayOf(0, 1, 2), intArrayOf(3, -1, 5), intArrayOf(6, 7, 8))
    for (row in 0 until 3) {
        for (col in 0 until 3) {
            val idx = mapping[row][col]
            val x = portal.x - 1 + col
            val y = portal.y - 1 + row
            when {
                idx == -1 -> if (portal.isOpen) drawGlyph(ctx, SpriteMapping.PORTAL_CENTER_OPEN, x, y)
                portal.hasStone(idx) -> drawGlyph(ctx, SpriteMapping.PORTAL_SLOT_FULL, x, y)
                else -> drawGlyph(ctx, SpriteMapping.PORTAL_SLOT_EMPTY, x, y)
            }
        }
    }
}