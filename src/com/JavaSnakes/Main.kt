package com.JavaSnakes

import com.JavaSnakes.states.menu.MenuPanel

import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.UIManager

class Main : JFrame() {
    init {
        title = "Snake"
        defaultCloseOperation = EXIT_ON_CLOSE

        changePanel(MenuPanel(this).mainPanel)
    }

    fun changePanel(newPanel: JPanel) {
        isVisible = false

        contentPane.removeAll()
        add(newPanel)

        pack()
        isResizable = false
        setLocationRelativeTo(null)

        isVisible = true
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
            } catch (e: java.lang.Exception) {
                JOptionPane.showMessageDialog(null, "GUI Error")
            }

            SwingUtilities.invokeLater { Main() }
        }
    }
}
