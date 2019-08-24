package com.JavaSnakes.snakes

import com.JavaSnakes.states.menu.PlayerSettings
import com.JavaSnakes.util.Direction
import com.JavaSnakes.util.GridPos

import java.util.HashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.exitProcess

class PlayerSnake(
        private var directionBuffer: Direction,
        initPos: GridPos,
        playerData: PlayerSettings.PlayerData
) : SnakeBase(directionBuffer, initPos, playerData.color) {
    companion object {
        private val NEXT_ID = AtomicInteger(1)
    }

    override val groupName = "Player"
    override val idInGroup = NEXT_ID.getAndIncrement()

    private val ctrlKeys: HashMap<Int, Direction> = HashMap()

    init {
        ctrlKeys[playerData.ctrls.up!!] = Direction.Up
        ctrlKeys[playerData.ctrls.down!!] = Direction.Down
        ctrlKeys[playerData.ctrls.left!!] = Direction.Left
        ctrlKeys[playerData.ctrls.right!!] = Direction.Right

        if (ctrlKeys.size != 4) exitProcess(1) // TODO: Properly handle duplicate control keys
    }

    override fun processDirection() {
        if (!direction.isOppositeOf(directionBuffer)) {
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
