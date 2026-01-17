package view.swing;

import javax.swing.*;
import java.awt.*;
import controller.GameController;

/**
 * Menu principal du jeu (pause menu).
 * Options: Sauver, Charger, Nouveau, Quitter
 */
public class MenuDialog extends JDialog {

    private GameController controller;
    private GameFrame frame;

    public MenuDialog(GameFrame frame, GameController controller) {
        super(frame, "Menu", true);
        this.frame = frame;
        this.controller = controller;

        setupDialog();
        createComponents();
    }

    private void setupDialog() {
        setSize(350, 450);
        setLocationRelativeTo(frame);
        getContentPane().setBackground(new Color(250, 250, 250));
        setLayout(new BorderLayout(10, 10));
    }

    private void createComponents() {
        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(63, 81, 181));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel title = new JLabel("MENU");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JButton saveBtn = createMenuButton("Sauvegarder", new Color(76, 175, 80));
        saveBtn.addActionListener(e -> {
            dispose();
            SaveLoadDialog dialog = new SaveLoadDialog(frame, controller, SaveLoadDialog.Mode.SAVE);
            dialog.setVisible(true);
            frame.refresh();
        });
        buttonsPanel.add(saveBtn);
        buttonsPanel.add(Box.createVerticalStrut(12));

        JButton loadBtn = createMenuButton("Charger", new Color(33, 150, 243));
        loadBtn.addActionListener(e -> {
            dispose();
            SaveLoadDialog dialog = new SaveLoadDialog(frame, controller, SaveLoadDialog.Mode.LOAD);
            dialog.setVisible(true);
            if (dialog.getSelectedSlot() != null) {
                frame.updateModel(controller.getModel());
            } else {
                frame.refresh();
            }
        });
        buttonsPanel.add(loadBtn);
        buttonsPanel.add(Box.createVerticalStrut(12));

        JButton newBtn = createMenuButton("Nouvelle Partie", new Color(156, 39, 176));
        newBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous vraiment commencer une nouvelle partie?\nLa progression non sauvegardee sera perdue.",
                    "Nouvelle Partie",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String cityName = JOptionPane.showInputDialog(this,
                        "Nom de votre nouvelle ville:", "SimCity");
                if (cityName == null || cityName.trim().isEmpty()) {
                    cityName = "SimCity";
                }
                controller.startConsole(cityName.trim());
                frame.updateModel(controller.getModel());
                dispose();
            }
        });
        buttonsPanel.add(newBtn);
        buttonsPanel.add(Box.createVerticalStrut(12));

        JButton quitBtn = createMenuButton("Quitter", new Color(244, 67, 54));
        quitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous vraiment quitter?\nLa progression non sauvegardee sera perdue.",
                    "Quitter",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        buttonsPanel.add(quitBtn);

        add(buttonsPanel, BorderLayout.CENTER);

        // Resume button at bottom
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(245, 245, 245));

        JButton resumeBtn = new JButton("Reprendre");
        resumeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        resumeBtn.addActionListener(e -> dispose());
        footer.add(resumeBtn);

        add(footer, BorderLayout.SOUTH);
    }

    private JButton createMenuButton(String text, Color accentColor) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(33, 33, 33));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15))));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(245, 245, 245));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });

        return btn;
    }
}
