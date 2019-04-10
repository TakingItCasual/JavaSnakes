package com.JavaSnakes;

import javax.swing.JFrame;

public class GameScreen extends JFrame {

    public GameScreen() {
        add(new GameLoop());

        setResizable(false);
        pack();

        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new GameScreen();
    }
}
