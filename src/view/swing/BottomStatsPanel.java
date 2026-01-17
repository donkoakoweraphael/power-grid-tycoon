package view.swing;

import javax.swing.*;
import java.awt.*;
import model.GameModel;

/**
 * Bottom statistics panel showing key metrics.
 */
public class BottomStatsPanel extends JPanel {
    
    private GameModel model;
    private JLabel productionLabel;
    private JLabel demandLabel;
    private JLabel deficitLabel;
    private JLabel pollutionLabel;
    
    public BottomStatsPanel(GameModel model) {
        this.model = model;
        
        setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
        setBackground(new Color(250, 250, 250));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
        
        productionLabel = createStatLabel("Production: 0.0 MW", new Color(76, 175, 80));
        demandLabel = createStatLabel("Demande: 0.0 MW", new Color(33, 150, 243));
        deficitLabel = createStatLabel("Equilibre: 0.0 MW", new Color(117, 117, 117));
        pollutionLabel = createStatLabel("Pollution: 0 PP", new Color(255, 152, 0));
        
        productionLabel.setFont(labelFont);
        demandLabel.setFont(labelFont);
        deficitLabel.setFont(labelFont);
        pollutionLabel.setFont(labelFont);
        
        add(productionLabel);
        add(createSeparator());
        add(demandLabel);
        add(createSeparator());
        add(deficitLabel);
        add(createSeparator());
        add(pollutionLabel);
        
        update();
    }
    
    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        return label;
    }
    
    private JLabel createSeparator() {
        JLabel sep = new JLabel("|");
        sep.setForeground(new Color(200, 200, 200));
        return sep;
    }
    
    public void update() {
        double production = model.getCity().getTotalEnergyAvailable();
        double demand = model.getCity().getTotalEnergyDemand();
        double balance = production - demand;
        double pollution = model.getCity().getTotalPollution();
        
        productionLabel.setText(String.format("Production: %.1f MW", production));
        demandLabel.setText(String.format("Demande: %.1f MW", demand));
        
        // Update balance color based on deficit/surplus
        if (balance >= 0) {
            deficitLabel.setText(String.format("Surplus: +%.1f MW", balance));
            deficitLabel.setForeground(new Color(76, 175, 80));
        } else {
            deficitLabel.setText(String.format("Deficit: %.1f MW", balance));
            deficitLabel.setForeground(new Color(244, 67, 54));
        }
        
        pollutionLabel.setText(String.format("Pollution: %.0f PP", pollution));
    }
}
