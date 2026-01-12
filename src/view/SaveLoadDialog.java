package view;

import controller.GameController;
import service.dto.SaveMetadata;
import javax.swing.*;
import java.awt.*;

/**
 * Dialog for selecting a save/load slot.
 * Purely functional: only handles slot management.
 */
public class SaveLoadDialog extends JDialog {

    private final GameController controller;
    private final boolean canSave;

    public SaveLoadDialog(Window owner, GameController controller, boolean canSave) {
        super(owner, canSave ? "Save / Load Game" : "Load Game", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.canSave = canSave;

        setupFrame();
        createComponents();
    }

    private void setupFrame() {
        setSize(480, 550);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(new Color(30, 30, 30));
        setLayout(new BorderLayout());
    }

    private void createComponents() {
        // --- Header ---
        JPanel header = new JPanel();
        header.setBackground(new Color(40, 40, 40));
        JLabel title = new JLabel("GAME SLOTS");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // --- Slots List ---
        JPanel slotsPanel = new JPanel();
        slotsPanel.setLayout(new BoxLayout(slotsPanel, BoxLayout.Y_AXIS));
        slotsPanel.setBackground(new Color(30, 30, 30));
        slotsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        slotsPanel.add(createSlotPanel("autosave", "Auto-Save"));
        slotsPanel.add(Box.createVerticalStrut(15));

        for (int i = 1; i <= 5; i++) {
            slotsPanel.add(createSlotPanel("slot" + i, "Save Slot " + i));
            slotsPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane scroll = new JScrollPane(slotsPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);

        JButton btnClose = new JButton("BACK");
        btnClose.addActionListener(e -> dispose());
        add(btnClose, BorderLayout.SOUTH);
    }

    private JPanel createSlotPanel(String id, String displayName) {
        SaveMetadata meta = controller.getSaveMetadata(id);

        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setBackground(new Color(45, 45, 45));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(meta.exists() ? Color.DARK_GRAY : Color.BLACK),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setForeground(meta.exists() ? Color.WHITE : Color.GRAY);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        info.add(nameLabel);

        if (meta.exists()) {
            JLabel stats = new JLabel(
                    String.format("%s - Day %d - %.0f Coins", meta.cityName(), meta.day(), meta.coins()));
            stats.setForeground(Color.LIGHT_GRAY);
            stats.setFont(new Font("SansSerif", Font.PLAIN, 11));
            info.add(stats);
        } else {
            JLabel empty = new JLabel("Empty");
            empty.setForeground(new Color(80, 80, 80));
            empty.setFont(new Font("SansSerif", Font.ITALIC, 10));
            info.add(empty);
        }
        p.add(info, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);

        if (canSave && !id.equals("autosave")) {
            JButton btnSave = new JButton("SAVE");
            btnSave.addActionListener(e -> {
                controller.handleSave(id);
                dispose();
                new SaveLoadDialog(getOwner(), controller, canSave).setVisible(true); // Refresh
            });
            actions.add(btnSave);
        }

        if (meta.exists()) {
            JButton btnLoad = new JButton("LOAD");
            btnLoad.addActionListener(e -> {
                controller.handleLoadGame(id);
                dispose();
            });
            actions.add(btnLoad);
        }

        p.add(actions, BorderLayout.EAST);
        p.setMaximumSize(new Dimension(440, 70));
        return p;
    }
}
