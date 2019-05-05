package com.JavaSnakes;

import com.JavaSnakes.panels.MenuPanel;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main extends JFrame {

    public Main() {
        setTitle("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        changePanel(new MenuPanel(this));
    }

    public void changePanel(JPanel newPanel) {
        setVisible(false);

        getContentPane().removeAll();
        add(newPanel);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (java.lang.Exception e) {
            JOptionPane.showMessageDialog(null, "GUI Error");
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main();
            }
        });
    }
}
