package com.JavaSnakes.snakes

import java.awt.Color
import java.util.HashMap

import com.JavaSnakes.util.Direction
import com.JavaSnakes.util.GridPos
import java.util.concurrent.atomic.AtomicInteger

class PlayerSnake(
        private var directionBuffer: Direction,
        initPos: GridPos,
        setColor: Color,
        ctrlUp: Int,
        ctrlDown: Int,
        ctrlLeft: Int,
        ctrlRight: Int
) : SnakeBase(directionBuffer, initPos, setColor) {
    companion object {
        private val NEXT_ID = AtomicInteger(1)
    }

    override val groupName = "Player"
    override val idInGroup = NEXT_ID.getAndIncrement()

    private val ctrlKeys: HashMap<Int, Direction> = HashMap()

    init {
        ctrlKeys[ctrlUp] = Direction.Up
        ctrlKeys[ctrlDown] = Direction.Down
        ctrlKeys[ctrlLeft] = Direction.Left
        ctrlKeys[ctrlRight] = Direction.Right

        if (ctrlKeys.size != 4) System.exit(1) // TODO: Properly handle duplicate control keys
    }

    override fun processDirection() {
        if (!directionsAreOpposite(direction, directionBuffer)) {
            direction = directionBuffer
        }
        directionBuffer = direction
    }

    fun bufferInput(inputKey: Int) {
        if (inputKey in ctrlKeys.keys) {
            directionBuffer = ctrlKeys[inputKey]!!
        }
    }
}
