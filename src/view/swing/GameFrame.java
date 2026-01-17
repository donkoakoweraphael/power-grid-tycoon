package view.swing;

import javax.swing.*;
import java.awt.*;
import model.GameModel;
import model.entity.Building;
import controller.GameController;

/**
 * Main Swing window for Power Grid Tycoon.
 * Minimal UI inspired by Universal Paperclips.
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
    }
    
    public void refresh() {
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
