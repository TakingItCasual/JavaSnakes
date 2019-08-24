package com.JavaSnakes.util

import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class CustomJSpinner {
    val spinner = JSpinner()
    var value: Int
        get() = spinner.value as Int
        set(value) { spinner.value = value }

    constructor(value: Int, min: Int?, max: Int?, stepSize: Int = 1) {
        setModel(value, min, max, stepSize)
    }

    fun setModel(value: Int, min: Int?, max: Int?, stepSize: Int = 1) {
        spinner.model = SpinnerNumberModel(value, min, max, stepSize)
    }
}
