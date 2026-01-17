package view.swing;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import controller.GameController;
import service.dto.SaveMetadata;

/**
 * Dialog de sauvegarde et chargement de partie.
 * Affiche les slots: autosave, day_save, slot1-5
 */
public class SaveLoadDialog extends JDialog {

    public enum Mode {
        SAVE, LOAD
    }

    private static final String[] SYSTEM_SLOTS = { "autosave", "day_save" };
    private static final String[] MANUAL_SLOTS = { "slot1", "slot2", "slot3", "slot4", "slot5" };

    private final GameController controller;
    private final Mode mode;
    private String selectedSlot = null;

    public SaveLoadDialog(Window owner, GameController controller, Mode mode) {
        super(owner, mode == Mode.SAVE ? "Sauvegarder la Partie" : "Charger une Partie",
                ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.mode = mode;

        setupFrame();
        createComponents();
    }

    private void setupFrame() {
        setSize(500, 450);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(new Color(250, 250, 250));
        setLayout(new BorderLayout(10, 10));
    }

    private void createComponents() {
        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(63, 81, 181));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel title = new JLabel(mode == Mode.SAVE ? "SAUVEGARDER" : "CHARGER");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Slots panel
        JPanel slotsPanel = new JPanel();
        slotsPanel.setLayout(new BoxLayout(slotsPanel, BoxLayout.Y_AXIS));
        slotsPanel.setBackground(Color.WHITE);
        slotsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // System slots section
        JLabel systemLabel = new JLabel("Sauvegardes Système");
        systemLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        systemLabel.setForeground(new Color(100, 100, 100));
        slotsPanel.add(systemLabel);
        slotsPanel.add(Box.createVerticalStrut(8));

        for (String slot : SYSTEM_SLOTS) {
            slotsPanel.add(createSlotPanel(slot, true));
            slotsPanel.add(Box.createVerticalStrut(5));
        }

        slotsPanel.add(Box.createVerticalStrut(15));

        // Manual slots section
        JLabel manualLabel = new JLabel("Sauvegardes Manuelles");
        manualLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        manualLabel.setForeground(new Color(100, 100, 100));
        slotsPanel.add(manualLabel);
        slotsPanel.add(Box.createVerticalStrut(8));

        for (String slot : MANUAL_SLOTS) {
            slotsPanel.add(createSlotPanel(slot, false));
            slotsPanel.add(Box.createVerticalStrut(5));
        }

        JScrollPane scroll = new JScrollPane(slotsPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Footer buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(new Color(245, 245, 245));

        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.addActionListener(e -> dispose());
        footer.add(cancelBtn);

        add(footer, BorderLayout.SOUTH);
    }

    private JPanel createSlotPanel(String slotName, boolean isSystem) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Slot info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);

        String displayName = getDisplayName(slotName);
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        String infoText = getSaveInfo(slotName);
        JLabel infoLabel = new JLabel(infoText);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(120, 120, 120));

        infoPanel.add(nameLabel);
        infoPanel.add(infoLabel);
        panel.add(infoPanel, BorderLayout.CENTER);

        // Action button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);

        boolean saveExists = doesSaveExist(slotName);

        if (mode == Mode.LOAD) {
            JButton loadBtn = new JButton("Charger");
            loadBtn.setEnabled(saveExists);
            loadBtn.setBackground(new Color(76, 175, 80));
            loadBtn.setForeground(Color.WHITE);
            loadBtn.addActionListener(e -> {
                selectedSlot = slotName;
                performLoad(slotName);
            });
            buttonPanel.add(loadBtn);
        } else {
            // SAVE mode
            if (isSystem) {
                JLabel protectedLabel = new JLabel("Protégé");
                protectedLabel.setForeground(new Color(200, 200, 200));
                protectedLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
                buttonPanel.add(protectedLabel);
            } else {
                JButton saveBtn = new JButton(saveExists ? "Écraser" : "Sauver");
                saveBtn.setBackground(new Color(33, 150, 243));
                saveBtn.setForeground(Color.WHITE);
                saveBtn.addActionListener(e -> {
                    selectedSlot = slotName;
                    performSave(slotName, saveExists);
                });
                buttonPanel.add(saveBtn);
            }
        }

        panel.add(buttonPanel, BorderLayout.EAST);
        return panel;
    }

    private String getDisplayName(String slotName) {
        switch (slotName) {
            case "autosave":
                return "Sauvegarde Automatique";
            case "day_save":
                return "Progression Journalière";
            case "slot1":
                return "Emplacement 1";
            case "slot2":
                return "Emplacement 2";
            case "slot3":
                return "Emplacement 3";
            case "slot4":
                return "Emplacement 4";
            case "slot5":
                return "Emplacement 5";
            default:
                return slotName;
        }
    }

    private String getSaveInfo(String slotName) {
        if (!doesSaveExist(slotName)) {
            return "Vide";
        }

        try {
            SaveMetadata meta = controller.getSaveMetadata(slotName);
            if (meta != null) {
                return String.format("%s | Jour %d | %.0f pièces | %s",
                        meta.cityName(),
                        meta.day(),
                        meta.coins(),
                        meta.savedAt());
            }
        } catch (Exception e) {
            // Fallback
        }

        File file = new File("saves/" + slotName + ".tycoon");
        if (file.exists()) {
            long lastMod = file.lastModified();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            return "Sauvegardé le " + sdf.format(new java.util.Date(lastMod));
        }

        return "Données inconnues";
    }

    private boolean doesSaveExist(String slotName) {
        File file = new File("saves/" + slotName + ".tycoon");
        return file.exists();
    }

    private void performLoad(String slotName) {
        try {
            controller.loadGame(slotName);

            // Update UI with new model
            if (getOwner() instanceof GameFrame) {
                ((GameFrame) getOwner()).updateModel(controller.getModel());
            }

            JOptionPane.showMessageDialog(this,
                    "Partie chargée depuis: " + getDisplayName(slotName),
                    "Chargement réussi",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSave(String slotName, boolean overwrite) {
        if (overwrite) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous écraser cette sauvegarde?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            controller.saveGame(slotName);
            JOptionPane.showMessageDialog(this,
                    "Partie sauvegardée dans: " + getDisplayName(slotName),
                    "Sauvegarde réussie",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la sauvegarde: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getSelectedSlot() {
        return selectedSlot;
    }
}
