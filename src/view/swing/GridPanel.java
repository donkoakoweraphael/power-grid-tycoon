package view.swing;

import javax.swing.*;
import java.awt.*;
import model.GameModel;
import model.entity.Building;
import model.entity.PowerPlant;
import model.entity.Residence;

/**
 * Grid panel displaying the city map.
 */
public class GridPanel extends JPanel {
    
    private static final int CELL_SIZE = 50;
    private static final Color GRASS_COLOR = new Color(34, 139, 34);
    private static final Color GRID_COLOR = new Color(0, 100, 0);
    
    private GameModel model;
    private GameFrame frame;
    
    public GridPanel(GameModel model, GameFrame frame) {
        this.model = model;
        this.frame = frame;
        
        int width = model.getCity().getWidth() * CELL_SIZE;
        int height = model.getCity().getHeight() * CELL_SIZE;
        setPreferredSize(new Dimension(width, height));
        setBackground(GRASS_COLOR);
        
        // Mouse click listener
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int gridX = e.getX() / CELL_SIZE;
                int gridY = e.getY() / CELL_SIZE;
                
                if (gridX >= 0 && gridX < model.getCity().getWidth() && 
                    gridY >= 0 && gridY < model.getCity().getHeight()) {
                    Building b = model.getCity().getGrid()[gridX][gridY];
                    frame.showBuildingInfo(b, gridX, gridY);
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int gridWidth = model.getCity().getWidth();
        int gridHeight = model.getCity().getHeight();
        
        // Draw grass tiles with checkerboard pattern for depth
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                int px = x * CELL_SIZE;
                int py = y * CELL_SIZE;
                
                // Alternating grass shades for depth
                boolean isDark = (x + y) % 2 == 0;
                Color grassShade = isDark ? 
                    new Color(60, 179, 113) :  // Medium sea green
                    new Color(46, 160, 100);   // Slightly darker
                
                g2d.setColor(grassShade);
                g2d.fillRect(px, py, CELL_SIZE, CELL_SIZE);
                
                // Subtle grid lines
                g2d.setColor(new Color(40, 120, 70, 100));
                g2d.drawRect(px, py, CELL_SIZE, CELL_SIZE);
            }
        }
        
        // Draw buildings
        Building[][] grid = model.getCity().getGrid();
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                Building b = grid[x][y];
                if (b != null) {
                    drawBuilding(g2d, b, x, y);
                }
            }
        }
    }
    
    private void drawBuilding(Graphics2D g2d, Building b, int x, int y) {
        int px = x * CELL_SIZE;
        int py = y * CELL_SIZE;
        
        // Building colors and style
        Color baseColor, accentColor;
        String label;
        
        if (b instanceof PowerPlant) {
            baseColor = new Color(255, 193, 7);    // Amber
            accentColor = new Color(255, 152, 0);  // Orange
            label = "P" + b.getLevel();
        } else if (b instanceof Residence) {
            baseColor = new Color(121, 85, 72);    // Brown
            accentColor = new Color(93, 64, 55);   // Dark brown
            label = "H" + b.getLevel();
        } else {
            baseColor = Color.GRAY;
            accentColor = Color.DARK_GRAY;
            label = "?";
        }
        
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(px + 7, py + 7, CELL_SIZE - 10, CELL_SIZE - 10, 8, 8);
        
        // Building base
        g2d.setColor(baseColor);
        g2d.fillRoundRect(px + 5, py + 5, CELL_SIZE - 10, CELL_SIZE - 10, 8, 8);
        
        // Roof/accent
        g2d.setColor(accentColor);
        g2d.fillRoundRect(px + 5, py + 5, CELL_SIZE - 10, 12, 8, 8);
        
        // Border
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.setStroke(new java.awt.BasicStroke(2));
        g2d.drawRoundRect(px + 5, py + 5, CELL_SIZE - 10, CELL_SIZE - 10, 8, 8);
        g2d.setStroke(new java.awt.BasicStroke(1));
        
        // Label
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = px + (CELL_SIZE - fm.stringWidth(label)) / 2;
        int textY = py + (CELL_SIZE + fm.getAscent()) / 2 + 2;
        
        // Text shadow
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(label, textX + 1, textY + 1);
        
        // Text
        g2d.setColor(Color.WHITE);
        g2d.drawString(label, textX, textY);
        
        // Health bar
        double healthPercent = b.getHealth() / b.getMaxHealth();
        int barWidth = CELL_SIZE - 14;
        int barHeight = 4;
        int barX = px + 7;
        int barY = py + CELL_SIZE - 10;
        
        // Health bar background
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        // Health bar fill
        Color healthColor = healthPercent > 0.6 ? new Color(76, 175, 80) :
                           healthPercent > 0.3 ? new Color(255, 193, 7) :
                           new Color(244, 67, 54);
        g2d.setColor(healthColor);
        g2d.fillRect(barX, barY, (int)(barWidth * healthPercent), barHeight);
    }
}
