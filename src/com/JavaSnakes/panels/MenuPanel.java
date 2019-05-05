package com.JavaSnakes.panels;

import com.JavaSnakes.Main;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JButton;

public class MenuPanel extends JPanel {

    private final Main owner;

    private CardLayout cardLayout;

    private JButton newGameButton, settingsButton, quitButton;
    private JButton startGameButton, toMainButton;
    private JPanel buttonGrid1, buttonGrid2;

    public MenuPanel(Main setOwner) {
        owner = setOwner;

        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> cardLayout.show(this, "new game card"));
        settingsButton = new JButton("Settings");
        quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));

        startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(e -> owner.changePanel(new GamePanel(100, 10)));
        toMainButton = new JButton("Back");
        toMainButton.addActionListener(e -> cardLayout.show(this, "main menu card"));

        buttonGrid1 = new JPanel(new GridLayout(3, 1));
        buttonGrid1.add(newGameButton);
        buttonGrid1.add(settingsButton);
        buttonGrid1.add(quitButton);
        JPanel gridBag1 = new JPanel(new GridBagLayout());
        gridBag1.add(buttonGrid1);

        buttonGrid2 = new JPanel(new GridLayout(2, 1));
        buttonGrid2.add(startGameButton);
        buttonGrid2.add(toMainButton);
        JPanel gridBag2 = new JPanel(new GridBagLayout());
        gridBag2.add(buttonGrid2);

        setLayout(new CardLayout());
        add(gridBag1, "main menu card");
        add(gridBag2, "new game card");
        cardLayout = (CardLayout) getLayout();

        setPreferredSize(new Dimension(300, 300));
    }
}
