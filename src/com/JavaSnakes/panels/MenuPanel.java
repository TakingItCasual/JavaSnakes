package com.JavaSnakes.panels;

import com.JavaSnakes.Main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

public class MenuPanel extends JPanel {

    private final Main owner;

    private CardLayout cardLayout;

    private JButton newGameButton;
    private JButton settingsButton;
    private JButton quitButton;

    private JButton startGameButton;
    private JSpinner playerSnakeSpinner;
    private JSpinner botSnakeSpinner;
    private JCheckBox wallCheckBox;
    private JButton toMainButton1;

    private JButton toMainButton2;
    private JSpinner mapWidthSpinner;
    private JSpinner mapHeightSpinner;
    private JSpinner frameDelaySpinner;

    private int maxSnakeCount;

    public MenuPanel(Main setOwner) {
        owner = setOwner;

        maxSnakeCount = 8;

        setLayout(new CardLayout());

        createMainMenuCard();
        createNewGameCard();
        createSettingsCard();

        cardLayout = (CardLayout) getLayout();
        setPreferredSize(new Dimension(300, 300));
    }

    private void createMainMenuCard() {
        newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> toNewGameCard());
        settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> toSettingsCard());
        quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> System.exit(0));

        JPanel gridBag = new JPanel(new GridBagLayout());
        gridBag.add(newGameButton, gridConstraint(0, 0));
        gridBag.add(settingsButton, gridConstraint(1, 0));
        gridBag.add(quitButton, gridConstraint(2, 0));

        add(gridBag, "main menu card");
    }

    private void createNewGameCard() {
        startGameButton = new JButton("Start Game");
        startGameButton.addActionListener(e -> startGame());
        JLabel playerSnakeCountLabel = new JLabel("Player count:");
        playerSnakeSpinner = new JSpinner(new SpinnerNumberModel(1, 0, maxSnakeCount, 1));
        playerSnakeSpinner.addChangeListener(e -> playerSnakeCountChanged());
        JLabel botSnakeCountLabel = new JLabel("Bot count:");
        botSnakeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, maxSnakeCount, 1));
        botSnakeSpinner.addChangeListener(e -> botSnakeCountChanged());
        wallCheckBox = new JCheckBox("Include walls");
        toMainButton1 = new JButton("Back");
        toMainButton1.addActionListener(e -> toMainMenuCard());

        JPanel gridBag = new JPanel(new GridBagLayout());
        gridBag.add(startGameButton, gridConstraint(0, 0, 2));
        gridBag.add(playerSnakeCountLabel, gridConstraint(1, 0));
        gridBag.add(playerSnakeSpinner, gridConstraint(1, 1));
        gridBag.add(botSnakeCountLabel, gridConstraint(2, 0));
        gridBag.add(botSnakeSpinner, gridConstraint(2, 1));
        gridBag.add(wallCheckBox, gridConstraint(3, 0));
        gridBag.add(toMainButton1, gridConstraint(4, 0, 2));

        add(gridBag, "new game card");
    }

    private void createSettingsCard() {
        toMainButton2 = new JButton("Back");
        toMainButton2.addActionListener(e -> toMainMenuCard());
        JLabel mapWidthLabel = new JLabel("Map width:");
        mapWidthSpinner = new JSpinner(new SpinnerNumberModel(30, 20, 100, 1));
        JLabel mapHeightLabel = new JLabel("Map height:");
        mapHeightSpinner = new JSpinner(new SpinnerNumberModel(20, 20, 100, 1));
        JLabel frameDelayLabel = new JLabel("Frame delay:");
        frameDelaySpinner = new JSpinner(new SpinnerNumberModel(100, 25, 1000, 25));
        frameDelaySpinner.addChangeListener(e -> frameDelayChanged());
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);

        JPanel gridBag = new JPanel(new GridBagLayout());
        gridBag.add(toMainButton2, gridConstraint(0, 0, 2));
        gridBag.add(mapWidthLabel, gridConstraint(1, 0));
        gridBag.add(mapWidthSpinner, gridConstraint(1, 1));
        gridBag.add(mapHeightLabel, gridConstraint(2, 0));
        gridBag.add(mapHeightSpinner, gridConstraint(2, 1));
        gridBag.add(frameDelayLabel, gridConstraint(3, 0));
        gridBag.add(frameDelaySpinner, gridConstraint(3, 1));
        gridBag.add(separator, gridConstraint(4, 0, 2, 5, 5));

        add(gridBag, "settings card");
    }

    private GridBagConstraints gridConstraint(int y, int x) {
        return gridConstraint(y, x, 1);
    }

    private GridBagConstraints gridConstraint(int y, int x, int w) {
        return gridConstraint(y, x, w, 0, 0);
    }

    private GridBagConstraints gridConstraint(int y, int x, int w, int topPad, int bottomPad) {
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.gridy = y;
        constraint.gridx = x;
        constraint.gridwidth = w;
        constraint.insets = new Insets(topPad, 0, bottomPad, 0);
        return constraint;
    }

    private void toMainMenuCard() {
        cardLayout.show(this, "main menu card");
        newGameButton.requestFocus();
    }

    private void toNewGameCard() {
        int minDimension = Math.min((int) mapWidthSpinner.getValue(), (int) mapHeightSpinner.getValue());
        maxSnakeCount = ((minDimension - 11) / 6 + 1) * 4;
        // Done to resize the spinners when the number of digits in maxSnakeCount changes
        playerSnakeSpinner.setModel(new SpinnerNumberModel(1, 0, maxSnakeCount, 1));
        botSnakeSpinner.setModel(new SpinnerNumberModel(0, 0, maxSnakeCount, 1));

        cardLayout.show(this, "new game card");
        startGameButton.requestFocus();
    }

    private void toSettingsCard() {
        cardLayout.show(this, "settings card");
        toMainButton2.requestFocus();
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

    private void frameDelayChanged() {
        int frameDelay = (int) frameDelaySpinner.getValue();
        if (frameDelay % 25 != 0) {
            frameDelaySpinner.setValue(Math.round((float) frameDelay / 25) * 25);
        }
    }

    private void startGame() {
        GamePanel gamePanel = new GamePanel(owner, (int) frameDelaySpinner.getValue(), 10);

        gamePanel.initBoard(
            (int) mapWidthSpinner.getValue(),
            (int) mapHeightSpinner.getValue(),
            wallCheckBox.isSelected()
        );

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
