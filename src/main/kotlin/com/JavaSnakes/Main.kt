package com.javasnakes

import com.javasnakes.states.menu.MenuPanel
import com.javasnakes.util.GameState

import java.util.Stack
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.UIManager

class Main : JFrame() {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            } catch (e: java.lang.Exception) {
                JOptionPane.showMessageDialog(null, "GUI Error")
            }

            SwingUtilities.invokeLater { Main() }
        }
    }

    private val stateStack = Stack<GameState>()

    init {
        title = "Snake"
        defaultCloseOperation = EXIT_ON_CLOSE

        toNewPanel(MenuPanel(this))
    }

    fun toNewPanel(newState: GameState) {
        stateStack.push(newState)
        changePanel(newState.mainPanel)
    }

    fun toPrevPanel() {
        stateStack.pop()
        stateStack.peek().cleanUp()
        changePanel(stateStack.peek().mainPanel)
    }

    private fun changePanel(newPanel: JPanel) {
        isVisible = false

        contentPane.removeAll()
        add(newPanel)

        pack()
        isResizable = false
        setLocationRelativeTo(null)

        isVisible = true
    }
}
