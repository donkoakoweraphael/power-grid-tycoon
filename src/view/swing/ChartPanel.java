package view.swing;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import model.GameModel;

/**
 * Panneau affichant un graphique de l'evolution du bonheur au fil du temps.
 */
public class ChartPanel extends JPanel {
    
    private GameModel model;
    private static final int PADDING = 30;
    private static final int MAX_POINTS = 50; // Nombre max de points a afficher
    
    public ChartPanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(300, 150));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth() - 2 * PADDING;
        int height = getHeight() - 2 * PADDING;
        
        // Titre
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2d.setColor(new Color(60, 60, 60));
        g2d.drawString("Evolution du Bonheur (%)", PADDING, 18);
        
        // Axes
        g2d.setColor(new Color(180, 180, 180));
        g2d.drawLine(PADDING, PADDING, PADDING, getHeight() - PADDING);
        g2d.drawLine(PADDING, getHeight() - PADDING, getWidth() - PADDING, getHeight() - PADDING);
        
        // Labels Y (0%, 50%, 100%)
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2d.setColor(new Color(120, 120, 120));
        g2d.drawString("100", 5, PADDING + 5);
        g2d.drawString("50", 10, PADDING + height / 2 + 5);
        g2d.drawString("0", 15, getHeight() - PADDING + 5);
        
        // Lignes de grille horizontales
        g2d.setColor(new Color(230, 230, 230));
        g2d.drawLine(PADDING, PADDING + height / 2, getWidth() - PADDING, PADDING + height / 2);
        g2d.drawLine(PADDING, PADDING + height / 4, getWidth() - PADDING, PADDING + height / 4);
        g2d.drawLine(PADDING, PADDING + 3 * height / 4, getWidth() - PADDING, PADDING + 3 * height / 4);
        
        // Donnees du graphique
        List<Double> happinessData = model.getHappinessHistory();
        
        if (happinessData.isEmpty()) {
            g2d.setColor(new Color(150, 150, 150));
            g2d.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            g2d.drawString("Pas encore de donnees...", PADDING + 20, PADDING + height / 2);
            return;
        }
        
        // Limiter aux derniers points
        int startIdx = Math.max(0, happinessData.size() - MAX_POINTS);
        List<Double> displayData = happinessData.subList(startIdx, happinessData.size());
        
        if (displayData.size() < 2) return;
        
        // Dessiner la courbe
        int pointCount = displayData.size();
        double xStep = (double) width / (pointCount - 1);
        
        // Remplir sous la courbe (gradient)
        int[] xPoints = new int[pointCount + 2];
        int[] yPoints = new int[pointCount + 2];
        
        for (int i = 0; i < pointCount; i++) {
            double value = Math.max(0, Math.min(100, displayData.get(i)));
            xPoints[i] = PADDING + (int)(i * xStep);
            yPoints[i] = getHeight() - PADDING - (int)(value * height / 100);
        }
        xPoints[pointCount] = PADDING + width;
        yPoints[pointCount] = getHeight() - PADDING;
        xPoints[pointCount + 1] = PADDING;
        yPoints[pointCount + 1] = getHeight() - PADDING;
        
        // Remplissage gradient
        GradientPaint gradient = new GradientPaint(
            0, PADDING, new Color(76, 175, 80, 100),
            0, getHeight() - PADDING, new Color(76, 175, 80, 20)
        );
        g2d.setPaint(gradient);
        g2d.fillPolygon(xPoints, yPoints, pointCount + 2);
        
        // Dessiner la ligne
        g2d.setColor(new Color(76, 175, 80));
        g2d.setStroke(new BasicStroke(2f));
        for (int i = 0; i < pointCount - 1; i++) {
            double val1 = Math.max(0, Math.min(100, displayData.get(i)));
            double val2 = Math.max(0, Math.min(100, displayData.get(i + 1)));
            
            int x1 = PADDING + (int)(i * xStep);
            int y1 = getHeight() - PADDING - (int)(val1 * height / 100);
            int x2 = PADDING + (int)((i + 1) * xStep);
            int y2 = getHeight() - PADDING - (int)(val2 * height / 100);
            
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // Dernier point (actuel)
        if (!displayData.isEmpty()) {
            double lastValue = displayData.get(displayData.size() - 1);
            int lastX = PADDING + (int)((pointCount - 1) * xStep);
            int lastY = getHeight() - PADDING - (int)(lastValue * height / 100);
            
            g2d.setColor(Color.WHITE);
            g2d.fillOval(lastX - 5, lastY - 5, 10, 10);
            g2d.setColor(new Color(76, 175, 80));
            g2d.drawOval(lastX - 5, lastY - 5, 10, 10);
            
            // Valeur actuelle
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2d.setColor(new Color(60, 60, 60));
            g2d.drawString(String.format("%.0f%%", lastValue), lastX - 15, lastY - 10);
        }
    }
    
    public void update() {
        repaint();
    }
}
