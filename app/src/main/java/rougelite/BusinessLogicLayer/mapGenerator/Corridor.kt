package rougelite.BusinessLogicLayer.mapGenerator

data class Corridor(
    val fromRoom: Int,
    val toRoom: Int,
    val path: List<IntArray>
)