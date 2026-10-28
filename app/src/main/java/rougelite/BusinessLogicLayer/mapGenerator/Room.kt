package rougelite.BusinessLogicLayer.mapGenerator

import rougelite.BusinessLogicLayer.mapGenerator.DoorCoordinates.Direction
import rougelite.BusinessLogicLayer.objects.Portal
import rougelite.BusinessLogicLayer.objects.PortalStone
import java.util.Random

class Room(
    override val roomIndex: Int,
    override val isCenterRoom: Boolean
) : DrawRoomsMap {

    override var x: Int = 0
        private set
    override var y: Int = 0
        private set

    override var height: Int = 0
    override var length: Int = 0
    override var tiles: Array<IntArray> = emptyArray()

    override var portal: Portal? = null
    override var portalStone: PortalStone? = null

    init {
        val rand = Random()
        height = MIN_HEIGHT + rand.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1)
        length = MIN_LENGTH + rand.nextInt(MAX_LENGTH - MIN_LENGTH + 1)

        tiles = Array(height) { i ->
            IntArray(length) { j ->
                if (i == 0 || i == height - 1 || j == 0 || j == length - 1) 1 else 0
            }
        }
    }

    fun setPosition(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    // Только для центральной комнаты
    fun generateCenterDoors() {
        val rand = Random()
        val doorCount = rand.nextInt(4) + 1
        val availableSides = mutableListOf(
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST
        )

        var i = 0
        while (i < doorCount && availableSides.isNotEmpty()) {
            val sideIndex = rand.nextInt(availableSides.size)
            val side = availableSides.removeAt(sideIndex)
            addDoorOnSide(side)
            i++
        }
    }

    fun generateNonCenterDoors() {
        if (isAdjacentToCenter()) {
            // Комнаты вокруг центра
            checkAndAddDoorIfCentralHas(Direction.NORTH)
            checkAndAddDoorIfCentralHas(Direction.SOUTH)
            checkAndAddDoorIfCentralHas(Direction.EAST)
            checkAndAddDoorIfCentralHas(Direction.WEST)
        } else {
            // Угловые комнаты
            addDoorIfNeeded(Direction.NORTH)
            addDoorIfNeeded(Direction.SOUTH)
            addDoorIfNeeded(Direction.EAST)
            addDoorIfNeeded(Direction.WEST)
        }
    }

    private fun isAdjacentToCenter(): Boolean {
        return roomIndex == 1 || roomIndex == 3 || roomIndex == 5 || roomIndex == 7
    }

    private fun checkAndAddDoorIfCentralHas(side: Direction) {
        if (!hasNeighborRoom(side)) {
            return
        }

        val neighborIndex = getNeighborIndex(side)

        if (neighborIndex == 4) {
            val oppositeSide = getOppositeSide(side)
            val centralDoor = DoorCoordinates.getDoor(4, oppositeSide)

            if (centralDoor != null) {
                addDoorOnSide(side)
            }
        } else {
            addDoorOnSide(side)
        }
    }

    private fun addDoorIfNeeded(side: Direction) {
        if (hasNeighborRoom(side)) {
            addDoorOnSide(side)
        }
    }

    private fun getNeighborIndex(side: Direction): Int {
        val gridX = roomIndex % 3
        val gridY = roomIndex / 3

        return when (side) {
            Direction.NORTH -> (gridY - 1) * 3 + gridX
            Direction.SOUTH -> (gridY + 1) * 3 + gridX
            Direction.EAST -> gridY * 3 + (gridX + 1)
            Direction.WEST -> gridY * 3 + (gridX - 1)
        }
    }

    private fun getOppositeSide(side: Direction): Direction {
        return when (side) {
            Direction.NORTH -> Direction.SOUTH
            Direction.SOUTH -> Direction.NORTH
            Direction.EAST -> Direction.WEST
            Direction.WEST -> Direction.EAST
        }
    }

    private fun hasNeighborRoom(side: Direction): Boolean {
        val gridX = roomIndex % 3
        val gridY = roomIndex / 3

        return when (side) {
            Direction.NORTH -> gridY > 0
            Direction.SOUTH -> gridY < 2
            Direction.EAST -> gridX < 2
            Direction.WEST -> gridX > 0
        }
    }

    private fun addDoorOnSide(side: Direction) {
        val rand = Random()
        val localX: Int
        val localY: Int
        val doorX: Int
        val doorY: Int

        when (side) {
            Direction.NORTH -> {
                localX = rand.nextInt(length - 2) + 1
                localY = 0
                doorX = x + localX
                doorY = y
            }
            Direction.SOUTH -> {
                localX = rand.nextInt(length - 2) + 1
                localY = height - 1
                doorX = x + localX
                doorY = y + height - 1
            }
            Direction.EAST -> {
                localX = length - 1
                localY = rand.nextInt(height - 2) + 1
                doorX = x + length - 1
                doorY = y + localY
            }
            Direction.WEST -> {
                localX = 0
                localY = rand.nextInt(height - 2) + 1
                doorX = x
                doorY = y + localY
            }
        }

        val door = DoorCoordinates.Door(roomIndex, doorX, doorY, side)
        DoorCoordinates.addDoor(door)
        tiles[localY][localX] = 2
    }

    override fun getTileAt(x: Int, y: Int): Int {
        val localX = x - this.x
        val localY = y - this.y
        if (localX !in 0 until length || localY !in 0 until height) {
            return -1
        }
        return tiles[localY][localX]
    }

    companion object {
        private const val MIN_HEIGHT = 8
        private const val MAX_HEIGHT = 14
        private const val MIN_LENGTH = 12
        private const val MAX_LENGTH = 20
    }
}

// TODO сделать секретную комнату дверь в которую открывается после убийства змее-мага!