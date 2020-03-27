package com.javasnakes.util

enum class Direction {
    Up,
    Down,
    Left,
    Right;

    fun opposite(): Direction {
        return when (this) {
            Up -> Down
            Down -> Up
            Left -> Right
            Right -> Left
        }
    }

    fun isOppositeOf(dir2: Direction) = dir2 === this.opposite()
}
