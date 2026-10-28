package rougelite.BusinessLogicLayer.message

import java.util.Locale

object GameMessages {

    // === РЮКЗАК ===

    const val BACKPACK_ADD_WEAPON = "Оружие добавлено в рюкзак"
    const val BACKPACK_ADD_AMMO = "Амуниция добавлена в рюкзак"
    const val BACKPACK_ADD_POTION = "Зелье добавлено в рюкзак"
    const val BACKPACK_ADD_REINFORCE = "Предмет добавлен в рюкзак"
    const val BACKPACK_REMOVE_AMMO = "Амуниция удалена из рюкзака"
    const val BACKPACK_REMOVE_POTION = "Зелье удалено из рюкзака"
    const val BACKPACK_FULL = "Рюкзак переполнен!"
    const val BACKPACK_HAS_WEAPON = "У тебя уже есть такое оружие!"
    const val BACKPACK_HAS_REINFORCE = "У тебя уже есть такой предмет!"

    // === ЭКИПИРОВКА ОРУЖИЯ ===

    const val EQUIP_WEAPON = "В твоих руках %s"
    const val UNEQUIP_WEAPON = "Ты убрал %s"
    const val NO_WEAPON = "У тебя пока нет оружия!"

    // === ЭКИПИРОВКА РУК ===

    const val EQUIP_FISTS = "Пока ты без оружия. Размахивай кулаками!"
    const val EQUIP_NAILS = "Пока ты без оружия. Кусайся и царапайся!"
    const val UNEQUIP_FISTS = "Ты спрятал кулаки!"
    const val UNEQUIP_NAILS = "Ты убрал когти!"
    const val NO_READY_FOR_FIGHT = "Тебе надо подготовиться к сражению!"

    // === ПОДБОР ПРЕДМЕТОВ ===

    const val PICKUP_WEAPON = "Ты нашел оружие!"
    const val PICKUP_AMMO = "Ты нашел амуницию!"
    const val PICKUP_POTION = "Ты нашел зелье здоровья!"
    const val PICKUP_REINFORCE = "Ты нашел предмет!"
    const val PICKUP_CROWN = "Корона Тьмы твоя! Одень ее и стань нашим новым повелителем!"

    // === ПОРТАЛ И КАМНИ ===

    const val IMPOSSIBLE_USE_PORTAL_STONE = "Ты не можешь активировать портал из этого места!"
    const val ACTIVATED_PORTAL = "ПОРТАЛ АКТИВИРОВАН!"
    const val INSERT_PORTAL_STONE = "Камень установлен!"
    const val ENTER_PORTAL = "Ты готов войти в портал?"
    const val PORTAL_NEXT_LEVEL = "Ты переместился на новый уровень подземелья!"
    const val PICKUP_PORTAL_STONE = "Ты подобрал камень портала"

    // === КАРМАН ===

    const val POCKET_ADD_PORTAL_STONE = "Камень установлен!"
    const val CLEAR_POCKET = "Ты использовал все камни! Карман свободен."

    // === СТРЕЛЬБА И АМУНИЦИЯ ===

    const val NO_AMMUNITION = "Требуется перезарядка оружия!"
    const val USE_AMMO = "Оружие перезаряжено!"
    const val FULL_AMMO = "Оружие полностью заряжено!"
    const val IMPOSSIBLE_USE_AMMO = "Не получится использовать с этим оружием!"
    const val BLADED_WEAPON = "Этому оружию не нужна амуниция!"
    const val NOT_EQUIP_WEAPON = "Нет экипированного оружия!"
    const val WALL_BLOCKS_SHOT = "Никаким оружием нельзя разрушить эти стены!"

    // === ЗЕЛЬЯ ===

    const val USE_HEALTH_POTION = "Ты выпил %s! Здоровье восстановлено!"
    const val FULL_HEALTH = "Здоровье уже полное!"

    // === РОСТ ХАРАКТЕРИСТИК ===

    const val EXPERIENCE_UP = "Получен опыт!"
    const val TREASURE_UP = "Получены сокровища!"
    const val LEVEL_UP = "Получен новый уровень!"

    // === СООБЩЕНИЯ ПРОТИВНИКОВ ===

    const val ENEMY_NOTICED_YOU = "Враг тебя заметил!"
    const val ZOMBIE_ATTACKED_YOU = "Зомби укусил тебя!"
    const val VAMPIRE_ATTACKED_YOU = "Вампир вонзил клыки в твою шею!"
    const val OGRE_ATTACKED_YOU = "Огр ударил тебя своей дубиной!"
    const val GHOST_ATTACKED_YOU = "Призрак вогнал тебя в ужас!"
    const val SNAKE_MAGE_ATTACKED_YOU = "Змей-Маг наложил на тебя смертельное заклятье!"
    const val BOSS_ATTACKED_YOU = "Владыка Тьмы поразил тебя темным клинком!"
    const val BOSS_SCARES_YOU = "Ты умрешь, смертный!"

    val BOSS_SCARES: List<String> =
            listOf(
                    "Ты умрешь, смертный!",
                    "Сдохни, червь!",
                    "Ты слабак!",
                    "Не смеши меня!",
                    "Ты никогда не победишь!"
            )

    // === БОЙ ===

    const val BATTLE_START = "⚔️ БОЙ НАЧАЛСЯ: %s vs %s ⚔️"
    const val BATTLE_ENEMY_DEFEATED = "✅ %s повержен!"
    const val BATTLE_PLAYER_DEAD = "💀 %s погиб в бою..."
    const val BATTLE_ATTACK = "👉 %s атакует %s"
    const val BATTLE_HIT = "   Попадание! Урон: %d. У цели осталось HP: %d"
    const val BATTLE_MISS = "   Промах!"

    //  ФОРМАТИРОВАНИЕ

    fun formatEquipWeapon(weaponName: String): String =
            String.format(Locale.ROOT, EQUIP_WEAPON, weaponName)

    fun formatUnequipWeapon(weaponName: String): String =
            String.format(Locale.ROOT, UNEQUIP_WEAPON, weaponName)

    fun formatUseReinforce(reinforceName: String): String =
            String.format(Locale.ROOT, USE_AMMO, reinforceName)

    fun formatUseHealthPotion(potionName: String): String =
            String.format(Locale.ROOT, USE_HEALTH_POTION, potionName)

    fun formatBattleStart(playerName: String, enemyName: String): String =
            String.format(Locale.ROOT, BATTLE_START, playerName, enemyName)

    fun formatEnemyDefeated(enemyName: String): String =
            String.format(Locale.ROOT, BATTLE_ENEMY_DEFEATED, enemyName)

    fun formatPlayerDead(playerName: String): String =
            String.format(Locale.ROOT, BATTLE_PLAYER_DEAD, playerName)

    fun formatAttack(attackerName: String, targetName: String): String =
            String.format(Locale.ROOT, BATTLE_ATTACK, attackerName, targetName)

    fun formatHit(damage: Int, remainingHp: Int): String =
            String.format(Locale.ROOT, BATTLE_HIT, damage, remainingHp)
}
