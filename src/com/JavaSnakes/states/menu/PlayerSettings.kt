package com.JavaSnakes.states.menu

import com.JavaSnakes.util.CustomJSpinner
import java.awt.Color
import java.awt.Font
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JTextField

class PlayerSettings {
    val snakeNum = CustomJSpinner(1, 1, null)
    val dirCtrlFields = Array(4) { JTextField("") }

    var players = mutableListOf(
        PlayerData(Color.blue, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT),
        PlayerData(Color.blue, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D)
    )
    var playerTemp = PlayerData()

    init {
        snakeNum.spinner.addChangeListener { snakeNumChanged() }

        for ((i, dirCtrlField) in dirCtrlFields.withIndex()) {
            dirCtrlField.addKeyListener(CtrlInput(i))
            dirCtrlField.addFocusListener(CtrlInputFocus(i))
        }
        val font = Font(dirCtrlFields[0].font.family, Font.BOLD, dirCtrlFields[0].font.size)
        for (dirCtrlField in dirCtrlFields) {
            dirCtrlField.isEditable = false
            dirCtrlField.font = font
        }

        snakeNumChanged()
    }

    private fun snakeNumChanged() {
        if (snakeNum.value > players.size) {
            snakeNum.value = players.size + 1
            playerTemp = PlayerData(Color.blue)
            for (dirCtrlField in dirCtrlFields) {
                dirCtrlField.text = "undefined"
            }
        } else {
            playerTemp = players[snakeNum.value - 1].clone()
            for ((i, dirCtrlField) in dirCtrlFields.withIndex()) {
                dirCtrlField.text = KeyEvent.getKeyText(playerTemp.ctrls[i]!!)
            }
        }
    }

    class PlayerData {
        var color: Color
        var ctrls: Controls

        constructor(
                setColor: Color = Color.black,
                upCtrl: Int? = null,
                downCtrl: Int? = null,
                leftCtrl: Int? = null,
                rightCtrl: Int? = null
        ) {
            color = setColor
            ctrls = Controls(upCtrl, downCtrl, leftCtrl, rightCtrl)
        }

        private constructor(setColor: Color, setCtrls: Controls) {
            color = setColor
            ctrls = setCtrls
        }

        fun clone(): PlayerData {
            return PlayerData(color, ctrls)
        }
    }

    class Controls : ArrayList<Int?> {
        var up: Int?
            get() = this[0]
            set(value) { this[0] = value }
        var down: Int?
            get() = this[1]
            set(value) { this[1] = value }
        var left: Int?
            get() = this[2]
            set(value) { this[2] = value }
        var right: Int?
            get() = this[3]
            set(value) { this[3] = value }

        // It is expected that valid key codes are positive integers
        val isValid: Boolean
            get() = !this.contains(null) && this.none { it!! < 0 } && this.distinct().size == 4

        constructor(upCtrl: Int? = null, downCtrl: Int? = null, leftCtrl: Int? = null, rightCtrl: Int? = null) {
            this.add(upCtrl)
            this.add(downCtrl)
            this.add(leftCtrl)
            this.add(rightCtrl)
        }
    }

    private inner class CtrlInput constructor(internal var dirIndex: Int) : KeyAdapter() {
        override fun keyPressed(e: KeyEvent) {
            val key = e.keyCode
            if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_SPACE) return
            if (key in playerTemp.ctrls) return

            dirCtrlFields[dirIndex].text = " " + KeyEvent.getKeyText(key)

            if (snakeNum.value > players.size) {
                playerTemp.ctrls[dirIndex] = key
                if (playerTemp.ctrls.isValid) {
                    players.add(playerTemp.clone())
                }
            } else {
                players[snakeNum.value - 1].ctrls[dirIndex] = key
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
