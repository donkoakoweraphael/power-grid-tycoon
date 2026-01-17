package view.swing;

import javax.swing.*;
import java.awt.*;
import model.GameModel;
import model.entity.PowerPlant;

/**
 * Panneau de statistiques affichant les mesures cles.
 */
public class BottomStatsPanel extends JPanel {

    private GameModel model;
    private JLabel productionLabel;
    private JLabel demandLabel;
    private JLabel balanceLabel; // NEW: Simple arithmetric balance
    private JLabel storageLabel; // NEW: Detailed storage/battery context
    private JLabel pollutionLabel;

    public BottomStatsPanel(GameModel model) {
        this.model = model;

        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Reduced gap from 30 to 20 to fit 5 items
        setBackground(new Color(250, 250, 250));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 12);

        productionLabel = createStatLabel("Prod: 0.0", new Color(46, 125, 50));
        demandLabel = createStatLabel("Dem: 0.0", new Color(25, 118, 210));
        balanceLabel = createStatLabel("Bilan: 0.0", Color.GRAY);
        storageLabel = createStatLabel("Stock: 0.0", Color.GRAY);
        pollutionLabel = createStatLabel("Poll: 0", new Color(230, 81, 0));

        productionLabel.setFont(labelFont);
        demandLabel.setFont(labelFont);
        balanceLabel.setFont(labelFont);
        storageLabel.setFont(labelFont);
        pollutionLabel.setFont(labelFont);

        add(productionLabel);
        add(createSeparator());
        add(demandLabel);
        add(createSeparator());
        add(balanceLabel);
        add(createSeparator());
        add(storageLabel);
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
        double production = model.getCity().getTotalEnergyProduced();
        double demand = model.getCity().getTotalEnergyDemand();
        double balance = production - demand;
        double pollution = model.getCity().getTotalPollution();

        // Battery Stats
        double currentStorage = model.getCity().getPowerPlants().stream()
                .mapToDouble(PowerPlant::getCurrentEnergyStored).sum();
        double totalCapacity = model.getCity().getTotalStorageCapacity();

        productionLabel.setText(String.format("Prod: %.1f MW", production));
        demandLabel.setText(String.format("Dem: %.1f MW", demand));

        // 1. Simple Balance (Restore Deficit/Surplus display)
        if (balance >= 0) {
            balanceLabel.setText(String.format("Surplus: +%.1f", balance));
            balanceLabel.setForeground(new Color(46, 125, 50)); // Green
        } else {
            balanceLabel.setText(String.format("Deficit: %.1f", balance)); // Negative sign is implicit or add -
            balanceLabel.setForeground(new Color(211, 47, 47)); // Red
        }

        // 2. Storage/Battery Status (Detailed Logic)
        if (balance >= 0) {
            double availableSpace = totalCapacity - currentStorage;
            double toStore = Math.min(balance, availableSpace);
            double lost = balance - toStore;

            if (lost > 0) {
                storageLabel.setText(String.format("Stock: +%.1f (Perdu: %.1f)", toStore, lost));
            } else {
                storageLabel.setText(String.format("Stock: +%.1f", toStore));
            }
            storageLabel.setForeground(new Color(76, 175, 80)); // Green
        } else {
            double deficit = Math.abs(balance);
            double coveredByBattery = Math.min(deficit, currentStorage);
            double missing = deficit - coveredByBattery;

            if (missing > 0) {
                storageLabel.setText(String.format("Manque: %.1f (Batt: %.1f)", missing, coveredByBattery));
                storageLabel.setForeground(new Color(211, 47, 47)); // Red Alert
            } else {
                storageLabel.setText(String.format("Batt: -%.1f", coveredByBattery));
                storageLabel.setForeground(new Color(255, 152, 0)); // Orange Warning
            }
        }

        pollutionLabel.setText(String.format("Poll: %.0f", pollution));
    }

    public void setModel(GameModel model) {
        this.model = model;
        update();
    }
}
