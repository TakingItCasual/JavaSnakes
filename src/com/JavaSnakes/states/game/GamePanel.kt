package com.JavaSnakes.states.game

import com.JavaSnakes.Board
import com.JavaSnakes.Main
import com.JavaSnakes.snakes.PlayerSnake
import com.JavaSnakes.snakes.SnakeBase
import com.JavaSnakes.util.Direction
import com.JavaSnakes.util.GameState
import com.JavaSnakes.util.asOrdinal
import com.JavaSnakes.util.GridPos
import com.JavaSnakes.util.MenuCard

import java.awt.CardLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.Insets
import java.awt.Toolkit
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane

class GamePanel(
        owner: Main,
        private val delay: Int,
        private val cellSize: Int,
        setMapW: Int,
        setMapH: Int,
        includeWalls: Boolean,
        setFoodCount: Int,
        setSnakes: List<SnakeBase>
) : GameState(owner), Runnable {
    private val cardLayout: CardLayout

    private val mainGamePanel = MainGamePanel()

    private val continueButton = JButton("Continue")
    private val quitToMenuButton = JButton("Quit to main menu")

    private val endCard = MenuCard()
    private val backToMainButton = JButton("Back to main menu")

    private val miniCell: Int
    private val miniOffset: Int

    private val board: Board

    private var inGame = true
    private var isPaused = false
    private val animator: Thread

    init {
        mainPanel.layout = CardLayout()

        mainPanel.add(mainGamePanel, "game card")
        createEscapeCard()
        createEndCard()

        cardLayout = mainPanel.layout as CardLayout
        miniOffset = 1
        miniCell = cellSize - 2 * miniOffset

        board = Board(setMapW, setMapH, includeWalls, setFoodCount, setSnakes)
        mainPanel.preferredSize = Dimension(board.width * cellSize, board.height * cellSize)

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

        endCard.addInGrid(backToMainButton, 0, 0)

        mainPanel.add(endCard, "end card")
    }

    private fun addScoresToEndCard() {
        val scoreSortedSnakes = board.snakes.sortedWith(
            compareBy<SnakeBase> { -it.score }.thenByDescending { it.groupName }.thenBy { it.idInGroup })
        val scoreSet = scoreSortedSnakes.map { it.score }.toSet()

        val labelList = MenuCard()
        val font = Font(JLabel().font.family, Font.BOLD, 14)
        for ((i, snake) in scoreSortedSnakes.withIndex()) {
            val placingLabel = JLabel((scoreSet.indexOf(snake.score) + 1).asOrdinal())
            placingLabel.font = font
            labelList.addInGrid(placingLabel, i, 0, padding = Insets(0, 10, 0, 0))

            val nameLabel = JLabel(snake.groupName + " " + snake.idInGroup)
            nameLabel.font = font
            nameLabel.foreground = snake.color
            labelList.addInGrid(nameLabel, i, 1, padding = Insets(0, 10, 0, 10))

            val scoreLabel = JLabel(snake.score.toString())
            scoreLabel.font = font
            scoreLabel.foreground = snake.color
            labelList.addInGrid(scoreLabel, i, 2, padding = Insets(0, 0, 0, 10))
        }

        val scrollPane = JScrollPane(labelList)
        scrollPane.border = BorderFactory.createEmptyBorder()
        scrollPane.minimumSize = Dimension(-1, mainGamePanel.height / 2)
        // TODO: Figure out how to set scrollPane's background color
        // TODO: Figure out how to properly disable the horizontal scrollbar
        endCard.addInGrid(scrollPane, 1, 0)
    }

    private fun toMainMenu() {
        owner.toPrevPanel()
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

    private fun toEndCard() {
        addScoresToEndCard()
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

        if (!board.checkFood()) inGame = false
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
                if (board.isWalled) drawWalls(g)
            }

            Toolkit.getDefaultToolkit().sync()
            g.dispose()
        }

        private fun drawFood(g: Graphics) {
            g.color = Color.red
            for (foodPos in board.foodsPos) {
                drawMiniSquare(g, foodPos)
            }
        }

        private fun drawSnakes(g: Graphics) {
            for (snake in board.liveSnakes) {
                g.color = snake.color

                drawFullSquare(g, snake.headPos())
                drawMiniSquareOffset(g, snake.coords[1], snake.coords[1].otherDir(snake.headPos()))

                // Iterator used instead of random access due to LinkedList type
                val iter = snake.coords.listIterator(1)
                var prevCoord = iter.next()
                while (iter.hasNext()) {
                    val nowCoord = iter.next()
                    drawMiniSquareOffset(g, prevCoord, prevCoord.otherDir(nowCoord))
                    drawMiniSquareOffset(g, nowCoord, nowCoord.otherDir(prevCoord))
                    prevCoord = nowCoord
                }
            }
        }

        private fun drawWalls(g: Graphics) {
            g.color = Color.black
            g.fillRect(0, 0, cellSize * board.width, cellSize)
            g.fillRect(0, 0, cellSize, cellSize * board.height)
            g.fillRect(0, cellSize * (board.height - 1), cellSize * board.width, cellSize)
            g.fillRect(cellSize * (board.width - 1), 0, cellSize, cellSize * board.height)

            g.color = Color.white
            g.fillRect(cellSize, cellSize - 1, cellSize * (board.width - 2), 1)
            g.fillRect(cellSize - 1, cellSize, 1, cellSize * (board.height - 2))
            g.fillRect(cellSize, cellSize * (board.height - 1), cellSize * (board.width - 2), 1)
            g.fillRect(cellSize * (board.width - 1), cellSize, 1, cellSize * (board.height - 2))
        }

        private fun drawFullSquare(g: Graphics, coord: GridPos) {
            g.fillRect(coord.x * cellSize, coord.y * cellSize, cellSize, cellSize)
        }

        private fun drawMiniSquare(g: Graphics, coord: GridPos) {
            g.fillRect(coord.x * cellSize + miniOffset, coord.y * cellSize + miniOffset, miniCell, miniCell)
        }

        private fun drawMiniSquareOffset(g: Graphics, coord: GridPos, otherDir: Direction) {
            var xOffset = 0
            var yOffset = 0
            when (otherDir) {
                Direction.Up -> yOffset = -1
                Direction.Down -> yOffset = 1
                Direction.Left -> xOffset = -1
                Direction.Right -> xOffset = 1
            }
            g.fillRect(
                coord.x * cellSize + miniOffset + xOffset,
                coord.y * cellSize + miniOffset + yOffset,
                miniCell, miniCell
            )
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
