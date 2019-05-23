package com.JavaSnakes.states.menu

import com.JavaSnakes.Main
import com.JavaSnakes.states.game.GamePanel
import com.JavaSnakes.states.game.InitSnakes
import com.JavaSnakes.util.MenuCard

import java.awt.CardLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Insets
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.ArrayList
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.JSpinner
import javax.swing.JTextField
import javax.swing.SpinnerNumberModel
import javax.swing.SwingConstants

class MenuPanel(private val owner: Main) {
    val mainPanel = JPanel()

    private val cardLayout: CardLayout

    private val newGameButton = JButton("New Game")
    private val settingsButton = JButton("Settings")
    private val quitButton = JButton("Quit")

    private val startGameButton = JButton("Start Game")
    private val playerSnakeSpinner = JSpinner()
    private val botSnakeSpinner = JSpinner()
    private val wallCheckBox = JCheckBox("Include walls")
    private val toMainButton1 = JButton("Back")

    private val toMainButton2 = JButton("Back")
    private val mapWidthSpinner = JSpinner()
    private val mapHeightSpinner = JSpinner()
    private val frameDelaySpinner = JSpinner()
    private val snakeNumSpinner = JSpinner()
    private val dirCtrlFields = Array(4) { JTextField("") }

    private var maxSnakeCount = 0
    private val playerControls: MutableList<IntArray>
    private var playerControlsTemp = IntArray(4)

    init {
        mainPanel.layout = CardLayout()

        maxSnakeCount = 8
        playerControls = ArrayList()
        playerControls.add(intArrayOf(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT))
        playerControls.add(intArrayOf(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D))

        createMainMenuCard()
        createNewGameCard()
        createSettingsCard()

        cardLayout = mainPanel.layout as CardLayout
        mainPanel.preferredSize = Dimension(300, 300)
    }

    private fun createMainMenuCard() {
        newGameButton.addActionListener { toNewGameCard() }
        settingsButton.addActionListener { toSettingsCard() }
        quitButton.addActionListener { System.exit(0) }

        val gridBag = MenuCard()
        gridBag.addInGrid(newGameButton, 0, 0)
        gridBag.addInGrid(settingsButton, 1, 0)
        gridBag.addInGrid(quitButton, 2, 0)

        mainPanel.add(gridBag, "main menu card")
    }

    private fun createNewGameCard() {
        startGameButton.addActionListener { startGame() }
        val playerSnakeCountLabel = JLabel("Player count:")
        playerSnakeSpinner.model = SpinnerNumberModel(1, 0, maxSnakeCount, 1)
        playerSnakeSpinner.addChangeListener { playerSnakeCountChanged() }
        val botSnakeCountLabel = JLabel("Bot count:")
        botSnakeSpinner.model = SpinnerNumberModel(0, 0, maxSnakeCount, 1)
        botSnakeSpinner.addChangeListener { botSnakeCountChanged() }
        toMainButton1.addActionListener { toMainMenuCard() }

        val gridBag = MenuCard()
        gridBag.addInGrid(startGameButton, 0, 0, 2)
        gridBag.addInGrid(playerSnakeCountLabel, 1, 0)
        gridBag.addInGrid(playerSnakeSpinner, 1, 1, padding = Insets(0, 5, 0, 0))
        gridBag.addInGrid(botSnakeCountLabel, 2, 0)
        gridBag.addInGrid(botSnakeSpinner, 2, 1, padding = Insets(0, 5, 0, 0))
        gridBag.addInGrid(wallCheckBox, 3, 0, 2)
        gridBag.addInGrid(toMainButton1, 4, 0, 2)

        mainPanel.add(gridBag, "new game card")
    }

    private fun createSettingsCard() {
        toMainButton2.addActionListener { toMainMenuCard() }
        val mapWidthLabel = JLabel("Map width:")
        mapWidthSpinner.model = SpinnerNumberModel(30, 20, 100, 1)
        val mapHeightLabel = JLabel("Map height:")
        mapHeightSpinner.model = SpinnerNumberModel(20, 20, 100, 1)
        val frameDelayLabel = JLabel("Frame delay:")
        frameDelaySpinner.model = SpinnerNumberModel(100, 25, 1000, 25)
        frameDelaySpinner.addChangeListener { frameDelayChanged() }
        val separator = JSeparator(SwingConstants.HORIZONTAL)
        val snakeNumLabel = JLabel("Player snake:")
        snakeNumSpinner.model = SpinnerNumberModel(1, 1, null, 1)
        snakeNumSpinner.addChangeListener { snakeNumChanged() }
        val upCtrlLabel = JLabel("Up key:")
        val downCtrlLabel = JLabel("Down key:")
        val leftCtrlLabel = JLabel("Left key:")
        val rightCtrlLabel = JLabel("Right key:")

        for ((i, dirCtrlField) in dirCtrlFields.withIndex()) {
            dirCtrlField.addKeyListener(CtrlInput(i))
            dirCtrlField.addFocusListener(CtrlInputFocus(i))
        }

        val font = Font(dirCtrlFields[0].font.family, Font.BOLD, dirCtrlFields[0].font.size)
        for (dirCtrlField in dirCtrlFields) {
            dirCtrlField.isEditable = false
            dirCtrlField.font = font
        }

        snakeNumChanged() // Fill out player settings with first player's info

        val gridBag = MenuCard()
        gridBag.addInGrid(toMainButton2, 0, 0, 2)
        gridBag.addInGrid(mapWidthLabel, 1, 0)
        gridBag.addInGrid(mapWidthSpinner, 1, 1)
        gridBag.addInGrid(mapHeightLabel, 2, 0)
        gridBag.addInGrid(mapHeightSpinner, 2, 1)
        gridBag.addInGrid(frameDelayLabel, 3, 0)
        gridBag.addInGrid(frameDelaySpinner, 3, 1)
        gridBag.addInGrid(separator, 4, 0, 2, Insets(5, 0, 5, 0))
        gridBag.addInGrid(snakeNumLabel, 5, 0)
        gridBag.addInGrid(snakeNumSpinner, 5, 1)
        gridBag.addInGrid(upCtrlLabel, 6, 0)
        gridBag.addInGrid(dirCtrlFields[0], 6, 1)
        gridBag.addInGrid(downCtrlLabel, 7, 0)
        gridBag.addInGrid(dirCtrlFields[1], 7, 1)
        gridBag.addInGrid(leftCtrlLabel, 8, 0)
        gridBag.addInGrid(dirCtrlFields[2], 8, 1)
        gridBag.addInGrid(rightCtrlLabel, 9, 0)
        gridBag.addInGrid(dirCtrlFields[3], 9, 1)

        mainPanel.add(gridBag, "settings card")
    }

    private fun toMainMenuCard() {
        cardLayout.show(mainPanel, "main menu card")
        newGameButton.requestFocus()
    }

    private fun toNewGameCard() {
        val minDimension = Math.min(mapWidthSpinner.value as Int, mapHeightSpinner.value as Int)
        maxSnakeCount = ((minDimension - 11) / 6 + 1) * 4
        // Done to resize the spinners when the number of digits in maxSnakeCount changes
        playerSnakeSpinner.model = SpinnerNumberModel(1, 0, maxSnakeCount, 1)
        botSnakeSpinner.model = SpinnerNumberModel(0, 0, maxSnakeCount, 1)

        cardLayout.show(mainPanel, "new game card")
        startGameButton.requestFocus()
    }

    private fun toSettingsCard() {
        cardLayout.show(mainPanel, "settings card")
        toMainButton2.requestFocus()
    }

    private fun playerSnakeCountChanged() {
        if (playerSnakeSpinner.value as Int == 0 && botSnakeSpinner.value as Int == 0) {
            botSnakeSpinner.value = 1
        } else if (playerSnakeSpinner.value as Int > playerControls.size) {
            playerSnakeSpinner.value = playerControls.size
        } else if (playerSnakeSpinner.value as Int + botSnakeSpinner.value as Int > maxSnakeCount) {
            botSnakeSpinner.value = maxSnakeCount - playerSnakeSpinner.value as Int
        }
    }

    private fun botSnakeCountChanged() {
        if (botSnakeSpinner.value as Int == 0 && playerSnakeSpinner.value as Int == 0) {
            playerSnakeSpinner.value = 1
        } else if (botSnakeSpinner.value as Int + playerSnakeSpinner.value as Int > maxSnakeCount) {
            playerSnakeSpinner.value = maxSnakeCount - botSnakeSpinner.value as Int
        }
    }

    private fun snakeNumChanged() {
        val playerNum = snakeNumSpinner.value as Int
        if (playerNum > playerControls.size) {
            snakeNumSpinner.value = playerControls.size + 1
            // It is expected that valid key codes are positive integers
            playerControlsTemp = intArrayOf(-1, -1, -1, -1)
            for (dirCtrlField in dirCtrlFields) {
                dirCtrlField.text = "undefined"
            }
        } else {
            playerControlsTemp = playerControls[playerNum - 1].clone()
            for ((i, dirCtrlField) in dirCtrlFields.withIndex()) {
                dirCtrlField.text = KeyEvent.getKeyText(playerControlsTemp[i])
            }
        }
    }

    private fun frameDelayChanged() {
        val frameDelay = frameDelaySpinner.value as Int
        if (frameDelay % 25 != 0) {
            frameDelaySpinner.value = Math.round(frameDelay.toFloat() / 25) * 25
        }
    }

    private fun startGame() {
        val mapW = mapWidthSpinner.value as Int
        val mapH = mapHeightSpinner.value as Int

        val initSnakes = InitSnakes(mapW, mapH)
        for (i in 0 until playerSnakeSpinner.value as Int) {
            initSnakes.addPlayerSnake(
                Color.blue,
                playerControls[i][0],
                playerControls[i][1],
                playerControls[i][2],
                playerControls[i][3]
            )
        }
        initSnakes.addBotSnakes(Color.black, botSnakeSpinner.value as Int)

        val gamePanel = GamePanel(
            owner, frameDelaySpinner.value as Int, 10,
            mapW, mapH, wallCheckBox.isSelected, initSnakes.snakes
        )
        owner.changePanel(gamePanel.mainPanel)
    }

    private inner class CtrlInput constructor(internal var dirIndex: Int) : KeyAdapter() {
        override fun keyPressed(e: KeyEvent) {
            val key = e.keyCode
            if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_SPACE) return
            if (key in playerControlsTemp) return

            playerControlsTemp[dirIndex] = key
            dirCtrlFields[dirIndex].text = " " + KeyEvent.getKeyText(key)

            val playerNum = snakeNumSpinner.value as Int
            if (playerNum > playerControls.size) {
                if (-1 !in playerControlsTemp) {
                    playerControls.add(playerControlsTemp.clone())
                }
            } else {
                playerControls[playerNum - 1][dirIndex] = key
            }
        }
    }

    // Class for giving visual indication of when JTextField is focused on (CtrlInput class helps with this)
    private inner class CtrlInputFocus constructor(internal var dirIndex: Int) : FocusListener {
        override fun focusGained(e: FocusEvent) {
            dirCtrlFields[dirIndex].text = " " + dirCtrlFields[dirIndex].text.trim()
        }

        override fun focusLost(e: FocusEvent) {
            dirCtrlFields[dirIndex].text = dirCtrlFields[dirIndex].text.trim()
        }
    }
}
