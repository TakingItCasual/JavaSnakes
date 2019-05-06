package com.JavaSnakes.panels;

import com.JavaSnakes.Main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class MenuPanel extends JPanel {

    private final Main owner;

    private CardLayout cardLayout;

    private JButton newGameButton;
    private JButton settingsButton;
    private JButton quitButton;

    private JButton startGameButton;
    private JLabel playerSnakeCountLabel;
    private JSpinner playerSnakeSpinner;
    private JLabel botSnakeCountLabel;
    private JSpinner botSnakeSpinner;
    private JCheckBox wallCheckBox;
    private JButton toMainButton;

    private int mapW;
    private int mapH;
    private int maxSnakeCount;

    public MenuPanel(Main setOwner) {
        owner = setOwner;

        mapW = 30;
        mapH = 20;
        maxSnakeCount = 8;

        setLayout(new CardLayout());

        createMainMenuCard();
        createNewGameCard();

        //((SpinnerNumberModel) playerSnakeSpinner.getModel()).setMaximum(maxSnakeCount + 1);
        //((SpinnerNumberModel) botSnakeSpinner.getModel()).setMaximum(maxSnakeCount + 1);

        cardLayout = (CardLayout) getLayout();
        setPreferredSize(new Dimension(300, 300));
    }

    private void createMainMenuCard() {
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> toNewGameCard());
        settingsButton = new JButton("Settings");
        quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));

        GridBagConstraints constraint = new GridBagConstraints();
        constraint.fill = GridBagConstraints.HORIZONTAL;

        JPanel gridBag = new JPanel(new GridBagLayout());
        constraint.gridy = 0;
        constraint.gridx = 0;
        gridBag.add(newGameButton, constraint);
        constraint.gridy = 1;
        gridBag.add(settingsButton, constraint);
        constraint.gridy = 2;
        gridBag.add(quitButton, constraint);

        add(gridBag, "main menu card");
    }

    private void createNewGameCard() {
        startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(e -> startGame());
        wallCheckBox = new JCheckBox("Include walls");
        playerSnakeCountLabel = new JLabel("Player count:");
        playerSnakeSpinner = new JSpinner(new SpinnerNumberModel(1, 0, maxSnakeCount, 1));
        playerSnakeSpinner.addChangeListener(e -> playerSnakeCountChanged());
        botSnakeCountLabel = new JLabel("Bot count:");
        botSnakeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, maxSnakeCount, 1));
        botSnakeSpinner.addChangeListener(e -> botSnakeCountChanged());
        toMainButton = new JButton("Back");
        toMainButton.addActionListener(e -> toMainMenuCard());

        GridBagConstraints constraint = new GridBagConstraints();
        constraint.fill = GridBagConstraints.HORIZONTAL;

        JPanel gridBag = new JPanel(new GridBagLayout());
        constraint.gridy = 0;
        constraint.gridx = 0;
        constraint.gridwidth = 2;
        gridBag.add(startGameButton, constraint);
        constraint.gridy = 1;
        constraint.gridwidth = 1;
        gridBag.add(playerSnakeCountLabel, constraint);
        constraint.gridx = 1;
        gridBag.add(playerSnakeSpinner, constraint);
        constraint.gridy = 2;
        constraint.gridx = 0;
        gridBag.add(botSnakeCountLabel, constraint);
        constraint.gridx = 1;
        gridBag.add(botSnakeSpinner, constraint);
        constraint.gridy = 3;
        constraint.gridx = 0;
        gridBag.add(wallCheckBox, constraint);
        constraint.gridy = 4;
        constraint.gridwidth = 2;
        gridBag.add(toMainButton, constraint);

        add(gridBag, "new game card");
    }

    private void toMainMenuCard() {
        cardLayout.show(this, "main menu card");
        newGameButton.requestFocus();
    }

    private void toNewGameCard() {
        cardLayout.show(this, "new game card");
        startGameButton.requestFocus();
    }

    private void playerSnakeCountChanged() {
        if ((int) playerSnakeSpinner.getValue() == 0 && (int) botSnakeSpinner.getValue() == 0) {
            botSnakeSpinner.setValue(1);
        } else if ((int) playerSnakeSpinner.getValue() + (int) botSnakeSpinner.getValue() > maxSnakeCount) {
            botSnakeSpinner.setValue(maxSnakeCount - (int) playerSnakeSpinner.getValue());
        }
    }

    private void botSnakeCountChanged() {
        if ((int) botSnakeSpinner.getValue() == 0 && (int) playerSnakeSpinner.getValue() == 0) {
            playerSnakeSpinner.setValue(1);
        } else if ((int) botSnakeSpinner.getValue() + (int) playerSnakeSpinner.getValue() > maxSnakeCount) {
            playerSnakeSpinner.setValue(maxSnakeCount - (int) botSnakeSpinner.getValue());
        }
    }

    private void startGame() {
        GamePanel gamePanel = new GamePanel(owner, 100, 10);

        gamePanel.initBoard(mapW, mapH, wallCheckBox.isSelected());

        for (int i = 0; i < (int) playerSnakeSpinner.getValue(); i++) {
            gamePanel.createPlayerSnake(Color.blue, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        }
        for (int i = 0; i < (int) botSnakeSpinner.getValue(); i++) {
            gamePanel.createBotSnake(Color.black);
        }

        gamePanel.initLoop();
        owner.changePanel(gamePanel);
    }
}
