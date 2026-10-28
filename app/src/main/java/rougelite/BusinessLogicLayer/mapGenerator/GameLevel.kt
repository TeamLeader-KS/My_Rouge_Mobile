package rougelite.BusinessLogicLayer.mapGenerator

import java.util.Arrays
import java.util.Random
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import rougelite.BusinessLogicLayer.characters.Enemy
import rougelite.BusinessLogicLayer.characters.Player
import rougelite.BusinessLogicLayer.characters.behavior.BossBehavior
import rougelite.BusinessLogicLayer.characters.behavior.GhostBehavior
import rougelite.BusinessLogicLayer.characters.behavior.OgreBehavior
import rougelite.BusinessLogicLayer.characters.behavior.SnakeMageBehavior
import rougelite.BusinessLogicLayer.characters.behavior.VampireBehavior
import rougelite.BusinessLogicLayer.characters.behavior.ZombieBehavior
import rougelite.BusinessLogicLayer.interfaces.EnemyBehavior
import rougelite.BusinessLogicLayer.objects.Ammo
import rougelite.BusinessLogicLayer.objects.GameObject
import rougelite.BusinessLogicLayer.objects.HealthPotion
import rougelite.BusinessLogicLayer.objects.Portal
import rougelite.BusinessLogicLayer.objects.PortalStone
import rougelite.BusinessLogicLayer.objects.Reinforce
import rougelite.BusinessLogicLayer.objects.Weapon
import rougelite.BusinessLogicLayer.logic.Settings

class GameLevel(var dungeonLevel: Int) {


    // ========== ТУМАН ВОЙНЫ ==========
    enum class MapExploration {
        UNKNOWN,
        VISITED,
        VISIBLE
    }

    val rooms: MutableList<DrawRoomsMap>
        get() = Companion.rooms

    var corridors: MutableList<Corridor> = ArrayList()
    var items: MutableList<GameObject> = ArrayList()

    var levelMap: Array<IntArray> = emptyArray()
    private val random: Random = Random()
    var portal: Portal? = null

    private var visitedRooms: BooleanArray = BooleanArray(9)
    private var openedCorridors: BooleanArray = BooleanArray(12)

    private var _enemies: MutableList<Enemy> = ArrayList()
    var enemies: MutableList<Enemy>
        get() = _enemies
        set(value) {
            _enemies = ArrayList(value)
        }

    private var lastRoom: LastRoom? = null

    // Размер игрового мира. Задаётся в buildLevel() и больше не зависит от размера экрана/терминала.
    private var worldWidth: Int = 0
    private var worldHeight: Int = 0
    private var explorationMap: Array<Array<MapExploration>>? = null

    init {
        DoorCoordinates.clear()
        Companion.rooms = ArrayList()
        generateRooms()
    }

    private fun createExplorationMap() {
        if (levelMap.isEmpty() || levelMap[0].isEmpty()) return
        val height = levelMap.size
        val width = levelMap[0].size
        explorationMap = Array(height) { Array(width) { MapExploration.UNKNOWN } }
    }

    fun transferVisibleToVisited(playerX: Int, playerY: Int, visionRadius: Int) {
        val map = explorationMap ?: return

        for (y in map.indices) {
            for (x in map[0].indices) {
                if (map[y][x] == MapExploration.VISIBLE) {
                    map[y][x] = MapExploration.VISITED
                }
            }
        }

        markVisibleArea(playerX, playerY, visionRadius)

        for (area in rooms) {
            val xStart = area.x
            val yStart = area.y
            val xEnd = xStart + area.length
            val yEnd = yStart + area.height

            if (playerX in xStart until xEnd && playerY in yStart until yEnd) {
                for (y in yStart until yEnd) {
                    for (x in xStart until xEnd) {
                        if (y in levelMap.indices && x in levelMap[0].indices) {
                            map[y][x] = MapExploration.VISITED
                        }
                    }
                }
                break
            }
        }
    }

    private fun markVisibleArea(centerX: Int, centerY: Int, radius: Int) {
        setVisible(centerX, centerY)

        for (dy in -radius..radius) {
            for (dx in -radius..radius) {
                if (abs(dx) == radius || abs(dy) == radius) {
                    castRay(centerX, centerY, centerX + dx, centerY + dy)
                }
            }
        }
    }

    private fun castRay(startX: Int, startY: Int, endX: Int, endY: Int) {
        var currX = startX
        var currY = startY

        val dx = abs(endX - currX)
        val dy = -abs(endY - currY)
        val sx = if (currX < endX) 1 else -1
        val sy = if (currY < endY) 1 else -1
        var err = dx + dy

        while (true) {
            setVisible(currX, currY)
            if (currX == endX && currY == endY) break

            val e2 = 2 * err
            if (e2 >= dy) {
                err += dy
                currX += sx
            }
            if (e2 <= dx) {
                err += dx
                currY += sy
            }

            if (currY !in levelMap.indices ||
                currX !in levelMap[0].indices ||
                levelMap[currY][currX] != -1
            ) {
                break
            }
        }
    }

    private fun setVisible(x: Int, y: Int) {
        val map = explorationMap ?: return
        if (y in levelMap.indices && x in levelMap[0].indices) {
            map[y][x] = MapExploration.VISIBLE
        }
    }

    fun openCellsInRadius(x: Int, y: Int, radius: Int) {
        val map = explorationMap ?: return
        for (dy in -radius..radius) {
            for (dx in -radius..radius) {
                val nx = x + dx
                val ny = y + dy
                if (ny in levelMap.indices && nx in levelMap[0].indices) {
                    map[ny][nx] = MapExploration.VISIBLE
                }
            }
        }
    }

    fun openRoom(roomIndex: Int) {
        if (roomIndex !in rooms.indices) return
        val map = explorationMap ?: return

        val area = rooms[roomIndex]
        if (area is Room) {
            for (y in area.y until area.y + area.height) {
                for (x in area.x until area.x + area.length) {
                    if (y in levelMap.indices && x in levelMap[0].indices) {
                        map[y][x] = MapExploration.VISITED
                    }
                }
            }
        }
    }

    fun isExplored(x: Int, y: Int): Boolean {
        if (y !in levelMap.indices || x !in levelMap[0].indices) return false
        val e = explorationMap?.getOrNull(y)?.getOrNull(x) ?: return false
        return e == MapExploration.VISITED || e == MapExploration.VISIBLE
    }

    fun getExploration(x: Int, y: Int): MapExploration {
        if (y !in levelMap.indices || x !in levelMap[0].indices) {
            return MapExploration.UNKNOWN
        }
        return explorationMap?.getOrNull(y)?.getOrNull(x) ?: MapExploration.UNKNOWN
    }

    fun isVisible(x: Int, y: Int): Boolean {
        return getExploration(x, y) == MapExploration.VISIBLE
    }

    fun isVisited(x: Int, y: Int): Boolean {
        val e = getExploration(x, y)
        return e == MapExploration.VISITED || e == MapExploration.VISIBLE
    }

    fun clearExploration() {
        createExplorationMap()
    }

    private fun generateRooms() {
        for (row in 0 until 3) {
            for (col in 0 until 3) {
                val index = row * 3 + col
                val isCenter = (row == 1 && col == 1)
                rooms.add(Room(index, isCenter))
            }
        }
    }

    fun buildLevel(windowWidth: Int, windowHeight: Int) {
        // windowWidth/windowHeight - это теперь размер ИГРОВОГО МИРА (см. GameConfig), а не терминала
        worldWidth = windowWidth
        worldHeight = windowHeight

        DoorCoordinates.clear()

        Companion.rooms = ArrayList()
        corridors = ArrayList()
        items = ArrayList()

        if (dungeonLevel == 21) {
            val lastRoom = LastRoom(4, true)
            this.lastRoom = lastRoom
            lastRoom.setPosition(windowWidth / 2 - 15, windowHeight / 2 - 5)
            rooms.add(lastRoom)

            val portal =
                Portal(lastRoom.x + lastRoom.length / 2 - 10, lastRoom.y + lastRoom.height / 2)
            this.portal = portal
            lastRoom.portal = portal

            buildLevelMapForLastRoom(lastRoom)
        } else {
            generateRooms()
            calculateAndSetRoomPositions(windowWidth, windowHeight)
            generateDoors()
            generateCorridorsBetweenDoors()
            setupPortalAndStone()
            buildLevelMap()
        }

        val occupiedPositions = ArrayList<IntArray>()
        for (area in rooms) {
            if (area is Room) {
                val stone = area.portalStone
                if (stone != null) {
                    occupiedPositions.add(intArrayOf(stone.stoneX, stone.stoneY))
                }
            }
        }

        allocationWeapon(occupiedPositions)
        allocationAmmo(occupiedPositions)
        allocationHealthPotion(occupiedPositions)
        allocationReinforce(occupiedPositions)
        createExplorationMap()
    }

    /**
     * Раскладывает 9 комнат по сетке 3x3 внутри мира размером worldWidth x worldHeight.
     *
     * Каждая комната живёт в своей ячейке (мир делится на 3 колонки и 3 ряда) и стоит ближе к её
     * центру с небольшим случайным смещением. Между комнатами соседних ячеек всегда остаётся
     * зазор не меньше ROOM_GAP_X / ROOM_GAP_Y, поэтому комнаты не пересекаются, а коридоры
     * между дверьми никогда не проходят сквозь чужие комнаты. От границ мира тоже есть отступ.
     */
//    fun calculateAndSetRoomPositions(windowWidth: Int, windowHeight: Int) {
//        val rand = Random()
//        val cellWidth = windowWidth / 3
//        val cellHeight = windowHeight / 3
//
//        // Задаем отступ в клетках (например, 10 клеток по краям)
//        val marginX = 20
//
//        for (area in rooms) {
//            if (area !is Room) continue
//
//            val col = area.roomIndex % 3
//            val row = area.roomIndex / 3
//
//            // свободное место в ячейке: минус обязательный зазор и по 1 клетке отступа от границ
//            val slackX = max(0, cellWidth - ROOM_GAP_X - area.length + marginX)
//            val slackY = max(0, cellHeight - ROOM_GAP_Y - area.height - 2)
//
//            val jitterX = min(slackX / 2, MAX_JITTER_X)
//            val jitterY = min(slackY / 2, MAX_JITTER_Y)
//
//            val offsetX = 1 + slackX / 2 + rand.nextInt(2 * jitterX + 1) - jitterX - marginX
//            val offsetY = 1 + slackY / 2 + rand.nextInt(2 * jitterY + 1) - jitterY
//
//            area.setPosition(col * cellWidth + offsetX, row * cellHeight + offsetY)
//        }
//    }

    fun calculateAndSetRoomPositions(windowWidth: Int, windowHeight: Int) {
        val rand = Random()

        // 1. Задаем жесткий отступ по краям карты
        val marginX = 20
        val marginY = 5

        // 2. Вычисляем ширину одной ячейки (делим на 3 оставшееся место)
        val usableWidth = windowWidth - (marginX)
        val usableHeight = windowHeight - (marginY)
        val cellWidth = usableWidth / 3
        val cellHeight = usableHeight / 3

        for (area in rooms) {
            if (area !is Room) continue

            val col = area.roomIndex % 3
            val row = area.roomIndex / 3

            // 3. Свободное место внутри ячейки (чтобы комнаты не прилипали к границам ячеек)
            val slackX = max(0, cellWidth - ROOM_GAP_X - area.length - 2)
            val slackY = max(0, cellHeight - ROOM_GAP_Y - area.height - 2)

            val jitterX = min(slackX / 2, MAX_JITTER_X)
            val jitterY = min(slackY / 2, MAX_JITTER_Y)

            // 4. Позиция = Отступ + Позиция ячейки + Случайный сдвиг внутри ячейки
            val offsetX = marginX + col * cellWidth + slackX / 2 + rand.nextInt(2 * jitterX + 1) - jitterX
            val offsetY = marginY + row * cellHeight + slackY / 2 + rand.nextInt(2 * jitterY + 1) - jitterY

            area.setPosition(offsetX, offsetY)
        }
    }

    private fun generateDoors() {
        for (area in rooms) {
            if (area is Room && area.roomIndex == 4) {
                area.generateCenterDoors()
                break
            }
        }

        for (area in rooms) {
            if (area is Room && area.roomIndex != 4) {
                area.generateNonCenterDoors()
            }
        }

        println("=== ДВЕРИ ПОСЛЕ ГЕНЕРАЦИИ ===")
        for (door in DoorCoordinates.getDoors()) {
            println("Комната ${door.roomIndex} | сторона ${door.side} | (${door.x}, ${door.y})")
        }
    }

    private fun generateCorridorsBetweenDoors() {
        for (i in rooms.indices) {
            for (j in i + 1 until rooms.size) {
                if (areRoomsAdjacent(i, j)) {
                    val door1 = findDoorFacingRoom(i, j)
                    val door2 = findDoorFacingRoom(j, i)
                    if (door1 != null && door2 != null) {
                        val path = buildCorridorBetweenDoors(door1, door2)
                        corridors.add(Corridor(i, j, path))
                    }
                }
            }
        }
    }

    private fun areRoomsAdjacent(roomA: Int, roomB: Int): Boolean {
        val ax = roomA % 3
        val ay = roomA / 3
        val bx = roomB % 3
        val by = roomB / 3
        return (abs(ax - bx) + abs(ay - by)) == 1
    }

    private fun findDoorFacingRoom(fromRoom: Int, toRoom: Int): DoorCoordinates.Door? {
        val ax = fromRoom % 3
        val ay = fromRoom / 3
        val bx = toRoom % 3
        val by = toRoom / 3

        val direction =
            when {
                bx > ax -> DoorCoordinates.Direction.EAST
                bx < ax -> DoorCoordinates.Direction.WEST
                by > ay -> DoorCoordinates.Direction.SOUTH
                by < ay -> DoorCoordinates.Direction.NORTH
                else -> null
            }
                ?: return null

        return DoorCoordinates.getDoors().firstOrNull {
            it.roomIndex == fromRoom && it.side == direction
        }
    }

    private fun buildCorridorBetweenDoors(
        door1: DoorCoordinates.Door,
        door2: DoorCoordinates.Door
    ): List<IntArray> {
        val path = ArrayList<IntArray>()
        val x1 = door1.x
        val y1 = door1.y
        val x2 = door2.x
        val y2 = door2.y
        val side1 = door1.side
        var currentX = x1
        var currentY = y1

        when (side1) {
            DoorCoordinates.Direction.NORTH -> currentY--
            DoorCoordinates.Direction.SOUTH -> currentY++
            DoorCoordinates.Direction.EAST -> currentX++
            DoorCoordinates.Direction.WEST -> currentX--
        }
        path.add(intArrayOf(currentX, currentY))

        val midX = (x1 + x2) / 2
        val midY = (y1 + y2) / 2

        if (side1 == DoorCoordinates.Direction.EAST || side1 == DoorCoordinates.Direction.WEST) {
            while (currentX != midX) {
                currentX += if (currentX < midX) 1 else -1
                path.add(intArrayOf(currentX, currentY))
            }
            while (currentY != midY) {
                currentY += if (currentY < midY) 1 else -1
                path.add(intArrayOf(currentX, currentY))
            }
        } else {
            while (currentY != midY) {
                currentY += if (currentY < midY) 1 else -1
                path.add(intArrayOf(currentX, currentY))
            }
            while (currentX != midX) {
                currentX += if (currentX < midX) 1 else -1
                path.add(intArrayOf(currentX, currentY))
            }
        }

        if (side1 == DoorCoordinates.Direction.EAST || side1 == DoorCoordinates.Direction.WEST) {
            while (currentY != y2) {
                currentY += if (currentY < y2) 1 else -1
                path.add(intArrayOf(currentX, currentY))
            }
        } else {
            while (currentX != x2) {
                currentX += if (currentX < x2) 1 else -1
                path.add(intArrayOf(currentX, currentY))
            }
        }

        while (currentX != x2 || currentY != y2) {
            if (currentX != x2) currentX += if (currentX < x2) 1 else -1
            if (currentY != y2) currentY += if (currentY < y2) 1 else -1
            path.add(intArrayOf(currentX, currentY))
        }

        if (path.isNotEmpty()) {
            val last = path[path.size - 1]
            if (last[0] == x2 && last[1] == y2) {
                path.removeAt(path.size - 1)
            }
        }
        return path
    }

    private fun buildLevelMap() {
        var maxX = 0
        var maxY = 0

        for (room in rooms) {
            maxX = max(maxX, room.x + room.length)
            maxY = max(maxY, room.y + room.height)
        }

        for (c in corridors) {
            for (point in c.path) {
                maxX = max(maxX, point[0] + 1)
                maxY = max(maxY, point[1] + 1)
            }
        }

        levelMap = Array(max(maxY, worldHeight)) { IntArray(max(maxX, worldWidth)) { -1 } }

        for (room in rooms) {
            val tiles = room.tiles
            for (i in 0 until room.height) {
                for (j in 0 until room.length) {
                    levelMap[room.y + i][room.x + j] = tiles[i][j]
                }
            }
        }

        for (c in corridors) {
            for (point in c.path) {
                val px = point[0]
                val py = point[1]
                if (py in levelMap.indices && px in levelMap[0].indices) {
                    if (levelMap[py][px] != 2 && levelMap[py][px] != 1) {
                        levelMap[py][px] = 3
                    }
                }
            }
        }
    }

    private fun setupPortalAndStone() {
        val centerRoom = rooms.filterIsInstance<Room>().firstOrNull { it.roomIndex == 4 }

        if (centerRoom != null) {
            val newPortal =
                Portal(
                    centerRoom.x + centerRoom.length / 2,
                    centerRoom.y + centerRoom.height / 2
                )
            centerRoom.portal = newPortal
            this.portal = newPortal
        }

        for (area in rooms) {
            when (area) {
                is Room -> {
                    if (area.roomIndex != 4) {
                        val stone =
                            PortalStone(
                                area.roomIndex,
                                area.x,
                                area.y,
                                area.length,
                                area.height
                            )
                        area.portalStone = stone
                    }
                }
                is LastRoom -> {
                    val stone =
                        PortalStone(area.roomIndex, area.x, area.y, area.length, area.height)
                    area.portalStone = stone
                }
            }
        }
    }

    private fun allocationWeapon(occupiedPositions: MutableList<IntArray>) {
        val rand = Random()
        val weaponTypes = Weapon.WeaponType.values()
        val randomCount = rand.nextInt(2) + Settings.cauntWeapon

        for (i in 0 until randomCount) {
            val area = getRandomNonCenterRoom(rand)
            if (area is Room) {
                val type = weaponTypes[rand.nextInt(weaponTypes.size)]
                val weapon = Weapon(type)
                checkingItemAllocation(weapon, area, occupiedPositions)
            }
        }
    }

    private fun allocationAmmo(occupiedPositions: MutableList<IntArray>) {
        val rand = Random()
        val ammoTypes = Ammo.AmmoType.values()
        val randomCount = rand.nextInt(3) + Settings.cauntAmmo

        for (i in 0 until randomCount) {
            val area = getRandomNonCenterRoom(rand)
            if (area is Room) {
                val type = ammoTypes[rand.nextInt(ammoTypes.size)]
                val ammo = Ammo(type)
                checkingItemAllocation(ammo, area, occupiedPositions)
            }
        }
    }

    private fun allocationHealthPotion(occupiedPositions: MutableList<IntArray>) {
        val rand = Random()
        val healthPotionTypes = HealthPotion.HealthPotionType.values()
        val randomCount = rand.nextInt(3) + Settings.cauntHealthPotion

        for (i in 0 until randomCount) {
            val area = getRandomNonCenterRoom(rand)
            if (area is Room) {
                val type = healthPotionTypes[rand.nextInt(healthPotionTypes.size)]
                val potion = HealthPotion(type)
                checkingItemAllocation(potion, area, occupiedPositions)
            }
        }
    }

    private fun allocationReinforce(occupiedPositions: MutableList<IntArray>) {
        val rand = Random()
        val reinforceTypes = Reinforce.ReinforceType.values()
        val randomCount = rand.nextInt(2) + Settings.cauntReinforce

        for (i in 0 until randomCount) {
            val area = getRandomNonCenterRoom(rand)
            if (area is Room) {
                val type = reinforceTypes[rand.nextInt(reinforceTypes.size)]
                val reinforce = Reinforce(type)
                checkingItemAllocation(reinforce, area, occupiedPositions)
            }
        }
    }

    fun addItem(item: GameObject) {
        items.add(item)
    }

    fun removeItem(item: GameObject) {
        items.remove(item)
    }

    fun getRoomAt(y: Int, x: Int): DrawRoomsMap? {
        return rooms.firstOrNull {
            x >= it.x && x < it.x + it.length && y >= it.y && y < it.y + it.height
        }
    }

    fun getItemAt(x: Int, y: Int): GameObject? {
        return items.firstOrNull { it.x == x && it.y == y }
    }

    fun visitRoom(roomIndex: Int) {
        if (roomIndex in visitedRooms.indices) {
            visitedRooms[roomIndex] = true
        }
    }

    fun isRoomVisited(roomIndex: Int): Boolean {
        return roomIndex in visitedRooms.indices && visitedRooms[roomIndex]
    }

    fun openCorridor(corridorIndex: Int) {
        if (corridorIndex in openedCorridors.indices) {
            openedCorridors[corridorIndex] = true
        }
    }

    fun isCorridorOpened(corridorIndex: Int): Boolean {
        return corridorIndex in openedCorridors.indices && openedCorridors[corridorIndex]
    }

    fun getCurrentRoomIndex(player: Player): Int {
        return getCurrentRoomIndexByCoordinates(player.levelX, player.levelY)
    }

    fun getCurrentRoomIndexByCoordinates(x: Int, y: Int): Int {
        for (i in rooms.indices) {
            val area = rooms[i]
            if (area is Room) {
                if (x >= area.x &&
                    x < area.x + area.length &&
                    y >= area.y &&
                    y < area.y + area.height
                ) {
                    return i
                }
            }
        }
        return -1
    }

    fun clearVisibility() {
        Arrays.fill(visitedRooms, false)
        Arrays.fill(openedCorridors, false)
    }

    fun addEnemy(enemy: Enemy) {
        _enemies.add(enemy)
    }

    fun removeEnemy(enemy: Enemy) {
        println("removeEnemy: удаляем $enemy")
        val removed = _enemies.remove(enemy)
        println("Удалён: $removed, осталось: ${_enemies.size}")
    }

    private fun createBehavior(type: Enemy.EnemyType): EnemyBehavior {
        return when (type) {
            Enemy.EnemyType.ZOMBIE -> ZombieBehavior()
            Enemy.EnemyType.VAMPIRE -> VampireBehavior()
            Enemy.EnemyType.GHOST -> GhostBehavior()
            Enemy.EnemyType.OGRE -> OgreBehavior()
            Enemy.EnemyType.SNAKE_MAGE -> SnakeMageBehavior()
            else -> ZombieBehavior()
        }
    }

    data class RisingEnemies<T>(val type: T, val chance: Int)

    private fun getRandomEnemyType(dungeonLevel: Int, random: Random): Enemy.EnemyType {
        val zombieChance = 90 - dungeonLevel * 5
        val vampireChance =
            if (dungeonLevel < 10) {
                50 + dungeonLevel * 5
            } else {
                65 - dungeonLevel * 2
            }
        val ogreChance = -15 + dungeonLevel * 3
        val ghostChance = -25 + dungeonLevel * 4
        val snakeMageChance = -35 + dungeonLevel * 5

        val risingEnemies =
            listOf(
                RisingEnemies(Enemy.EnemyType.ZOMBIE, zombieChance),
                RisingEnemies(Enemy.EnemyType.VAMPIRE, vampireChance),
                RisingEnemies(Enemy.EnemyType.GHOST, ghostChance),
                RisingEnemies(Enemy.EnemyType.OGRE, ogreChance),
                RisingEnemies(Enemy.EnemyType.SNAKE_MAGE, snakeMageChance)
            )

        return chooseRisingEnemies(risingEnemies, random)
    }

    private fun chooseRisingEnemies(
        risingEnemies: List<RisingEnemies<Enemy.EnemyType>>,
        random: Random
    ): Enemy.EnemyType {
        if (risingEnemies.isEmpty()) {
            return Enemy.EnemyType.ZOMBIE
        }

        val totalChance = risingEnemies.sumOf { it.chance }
        if (totalChance <= 0) {
            return risingEnemies[0].type
        }

        var roll = random.nextInt(totalChance)
        for (challenger in risingEnemies) {
            roll -= challenger.chance
            if (roll < 0) {
                return challenger.type
            }
        }
        return risingEnemies.last().type
    }

    fun generateEnemies(dungeonLevel: Int) {
        val rand = Random()

        if (dungeonLevel == 21) {
            val area = rooms.firstOrNull()
            if (area is LastRoom) {
                val boss =
                    Enemy(
                        Enemy.EnemyType.BOSS,
                        area.bossPositionX,
                        area.bossPositionY,
                        dungeonLevel,
                        BossBehavior()
                    )
                addEnemy(boss)
            }
            return
        }

        for (area in rooms) {
            if (area is Room) {
                if (area.roomIndex == 4) continue

                val enemyCount = rand.nextInt(3) + 2
                for (j in 0 until enemyCount) {
                    val x = area.x + 1 + rand.nextInt(area.length - 2)
                    val y = area.y + 1 + rand.nextInt(area.height - 2)

                    val type = getRandomEnemyType(dungeonLevel, rand)
                    val behavior = createBehavior(type)
                    val enemy = Enemy(type, x, y, dungeonLevel, behavior)
                    addEnemy(enemy)
                }
            }
        }
    }

    private fun isOccupied(x: Int, y: Int, occupiedPositions: List<IntArray>): Boolean {
        return occupiedPositions.any { it[0] == x && it[1] == y }
    }

    private fun checkingItemAllocation(
        item: GameObject,
        room: Room,
        occupiedPositions: MutableList<IntArray>
    ): Boolean {
        val rand = Random()
        for (attempt in 0 until 50) {
            val x = room.x + 1 + rand.nextInt(room.length - 2)
            val y = room.y + 1 + rand.nextInt(room.height - 2)
            if (!isOccupied(x, y, occupiedPositions)) {
                item.setPosition(x, y)
                items.add(item)
                occupiedPositions.add(intArrayOf(x, y))
                return true
            }
        }
        return false
    }

    private fun getRandomNonCenterRoom(rand: Random): DrawRoomsMap? {
        val validRooms = rooms.filterIsInstance<Room>().filter { it.roomIndex != 4 }
        if (validRooms.isEmpty()) return null
        return validRooms[rand.nextInt(validRooms.size)]
    }

    fun checkPositionPortalStone(x: Int, y: Int): Boolean {
        for (area in rooms) {
            if (area is Room) {
                val stone = area.portalStone
                if (stone != null && stone.stoneX == x && stone.stoneY == y) {
                    return true
                }
            }
        }
        return false
    }

    private fun buildLevelMapForLastRoom(lastRoom: LastRoom) {
        val startX = lastRoom.x
        val startY = lastRoom.y
        val roomHeight = lastRoom.height
        val roomLength = lastRoom.length

        val mapHeight = max(startY + roomHeight, worldHeight)
        val mapWidth = max(startX + roomLength, worldWidth)

        levelMap = Array(mapHeight) { IntArray(mapWidth) { -1 } }

        val tiles = lastRoom.tiles
        for (i in 0 until roomHeight) {
            for (j in 0 until roomLength) {
                levelMap[startY + i][startX + j] = tiles[i][j]
            }
        }
    }

    fun getExplorationMapForSave(): Array<IntArray>? {
        val map = explorationMap ?: return null
        val height = map.size
        val width = map[0].size
        val result = Array(height) { IntArray(width) }

        for (y in 0 until height) {
            for (x in 0 until width) {
                result[y][x] = map[y][x].ordinal
            }
        }
        return result
    }

    fun setExplorationMap(savedMap: Array<IntArray>?) {
        if (savedMap == null || savedMap.isEmpty()) return
        val height = savedMap.size
        val width = savedMap[0].size
        val values = MapExploration.values()

        explorationMap =
            Array(height) { y ->
                Array(width) { x ->
                    val v = savedMap[y][x]
                    if (v in values.indices) values[v] else MapExploration.UNKNOWN
                }
            }
    }

    companion object {
        // минимальный зазор между комнатами соседних ячеек (место под коридор)
        private const val ROOM_GAP_X = 6
        private const val ROOM_GAP_Y = 4

        // насколько комната может смещаться от центра своей ячейки
        private const val MAX_JITTER_X = 4
        private const val MAX_JITTER_Y = 2

        @JvmField var rooms: MutableList<DrawRoomsMap> = ArrayList()

        @JvmStatic
        fun getRoomByIndex(index: Int): DrawRoomsMap? {
            return rooms.firstOrNull { it.roomIndex == index }
        }
    }
}