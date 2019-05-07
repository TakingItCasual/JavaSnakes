package com.JavaSnakes.util;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JPanel;

public class MenuCard extends JPanel {

    public MenuCard() {
        setLayout(new GridBagLayout());
    }

    public void addInGrid(Component component, int y, int x) {
        addInGrid(component, y, x, 1);
    }

    public void addInGrid(Component component, int y, int x, int w) {
        addInGrid(component, y, x, w, null);
    }

    public void addInGrid(Component component, int y, int x, int w, Insets padding) {
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.gridy = y;
        constraint.gridx = x;
        constraint.gridwidth = w;
        if (padding != null) constraint.insets = padding;
        add(component, constraint);
    }
}
