package com.JavaSnakes.panels;

import com.JavaSnakes.Main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
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
    private JSpinner snakeNumSpinner;
    private JTextField[] dirCtrlFields;

    private int maxSnakeCount;
    private List<int[]> playerControls;
    private int[] playerControlsTemp;

    public MenuPanel(Main setOwner) {
        owner = setOwner;

        maxSnakeCount = 8;
        playerControls = new ArrayList<>();
        playerControls.add(new int[]{KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT});
        playerControls.add(new int[]{KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D});
        playerControlsTemp = new int[4];

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
        gridBag.add(newGameButton, constraint(0, 0));
        gridBag.add(settingsButton, constraint(1, 0));
        gridBag.add(quitButton, constraint(2, 0));

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
        gridBag.add(startGameButton, constraint(0, 0, 2));
        gridBag.add(playerSnakeCountLabel, constraint(1, 0));
        gridBag.add(playerSnakeSpinner, constraint(1, 1, 1, new Insets(0, 5, 0, 0)));
        gridBag.add(botSnakeCountLabel, constraint(2, 0));
        gridBag.add(botSnakeSpinner, constraint(2, 1, 1, new Insets(0, 5, 0, 0)));
        gridBag.add(wallCheckBox, constraint(3, 0, 2));
        gridBag.add(toMainButton1, constraint(4, 0, 2));

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
        JLabel snakeNumLabel = new JLabel("Player snake:");
        snakeNumSpinner = new JSpinner(new SpinnerNumberModel(1, 1, null, 1));
        snakeNumSpinner.addChangeListener(e -> snakeNumChanged());
        JLabel upCtrlLabel = new JLabel("Up key:");
        JLabel downCtrlLabel = new JLabel("Down key:");
        JLabel leftCtrlLabel = new JLabel("Left key:");
        JLabel rightCtrlLabel = new JLabel("Right key:");

        dirCtrlFields = new JTextField[4];
        for (int i = 0; i < dirCtrlFields.length; i++) {
            dirCtrlFields[i] = new JTextField("");
            dirCtrlFields[i].addKeyListener(new ctrlInput(i));
            dirCtrlFields[i].addFocusListener(new ctrlInputFocus(i));
        }

        String fontFamily = dirCtrlFields[0].getFont().getFamily();
        int fontSize = dirCtrlFields[0].getFont().getSize();
        for (JTextField dirCtrlField : dirCtrlFields) {
            dirCtrlField.setEditable(false);
            dirCtrlField.setFont(new Font(fontFamily, Font.BOLD, fontSize));
        }

        snakeNumChanged(); // Fill out player settings with first player's info

        JPanel gridBag = new JPanel(new GridBagLayout());
        gridBag.add(toMainButton2, constraint(0, 0, 2));
        gridBag.add(mapWidthLabel, constraint(1, 0));
        gridBag.add(mapWidthSpinner, constraint(1, 1));
        gridBag.add(mapHeightLabel, constraint(2, 0));
        gridBag.add(mapHeightSpinner, constraint(2, 1));
        gridBag.add(frameDelayLabel, constraint(3, 0));
        gridBag.add(frameDelaySpinner, constraint(3, 1));
        gridBag.add(separator, constraint(4, 0, 2, new Insets(5, 0, 5, 0)));
        gridBag.add(snakeNumLabel, constraint(5, 0));
        gridBag.add(snakeNumSpinner, constraint(5, 1));
        gridBag.add(upCtrlLabel, constraint(6, 0));
        gridBag.add(dirCtrlFields[0], constraint(6, 1));
        gridBag.add(downCtrlLabel, constraint(7, 0));
        gridBag.add(dirCtrlFields[1], constraint(7, 1));
        gridBag.add(leftCtrlLabel, constraint(8, 0));
        gridBag.add(dirCtrlFields[2], constraint(8, 1));
        gridBag.add(rightCtrlLabel, constraint(9, 0));
        gridBag.add(dirCtrlFields[3], constraint(9, 1));

        add(gridBag, "settings card");
    }

    private GridBagConstraints constraint(int y, int x) {
        return constraint(y, x, 1);
    }

    private GridBagConstraints constraint(int y, int x, int w) {
        return constraint(y, x, w, null);
    }

    private GridBagConstraints constraint(int y, int x, int w, Insets padding) {
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.fill = GridBagConstraints.HORIZONTAL;
        constraint.gridy = y;
        constraint.gridx = x;
        constraint.gridwidth = w;
        if (padding != null) constraint.insets = padding;
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
        } else if ((int) playerSnakeSpinner.getValue() > playerControls.size()) {
            playerSnakeSpinner.setValue(playerControls.size());
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

    private void snakeNumChanged() {
        int playerNum = (int) snakeNumSpinner.getValue();
        if (playerNum > playerControls.size()) {
            snakeNumSpinner.setValue(playerControls.size() + 1);
            // It is expected that valid key codes are positive integers
            playerControlsTemp = new int[]{-1, -1, -1, -1};
            for (JTextField dirCtrlField : dirCtrlFields) {
                dirCtrlField.setText("undefined");
            }
        } else {
            playerControlsTemp = playerControls.get(playerNum - 1).clone();
            for (int i = 0; i < dirCtrlFields.length; i++) {
                dirCtrlFields[i].setText(KeyEvent.getKeyText(playerControlsTemp[i]));
            }
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
            gamePanel.createPlayerSnake(
                Color.blue,
                playerControls.get(i)[0], playerControls.get(i)[1], playerControls.get(i)[2], playerControls.get(i)[3]
            );
        }
        for (int i = 0; i < (int) botSnakeSpinner.getValue(); i++) {
            gamePanel.createBotSnake(Color.black);
        }

        gamePanel.initLoop();
        owner.changePanel(gamePanel);
    }

    private class ctrlInput extends KeyAdapter {

        int dirIndex;

        private ctrlInput(int setDirIndex) {
            dirIndex = setDirIndex;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_SPACE) return;
            if (IntStream.of(playerControlsTemp).anyMatch(x -> x == key)) return;

            playerControlsTemp[dirIndex] = key;
            dirCtrlFields[dirIndex].setText(" " + KeyEvent.getKeyText(key));

            int playerNum = (int) snakeNumSpinner.getValue();
            if (playerNum > playerControls.size()) {
                if (IntStream.of(playerControlsTemp).noneMatch(x -> x == -1)) {
                    playerControls.add(playerControlsTemp.clone());
                }
            } else {
                playerControls.get(playerNum - 1)[dirIndex] = key;
            }
        }
    }

    private class ctrlInputFocus implements FocusListener {

        int dirIndex;

        private ctrlInputFocus(int setDirIndex) {
            dirIndex = setDirIndex;
        }

        @Override
        public void focusGained(FocusEvent e) {
            dirCtrlFields[dirIndex].setText(" " + dirCtrlFields[dirIndex].getText().trim());
        }

        @Override
        public void focusLost(FocusEvent e) {
            dirCtrlFields[dirIndex].setText(dirCtrlFields[dirIndex].getText().trim());
        }
    }
}
