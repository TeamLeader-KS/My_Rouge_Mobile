package com.example.my_rouge_mobile.ui.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.my_rouge_mobile.mapping.SpriteMapping
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.logic.WeaponEngine
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel
import rougelite.BusinessLogicLayer.objects.Weapon

/** Бывший WeaponRender.drawProjectiles. */
internal fun DrawScope.drawProjectiles(ctx: RenderContext, weaponEngine: WeaponEngine) {
    for (p in weaponEngine.projectiles) {
        drawGlyph(ctx, SpriteMapping.projectile(p.weapon, p.direction), p.x, p.y)
    }
}

/** Бывший WeaponRender.drawRays: лазер и бластер рисуются тонкой линией по горизонтали. */
internal fun DrawScope.drawRays(ctx: RenderContext, weaponEngine: WeaponEngine) {
    val cam = ctx.cam
    for (ray in weaponEngine.rays) {
        val color = if (ray.weapon.type == Weapon.WeaponType.LASER) SpriteMapping.RAY_LASER else SpriteMapping.RAY_BLASTER
        val y = ray.startRayY
        if (y < cam.top || y >= cam.top + cam.rows) continue
        val x1 = minOf(ray.startRayX, ray.endRayX)
        val x2 = maxOf(ray.startRayX, ray.endRayX)
        val s = cam.tilePx
        drawRect(
            color = color,
            topLeft = Offset(cam.screenX(x1), cam.screenY(y) + s * 0.4f),
            size = Size((x2 - x1 + 1) * s, s * 0.2f)
        )
    }
}

/** Бывший WeaponRender.drawWaves: волна аннигилятора закрашивает пол всей комнаты. */
internal fun DrawScope.drawWaves(ctx: RenderContext, weaponEngine: WeaponEngine, level: GameLevel, player: Player) {
    val cam = ctx.cam
    for (wave in weaponEngine.waves) {
        val room = level.rooms[wave.roomIndex]
        for (y in room.y until room.y + room.height) {
            for (x in room.x until room.x + room.length) {
                if (!cam.isVisible(x, y)) continue
                val tile = level.levelMap[y][x]
                if (tile != 0 && tile != 3) continue
                if (x == player.levelX && y == player.levelY) continue
                fillTile(cam, x, y, SpriteMapping.WAVE.copy(alpha = 0.45f))
            }
        }
    }
}