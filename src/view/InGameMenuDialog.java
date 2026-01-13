package view;

import controller.GameController;
import javax.swing.*;
import java.awt.*;

/**
 * Main in-game menu.
 * Provides access to Shop, Settings, Save/Load and Main Menu.
 */
public class InGameMenuDialog extends JDialog {

    private final GameController controller;

    public InGameMenuDialog(Window owner, GameController controller) {
        super(owner, "City Management Menu", ModalityType.APPLICATION_MODAL);
        this.controller = controller;

        setupFrame();
        createComponents();
    }

    private void setupFrame() {
        setSize(400, 500);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(new Color(25, 25, 25));
        setLayout(new GridBagLayout());
    }

    private void createComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 40, 10, 40);

        JLabel title = new JLabel("MENU", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(Color.CYAN);
        add(title, gbc);

        add(Box.createVerticalStrut(30), gbc);

        add(createMenuButton("BOUTIQUE", new Color(255, 193, 7), e -> {
            new ShopDialog(this, controller).setVisible(true);
        }), gbc);

        add(createMenuButton("CONFIGURATION", new Color(0, 150, 136), e -> {
            new CitySettingsDialog(this, controller).setVisible(true);
        }), gbc);

        add(createMenuButton("SAVE / LOAD", new Color(63, 81, 181), e -> {
            new SaveLoadDialog(this, controller, true).setVisible(true);
        }), gbc);

        add(Box.createVerticalStrut(20), gbc);

        add(createMenuButton("MAIN MENU", new Color(158, 158, 158), e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Return to Main Menu? Unsaved progress will be lost.",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                controller.start();
                dispose();
            }
        }), gbc);

        add(Box.createVerticalStrut(10), gbc);

        JButton btnResume = new JButton("RESUME GAME");
        btnResume.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnResume.setForeground(Color.WHITE);
        btnResume.setBackground(new Color(40, 40, 40));
        btnResume.setFocusPainted(false);
        btnResume.addActionListener(e -> dispose());
        add(btnResume, gbc);
    }

    private JButton createMenuButton(String text, Color bg, java.awt.event.ActionListener al) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(250, 45));
        btn.addActionListener(al);
        return btn;
    }
}
