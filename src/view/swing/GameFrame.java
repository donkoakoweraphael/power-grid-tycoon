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
    
    public GameFrame(GameController controller, GameModel model) {
        this.controller = controller;
        this.model = model;
        
        setTitle("Power Grid Tycoon - Simulation Energetique");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        
        // Create panels
        gridPanel = new GridPanel(model, this);
        statusPanel = new StatusPanel(model);
        controlPanel = new ControlPanel(controller, this);
        infoPanel = new InfoPanel(model);
        bottomStatsPanel = new BottomStatsPanel(model);
        
        // Center panel with grid and bottom stats
        JPanel centerPanel = new JPanel(new BorderLayout(0, 0));
        centerPanel.add(gridPanel, BorderLayout.CENTER);
        centerPanel.add(bottomStatsPanel, BorderLayout.SOUTH);
        
        // Layout
        add(statusPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Demarrer l'animation de l'horloge (affichage seulement, ne fait pas avancer le jeu)
        statusPanel.startClockAnimation();
        
        // Demarrer l'animation des stats (rafraichir l'UI toutes les 500ms)
        startStatsAnimation();
    }
    
    private void startStatsAnimation() {
        javax.swing.Timer statsTimer = new javax.swing.Timer(500, e -> {
            // Mettre a jour l'affichage des stats seulement, ne pas avancer le jeu
            statusPanel.update();
            bottomStatsPanel.update();
        });
        statsTimer.start();
    }
    
    public void refresh() {
        // Verifier le game over
        if (model.getState() == GameState.GAME_OVER) {
            statusPanel.stopClockAnimation();
            javax.swing.JOptionPane.showMessageDialog(this, 
                "GAME OVER!\n\nBonheur < 5% ou Argent < -10\n\nLa partie est terminee.", 
                "Fin de partie", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        gridPanel.repaint();
        statusPanel.update();
        controlPanel.update();
        infoPanel.update();
        bottomStatsPanel.update();
    }
    
    public void showBuildingInfo(Building b, int x, int y) {
        infoPanel.showBuildingInfo(b, x, y);
    }
}
