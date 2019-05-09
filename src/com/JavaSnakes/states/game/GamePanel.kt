package com.JavaSnakes.states.game

import com.JavaSnakes.Board
import com.JavaSnakes.Main
import com.JavaSnakes.snakes.PlayerSnake
import com.JavaSnakes.snakes.SnakeBase
import com.JavaSnakes.states.menu.MenuPanel
import com.JavaSnakes.util.GridPos
import com.JavaSnakes.util.MenuCard

import java.awt.CardLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.Toolkit
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class GamePanel(
        private val owner: Main,
        private val delay: Int,
        private val cellSize: Int,
        setMapW: Int,
        setMapH: Int,
        includeWalls: Boolean,
        setSnakes: List<SnakeBase>
) : Runnable {
    var mainPanel: JPanel = JPanel()

    private val cardLayout: CardLayout

    private val mainGamePanel: MainGamePanel

    private var continueButton: JButton = JButton("Continue")
    private var quitToMenuButton: JButton = JButton("Quit to main menu")

    private var backToMainButton: JButton = JButton("Back to main menu")

    private val miniCell: Int
    private val miniOffset: Int

    private val board: Board

    private var inGame: Boolean = false
    private var isPaused: Boolean = false
    private val animator: Thread

    init {
        mainPanel.layout = CardLayout()

        mainGamePanel = MainGamePanel()
        mainPanel.add(mainGamePanel, "game card")
        createEscapeCard()
        createEndCard()

        cardLayout = mainPanel.layout as CardLayout
        miniOffset = 1
        miniCell = cellSize - 2 * miniOffset

        board = Board(setMapW, setMapH, includeWalls)
        mainPanel.preferredSize = Dimension(board.width * cellSize, board.height * cellSize)
        SnakeBase.board = board

        board.snakes.addAll(setSnakes)
        board.liveSnakes.addAll(setSnakes)

        board.createFood()

        inGame = true
        animator = Thread(this)
        animator.start()
    }

    private fun createEscapeCard() {
        continueButton.addActionListener { toGameCard() }
        quitToMenuButton.addActionListener { toMainMenu() }

        val gridBag = MenuCard()
        gridBag.addInGrid(continueButton, 0, 0)
        gridBag.addInGrid(quitToMenuButton, 1, 0)

        mainPanel.add(gridBag, "escape card")
    }

    private fun createEndCard() {
        backToMainButton.addActionListener { toMainMenu() }
        val gameOverLabel = JLabel("Game Over")

        val gridBag = MenuCard()
        gridBag.addInGrid(backToMainButton, 0, 0)
        gridBag.addInGrid(gameOverLabel, 1, 0)

        mainPanel.add(gridBag, "end card")
    }

    private fun toMainMenu() {
        owner.changePanel(MenuPanel(owner).mainPanel)
    }

    private fun toGameCard() {
        isPaused = false
        cardLayout.show(mainPanel, "game card")
        mainGamePanel.requestFocus()
    }

    private fun toEscapeCard() {
        isPaused = true
        cardLayout.show(mainPanel, "escape card")
        continueButton.requestFocus()
    }

    // TODO: Add scores
    private fun toEndCard() {
        cardLayout.show(mainPanel, "end card")
        backToMainButton.requestFocus()
    }

    override fun run() {
        var beforeTime = System.currentTimeMillis()
        frameDelay(beforeTime)

        while (inGame) {
            beforeTime = System.currentTimeMillis()

            if (!isPaused) {
                gameLogic()
                mainPanel.paintImmediately(mainPanel.bounds)
            }

            frameDelay(beforeTime)
        }
        toEndCard()
    }

    private fun gameLogic() {
        for (snake in board.liveSnakes)
            snake.processDirection()
        for (snake in board.liveSnakes)
            snake.moveHead()

        board.checkCollisions()
        board.killCollidedSnakes()
        if (board.liveSnakes.size == 0) inGame = false

        board.checkFood()
        for (snake in board.liveSnakes)
            snake.removeTailEnd()
    }

    private fun frameDelay(beforeTime: Long) {
        val sleep = delay - (System.currentTimeMillis() - beforeTime)
        if (sleep > 0) {
            try {
                Thread.sleep(sleep)
            } catch (e: InterruptedException) {
            }
        }
    }

    private inner class MainGamePanel : JPanel() {
        init {
            addKeyListener(Input())
            background = Color.darkGray
            isFocusable = true
        }

        public override fun paintComponent(g: Graphics) {
            super.paintComponent(g)

            if (inGame) {
                drawFood(g)
                drawSnakes(g)
                drawWalls(g)
            } else {
                drawGameOver(g)
            }

            Toolkit.getDefaultToolkit().sync()
            g.dispose()
        }

        private fun drawFood(g: Graphics) {
            g.color = Color.red
            drawMiniSquare(g, board.foodPos)
        }

        private fun drawSnakes(g: Graphics) {
            for (snake in board.liveSnakes) {
                g.color = snake.color

                drawFullSquare(g, snake.headPos())
                val iter = snake.coords.listIterator(1)
                while (iter.hasNext()) {
                    drawMiniSquare(g, iter.next())
                }
            }
        }

        private fun drawWalls(g: Graphics) {
            g.color = Color.black
            for (x in 0 until board.width) {
                for (y in 0 until board.height) {
                    if (board.walls[x][y]) {
                        drawFullSquare(g, x, y)
                    }
                }
            }
        }

        private fun drawFullSquare(g: Graphics, coord: GridPos) {
            drawFullSquare(g, coord.x, coord.y)
        }

        private fun drawFullSquare(g: Graphics, x: Int, y: Int) {
            g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize)
        }

        private fun drawMiniSquare(g: Graphics, coord: GridPos) {
            g.fillRect(coord.x * cellSize + miniOffset, coord.y * cellSize + miniOffset, miniCell, miniCell)
        }

        private fun drawGameOver(g: Graphics) {
            val small = Font("Helvetica", Font.BOLD, 14)
            val metr = getFontMetrics(small)

            g.color = Color.white
            g.font = small
            val fontHeight = g.fontMetrics.height

            val msgs = arrayOfNulls<String>(board.snakes.size + 1)
            msgs[0] = "Game Over"
            for (i in board.snakes.indices) {
                msgs[i + 1] = "Snake " + board.snakes[i].id + ": " + board.snakes[i].score
            }

            val yOffset = fontHeight * (board.snakes.size / 2)
            for (i in msgs.indices) {
                if (i > 0) g.color = board.snakes[i - 1].color
                g.drawString(
                    msgs[i],
                    (width - metr.stringWidth(msgs[i])) / 2,
                    height / 2 - yOffset + i * fontHeight
                )
            }
        }

        private inner class Input : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                val key = e.keyCode

                if (key == KeyEvent.VK_ESCAPE)
                    toEscapeCard()

                for (snake in board.liveSnakes) {
                    if (snake !is PlayerSnake) continue
                    snake.bufferInput(key)
                }
            }
        }
    }
}
