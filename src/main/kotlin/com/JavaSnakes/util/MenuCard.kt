package com.javasnakes.util

import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JPanel

class MenuCard : JPanel() {
    init {
        layout = GridBagLayout()
    }

    fun addInGrid(component: Component, y: Int, x: Int, w: Int = 1, padding: Insets? = null) {
        val constraint = GridBagConstraints()
        constraint.fill = GridBagConstraints.HORIZONTAL
        constraint.gridy = y
        constraint.gridx = x
        constraint.gridwidth = w
        if (padding != null) constraint.insets = padding
        add(component, constraint)
    }
}
