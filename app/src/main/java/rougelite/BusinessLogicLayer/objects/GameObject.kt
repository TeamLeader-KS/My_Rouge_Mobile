package rougelite.BusinessLogicLayer.objects

abstract class GameObject {

    var x: Int = 0
        private set

    var y: Int = 0
        private set

    open fun setPosition(x: Int, y: Int) {
        this.x = x
        this.y = y
    }
}