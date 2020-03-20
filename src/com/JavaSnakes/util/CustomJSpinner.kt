package com.JavaSnakes.util

import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.JSpinner
import javax.swing.JSpinner.DefaultEditor
import javax.swing.SpinnerNumberModel

class CustomJSpinner {
    constructor(value: Int, min: Int?, max: Int?, stepSize: Int = 1) {
        setModel(value, min, max, stepSize)
    }

    val spinner = JSpinner()
    var value: Int
        get() = spinner.value as Int
        set(value) { spinner.value = value }

    fun setModel(value: Int, min: Int?, max: Int?, stepSize: Int = 1) {
        spinner.model = SpinnerNumberModel(value, min, max, stepSize)
        if (min != null || max != null) { addRoundToLimitsListener() }
    }

    // Value rounded to nearest limit, if out of bounds
    private fun addRoundToLimitsListener() {
        val textField = (spinner.editor as DefaultEditor).textField
        for (listener in textField.focusListeners) {
            // Unsure if needed, upcoming listener is added multiple times
            textField.removeFocusListener(listener)
        }
        textField.addFocusListener(object : FocusListener {
            override fun focusLost(e: FocusEvent) {
                val dirtyValue = textField.text.toIntOrNull() ?: return
                val max = (spinner.model as SpinnerNumberModel).maximum as Int?
                val min = (spinner.model as SpinnerNumberModel).minimum as Int?
                if (max != null && dirtyValue > max) {
                    value = max
                } else if (min != null && dirtyValue < min) {
                    value = min
                }
            }

            override fun focusGained(e: FocusEvent) {} // Unused abstract method
        })
    }
}
