package rougelite.BusinessLogicLayer.mapGenerator

object DoorCoordinates {

    enum class Direction {
        NORTH, SOUTH, EAST, WEST
    }

    data class Door(
        val roomIndex: Int,
        val x: Int,
        val y: Int,
        val side: Direction
    )

    private val doors: MutableList<Door> = ArrayList()

    @JvmStatic
    fun addDoor(door: Door) {
        doors.add(door)
    }

    @JvmStatic
    fun getDoors(): List<Door> {
        return ArrayList(doors)
    }

    @JvmStatic
    fun clear() {
        doors.clear()
    }

    @JvmStatic
    fun getDoor(roomIndex: Int, side: Direction): Door? {
        return doors.firstOrNull { it.roomIndex == roomIndex && it.side == side }
    }
}