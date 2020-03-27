package com.javasnakes.states.game

import com.javasnakes.snakes.BotSnake
import com.javasnakes.snakes.PlayerSnake
import com.javasnakes.snakes.SnakeBase
import com.javasnakes.states.menu.PlayerSettings
import com.javasnakes.util.Direction
import com.javasnakes.util.GridPos

import java.awt.Color
import java.util.concurrent.ThreadLocalRandom

class InitSnakes(private val mapWidth: Int, private val mapHeight: Int) {
    val snakes = arrayListOf<SnakeBase>()

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

    fun addPlayerSnake(playerData: PlayerSettings.PlayerData) {
        snakes.add(PlayerSnake(snakeInitDir, snakeInitPos, playerData))
    }

    fun addBotSnakes(count: Int) {
        var r: Int
        var g: Int
        var b: Int
        for (i in 0 until count) {
            while (true) {
                r = ThreadLocalRandom.current().nextInt(256)
                g = ThreadLocalRandom.current().nextInt(256)
                b = ThreadLocalRandom.current().nextInt(256)
                if (listOf(r, g, b).min()!! + 100 < listOf(r, g, b).max()!!) break
            }
            snakes.add(BotSnake(snakeInitDir, snakeInitPos, Color(r, g, b)))
        }
    }
}
