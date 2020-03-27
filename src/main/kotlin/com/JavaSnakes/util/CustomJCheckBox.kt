package com.javasnakes.util

import javax.swing.JCheckBox

class CustomJCheckBox {
    val checkBox = JCheckBox()
    var value: Boolean
        get() = checkBox.isSelected
        set(value) { checkBox.isSelected = value }
}
