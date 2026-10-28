package rougelite.BusinessLogicLayer.interfaces

interface AllocationObject {
    val totalNumberOfItems: Int
    val typeObject: String

    fun allocation()
}