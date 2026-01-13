package view;

import controller.GameController;
import viewmodel.GameViewModel;
import observer.GameViewObserver;
import model.entity.PowerPlant;
import model.entity.Residence;

import javax.swing.*;
import java.awt.*;

/**
 * Main View of the game.
 * Uses Swing for the UI and follows the MVVM pattern.
 */
public class GameView extends JFrame implements GameViewObserver {

    private final GameController controller;
    private final GameViewModel viewModel;

    private JLabel labelName, labelDay, labelCoins, labelPrice;
    private JLabel lblPopulation, lblEnergyRatio, lblPurchasingPower, lblConsumption, lblTotalCapacity;
    private JLabel lblHapVal, lblPolVal;
    private JProgressBar progressHappiness, progressPollution;
    private JPanel panelPlants, panelResidences;
    private JButton btnNextDay, btnNextMulti, btnMenu;

    // Graphs
    private StatChart chartCoins, chartHappiness, chartPollution;

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
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(15, 15, 15));
    }

    private void createComponents() {
        labelName = createHeaderLabel(viewModel.getCityName(), 26);
        labelDay = createHeaderLabel(viewModel.getCurrentDayText(), 16);
        labelCoins = createHeaderLabel(viewModel.getCoinsText(), 18);
        labelCoins.setForeground(new Color(76, 175, 80));

        labelPrice = createHeaderLabel(viewModel.getFormattedPrice(), 18);
        labelPrice.setForeground(Color.CYAN);

        lblPopulation = createHeaderLabel("0 Citizens", 14);
        lblEnergyRatio = createHeaderLabel("Energy: 0/0", 14);
        lblPurchasingPower = createHeaderLabel("P.Power: 0", 14);
        lblConsumption = createHeaderLabel("Cons: 0", 14);
        lblTotalCapacity = createHeaderLabel("0 MWh", 14);

        progressHappiness = new JProgressBar(JProgressBar.VERTICAL, 0, 100);
        progressHappiness.setStringPainted(false);
        progressHappiness.setForeground(new Color(33, 150, 243));
        progressHappiness.setBackground(new Color(30, 30, 30));
        lblHapVal = createHeaderLabel("0", 18);
        lblHapVal.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressPollution = new JProgressBar(JProgressBar.VERTICAL, 0, 1000);
        progressPollution.setStringPainted(false);
        progressPollution.setForeground(new Color(255, 152, 0));
        progressPollution.setBackground(new Color(30, 30, 30));
        lblPolVal = createHeaderLabel("0", 18);
        lblPolVal.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnNextDay = new JButton("NEXT DAY");
        styleButton(btnNextDay, new Color(63, 81, 181), Color.WHITE);
        btnNextDay.addActionListener(e -> controller.handleNextDay());

        btnNextMulti = new JButton("SKIP DAYS...");
        styleButton(btnNextMulti, new Color(103, 58, 183), Color.WHITE);
        btnNextMulti.addActionListener(e -> {
            DaySkipDialog dialog = new DaySkipDialog(this);
            dialog.setVisible(true);
            int days = dialog.getSelectedDays();
            if (days > 0) {
                controller.handleNextDays(days);
            }
        });

        btnMenu = new JButton("MENU");
        styleButton(btnMenu, new Color(45, 45, 45), Color.YELLOW);
        btnMenu.addActionListener(e -> openMenu());

        // Binding Escape key
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "openMenu");
        getRootPane().getActionMap().put("openMenu", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                openMenu();
            }
        });

        panelPlants = new JPanel();
        panelPlants.setLayout(new BoxLayout(panelPlants, BoxLayout.Y_AXIS));
        panelPlants.setOpaque(false);

        panelResidences = new JPanel();
        panelResidences.setLayout(new BoxLayout(panelResidences, BoxLayout.Y_AXIS));
        panelResidences.setOpaque(false);

        chartCoins = new StatChart("COINS (30d)", new Color(76, 175, 80));
        chartHappiness = new StatChart("HAPPINESS (30d)", new Color(33, 150, 243));
        chartPollution = new StatChart("POLLUTION (30d)", new Color(255, 152, 0));
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // --- Top Bar ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 30, 30));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        JPanel leftGroup = new JPanel(new GridLayout(2, 1));
        leftGroup.setOpaque(false);
        leftGroup.add(labelName);
        leftGroup.add(labelDay);

        JPanel centerGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
        centerGroup.setOpaque(false);
        centerGroup.add(labelCoins);
        centerGroup.add(labelPrice);

        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightGroup.setOpaque(false);
        rightGroup.add(btnNextDay);
        rightGroup.add(btnNextMulti);
        rightGroup.add(btnMenu);

        topBar.add(leftGroup, BorderLayout.WEST);
        topBar.add(centerGroup, BorderLayout.CENTER);
        topBar.add(rightGroup, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // --- Left Sidebar (Tabs) ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setPreferredSize(new Dimension(320, 0));
        tabs.setBackground(new Color(25, 25, 25));
        tabs.setForeground(Color.WHITE);

        JScrollPane scrollPlants = new JScrollPane(panelPlants);
        scrollPlants.setOpaque(false);
        scrollPlants.getViewport().setOpaque(false);
        scrollPlants.setBorder(null);
        tabs.addTab("POWER", scrollPlants);

        JScrollPane scrollRes = new JScrollPane(panelResidences);
        scrollRes.setOpaque(false);
        scrollRes.getViewport().setOpaque(false);
        scrollRes.setBorder(null);
        tabs.addTab("RESIDENTIAL", scrollRes);

        add(tabs, BorderLayout.WEST);

        // --- Right Sidebar (Gauges & Detailed Stats) ---
        JPanel rightSidebar = new JPanel();
        rightSidebar.setLayout(new BoxLayout(rightSidebar, BoxLayout.Y_AXIS));
        rightSidebar.setPreferredSize(new Dimension(240, 0));
        rightSidebar.setBackground(new Color(25, 25, 25));
        rightSidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        // Vertical Gauges Panel
        JPanel gauges = new JPanel(new GridLayout(1, 2, 20, 0));
        gauges.setOpaque(false);
        gauges.setMaximumSize(new Dimension(200, 250));

        gauges.add(createOverlayGauge(progressHappiness, lblHapVal, "HAPPINESS"));
        gauges.add(createOverlayGauge(progressPollution, lblPolVal, "POLLUTION"));

        rightSidebar.add(gauges);
        rightSidebar.add(Box.createVerticalStrut(30));

        // Metrics Panel
        JPanel metrics = new JPanel(new GridLayout(5, 1, 0, 12));
        metrics.setOpaque(false);
        metrics.add(createMetricBox("POPULATION", lblPopulation));
        metrics.add(createMetricBox("DAILY PRODUCTION", lblTotalCapacity));
        metrics.add(createMetricBox("ENERGY STATUS", lblEnergyRatio));
        metrics.add(createMetricBox("PURCHASING POWER", lblPurchasingPower));
        metrics.add(createMetricBox("DAILY CONSUMPTION", lblConsumption));
        rightSidebar.add(metrics);

        add(rightSidebar, BorderLayout.EAST);

        // --- Bottom Panel ---
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        bottomPanel.setBackground(new Color(20, 20, 20));
        bottomPanel.setPreferredSize(new Dimension(0, 200));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        bottomPanel.add(chartCoins);
        bottomPanel.add(chartHappiness);
        bottomPanel.add(chartPollution);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Center (Map) ---
        JPanel cityMap = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(35, 35, 35));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < getWidth(); i += 50)
                    g.drawLine(i, 0, i, getHeight());
                for (int i = 0; i < getHeight(); i += 50)
                    g.drawLine(0, i, getWidth(), i);

                int offsetX = 40, offsetY = 40;
                g.setColor(new Color(79, 195, 247));
                for (int i = 0; i < viewModel.getResidences().size(); i++) {
                    g.fillRect(offsetX + (i % 8) * 60, offsetY + (i / 8) * 60, 45, 45);
                }
                g.setColor(new Color(255, 213, 79));
                for (int i = 0; i < viewModel.getPowerPlants().size(); i++) {
                    g.fillOval(offsetX + (i % 8) * 60, offsetY + 300 + (i / 8) * 60, 45, 45);
                }
            }
        };
        add(cityMap, BorderLayout.CENTER);
    }

    private JPanel createOverlayGauge(JProgressBar bar, JLabel label, String title) {
        JPanel container = new JPanel(new BorderLayout(0, 5));
        container.setOpaque(false);

        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("SansSerif", Font.BOLD, 10));
        t.setForeground(Color.GRAY);
        container.add(t, BorderLayout.NORTH);

        JPanel overlay = new JPanel();
        overlay.setLayout(new OverlayLayout(overlay));
        overlay.setOpaque(false);

        label.setForeground(Color.WHITE);
        overlay.add(label);
        overlay.add(bar);

        container.add(overlay, BorderLayout.CENTER);
        return container;
    }

    private JPanel createMetricBox(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.PLAIN, 10));
        t.setForeground(Color.GRAY);
        p.add(t, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    private JLabel createHeaderLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, size));
        label.setForeground(Color.WHITE);
        return label;
    }

    @Override
    public void onViewUpdated() {
        labelName.setText(viewModel.getCityName());
        labelDay.setText(viewModel.getCurrentDayText());
        labelCoins.setText(viewModel.getCoinsText());
        labelPrice.setText(viewModel.getFormattedPrice());

        lblPopulation.setText(viewModel.getPopulationText());
        lblEnergyRatio.setText(viewModel.getEnergyRatiosText());
        lblPurchasingPower.setText(viewModel.getPurchasingPowerText());
        lblConsumption.setText(viewModel.getDemandText());
        lblTotalCapacity.setText(viewModel.getTotalCapacityText());

        int hapVal = (int) viewModel.getHappinessValue();
        progressHappiness.setValue(hapVal);
        lblHapVal.setText(String.valueOf(hapVal));

        int polVal = (int) viewModel.getPollutionValue();
        progressPollution.setValue(polVal);
        lblPolVal.setText(String.valueOf(polVal));

        // Update Plant List
        panelPlants.removeAll();
        for (PowerPlant plant : viewModel.getPowerPlants()) {
            panelPlants.add(createPlantWidget(plant));
            panelPlants.add(Box.createVerticalStrut(10));
        }

        // Update Residences List
        panelResidences.removeAll();
        for (Residence res : viewModel.getResidences()) {
            panelResidences.add(createResidenceWidget(res));
            panelResidences.add(Box.createVerticalStrut(10));
        }

        chartCoins.setData(viewModel.getCoinHistory());
        chartHappiness.setData(viewModel.getHappinessHistory());
        chartPollution.setData(viewModel.getPollutionHistory());

        panelPlants.revalidate();
        panelPlants.repaint();
        panelResidences.revalidate();
        panelResidences.repaint();
        repaint();

        if (viewModel.isGameOver()) {
            showGameOverDialog();
        }
    }

    private JPanel createPlantWidget(PowerPlant plant) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(new Color(40, 40, 40));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel name = new JLabel(plant.getClass().getSimpleName().replace("Plant", "").toUpperCase());
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel status = new JLabel(plant.getStatus().toString());
        status.setForeground(getStatusColor(plant.getStatus().toString()));
        status.setFont(new Font("SansSerif", Font.PLAIN, 10));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(name);
        left.add(status);

        JButton btnInfo = new JButton("INFO");
        btnInfo.addActionListener(e -> new PlantInfoDialog(this, controller, plant).setVisible(true));

        p.add(left, BorderLayout.CENTER);
        p.add(btnInfo, BorderLayout.EAST);

        p.setMaximumSize(new Dimension(280, 70));
        return p;
    }

    private JPanel createResidenceWidget(Residence res) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(new Color(30, 40, 50));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 70, 90)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel name = new JLabel(res.getId().toUpperCase());
        name.setForeground(new Color(100, 200, 255));
        name.setFont(new Font("SansSerif", Font.BOLD, 12));

        JLabel info = new JLabel(viewModel.getResidenceInfo(res));
        info.setForeground(Color.LIGHT_GRAY);
        info.setFont(new Font("SansSerif", Font.PLAIN, 10));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(name);
        left.add(info);

        p.add(left, BorderLayout.CENTER);

        p.setMaximumSize(new Dimension(280, 60));
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

    private void openMenu() {
        new InGameMenuDialog(this, controller).setVisible(true);
    }

    private void showGameOverDialog() {
        SwingUtilities.invokeLater(() -> {
            int option = JOptionPane.showConfirmDialog(this,
                    "YOUR CITY HAS COLLAPSED!\nReason: Crisis (Bankrupt/Unhappy/Polluted)\nWould you like to reload the autosave?",
                    "GAME OVER", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                controller.handleLoad("autosave");
            }
        });
    }
}
