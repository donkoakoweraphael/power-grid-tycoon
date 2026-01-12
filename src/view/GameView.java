package view;

import controller.GameController;
import viewmodel.GameViewModel;
import observer.GameViewObserver;
import model.entity.PowerPlant;

import javax.swing.*;
import java.awt.*;

/**
 * Main View of the game.
 * Uses Swing for the UI and follows the MVVM pattern.
 */
public class GameView extends JFrame implements GameViewObserver {

    private final GameController controller;
    private final GameViewModel viewModel;

    // UI Components
    private JLabel labelName, labelDay, labelCoins, labelHappiness, labelPollution;
    private JProgressBar progressHappiness, progressPollution;
    private JPanel panelPlants;
    private JButton btnNextDay;

    public GameView(GameController controller) {
        this.controller = controller;
        this.viewModel = controller.getViewModel();

        // Register this view to the ViewModel
        this.viewModel.addViewListener(this);

        setupFrame();
        createComponents();
        layoutComponents();

        // Initial update
        onViewUpdated();

        setVisible(true);
    }

    private void setupFrame() {
        setTitle("Power Grid Tycoon - " + viewModel.getCityName());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(18, 18, 18)); // Dark background
    }

    private void createComponents() {
        labelName = createHeaderLabel(viewModel.getCityName(), 24);
        labelDay = createHeaderLabel(viewModel.getCurrentDayText(), 16);
        labelCoins = createHeaderLabel(viewModel.getCoinsText(), 18);
        labelCoins.setForeground(new Color(76, 175, 80)); // Green neon

        labelHappiness = createHeaderLabel("Happiness", 14);
        progressHappiness = new JProgressBar(0, 100);
        progressHappiness.setStringPainted(true);
        progressHappiness.setForeground(new Color(33, 150, 243)); // Blue neon

        labelPollution = createHeaderLabel("Pollution", 14);
        progressPollution = new JProgressBar(0, 1000); // Max 1000 for now
        progressPollution.setStringPainted(true);
        progressPollution.setForeground(new Color(255, 152, 0)); // Orange neon

        btnNextDay = new JButton("NEXT DAY");
        btnNextDay.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnNextDay.setBackground(new Color(63, 81, 181));
        btnNextDay.setForeground(Color.WHITE);
        btnNextDay.setFocusPainted(false);
        btnNextDay.addActionListener(e -> controller.handleNextDay());

        panelPlants = new JPanel();
        panelPlants.setLayout(new BoxLayout(panelPlants, BoxLayout.Y_AXIS));
        panelPlants.setOpaque(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // --- Top Bar ---
        JPanel topBar = new JPanel(new GridLayout(1, 5, 20, 0));
        topBar.setBackground(new Color(33, 33, 33, 200));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel pName = new JPanel(new GridLayout(2, 1));
        pName.setOpaque(false);
        pName.add(labelName);
        pName.add(labelDay);

        topBar.add(pName);
        topBar.add(labelCoins);

        JPanel pHappy = new JPanel(new BorderLayout());
        pHappy.setOpaque(false);
        pHappy.add(labelHappiness, BorderLayout.NORTH);
        pHappy.add(progressHappiness, BorderLayout.CENTER);
        topBar.add(pHappy);

        JPanel pPollution = new JPanel(new BorderLayout());
        pPollution.setOpaque(false);
        pPollution.add(labelPollution, BorderLayout.NORTH);
        pPollution.add(progressPollution, BorderLayout.CENTER);
        topBar.add(pPollution);

        topBar.add(btnNextDay);

        add(topBar, BorderLayout.NORTH);

        // --- Left Sidebar (Plants) ---
        JScrollPane scrollPlants = new JScrollPane(panelPlants);
        scrollPlants.setPreferredSize(new Dimension(300, 0));
        scrollPlants.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "INFRASTRUCTURE", 0, 0, null, Color.WHITE));
        scrollPlants.setOpaque(false);
        scrollPlants.getViewport().setOpaque(false);
        add(scrollPlants, BorderLayout.WEST);

        // --- Center (Map View Placeholder) ---
        JPanel cityMap = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(40, 40, 40));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(0, 255, 255, 30));
                // Draw some grid lines
                for (int i = 0; i < getWidth(); i += 50)
                    g.drawLine(i, 0, i, getHeight());
                for (int i = 0; i < getHeight(); i += 50)
                    g.drawLine(0, i, getWidth(), i);

                g.setColor(Color.CYAN);
                g.drawString("CITY GRID VIEW - WIP", getWidth() / 2 - 50, getHeight() / 2);
            }
        };
        add(cityMap, BorderLayout.CENTER);
    }

    private JLabel createHeaderLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
        label.setForeground(Color.WHITE);
        return label;
    }

    @Override
    public void onViewUpdated() {
        // Update basic labels
        labelDay.setText(viewModel.getCurrentDayText());
        labelCoins.setText(viewModel.getCoinsText());

        progressHappiness.setValue((int) viewModel.getHappinessValue());
        progressHappiness.setString(viewModel.getHappinessText());

        progressPollution.setValue((int) viewModel.getPollutionValue());
        progressPollution.setString(viewModel.getPollutionText());

        // Update Plant List
        panelPlants.removeAll();
        for (PowerPlant plant : viewModel.getPowerPlants()) {
            panelPlants.add(createPlantWidget(plant));
            panelPlants.add(Box.createVerticalStrut(10));
        }
        panelPlants.revalidate();
        panelPlants.repaint();

        // Check Game Over
        if (viewModel.isGameOver()) {
            showGameOverDialog();
        }
    }

    private JPanel createPlantWidget(PowerPlant plant) {
        JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
        p.setBackground(new Color(45, 45, 45));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JLabel name = new JLabel(plant.getClass().getSimpleName() + " (" + plant.getId() + ")");
        name.setForeground(Color.WHITE);
        JLabel status = new JLabel(plant.getStatus().toString());
        status.setForeground(getStatusColor(plant.getStatus().toString()));

        JButton btnToggle = new JButton("Toggle");
        btnToggle.addActionListener(e -> controller.handleTogglePlant(plant));

        JButton btnUpgrade = new JButton("UPG (" + (int) plant.getUpgradeCost() + ")");
        btnUpgrade.setEnabled(plant.getLevel() < plant.getMaxLevel());
        btnUpgrade.addActionListener(e -> controller.handleUpgradeBuilding(plant.getId()));

        p.add(name);
        p.add(status);
        p.add(btnToggle);
        p.add(btnUpgrade);

        p.setMaximumSize(new Dimension(280, 80));
        return p;
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "ACTIVE":
                return Color.GREEN;
            case "INACTIVE":
                return Color.RED;
            case "UPGRADING":
                return Color.YELLOW;
            case "UNDER_CONSTRUCTION":
                return Color.ORANGE;
            default:
                return Color.WHITE;
        }
    }

    private void showGameOverDialog() {
        // Only show once per game over state transition
        SwingUtilities.invokeLater(() -> {
            int option = JOptionPane.showConfirmDialog(this,
                    "YOUR CITY HAS COLLAPSED!\nWould you like to reload the autosave?",
                    "GAME OVER", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                controller.handleLoad("autosave");
            }
        });
    }
}
