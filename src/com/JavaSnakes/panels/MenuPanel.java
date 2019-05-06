package com.JavaSnakes.panels;

import com.JavaSnakes.Main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
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

    private JPanel buttonGrid1, buttonGrid2;

    private int mapW;
    private int mapH;
    private int maxSnakeCount;

    public MenuPanel(Main setOwner) {
        owner = setOwner;

        mapW = 30;
        mapH = 20;
        maxSnakeCount = 8;

        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> toNewGameCard());
        settingsButton = new JButton("Settings");
        quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));

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

        //((SpinnerNumberModel) playerSnakeSpinner.getModel()).setMaximum(maxSnakeCount + 1);
        //((SpinnerNumberModel) botSnakeSpinner.getModel()).setMaximum(maxSnakeCount + 1);

        buttonGrid1 = new JPanel(new GridLayout(3, 1));
        buttonGrid1.add(newGameButton);
        buttonGrid1.add(settingsButton);
        buttonGrid1.add(quitButton);
        JPanel gridBag1 = new JPanel(new GridBagLayout());
        gridBag1.add(buttonGrid1);

        buttonGrid2 = new JPanel(new GridLayout(5, 2));
        buttonGrid2.add(startGameButton);
        buttonGrid2.add(playerSnakeCountLabel);
        buttonGrid2.add(playerSnakeSpinner);
        buttonGrid2.add(botSnakeCountLabel);
        buttonGrid2.add(botSnakeSpinner);
        buttonGrid2.add(wallCheckBox);
        buttonGrid2.add(toMainButton);
        JPanel gridBag2 = new JPanel(new GridBagLayout());
        gridBag2.add(buttonGrid2);

        setLayout(new CardLayout());
        add(gridBag1, "main menu card");
        add(gridBag2, "new game card");
        cardLayout = (CardLayout) getLayout();

        setPreferredSize(new Dimension(300, 300));
    }

    private void toNewGameCard() {
        cardLayout.show(this, "new game card");
        startGameButton.requestFocus();
    }

    private void toMainMenuCard() {
        cardLayout.show(this, "main menu card");
        newGameButton.requestFocus();
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
