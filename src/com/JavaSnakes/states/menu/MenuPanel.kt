package com.JavaSnakes.states.menu

import com.JavaSnakes.Main
import com.JavaSnakes.states.game.GamePanel
import com.JavaSnakes.states.game.InitSnakes
import com.JavaSnakes.util.CustomJCheckBox
import com.JavaSnakes.util.CustomJSpinner
import com.JavaSnakes.util.MenuCard

import java.awt.CardLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.system.exitProcess

class MenuPanel(private val owner: Main) {
    val mainPanel = JPanel()

    private val cardLayout: CardLayout

    private val newGameButton = JButton("New Game")
    private val settingsButton = JButton("Settings")
    private val quitButton = JButton("Quit")

    private val startGameButton = JButton("Start Game")
    private val playerCount = CustomJSpinner(1, 0, 1)
    private val botCount = CustomJSpinner(0, 0, 0)
    private val wallsEnabled = CustomJCheckBox()
    private val toMainButton1 = JButton("Back")

    private val toMainButton2 = JButton("Back")
    private val mapW = CustomJSpinner(30, 20, 100)
    private val mapH = CustomJSpinner(20, 20, 100)
    private val frameDelay = CustomJSpinner(100, 25, 1000, 25)
    private val playerSettings = PlayerSettings()

    private val maxSnakeCount: Int
        get() = ((min(mapW.value, mapH.value) - 11) / 6 + 1) * 4

    init {
        mainPanel.layout = CardLayout()

        createMainMenuCard()
        createNewGameCard()
        createSettingsCard()

        cardLayout = mainPanel.layout as CardLayout
        mainPanel.preferredSize = Dimension(300, 300)
    }

    private fun createMainMenuCard() {
        newGameButton.addActionListener { toNewGameCard() }
        settingsButton.addActionListener { toSettingsCard() }
        quitButton.addActionListener { exitProcess(0) }

        val gridBag = MenuCard()
        gridBag.addInGrid(newGameButton, 0, 0)
        gridBag.addInGrid(settingsButton, 1, 0)
        gridBag.addInGrid(quitButton, 2, 0)

        mainPanel.add(gridBag, "main menu card")
    }

    private fun createNewGameCard() {
        startGameButton.addActionListener { startGame() }
        val playerSnakeCountLabel = JLabel("Player count:")
        playerCount.spinner.addChangeListener { playerSnakeCountChanged() }
        val botSnakeCountLabel = JLabel("Bot count:")
        botCount.spinner.addChangeListener { botSnakeCountChanged() }
        wallsEnabled.checkBox.text = "Include walls"
        toMainButton1.addActionListener { toMainMenuCard() }

        val gridBag = MenuCard()
        gridBag.addInGrid(startGameButton, 0, 0, 2)
        gridBag.addInGrid(playerSnakeCountLabel, 1, 0)
        gridBag.addInGrid(playerCount.spinner, 1, 1, padding = Insets(0, 5, 0, 0))
        gridBag.addInGrid(botSnakeCountLabel, 2, 0)
        gridBag.addInGrid(botCount.spinner, 2, 1, padding = Insets(0, 5, 0, 0))
        gridBag.addInGrid(wallsEnabled.checkBox, 3, 0, 2)
        gridBag.addInGrid(toMainButton1, 4, 0, 2)

        mainPanel.add(gridBag, "new game card")
    }

    private fun createSettingsCard() {
        toMainButton2.addActionListener { toMainMenuCard() }
        val mapWidthLabel = JLabel("Map width:")
        val mapHeightLabel = JLabel("Map height:")
        val frameDelayLabel = JLabel("Frame delay:")
        frameDelay.spinner.addChangeListener { frameDelayChanged() }
        val separator = JSeparator(SwingConstants.HORIZONTAL)
        val snakeNumLabel = JLabel("Player snake:")
        val upCtrlLabel = JLabel("Up key:")
        val downCtrlLabel = JLabel("Down key:")
        val leftCtrlLabel = JLabel("Left key:")
        val rightCtrlLabel = JLabel("Right key:")

        val gridBag = MenuCard()
        gridBag.addInGrid(toMainButton2, 0, 0, 2)
        gridBag.addInGrid(mapWidthLabel, 1, 0)
        gridBag.addInGrid(mapW.spinner, 1, 1)
        gridBag.addInGrid(mapHeightLabel, 2, 0)
        gridBag.addInGrid(mapH.spinner, 2, 1)
        gridBag.addInGrid(frameDelayLabel, 3, 0)
        gridBag.addInGrid(frameDelay.spinner, 3, 1)
        gridBag.addInGrid(separator, 4, 0, 2, Insets(5, 0, 5, 0))
        gridBag.addInGrid(snakeNumLabel, 5, 0)
        gridBag.addInGrid(playerSettings.snakeNum.spinner, 5, 1)
        gridBag.addInGrid(upCtrlLabel, 6, 0)
        gridBag.addInGrid(playerSettings.dirCtrlFields[0], 6, 1)
        gridBag.addInGrid(downCtrlLabel, 7, 0)
        gridBag.addInGrid(playerSettings.dirCtrlFields[1], 7, 1)
        gridBag.addInGrid(leftCtrlLabel, 8, 0)
        gridBag.addInGrid(playerSettings.dirCtrlFields[2], 8, 1)
        gridBag.addInGrid(rightCtrlLabel, 9, 0)
        gridBag.addInGrid(playerSettings.dirCtrlFields[3], 9, 1)

        mainPanel.add(gridBag, "settings card")
    }

    private fun toMainMenuCard() {
        cardLayout.show(mainPanel, "main menu card")
        newGameButton.requestFocus()
    }

    private fun toNewGameCard() {
        // Done to resize the spinners when the number of digits in maxSnakeCount changes
        playerCount.setModel(1, 0, maxSnakeCount)
        botCount.setModel(0, 0, maxSnakeCount)

        cardLayout.show(mainPanel, "new game card")
        startGameButton.requestFocus()
    }

    private fun toSettingsCard() {
        cardLayout.show(mainPanel, "settings card")
        toMainButton2.requestFocus()
    }

    private fun playerSnakeCountChanged() {
        if (playerCount.value == 0 && botCount.value == 0) {
            botCount.value = 1
        } else if (playerCount.value > playerSettings.players.size) {
            playerCount.value = playerSettings.players.size
        } else if (playerCount.value + botCount.value > maxSnakeCount) {
            botCount.value = maxSnakeCount - playerCount.value
        }
    }

    private fun botSnakeCountChanged() {
        if (botCount.value == 0 && playerCount.value == 0) {
            playerCount.value = 1
        } else if (botCount.value + playerCount.value > maxSnakeCount) {
            playerCount.value = maxSnakeCount - botCount.value
        }
    }

    private fun frameDelayChanged() {
        if (frameDelay.value % 25 != 0) {
            frameDelay.value = (frameDelay.value.toFloat() / 25).roundToInt() * 25
        }
    }

    private fun startGame() {
        val initSnakes = InitSnakes(mapW.value, mapH.value)
        for (i in 0 until playerCount.value) {
            initSnakes.addPlayerSnake(playerSettings.players[i])
        }
        initSnakes.addBotSnakes(Color.black, botCount.value)

        val gamePanel = GamePanel(
            owner, frameDelay.value, 10,
            mapW.value, mapH.value, wallsEnabled.value, initSnakes.snakes
        )
        owner.changePanel(gamePanel.mainPanel)
    }
}
