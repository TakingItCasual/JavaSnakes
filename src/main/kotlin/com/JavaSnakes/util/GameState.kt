package com.javasnakes.util

import com.javasnakes.Main

import javax.swing.JPanel

open class GameState(protected val owner: Main) {
    val mainPanel = JPanel()

    open fun cleanUp() {} // Overridden if the state should be changed when returned to from a later state
}
