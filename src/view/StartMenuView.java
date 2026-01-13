package view;

import controller.GameController;
import javax.swing.*;
import java.awt.*;

/**
 * Main menu screen for the game.
 */
public class StartMenuView extends JFrame {

    private final GameController controller;

    public StartMenuView(GameController controller) {
        this.controller = controller;
        setupFrame();
        createComponents();
    }

    private void setupFrame() {
        setTitle("Power Grid Tycoon - Main Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(18, 18, 18));
        setLayout(new GridBagLayout());
    }

    private void createComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 50, 10, 50);

        JLabel title = new JLabel("POWER GRID TYCOON", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 48));
        title.setForeground(new Color(0, 255, 255));
        add(title, gbc);

        add(Box.createVerticalStrut(50), gbc);

        JButton btnNew = createMenuButton("NEW GAME", new Color(63, 81, 181));
        btnNew.addActionListener(e -> handleNewGameRequest());
        add(btnNew, gbc);

        JButton btnLoad = createMenuButton("LOAD GAME", new Color(76, 175, 80));
        btnLoad.addActionListener(e -> new SaveLoadDialog(this, controller, false).setVisible(true));
        add(btnLoad, gbc);

        JButton btnExit = createMenuButton("EXIT", new Color(183, 28, 28));
        btnExit.addActionListener(e -> System.exit(0));
        add(btnExit, gbc);
    }

    private JButton createMenuButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 20));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(300, 50));
        return btn;
    }

    private void handleNewGameRequest() {
        String name = JOptionPane.showInputDialog(this, "Enter City Name:", "New Game", JOptionPane.QUESTION_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            controller.handleNewGame(name.trim());
        }
    }
}
