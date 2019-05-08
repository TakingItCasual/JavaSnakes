package com.JavaSnakes.util

class GridPos {

    @JvmField var x: Int = 0
    @JvmField var y: Int = 0

    @JvmOverloads constructor(setX: Int = 0, setY: Int = 0) {
        x = setX
        y = setY
    }

    constructor(other: GridPos): this(other.x, other.y)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GridPos) return false
        return this.x == other.x && this.y == other.y
    }

    override fun hashCode(): Int {
        return x + y shl 16 // Hashes are unique as long as x and y can be represented as shorts
    }
}
