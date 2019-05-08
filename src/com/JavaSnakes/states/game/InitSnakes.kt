package com.JavaSnakes.states.game

import com.JavaSnakes.snakes.BotSnake
import com.JavaSnakes.snakes.PlayerSnake
import com.JavaSnakes.snakes.SnakeBase
import com.JavaSnakes.util.Direction
import com.JavaSnakes.util.GridPos

import java.awt.Color
import java.util.ArrayList

class InitSnakes(private val mapWidth: Int, private val mapHeight: Int) {

    val snakes: MutableList<SnakeBase>

    private val snakeInitDir: Direction
        get() = when {
            snakes.size % 4 == 0 -> Direction.Right
            snakes.size % 4 == 1 -> Direction.Left
            snakes.size % 4 == 2 -> Direction.Down
            else -> Direction.Up
        }

    private val snakeInitPos: GridPos
        get() {
            val extra = snakes.size / 4

            var initPosX = 3 + 3 * extra
            var initPosY = 3 + 3 * extra
            if (snakes.size % 4 == 1 || snakes.size % 4 == 2) {
                initPosX = mapWidth - 4 - 3 * extra
            }
            if (snakes.size % 4 == 1 || snakes.size % 4 == 3) {
                initPosY = mapHeight - 4 - 3 * extra
            }

            return GridPos(initPosX, initPosY)
        }

    init {
        snakes = ArrayList()
    }

    fun addPlayerSnake(snakeColor: Color, ctrlUp: Int, ctrlDown: Int, ctrlLeft: Int, ctrlRight: Int) {
        snakes.add(PlayerSnake(
            snakeInitDir, snakeInitPos, snakeColor,
            ctrlUp, ctrlDown, ctrlLeft, ctrlRight
        ))
    }

    fun addBotSnakes(snakeColor: Color, count: Int) {
        for (i in 0 until count) {
            snakes.add(BotSnake(snakeInitDir, snakeInitPos, snakeColor))
        }
    }
}