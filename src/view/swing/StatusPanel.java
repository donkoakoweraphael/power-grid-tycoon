package view.swing;

import javax.swing.*;
import java.awt.*;
import model.GameModel;

/**
 * Status panel displaying game stats.
 */
public class StatusPanel extends JPanel {
    
    private GameModel model;
    private JLabel dayLabel;
    private JLabel moneyLabel;
    private JLabel popLabel;
    private JLabel happinessLabel;
    private JLabel energyLabel;
    
    public StatusPanel(GameModel model) {
        this.model = model;
        
        setLayout(new FlowLayout(FlowLayout.LEFT, 25, 15));
        setBackground(new Color(250, 250, 250));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        
        dayLabel = new JLabel();
        moneyLabel = new JLabel();
        popLabel = new JLabel();
        happinessLabel = new JLabel();
        energyLabel = new JLabel();
        
        dayLabel.setFont(labelFont);
        moneyLabel.setFont(labelFont);
        popLabel.setFont(labelFont);
        happinessLabel.setFont(labelFont);
        energyLabel.setFont(labelFont);
        
        add(dayLabel);
        add(createSeparator());
        add(moneyLabel);
        add(createSeparator());
        add(popLabel);
        add(createSeparator());
        add(happinessLabel);
        add(createSeparator());
        add(energyLabel);
        
        update();
    }
    
    private JLabel createSeparator() {
        JLabel sep = new JLabel("|");
        sep.setForeground(new Color(180, 180, 180));
        return sep;
    }
    
    public void update() {
        dayLabel.setText(String.format("Jour %d | %02d:00", 
            model.getCity().getCurrentDay(), 
            model.getCity().getCurrentHour()));
        
        moneyLabel.setText(String.format("Argent: %.0f pieces", 
            model.getCity().getTotalCoins()));
        
        popLabel.setText(String.format("Pop: %d habitants", 
            model.getCity().getTotalPopulation()));
        
        happinessLabel.setText(String.format("Bonheur: %.0f%%", 
            model.getCity().getGlobalHappiness()));
        
        // Calculate actual production and demand
        double production = model.getCity().getTotalEnergyAvailable();
        double demand = model.getCity().getTotalEnergyDemand();
        
        energyLabel.setText(String.format("Energie: %.1f / %.1f MW", 
            production, demand));
    }
}
