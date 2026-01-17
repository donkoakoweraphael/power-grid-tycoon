package view.swing;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import model.GameModel;
import model.entity.Building;
import model.entity.PowerPlant;
import model.entity.Residence;
import model.enums.PlantStatus;
import controller.GameController;

/**
 * Panneau d'informations moderne avec design en cartes.
 * Inclut le bouton d'amélioration avec prix correct.
 */
public class InfoPanel extends JPanel {

    private static final Color BG_COLOR = new Color(250, 250, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color ACCENT_COLOR = new Color(76, 175, 80);

    private GameModel model;
    private GameController controller;
    private GameFrame frame;

    private JPanel eventCard;
    private JPanel buildingCard;
    private JPanel buildingContentPanel; // Dedicated container for dynamic content
    private Building selectedBuilding;
    private int selectedX = -1;
    private int selectedY = -1;

    public InfoPanel(GameModel model, GameController controller, GameFrame frame) {
        this.model = model;
        this.controller = controller;
        this.frame = frame;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(320, 600));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Carte du journal des evenements
        eventCard = createCard("Evenements Recents");
        add(eventCard);
        add(Box.createVerticalStrut(15));

        // Carte d'info du batiment
        buildingCard = createCard("Details du Batiment");

        // Initialize content panel
        buildingContentPanel = new JPanel();
        buildingContentPanel.setLayout(new BoxLayout(buildingContentPanel, BoxLayout.Y_AXIS));
        buildingContentPanel.setBackground(CARD_BG);
        buildingCard.add(buildingContentPanel, BorderLayout.CENTER);

        add(buildingCard);
        add(Box.createVerticalGlue());

        updateEventLog();
        showDefaultBuildingInfo();
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        card.add(titleLabel, BorderLayout.NORTH);

        return card;
    }

    public void updateEventLog() {
        // Remove old content
        Component[] components = eventCard.getComponents();
        for (Component c : components) {
            if (c instanceof JScrollPane) {
                eventCard.remove(c);
            }
        }

        JPanel eventsContainer = new JPanel();
        eventsContainer.setLayout(new BoxLayout(eventsContainer, BoxLayout.Y_AXIS));
        eventsContainer.setBackground(CARD_BG);

        java.util.List<String> events = model.getCity().getEventLog();
        if (events.isEmpty()) {
            JLabel noEvents = new JLabel("Aucun evenement");
            noEvents.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            noEvents.setForeground(TEXT_SECONDARY);
            eventsContainer.add(noEvents);
        } else {
            for (String event : events) {
                JPanel eventItem = createEventItem(event);
                eventsContainer.add(eventItem);
                eventsContainer.add(Box.createVerticalStrut(8));
            }
        }

        JScrollPane scroll = new JScrollPane(eventsContainer);
        scroll.setBorder(null);
        scroll.setBackground(CARD_BG);
        scroll.setPreferredSize(new Dimension(280, 180));
        eventCard.add(scroll, BorderLayout.CENTER);
        eventCard.revalidate();
        eventCard.repaint();
    }

    private JPanel createEventItem(String event) {
        JPanel item = new JPanel(new BorderLayout(8, 0));
        item.setBackground(new Color(245, 245, 245));
        item.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Icone selon le type d'evenement
        String icon = "•";
        Color iconColor = ACCENT_COLOR;
        if (event.contains("FIRE") || event.contains("STORM") || event.contains("EARTHQUAKE")
                || event.contains("TORNADO")) {
            icon = "!";
            iconColor = new Color(244, 67, 54);
        } else if (event.contains("Construction")) {
            icon = "+";
            iconColor = new Color(33, 150, 243);
        }

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        iconLabel.setForeground(iconColor);
        item.add(iconLabel, BorderLayout.WEST);

        JLabel text = new JLabel("<html>" + event.replace("\n", "<br>") + "</html>");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        text.setForeground(TEXT_PRIMARY);
        item.add(text, BorderLayout.CENTER);

        return item;
    }

    private void showDefaultBuildingInfo() {
        updateBuildingCard("Cliquez sur un batiment", "pour voir ses details.", null, null, null);
    }

    public void showBuildingInfo(Building b, int x, int y) {
        this.selectedBuilding = b;
        this.selectedX = x;
        this.selectedY = y;

        if (b == null) {
            updateBuildingCard("Terrain vide", "Position: (" + x + "," + y + ")", null, null, null);
            return;
        }

        String title = "";
        String details = "";
        Color accentColor = ACCENT_COLOR;
        JButton upgradeBtn = null;
        JButton toggleBtn = null;

        if (b instanceof PowerPlant) {
            PowerPlant p = (PowerPlant) b;
            title = p.getClass().getSimpleName() + " Niv." + p.getLevel();
            accentColor = new Color(255, 193, 7);

            double upgradeCost = p.getUpgradeCost();
            String upgradeInfo = "";
            if (p.getLevel() < p.getMaxLevel()) {
                upgradeInfo = String.format("<b>Amelioration:</b> %.0f pieces<br>", upgradeCost);
            } else {
                upgradeInfo = "<b>Niveau MAX atteint</b><br>";
            }

            // Rich Status Logic
            String statusHtml = p.getStatus().toString();
            String statusColor = "black";
            if (p.getStatus() == PlantStatus.UNDER_CONSTRUCTION) {
                statusHtml = "EN CONSTRUCTION (" + p.getRemainingTime() + " jours)";
                statusColor = "#FF5722"; // Deep Orange
            } else if (p.getStatus() == PlantStatus.PAUSED
                    || p.getStatus() == PlantStatus.INACTIVE) {
                statusHtml = "A L'ARRET";
                statusColor = "#9E9E9E"; // Grey
            } else if (p.getStatus() == PlantStatus.ACTIVE) {
                statusHtml = "ACTIF";
                statusColor = "#4CAF50"; // Green
            }

            details = String.format(
                    "<html><div style='line-height:1.4'>" +
                            "<b>Position:</b> (%d,%d)<br>" +
                            "<b>Statut:</b> <span style='color:%s'><b>%s</b></span><br>" +
                            "<b>Production:</b> %.1f MW<br>" +
                            "<b>Stockage:</b> %.0f/%.0f MWh<br>" +
                            "<b>Cout:</b> %.0f pieces/jour<br>" +
                            "<b>Pollution:</b> %.1f PP/jour<br>" +
                            "<b>Sante:</b> %.0f%%<br><br>" +
                            upgradeInfo + "</div></html>",
                    x, y, statusColor, statusHtml, p.getPowerOutput(),
                    p.getCurrentEnergyStored(), p.getStorageCapacity(),
                    p.getDailyCost(), p.getPollutionRate(),
                    (b.getHealth() / b.getMaxHealth()) * 100);

            // Create upgrade button if not max level
            if (p.getLevel() < p.getMaxLevel()) {
                upgradeBtn = createUpgradeButton(b, upgradeCost);
            }

            // Create Toggle Button (if Active or Paused)
            if (p.getStatus() == PlantStatus.ACTIVE || p.getStatus() == PlantStatus.PAUSED
                    || p.getStatus() == PlantStatus.INACTIVE) {
                boolean isActive = p.getStatus() == PlantStatus.ACTIVE;
                toggleBtn = createToggleButton(p, isActive);
            }

        } else if (b instanceof Residence) {
            Residence r = (Residence) b;
            title = "Maison Niv." + r.getLevel();
            accentColor = new Color(121, 85, 72);

            details = String.format(
                    "<html><div style='line-height:1.4'>" +
                            "<b>Position:</b> (%d,%d)<br>" +
                            "<b>Habitants:</b> %d/%d<br>" +
                            "<b>Demande:</b> %.2f MW<br>" +
                            "<b>Alimentee:</b> %s<br>" +
                            "<b>Pouvoir d'achat:</b> %.1f pieces/MWh<br>" +
                            "<b>Sante:</b> %.0f%%</div></html>",
                    x, y, r.getCurrentOccupancy(), r.getMaxCapacity(),
                    r.getByHourDemand(model.getCity().getCurrentHour()),
                    r.isSupplied() ? "OUI" : "NON",
                    r.getPurchasingPower(),
                    (b.getHealth() / b.getMaxHealth()) * 100);
            // Note: Residences don't have upgrades in this version
        }

        updateBuildingCard(title, details, accentColor, upgradeBtn, toggleBtn);
    }

    private JButton createToggleButton(PowerPlant p, boolean isActive) {
        String label = isActive ? "Désactiver" : "Activer";
        Color color = isActive ? new Color(244, 67, 54) : new Color(76, 175, 80); // Red to Stop, Green to Start

        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addActionListener(e -> {
            try {
                controller.handleTogglePlant(p.getId());
                frame.refresh(); // Refresh global UI
                showBuildingInfo(p, selectedX, selectedY); // Refresh local card
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur: " + ex.getMessage());
            }
        });

        return btn;
    }

    private JButton createUpgradeButton(Building b, double cost) {
        JButton btn = new JButton(String.format("^ Ameliorer (%.0f 🪙)", cost));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(156, 39, 176));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        // Fix for linux visibility
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        btn.addActionListener(e -> {
            try {
                controller.handleUpgradeBuilding(b.getId());
                frame.refresh();
                // Re-show updated building info
                showBuildingInfo(b, selectedX, selectedY);
                JOptionPane.showMessageDialog(frame,
                        "Batiment ameliore au niveau " + b.getLevel() + "!",
                        "Amelioration reussie",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "Erreur: " + ex.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        return btn;
    }

    private void updateBuildingCard(String title, String content, Color accent, JButton actionButton,
            JButton toggleButton) {
        // Clear only the content container, PRESERVING the title header
        buildingContentPanel.removeAll();

        // Title with accent bar (Specific to the building being shown)
        if (accent != null) {
            JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
            titlePanel.setBackground(CARD_BG);

            JPanel accentBar = new JPanel();
            accentBar.setBackground(accent);
            accentBar.setPreferredSize(new Dimension(4, 20));
            titlePanel.add(accentBar, BorderLayout.WEST);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            titleLabel.setForeground(TEXT_PRIMARY);
            titlePanel.add(titleLabel, BorderLayout.CENTER);

            buildingContentPanel.add(titlePanel);
            buildingContentPanel.add(Box.createVerticalStrut(12));
        }

        // Content
        JLabel contentLabel = new JLabel(content);
        contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contentLabel.setForeground(TEXT_PRIMARY);
        buildingContentPanel.add(contentLabel);

        // Add action buttons
        if (actionButton != null || toggleButton != null) {
            buildingContentPanel.add(Box.createVerticalStrut(15));
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            btnPanel.setBackground(CARD_BG);
            if (actionButton != null)
                btnPanel.add(actionButton);
            if (toggleButton != null)
                btnPanel.add(toggleButton);
            buildingContentPanel.add(btnPanel);
        }

        // Refresh specifically the content panel
        buildingContentPanel.revalidate();
        buildingContentPanel.repaint();
        // Also refresh the card to be safe
        buildingCard.revalidate();
        buildingCard.repaint();
    }

    public void update() {
        updateEventLog();
    }

    public void setModel(GameModel model) {
        this.model = model;
        updateEventLog();
        showDefaultBuildingInfo();
    }
}
