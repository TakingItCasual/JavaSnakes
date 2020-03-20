package com.JavaSnakes.states.menu

import com.JavaSnakes.Main
import com.JavaSnakes.states.game.GamePanel
import com.JavaSnakes.states.game.InitSnakes
import com.JavaSnakes.util.CustomJCheckBox
import com.JavaSnakes.util.CustomJSpinner
import com.JavaSnakes.util.GameState
import com.JavaSnakes.util.GridPos
import com.JavaSnakes.util.MenuCard

import java.awt.CardLayout
import java.awt.Dimension
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JSeparator
import javax.swing.SpinnerNumberModel
import javax.swing.SwingConstants
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.system.exitProcess

class MenuPanel(owner: Main) : GameState(owner) {
    private val cardLayout: CardLayout
    companion object {
        private const val MAIN_MENU_CARD = "main menu card"
        private const val NEW_GAME_CARD = "new game card"
        private const val SETTINGS_CARD = "settings card"
    }

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
    private val foodCount = CustomJSpinner(2, 1, 10000)
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

    override fun cleanUp() {
        toMainMenuCard()
    }

    private fun createMainMenuCard() {
        newGameButton.addActionListener { toNewGameCard() }
        settingsButton.addActionListener { toSettingsCard() }
        quitButton.addActionListener { exitProcess(0) }

        val gridBag = MenuCard()
        gridBag.addInGrid(newGameButton, 0, 0)
        gridBag.addInGrid(settingsButton, 1, 0)
        gridBag.addInGrid(quitButton, 2, 0)

        mainPanel.add(gridBag, MAIN_MENU_CARD)
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

        mainPanel.add(gridBag, NEW_GAME_CARD)
    }

    private fun createSettingsCard() {
        toMainButton2.addActionListener { toMainMenuCard() }
        val mapWidthLabel = JLabel("Map width:")
        val mapHeightLabel = JLabel("Map height:")
        val frameDelayLabel = JLabel("Frame delay:")
        frameDelay.spinner.addChangeListener { frameDelayChanged() }
        val foodCountLabel = JLabel("Food count:")
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
        gridBag.addInGrid(foodCountLabel, 4, 0)
        gridBag.addInGrid(foodCount.spinner, 4, 1)
        gridBag.addInGrid(separator, 5, 0, 2, Insets(5, 0, 5, 0))
        gridBag.addInGrid(snakeNumLabel, 6, 0)
        gridBag.addInGrid(playerSettings.snakeNum.spinner, 6, 1)
        gridBag.addInGrid(upCtrlLabel, 7, 0)
        gridBag.addInGrid(playerSettings.dirCtrlFields[0], 7, 1)
        gridBag.addInGrid(downCtrlLabel, 8, 0)
        gridBag.addInGrid(playerSettings.dirCtrlFields[1], 8, 1)
        gridBag.addInGrid(leftCtrlLabel, 9, 0)
        gridBag.addInGrid(playerSettings.dirCtrlFields[2], 9, 1)
        gridBag.addInGrid(rightCtrlLabel, 10, 0)
        gridBag.addInGrid(playerSettings.dirCtrlFields[3], 10, 1)

        mainPanel.add(gridBag, SETTINGS_CARD)
    }

    private fun toMainMenuCard() {
        cardLayout.show(mainPanel, MAIN_MENU_CARD)
        newGameButton.requestFocus()
    }

    private fun toNewGameCard() {
        // Done to resize the spinners when the number of digits in maxSnakeCount changes
        playerCount.setModel(1, 0, maxSnakeCount)
        botCount.setModel(0, 0, maxSnakeCount)

        cardLayout.show(mainPanel, NEW_GAME_CARD)
        startGameButton.requestFocus()
    }

    private fun toSettingsCard() {
        cardLayout.show(mainPanel, SETTINGS_CARD)
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
        val stepSize = (frameDelay.spinner.model as SpinnerNumberModel).stepSize as Int
        if (frameDelay.value % stepSize != 0) {
            // Value rounded to nearest step size multiple
            frameDelay.value = (frameDelay.value.toFloat() / stepSize).roundToInt() * stepSize
        }
    }

    private fun startGame() {
        GridPos.boardW = mapW.value
        GridPos.boardH = mapH.value

        val initSnakes = InitSnakes(mapW.value, mapH.value)
        for (i in 0 until playerCount.value) {
            initSnakes.addPlayerSnake(playerSettings.players[i])
        }
        initSnakes.addBotSnakes(botCount.value)

        val gamePanel = GamePanel(
            owner, frameDelay.value, 10,
            mapW.value, mapH.value, wallsEnabled.value, foodCount.value, initSnakes.snakes
        )
        owner.toNewPanel(gamePanel)
    }
}
