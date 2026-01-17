package view.swing;

import javax.swing.*;
import java.awt.*;
import model.GameModel;
import model.entity.PowerPlant;
import controller.GameController;

/**
 * Panneau d'etat affichant les statistiques du jeu.
 */
public class StatusPanel extends JPanel {

    private GameModel model;
    private GameController controller;
    private GameFrame frame;

    private JLabel dayLabel;
    private JLabel moneyLabel;
    private JLabel popLabel;
    private JLabel happinessLabel;
    private JLabel energyLabel;
    private JLabel priceLabel;
    private JLabel ppLabel;

    private javax.swing.Timer clockTimer;
    private int displayMinutes = 0;
    private int lastHour = -1;

    public StatusPanel(GameModel model, GameController controller, GameFrame frame) {
        this.model = model;
        this.controller = controller;
        this.frame = frame;
        this.lastHour = model.getCity().getCurrentHour();

        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        setBackground(new Color(250, 250, 250));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);

        // Menu button at the start
        JButton menuBtn = new JButton("☰ Menu");
        menuBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuBtn.setBackground(new Color(63, 81, 181));
        menuBtn.setForeground(Color.WHITE);
        menuBtn.setFocusPainted(false);
        menuBtn.setOpaque(true);
        menuBtn.setBorderPainted(false);
        menuBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        menuBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuBtn.addActionListener(e -> {
            MenuDialog menu = new MenuDialog(frame, controller);
            menu.setVisible(true);
        });
        add(menuBtn);
        add(createSeparator());

        dayLabel = new JLabel();
        moneyLabel = new JLabel();
        popLabel = new JLabel();
        happinessLabel = new JLabel();
        energyLabel = new JLabel();
        priceLabel = new JLabel();
        ppLabel = new JLabel();

        dayLabel.setFont(labelFont);
        moneyLabel.setFont(labelFont);
        popLabel.setFont(labelFont);
        happinessLabel.setFont(labelFont);
        energyLabel.setFont(labelFont);
        priceLabel.setFont(labelFont);
        ppLabel.setFont(labelFont);

        add(dayLabel);
        add(createSeparator());
        add(moneyLabel);
        add(createSeparator());
        add(popLabel);
        add(createSeparator());
        add(happinessLabel);
        add(createSeparator());
        add(energyLabel);
        add(createSeparator());
        add(priceLabel);
        add(createSeparator());
        add(ppLabel);

        update();
    }

    public void startClockAnimation() {
        // Horloge visuelle plus lente (250ms = 1 minute simulee)
        // 1h simulee = 15 secondes reelles
        if (clockTimer != null && clockTimer.isRunning())
            return;

        clockTimer = new javax.swing.Timer(250, e -> {
            displayMinutes = (displayMinutes + 1) % 60;
            updateClockDisplay();
        });
        clockTimer.start();
    }

    public void stopClockAnimation() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
    }

    private void updateClockDisplay() {
        // Mettre a jour uniquement l'affichage du temps avec les minutes animees
        dayLabel.setText(String.format("[J%d %02d:%02d]",
                model.getCity().getCurrentDay(),
                model.getCity().getCurrentHour(),
                displayMinutes));
    }

    private JLabel createSeparator() {
        JLabel sep = new JLabel("|");
        sep.setForeground(new Color(180, 180, 180));
        return sep;
    }

    public void update() {
        // Detecter le changement d'heure reelle pour reset les minutes du visuel
        int currentHour = model.getCity().getCurrentHour();
        if (currentHour != lastHour) {
            displayMinutes = 0;
            lastHour = currentHour;
        }

        // On ne force pas le redraw du label temps ici pour ne pas ecraser l'animation
        // Sauf si les minutes sont a 0 (nouvelle heure)
        if (displayMinutes == 0) {
            dayLabel.setText(String.format("[J%d %02d:%02d]",
                    model.getCity().getCurrentDay(),
                    currentHour,
                    0));
        }

        moneyLabel.setText(String.format("🪙 %.0f",
                model.getCity().getTotalCoins()));

        // Population / Capacity
        int pop = model.getCity().getTotalPopulation();
        int housingCap = model.getCity().getTotalHousingCapacity();
        popLabel.setText(String.format("Pop: %d / %d", pop, housingCap));

        double happiness = model.getCity().getGlobalHappiness();
        String happyIcon = happiness > 70 ? ":)" : happiness > 40 ? ":|" : ":(";
        happinessLabel.setText(String.format("%s %.0f%%", happyIcon, happiness));

        // User requested Stored / Capacity
        double stored = model.getCity().getPowerPlants().stream()
                .mapToDouble(PowerPlant::getCurrentEnergyStored).sum();
        double capacity = model.getCity().getTotalStorageCapacity();

        energyLabel.setText(String.format("[E] %.1f / %.1f MWh",
                stored, capacity));

        // Price Label
        priceLabel.setText(String.format("Prix élec : 🪙 %.2f/MWh", model.getCity().getElectricityPrice()));

        // Purchasing Power Label
        ppLabel.setText(String.format("Pouvoir d'achat : 🪙 %.1f/MWh", model.getCity().getAvgPurchasingPower()));
    }

    public void setModel(GameModel model) {
        this.model = model;
        this.lastHour = model.getCity().getCurrentHour();
        update();
    }
}
