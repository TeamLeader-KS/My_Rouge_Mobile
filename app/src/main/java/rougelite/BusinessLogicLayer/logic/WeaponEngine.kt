package rougelite.BusinessLogicLayer.logic

import rougelite.BusinessLogicLayer.characters.Enemy
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.logic.MoveLogic.Direction
import rougelite.BusinessLogicLayer.mapGenerator.GameLevel
import rougelite.BusinessLogicLayer.message.GameMessages
import rougelite.BusinessLogicLayer.message.Messages
import rougelite.BusinessLogicLayer.objects.Weapon

class WeaponEngine(
    private val player: Player,
    var currentLevel: GameLevel,
    private val gameEngine: GameEngine
) {

    private val _projectiles: MutableList<Projectile> = ArrayList()
    val projectiles: List<Projectile>
        get() = ArrayList(_projectiles)

    var lastHorizontalDirection: Direction = Direction.RIGHT
        private set

    private val _rays: MutableList<Ray> = ArrayList()
    val rays: List<Ray>
        get() = ArrayList(_rays)

    private val _waves: MutableList<AnnihilationWave> = ArrayList()
    val waves: List<AnnihilationWave>
        get() = ArrayList(_waves)

    fun updateDirection(direction: Direction) {
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            lastHorizontalDirection = direction
            player.facing = direction
        }
    }

    fun clearRays() {
        _rays.clear()
    }

    fun clearWaves() {
        _waves.clear()
    }

    // ======= СТРЕЛЬБА =========
    fun shoot(): Boolean {

        _rays.clear()
        _waves.clear()

        val weapon = player.backpack.equippedWeapon
        if (weapon == null) {
            return meleeAttack()
        }

        if (weapon.maxAmmo > 0 && weapon.currentAmmo <= 0) {
            Messages.show(GameMessages.NO_AMMUNITION, 1500)
            return false
        }



        val shootDirection = lastHorizontalDirection


        var startX = player.levelX
        val startY = player.levelY
        val levelMap = currentLevel.levelMap

        when (shootDirection) {
            Direction.LEFT -> startX = player.levelX - 1
            Direction.RIGHT -> startX = player.levelX + 1
            else -> {}
        }

        // Если пуля должна появиться в стене (мы вплотную к ней) — не стреляем
        if (startX !in levelMap[0].indices || levelMap[startY][startX] == 1) {
            Messages.show(GameMessages.WALL_BLOCKS_SHOT, 1500)
            return false
        }

        // ===== ЛУК, ДРОБОВИК, МИНИГАН =====
        if (weapon.type == Weapon.WeaponType.BOW ||
            weapon.type == Weapon.WeaponType.SHOTGUN ||
            weapon.type == Weapon.WeaponType.MINIGUN
        ) {
            weapon.fire()

            val damageFromDexterity = weapon.weaponPower + player.dexterity

            val projectile =
                Projectile(
                    x = startX,
                    y = startY,
                    direction = shootDirection,
                    weapon = weapon,
                    damage = damageFromDexterity,
                    isLaser = false
                )
            _projectiles.add(projectile)
            return true
        }

        // ===== ЛАЗЕР И БЛАСТЕР - создаём луч (RAY) =====
        if (weapon.type == Weapon.WeaponType.LASER || weapon.type == Weapon.WeaponType.BLASTER) {
            weapon.fire()

            val maxDistance = 55
            val levelMap = currentLevel.levelMap

            val rayStartX =
                when (shootDirection) {
                    Direction.LEFT -> startX - 1
                    Direction.RIGHT -> startX + 1
                    else -> startX
                }

            var endX = rayStartX
            val damage = weapon.weaponPower

            val enemiesToRemove = ArrayList<Enemy>()

            for (i in 0..maxDistance) {
                val checkX = rayStartX + if (shootDirection == Direction.RIGHT) i else -i

                if (checkX !in levelMap[0].indices) break
                if (levelMap[startY][checkX] == 1) break

                for (enemy in currentLevel.enemies) {
                    if (enemy.isAlive && enemy.x == checkX && enemy.y == startY) {
                        val killed = enemy.takeDamageAndCheckDeath(damage)
                        if (killed) {
                            handleEnemyDeath(enemy)
                            enemiesToRemove.add(enemy)
                        }
                    }
                }
                endX = checkX
            }

            for (enemy in enemiesToRemove) {
                currentLevel.removeEnemy(enemy)
            }

            val ray = Ray(rayStartX, startY, endX, startY, weapon)
            _rays.add(ray)


            return true
        }

        // ===== АННИГИЛЯТОР - создаём волну по комнате =====
        if (weapon.type == Weapon.WeaponType.WARP_ANNIHILATOR) {
            weapon.fire()

            val playerX = player.levelX
            val playerY = player.levelY

            val currentRoom =
                currentLevel.rooms.firstOrNull { room ->
                    playerX >= room.x &&
                            playerX < room.x + room.length &&
                            playerY >= room.y &&
                            playerY < room.y + room.height
                }

            if (currentRoom != null) {
                val wave = AnnihilationWave(currentRoom.roomIndex, weapon)
                _waves.add(wave)

                val damage = weapon.weaponPower
                val enemiesToRemove = ArrayList<Enemy>()

                for (enemy in currentLevel.enemies) {
                    if (enemy.x >= currentRoom.x &&
                        enemy.x < currentRoom.x + currentRoom.length &&
                        enemy.y >= currentRoom.y &&
                        enemy.y < currentRoom.y + currentRoom.height
                    ) {
                        val isKilledByThisHit = enemy.takeDamageAndCheckDeath(damage)


                        if (isKilledByThisHit) {
                            handleEnemyDeath(enemy)
                            enemiesToRemove.add(enemy)
                        }
                    }
                }

                for (enemy in enemiesToRemove) {
                    currentLevel.enemies.remove(enemy)
                }
            }

            return true
        }

        // ===== ОСТАЛЬНОЕ ОРУЖИЕ =====
        if (weapon.type != Weapon.WeaponType.KNIFE &&
            weapon.type != Weapon.WeaponType.SWORD &&
            weapon.type != Weapon.WeaponType.WARHAMMER
        ) {
            weapon.fire()

            val damage = weapon.weaponPower
            val projectile =
                Projectile(
                    x = startX,
                    y = startY,
                    direction = shootDirection,
                    weapon = weapon,
                    damage = damage,
                    isLaser = false
                )
            _projectiles.add(projectile)


            return true
        }

        // Холодное оружие
        return meleeAttack()
    }

    fun update() {
        val iterator = _projectiles.iterator()
        while (iterator.hasNext()) {
            val p = iterator.next()

            p.move()
            p.increaseDistance()

            val levelMap = currentLevel.levelMap

            // Проверка границ
            if (p.x !in levelMap[0].indices || p.y !in levelMap.indices) {

                iterator.remove()
                continue
            }

            // Проверка стены
            val tile = levelMap[p.y][p.x]
            if (tile == 1) {

                iterator.remove()
                continue
            }

            // Проверка врагов
            var hitEnemy = false
            var enemyToRemove: Enemy? = null

            for (enemy in currentLevel.enemies) {
                if (enemy.isAlive && enemy.x == p.x && enemy.y == p.y) {
                    val isKilledByThisHit = enemy.takeDamageAndCheckDeath(p.damage)


                    if (isKilledByThisHit) {
                        handleEnemyDeath(enemy)
                        enemyToRemove = enemy
                    }
                    hitEnemy = true
                    break
                }
            }

            if (enemyToRemove != null) {
                currentLevel.removeEnemy(enemyToRemove)
            }

            if (hitEnemy) {
                iterator.remove()
                continue
            }

            if (p.isOutOfBounds()) {

                iterator.remove()
            }
        }
    }

    /** Проверяет, не влетел ли враг в клетку, где уже летит снаряд. */
    fun checkEnemyCollisionsWithProjectiles() {
        val enemiesToRemove = ArrayList<Enemy>()

        for (enemy in currentLevel.enemies) {
            if (!enemy.isAlive) continue

            // Ищем снаряд, который находится на той же клетке, что и враг
            val hitProjectile = _projectiles.find { p -> p.x == enemy.x && p.y == enemy.y }

            if (hitProjectile != null) {
                val isKilled = enemy.takeDamageAndCheckDeath(hitProjectile.damage)

                _projectiles.remove(hitProjectile) // Снаряд исчезает при попадании

                if (isKilled) {
                    handleEnemyDeath(enemy)
                    enemiesToRemove.add(enemy)
                }
            }
        }

        for (enemy in enemiesToRemove) {
            currentLevel.removeEnemy(enemy)
        }
    }

    /** Проверяет, не встал ли враг на линию луча (лазер/бластер). */
    fun checkEnemyCollisionsWithRays() {
        val enemiesToRemove = ArrayList<Enemy>()

        for (enemy in currentLevel.enemies) {
            if (!enemy.isAlive) continue

            // Проверяем каждый луч
            for (ray in _rays) {
                // Враг должен быть на той же строке (Y), что и луч
                // И его X должен быть между началом и концом луча
                if (enemy.y == ray.startRayY) {
                    val minX = minOf(ray.startRayX, ray.endRayX)
                    val maxX = maxOf(ray.startRayX, ray.endRayX)

                    if (enemy.x in minX..maxX) {
                        val damage = ray.weapon.weaponPower
                        val isKilled = enemy.takeDamageAndCheckDeath(damage)

                        if (isKilled) {
                            handleEnemyDeath(enemy)
                            enemiesToRemove.add(enemy)
                        }
                    }
                }
            }
        }

        for (enemy in enemiesToRemove) {
            currentLevel.removeEnemy(enemy)
        }
    }

    fun meleeAttack(): Boolean {
        val weapon = player.backpack.equippedWeapon
        val hands = player.equippedHands

        if (weapon == null && hands == null) {
            Messages.show(GameMessages.NO_READY_FOR_FIGHT, 1500)
            return false
        }

        val damageFromStrength: Int
        val attackName: String

        if (weapon != null &&
            (weapon.type == Weapon.WeaponType.KNIFE ||
                    weapon.type == Weapon.WeaponType.SWORD ||
                    weapon.type == Weapon.WeaponType.WARHAMMER)
        ) {
            damageFromStrength = weapon.weaponPower
            attackName = weapon.name

        } else {
            val nonNullHands = hands ?: return false
            damageFromStrength = nonNullHands.handsPower
            attackName = nonNullHands.name

        }

        val totalDamage = damageFromStrength + player.strength


        var targetX = player.levelX
        val targetY = player.levelY

        when (player.facing) {
            Direction.LEFT -> targetX -= 2
            Direction.RIGHT -> targetX += 2
            else -> return false
        }

        for (enemy in currentLevel.enemies) {
            if (enemy.isAlive && enemy.x == targetX && enemy.y == targetY) {
                enemy.takeDamage(totalDamage)


                if (!enemy.isAlive) {
                    handleEnemyDeath(enemy)
                    currentLevel.removeEnemy(enemy)
                }
                return true
            }
        }


        return false
    }

    private fun handleEnemyDeath(enemy: Enemy) {

        gameEngine.addExperienceFromEnemy(enemy)
        gameEngine.addTreasuresFromEnemy(enemy)

        if (enemy.enemyType == Enemy.EnemyType.BOSS) {
            Messages.show(GameMessages.PICKUP_CROWN, 5000)
        }

        val behavior = enemy.behavior
        if (!currentLevel.checkPositionPortalStone(enemy.x, enemy.y)) {
            val drop = behavior.dropGenerator(enemy)
            if (drop != null) {
                drop.setPosition(enemy.x, enemy.y)
                currentLevel.addItem(drop)

            }
        }
    }

    // ==================== ВЛОЖЕННЫЕ КЛАССЫ ====================

    class Projectile(
        @JvmField var x: Int,
        @JvmField var y: Int,
        val direction: Direction,
        val weapon: Weapon,
        val damage: Int,
        val isLaser: Boolean
    ) {
        val startX: Int = x
        val startY: Int = y
        var distance: Int = 0
            private set

        fun move() {
            when (direction) {
                Direction.LEFT -> x--
                Direction.RIGHT -> x++
                else -> {}
            }
        }

        fun increaseDistance() {
            distance++
        }

        fun isOutOfBounds(): Boolean = distance >= MAX_DISTANCE

        companion object {
            const val MAX_DISTANCE: Int = 30
        }
    }

    class Ray(
        @JvmField var startRayX: Int,
        @JvmField var startRayY: Int,
        @JvmField var endRayX: Int,
        @JvmField var endRayY: Int,
        @JvmField var weapon: Weapon
    )

    class AnnihilationWave(@JvmField var roomIndex: Int, @JvmField var weapon: Weapon)
}