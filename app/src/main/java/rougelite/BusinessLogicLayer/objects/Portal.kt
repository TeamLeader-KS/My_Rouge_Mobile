package rougelite.BusinessLogicLayer.objects

class Portal(val x: Int, val y: Int) {

    private val stonesInserted: BooleanArray = BooleanArray(9)
    var isOpen: Boolean = false

    fun insertStone(roomIndex: Int) {
        if (roomIndex in 0..8 && roomIndex != 4) {
            stonesInserted[roomIndex] = true
        }
    }

    fun isFullyActivated(): Boolean {
        for (i in 0..8) {
            if (i != 4 && !stonesInserted[i]) {
                return false
            }
        }
        return true
    }

    fun hasStone(roomIndex: Int): Boolean {
        return stonesInserted[roomIndex]
    }

    // Сохранено для совместимости с кодом, вызывающим portal.portalOpen(true)
    fun portalOpen(open: Boolean) {
        this.isOpen = open
    }

    fun getInsertedStones(): List<Int> {
        val insertedIndices = ArrayList<Int>()
        for (i in stonesInserted.indices) {
            if (stonesInserted[i]) {
                insertedIndices.add(i)
            }
        }
        return insertedIndices
    }
}