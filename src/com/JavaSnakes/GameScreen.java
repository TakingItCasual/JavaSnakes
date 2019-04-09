package com.JavaSnakes;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class GameScreen extends JFrame {

    public GameScreen() {
        add(new GameMap());

        setResizable(false);
        pack();

        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame ex = new GameScreen();
            ex.setVisible(true);
        });
    }
}
