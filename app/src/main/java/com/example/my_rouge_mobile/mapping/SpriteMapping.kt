package com.example.my_rouge_mobile.mapping

import androidx.compose.ui.graphics.Color
import com.example.my_rouge_mobile.ui.theme.GameColors
import rougelite.BusinessLogicLayer.characters.Enemy
import rougelite.BusinessLogicLayer.logic.MoveLogic
import rougelite.BusinessLogicLayer.objects.Ammo
import rougelite.BusinessLogicLayer.objects.DarkCrown
import rougelite.BusinessLogicLayer.objects.GameObject
import rougelite.BusinessLogicLayer.objects.Hands
import rougelite.BusinessLogicLayer.objects.HealthPotion
import rougelite.BusinessLogicLayer.objects.Reinforce
import rougelite.BusinessLogicLayer.objects.Weapon
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Фигуры, которые рисуются векторно, а не шрифтом. Символы ⚡ ♥ ☠ в Android - эмодзи:
 * система рисует их цветной картинкой и игнорирует цвет, поэтому для них нужна своя отрисовка.
 */
enum class GlyphShape { NONE, HEART, BOLT, SKULL }

/** Один «спрайт»: символ + цвет (или фигура). Позже можно заменить на картинку, не трогая рендереры. */
data class Glyph(val text: String, val color: Color, val shape: GlyphShape = GlyphShape.NONE) // , val image: ImageBitmap? = null)

/** Цвета ANSI из старой версии, чуть подсвеченные, чтобы читались на чёрном. */
private object C {
    val Green = Color(0xFF4CAF50)
    val GreenBright = GameColors.GreenBright
    val Red = GameColors.Red
    val RedBright = Color(0xFFFF5252)
    val White = Color(0xFFE0E0E0)
    val WhiteBright = Color.White
    val Yellow = GameColors.Gold
    val YellowBright = GameColors.GoldBright
    val Magenta = Color(0xFFAB47BC)
    val MagentaBright = Color(0xFFFF80FF)
    val BlueBright = Color(0xFF5C9DFF)
    val Cyan = Color(0xFF00ACC1)
    val CyanBright = Color(0xFF18FFFF)
    val Orange = Color(0xFFFF9800)
}

object SpriteMapping {

    val PLAYER = Glyph("@", C.GreenBright)
    val CROWN = Glyph("♛", C.YellowBright)
    val PORTAL_STONE = Glyph("●", C.BlueBright)
    val PORTAL_SLOT_FULL = Glyph("●", C.BlueBright)
    val PORTAL_SLOT_EMPTY = Glyph("○", C.Red)
    val PORTAL_CENTER_OPEN = Glyph("░", C.BlueBright)

    val RAY_LASER = C.RedBright
    val RAY_BLASTER = C.BlueBright
    val WAVE = C.MagentaBright

    val WALL_COLOR = Color(0xFF8A7500)
    val DOOR_COLOR = Color(0xFF2E7D32)
    val FLOOR_COLOR = C.Cyan
    val FLOOR_BG = Color(0xFF0A1517)

    /** Усиления делятся на 3 группы по тому, что они дают. */
    val REINFORCE_HEALTH_COLOR = C.RedBright      // здоровье - красный
    val REINFORCE_STRENGTH_COLOR = C.Orange       // сила - оранжевый
    val REINFORCE_DEXTERITY_COLOR = C.CyanBright  // ловкость - голубой
    val CORRIDOR_COLOR = C.Cyan

    // ── враги (бывший EnemyRender) ──
    fun enemy(type: Enemy.EnemyType): Glyph = when (type) {
        Enemy.EnemyType.ZOMBIE -> Glyph("Z", C.Green)
        Enemy.EnemyType.VAMPIRE -> Glyph("V", C.Red)
        Enemy.EnemyType.GHOST -> Glyph("G", C.White)
        Enemy.EnemyType.OGRE -> Glyph("O", C.Yellow)
        Enemy.EnemyType.SNAKE_MAGE -> Glyph("S", C.White)
        Enemy.EnemyType.BOSS -> Glyph("M", C.MagentaBright)
        else -> Glyph("?", C.White)
    }

    // ── предметы на полу (бывшие drawWeapon/Ammo/HealthPotion/Reinforce/Crown) ──
    fun item(item: GameObject): Glyph? = when (item) {
        is Weapon -> weaponOnFloor(item.type)
        is Ammo -> ammoOnFloor(item.type)
        is HealthPotion -> Glyph("", potionColor(item.type), GlyphShape.HEART)
        is Reinforce -> Glyph("?", reinforceColor(item))
        is DarkCrown -> CROWN
        else -> null
    }

    private fun weaponOnFloor(type: Weapon.WeaponType): Glyph {
        val color = weaponColor(type)
        return when (type) {
            Weapon.WeaponType.KNIFE -> Glyph("←", color)
            Weapon.WeaponType.SWORD -> Glyph("⚔", color)
            Weapon.WeaponType.WARHAMMER -> Glyph("⚒", color)
            Weapon.WeaponType.BOW -> Glyph("}", color)
            Weapon.WeaponType.SHOTGUN,
            Weapon.WeaponType.MINIGUN,
            Weapon.WeaponType.LASER,
            Weapon.WeaponType.BLASTER -> Glyph("⌐", color)
            Weapon.WeaponType.WARP_ANNIHILATOR -> Glyph("", color, GlyphShape.SKULL)
            else -> Glyph("!", color)
        }
    }

    private fun ammoOnFloor(type: Ammo.AmmoType): Glyph {
        val color = ammoColor(type)
        return when (type) {
            Ammo.AmmoType.BOW_AMMUNITION -> Glyph("↑", color)
            Ammo.AmmoType.SHOTGUN_AMMUNITION,
            Ammo.AmmoType.MINIGUN_AMMUNITION -> Glyph("■", color)
            Ammo.AmmoType.LASER_AMMUNITION,
            Ammo.AmmoType.BLASTER_AMMUNITION,
            Ammo.AmmoType.WARP_ANNIHILATOR_AMMUNITION -> Glyph("", color, GlyphShape.BOLT)
            else -> Glyph("■", color)
        }
    }

    // ── оружие/руки в руках игрока (бывший WeaponRender.getWeaponSymbol/getHandsSymbol) ──
    fun weaponInHand(type: Weapon.WeaponType, facingLeft: Boolean): Glyph {
        if (type == Weapon.WeaponType.WARP_ANNIHILATOR) return Glyph("", C.YellowBright, GlyphShape.SKULL)
        val ch = when (type) {
            Weapon.WeaponType.KNIFE -> if (facingLeft) "⟶" else "⟵"
            Weapon.WeaponType.SWORD -> "⚔"
            Weapon.WeaponType.WARHAMMER -> "⚒"
            Weapon.WeaponType.BOW -> if (facingLeft) "{" else "}"
            Weapon.WeaponType.SHOTGUN,
            Weapon.WeaponType.MINIGUN,
            Weapon.WeaponType.LASER,
            Weapon.WeaponType.BLASTER -> if (facingLeft) "¬" else "⌐"
            Weapon.WeaponType.WARP_ANNIHILATOR -> "☠"
            else -> "!"
        }
        return Glyph(ch, C.YellowBright)
    }

    /** Возвращает цвет оружия для интерфейса (рюкзак, HUD). */
    fun weaponColor(type: Weapon.WeaponType): Color = when (type) {
        Weapon.WeaponType.KNIFE -> C.YellowBright
        Weapon.WeaponType.SWORD -> C.WhiteBright
        Weapon.WeaponType.WARHAMMER -> C.Yellow
        Weapon.WeaponType.BOW -> C.GreenBright
        Weapon.WeaponType.SHOTGUN -> C.YellowBright
        Weapon.WeaponType.MINIGUN -> C.WhiteBright
        Weapon.WeaponType.LASER -> C.RedBright      // Лазер - красный
        Weapon.WeaponType.BLASTER -> C.BlueBright    // Бластер - синий
        Weapon.WeaponType.WARP_ANNIHILATOR -> C.MagentaBright // Варп - розовый
        else -> C.White
    }

    /** Цвет патронов: тот же на карте и в рюкзаке. */
    fun ammoColor(type: Ammo.AmmoType): Color = when (type) {
        Ammo.AmmoType.BOW_AMMUNITION -> C.GreenBright
        Ammo.AmmoType.SHOTGUN_AMMUNITION -> C.YellowBright
        Ammo.AmmoType.MINIGUN_AMMUNITION -> C.WhiteBright
        Ammo.AmmoType.LASER_AMMUNITION -> C.RedBright
        Ammo.AmmoType.BLASTER_AMMUNITION -> C.CyanBright
        Ammo.AmmoType.WARP_ANNIHILATOR_AMMUNITION -> C.MagentaBright
        else -> C.White
    }

    /** Цвет зелья: тот же на карте и в рюкзаке. */
    fun potionColor(type: HealthPotion.HealthPotionType): Color = when (type) {
        HealthPotion.HealthPotionType.LOW_HEALTH -> C.YellowBright
        HealthPotion.HealthPotionType.AVERAGE_HEALTH -> C.GreenBright
        HealthPotion.HealthPotionType.BIG_HEALTH -> C.RedBright
        else -> C.White
    }

    /**
     * Цвет усиления по тому, что оно даёт: здоровье - красный, сила - оранжевый, ловкость - голубой.
     * Если усиление даёт сразу несколько бонусов, берётся первый по порядку: здоровье, сила, ловкость.
     */
    fun reinforceColor(reinforce: Reinforce): Color = when {
        reinforce.healthBooster > 0 -> REINFORCE_HEALTH_COLOR
        reinforce.strengthBooster > 0 -> REINFORCE_STRENGTH_COLOR
        reinforce.dexterityBooster > 0 -> REINFORCE_DEXTERITY_COLOR
        else -> C.Magenta
    }

    fun hands(type: Hands.HandsType): Glyph = Glyph(
        when (type) {
            Hands.HandsType.FISTS -> ":"
            Hands.HandsType.NAILS -> "≈"
            else -> ":"
        },
        C.YellowBright
    )

    // ── снаряды (бывший getProjectileSymbol/Color) ──
    fun projectile(weapon: Weapon, direction: MoveLogic.Direction): Glyph = when (weapon.type) {
        Weapon.WeaponType.LASER -> Glyph("-", C.RedBright)
        Weapon.WeaponType.BLASTER -> Glyph("~", C.BlueBright)
        Weapon.WeaponType.MINIGUN -> Glyph("•", C.WhiteBright)
        Weapon.WeaponType.SHOTGUN -> Glyph("░", C.YellowBright)
        Weapon.WeaponType.BOW ->
            // Нормальные стрелки
            Glyph(if (direction == MoveLogic.Direction.RIGHT) "⟶" else "⟵", C.GreenBright)
        else -> Glyph("•", C.Cyan)
    }
}