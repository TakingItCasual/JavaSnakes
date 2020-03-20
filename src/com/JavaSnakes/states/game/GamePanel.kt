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
import java.awt.Toolkit
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.DefaultTableColumnModel
import javax.swing.table.DefaultTableModel
import kotlin.math.max

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
    companion object {
        private const val GAME_CARD = "game card"
        private const val ESCAPE_CARD = "escape card"
        private const val END_CARD = "end card"
    }

    private val mainGamePanel = MainGamePanel()

    private val continueButton = JButton("Continue")
    private val quitToMenuButton = JButton("Quit")

    private val endCard = MenuCard()
    private val backToMainButton = JButton("Continue")

    private val miniCell: Int
    private val miniOffset: Int

    private val board: Board

    private var inGame = true
    private var isPaused = false
    private val animator: Thread

    init {
        mainPanel.layout = CardLayout()

        mainPanel.add(mainGamePanel, GAME_CARD)
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
        quitToMenuButton.addActionListener { toEndCard() }

        val gridBag = MenuCard()
        gridBag.addInGrid(continueButton, 0, 0)
        gridBag.addInGrid(quitToMenuButton, 1, 0)

        mainPanel.add(gridBag, ESCAPE_CARD)
    }

    private fun createEndCard() {
        backToMainButton.addActionListener { toMainMenu() }

        endCard.addInGrid(backToMainButton, 0, 0)

        mainPanel.add(endCard, END_CARD)
    }

    private fun addScoresToEndCard() {
        val scoreSortedSnakes = board.snakes.sortedWith(
            compareBy<SnakeBase> { -it.score }.thenByDescending { it.groupName }.thenBy { it.idInGroup })
        val scoreSet = scoreSortedSnakes.map { it.score }.toSet()

        val columnNames = arrayOf("Place", "Snake", "Score")
        var data = mutableListOf<Array<String>>()
        for (snake in scoreSortedSnakes) {
            data.add(arrayOf(
                (scoreSet.indexOf(snake.score) + 1).asOrdinal(),
                snake.groupName + " " + snake.idInGroup,
                snake.score.toString()
            ))
        }

        val scoreTable = JTable(data.toTypedArray(), columnNames)
        scoreTable.setShowGrid(false)
        scoreTable.intercellSpacing = Dimension(0, 0)
        scoreTable.font = Font(JLabel().font.family, Font.BOLD, 14)
        scoreTable.model = object : DefaultTableModel(data.toTypedArray(), columnNames) {
            // Disable table editing
            override fun isCellEditable(row: Int, column: Int): Boolean {
                return false
            }
        }
        fitColumnWidthToContent(scoreTable)
        /*val tableSorter: TableRowSorter<TableModel> = TableRowSorter(scoreTable.model)
        scoreTable.rowSorter = tableSorter
        val sortKeys: MutableList<RowSorter.SortKey> = ArrayList()
        sortKeys.add(RowSorter.SortKey(0, SortOrder.ASCENDING))
        tableSorter.sortKeys = sortKeys*/
        // Disable (most?) table interactions
        scoreTable.isFocusable = false
        scoreTable.tableHeader.reorderingAllowed = false
        scoreTable.tableHeader.resizingAllowed = false
        scoreTable.rowSelectionAllowed = false

        //scoreTable.background = Color.darkGray

        val scrollPane = JScrollPane(scoreTable)
        scrollPane.border = BorderFactory.createEmptyBorder()
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        scrollPane.preferredSize = Dimension(scoreTable.preferredSize.width, mainGamePanel.height / 2)
        endCard.addInGrid(scrollPane, 1, 0)
    }

    private fun fitColumnWidthToContent(table: JTable) {
        table.autoResizeMode = JTable.AUTO_RESIZE_OFF
        val colModel = table.columnModel as DefaultTableColumnModel
        for (v in 0 until table.columnCount) {
            val col = colModel.getColumn(v)
            var width: Int

            var renderer = col.headerRenderer
            if (renderer == null) {
                renderer = table.tableHeader.defaultRenderer
            }
            var comp = renderer.getTableCellRendererComponent(
                    table, col.headerValue, false, false, 0, 0)
            width = comp.preferredSize.width

            for (r in 0 until table.rowCount) {
                renderer = table.getCellRenderer(r, v)
                comp = renderer.getTableCellRendererComponent(
                        table, table.getValueAt(r, v), false, false, r, v)
                width = max(width, comp.preferredSize.width)
            }
            col.preferredWidth = width + 10
            if (v == table.columnCount - 1) {
                col.preferredWidth += JScrollPane().verticalScrollBar.preferredSize.width
            }
            col.minWidth = col.preferredWidth
            col.maxWidth = col.preferredWidth
        }
    }

    private fun toMainMenu() {
        owner.toPrevPanel()
    }

    private fun toGameCard() {
        isPaused = false
        cardLayout.show(mainPanel, GAME_CARD)
        mainGamePanel.requestFocus()
    }

    private fun toEscapeCard() {
        isPaused = true
        cardLayout.show(mainPanel, ESCAPE_CARD)
        continueButton.requestFocus()
    }

    private fun toEndCard() {
        addScoresToEndCard()
        cardLayout.show(mainPanel, END_CARD)
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
                drawOffsetMiniSquare(g, snake.coords[1], snake.coords[1].otherDir(snake.headPos()))

                // Iterator used instead of random access due to LinkedList type
                val iter = snake.coords.listIterator(1)
                var prevCoord = iter.next()
                while (iter.hasNext()) {
                    val nowCoord = iter.next()
                    drawOffsetMiniSquare(g, prevCoord, prevCoord.otherDir(nowCoord))
                    drawOffsetMiniSquare(g, nowCoord, nowCoord.otherDir(prevCoord))
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

        private fun drawOffsetMiniSquare(g: Graphics, coord: GridPos, otherDir: Direction) {
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
