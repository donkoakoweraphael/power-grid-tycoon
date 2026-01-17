package view.swing;

import javax.swing.*;
import java.awt.*;
import controller.GameController;
import model.GameModel;
import model.enums.GameState;
import model.entity.Building;
import observer.GameViewObserver;
import viewmodel.GameViewModel;

/**
 * Fenetre principale Swing pour Power Grid Tycoon.
 * Interface minimaliste inspiree de Universal Paperclips.
 * Implements Observer pattern for automatic UI updates.
 */
public class GameFrame extends JFrame implements GameViewObserver {

    private GameController controller;
    private GameModel model;
    private GameViewModel viewModel;

    public GameViewModel getViewModel() {
        return viewModel;
    }

    private GridPanel gridPanel;
    private StatusPanel statusPanel;
    private ControlPanel controlPanel;
    private InfoPanel infoPanel;
    private BottomStatsPanel bottomStatsPanel;
    private ChartPanel happinessChartPanel;
    private ChartPanel profitChartPanel;

    public GameFrame(GameController controller, GameModel model, String cityName) {
        this.controller = controller;
        this.model = model;

        // Create ViewModel and register this frame as observer
        this.viewModel = new GameViewModel(model);
        this.viewModel.addViewListener(this);

        setTitle("Power Grid Tycoon - " + cityName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        // Create panels
        gridPanel = new GridPanel(model, this);
        statusPanel = new StatusPanel(model, controller, this);
        controlPanel = new ControlPanel(controller, this);
        infoPanel = new InfoPanel(model, controller, this);
        bottomStatsPanel = new BottomStatsPanel(model);

        happinessChartPanel = new ChartPanel(model, "Evolution du Bonheur",
                new Color(76, 175, 80),
                GameModel::getHappinessHistory);

        profitChartPanel = new ChartPanel(model, "Historique Profit",
                new Color(33, 150, 243),
                m -> m.getCity().getProfitHistory());

        // Panel gauche avec graphique
        JPanel leftPanel = new JPanel(new BorderLayout(0, 5));
        leftPanel.setBackground(new java.awt.Color(245, 245, 245));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));

        JLabel chartTitle = new JLabel("Statistiques", SwingConstants.CENTER);
        chartTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        chartTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        leftPanel.add(chartTitle, BorderLayout.NORTH);

        JPanel chartsContainer = new JPanel(new GridLayout(2, 1, 0, 10));
        chartsContainer.setBackground(new java.awt.Color(245, 245, 245));
        chartsContainer.add(happinessChartPanel);
        chartsContainer.add(profitChartPanel);

        leftPanel.add(chartsContainer, BorderLayout.CENTER);

        // Center panel with grid and bottom stats
        JPanel centerPanel = new JPanel(new BorderLayout(0, 0));

        // Wrap GridPanel in JScrollPane
        JScrollPane gridScrollPane = new JScrollPane(gridPanel);
        gridScrollPane.setBorder(null);
        gridScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        gridScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        // Add minimal scrollbars logic if needed or just styling

        centerPanel.add(gridScrollPane, BorderLayout.CENTER);
        centerPanel.add(bottomStatsPanel, BorderLayout.SOUTH);

        // Layout principal
        add(statusPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(1200, 800));
        pack();
        setLocationRelativeTo(null);

        // Fullscreen by default (Maximized)
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);

        // Demarrer l'animation de l'horloge
        statusPanel.startClockAnimation();

        // Demarrer l'animation des stats
        startStatsAnimation();
    }

    private void startStatsAnimation() {
        javax.swing.Timer statsTimer = new javax.swing.Timer(500, e -> {
            // Mettre a jour l'affichage des stats seulement
            statusPanel.update();
            bottomStatsPanel.update();
            happinessChartPanel.update();
            profitChartPanel.update();
        });
        statsTimer.start();
    }

    public void refresh() {
        // Verifier le game over
        if (model.getState() == GameState.GAME_OVER) {
            statusPanel.stopClockAnimation();

            String[] options = { "Charger une sauvegarde", "Nouvelle partie", "Quitter" };
            int choice = javax.swing.JOptionPane.showOptionDialog(this,
                    "GAME OVER!\n\nBonheur < 5% ou Argent < -10\n\nQue voulez-vous faire?",
                    "Fin de partie",
                    javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == 0) {
                // Charger une sauvegarde - utiliser SaveLoadDialog
                SaveLoadDialog loadDialog = new SaveLoadDialog(this, controller, SaveLoadDialog.Mode.LOAD);
                loadDialog.setVisible(true);

                if (loadDialog.getSelectedSlot() != null) {
                    // Update model reference for the whole UI
                    updateModel(controller.getModel());
                    statusPanel.startClockAnimation();
                }
            } else if (choice == 1) {
                // Nouvelle partie
                String cityName = javax.swing.JOptionPane.showInputDialog(this,
                        "Nom de votre nouvelle ville:", "SimCity");
                if (cityName == null || cityName.trim().isEmpty()) {
                    cityName = "SimCity";
                }
                controller.startConsole(cityName.trim());
                updateModel(controller.getModel());
                statusPanel.startClockAnimation();
            } else {
                // Quitter
                System.exit(0);
            }
        }

        gridPanel.repaint();
        statusPanel.update();
        controlPanel.update();
        infoPanel.update();
        bottomStatsPanel.update();
        happinessChartPanel.update();
        profitChartPanel.update();
    }

    public void showBuildingInfo(Building b, int x, int y) {
        infoPanel.showBuildingInfo(b, x, y);
    }

    public void startConstructionMode(String type, boolean isPowerPlant) {
        gridPanel.startConstructionMode(type, isPowerPlant);
        javax.swing.JOptionPane.showMessageDialog(this,
                "Mode Construction Active!\n\nCliquez sur une case LIBRE (verte) pour construire.\nClic-Droit pour annuler.",
                "Construction", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    public void startMoveMode() {
        gridPanel.startMoveMode();
    }

    public void stopMoveMode() {
        gridPanel.cancelConstructionMode();
    }

    public void notifyMoveModeEnded() {
        controlPanel.onMoveModeEnded();
    }

    public void finalizeConstruction(String type, boolean isPowerPlant, int x, int y) {
        try {
            if (isPowerPlant) {
                String id = type + "-" + System.currentTimeMillis();
                controller.handleBuyPlant(type, id, x, y);
            } else {
                controller.handleBuildResidence(x, y);
            }

            refresh();

            // Auto-select the new building
            try {
                Building b = model.getCity().getGrid()[x][y];
                showBuildingInfo(b, x, y);
            } catch (Exception ex) {
                // Ignore selection error
            }

        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public void finalizeMove(int oldX, int oldY, int newX, int newY) {
        try {
            controller.handleMoveBuilding(oldX, oldY, newX, newY);
            refresh();
            javax.swing.JOptionPane.showMessageDialog(this, "Batiment deplace avec succes!", "Succes",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the game model for the whole frame (used when loading a save).
     */
    public void updateModel(GameModel newModel) {
        this.model = newModel;

        // Recreate ViewModel and register
        this.viewModel = new GameViewModel(newModel);
        this.viewModel.addViewListener(this);

        // Update all panels
        gridPanel.setModel(newModel);
        statusPanel.setModel(newModel);
        infoPanel.setModel(newModel);
        bottomStatsPanel.setModel(newModel);
        happinessChartPanel.setModel(newModel);
        profitChartPanel.setModel(newModel);

        // Manually trigger a refresh
        onViewUpdated();
    }

    /**
     * Called automatically by GameViewModel when model state changes.
     * This implements the Observer pattern for reactive UI updates.
     */
    @Override
    public void onViewUpdated() {
        // Use SwingUtilities to ensure thread-safe UI updates
        SwingUtilities.invokeLater(() -> {
            gridPanel.repaint();
            statusPanel.update();
            controlPanel.update();
            infoPanel.update();
            bottomStatsPanel.update();
            happinessChartPanel.update();
            profitChartPanel.update();
        });
    }
}
