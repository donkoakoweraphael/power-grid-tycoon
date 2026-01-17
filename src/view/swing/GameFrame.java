package view.swing;

import javax.swing.*;
import java.awt.*;
import controller.GameController;
import model.GameModel;
import model.enums.GameState;
import model.entity.Building;

/**
 * Fenetre principale Swing pour Power Grid Tycoon.
 * Interface minimaliste inspiree de Universal Paperclips.
 */
public class GameFrame extends JFrame {
    
    private GameController controller;
    private GameModel model;
    
    private GridPanel gridPanel;
    private StatusPanel statusPanel;
    private ControlPanel controlPanel;
    private InfoPanel infoPanel;
    private BottomStatsPanel bottomStatsPanel;
    private ChartPanel chartPanel;
    
    public GameFrame(GameController controller, GameModel model, String cityName) {
        this.controller = controller;
        this.model = model;
        
        setTitle("Power Grid Tycoon - " + cityName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        
        // Create panels
        gridPanel = new GridPanel(model, this);
        statusPanel = new StatusPanel(model);
        controlPanel = new ControlPanel(controller, this);
        infoPanel = new InfoPanel(model);
        bottomStatsPanel = new BottomStatsPanel(model);
        chartPanel = new ChartPanel(model);
        
        // Panel gauche avec graphique
        JPanel leftPanel = new JPanel(new BorderLayout(0, 5));
        leftPanel.setBackground(new java.awt.Color(245, 245, 245));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
        
        JLabel chartTitle = new JLabel("Statistiques", SwingConstants.CENTER);
        chartTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        chartTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        leftPanel.add(chartTitle, BorderLayout.NORTH);
        leftPanel.add(chartPanel, BorderLayout.CENTER);
        
        // Center panel with grid and bottom stats
        JPanel centerPanel = new JPanel(new BorderLayout(0, 0));
        centerPanel.add(gridPanel, BorderLayout.CENTER);
        centerPanel.add(bottomStatsPanel, BorderLayout.SOUTH);
        
        // Layout principal
        add(statusPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
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
            chartPanel.update();
        });
        statsTimer.start();
    }
    
    public void refresh() {
        // Verifier le game over
        if (model.getState() == GameState.GAME_OVER) {
            statusPanel.stopClockAnimation();
            
            String[] options = {"Charger une sauvegarde", "Nouvelle partie", "Quitter"};
            int choice = javax.swing.JOptionPane.showOptionDialog(this,
                "GAME OVER!\n\nBonheur < 5% ou Argent < -10\n\nQue voulez-vous faire?",
                "Fin de partie",
                javax.swing.JOptionPane.DEFAULT_OPTION,
                javax.swing.JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);
            
            if (choice == 0) {
                // Charger une sauvegarde
                String name = javax.swing.JOptionPane.showInputDialog(this, "Nom de la sauvegarde:", "sauvegarde1");
                if (name != null && !name.trim().isEmpty()) {
                    try {
                        controller.loadGame(name.trim());
                        statusPanel.startClockAnimation();
                    } catch (Exception ex) {
                        javax.swing.JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage());
                    }
                }
            } else if (choice == 1) {
                // Nouvelle partie
                controller.startConsole("SimCity");
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
        chartPanel.update();
    }
    
    public void showBuildingInfo(Building b, int x, int y) {
        infoPanel.showBuildingInfo(b, x, y);
    }
}
