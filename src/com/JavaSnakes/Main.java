package com.JavaSnakes;

import javax.swing.JFrame;

public class Main extends JFrame {

    public Main() {
        add(new GameLoop(100, 10));

        setResizable(false);
        pack();

        setTitle("Snake");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }
}
