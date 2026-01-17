package view.swing;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import model.GameModel;
import model.entity.Building;
import model.entity.PowerPlant;
import model.entity.Residence;

/**
 * Modern information panel with card-based design.
 */
public class InfoPanel extends JPanel {
    
    private static final Color BG_COLOR = new Color(250, 250, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color ACCENT_COLOR = new Color(76, 175, 80);
    
    private GameModel model;
    private JPanel eventCard;
    private JPanel buildingCard;
    private Building selectedBuilding;
    
    public InfoPanel(GameModel model) {
        this.model = model;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(320, 600));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Event log card
        eventCard = createCard("Evenements Recents");
        add(eventCard);
        add(Box.createVerticalStrut(15));
        
        // Building info card
        buildingCard = createCard("Details du Batiment");
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
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
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
        
        // Icon based on event type
        String icon = "•";
        Color iconColor = ACCENT_COLOR;
        if (event.contains("FIRE") || event.contains("STORM") || event.contains("EARTHQUAKE") || event.contains("TORNADO")) {
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
        updateBuildingCard("Cliquez sur un batiment", "pour voir ses details.", null);
    }
    
    public void showBuildingInfo(Building b, int x, int y) {
        this.selectedBuilding = b;
        
        if (b == null) {
            updateBuildingCard("Terrain vide", "Position: (" + x + "," + y + ")", null);
            return;
        }
        
        String title = "";
        String details = "";
        Color accentColor = ACCENT_COLOR;
        
        if (b instanceof PowerPlant) {
            PowerPlant p = (PowerPlant) b;
            title = p.getClass().getSimpleName() + " Niv." + p.getLevel();
            accentColor = new Color(255, 193, 7);
            
            details = String.format(
                "<html><div style='line-height:1.4'>" +
                "<b>Position:</b> (%d,%d)<br>" +
                "<b>Statut:</b> %s<br>" +
                "<b>Production:</b> %.1f MW<br>" +
                "<b>Stockage:</b> %.0f/%.0f MWh<br>" +
                "<b>Cout:</b> %.0f pieces/jour<br>" +
                "<b>Pollution:</b> %.1f PP/jour<br>" +
                "<b>Sante:</b> %.0f%%</div></html>",
                x, y, p.getStatus(), p.getPowerOutput(),
                p.getCurrentEnergyStored(), p.getStorageCapacity(),
                p.getDailyCost(), p.getPollutionRate(),
                (b.getHealth() / b.getMaxHealth()) * 100
            );
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
                (b.getHealth() / b.getMaxHealth()) * 100
            );
        }
        
        updateBuildingCard(title, details, accentColor);
    }
    
    private void updateBuildingCard(String title, String content, Color accent) {
        // Remove old content
        Component[] components = buildingCard.getComponents();
        for (Component c : components) {
            if (!(c instanceof JLabel && ((JLabel)c).getText().equals("Details du Batiment"))) {
                buildingCard.remove(c);
            }
        }
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        
        // Title with accent bar
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
            
            contentPanel.add(titlePanel);
            contentPanel.add(Box.createVerticalStrut(12));
        }
        
        // Content
        JLabel contentLabel = new JLabel(content);
        contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contentLabel.setForeground(TEXT_PRIMARY);
        contentPanel.add(contentLabel);
        
        buildingCard.add(contentPanel, BorderLayout.CENTER);
        buildingCard.revalidate();
        buildingCard.repaint();
    }
    
    public void update() {
        updateEventLog();
    }
}
