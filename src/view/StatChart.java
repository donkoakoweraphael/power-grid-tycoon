package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A simple line chart component to visualize historical data.
 */
public class StatChart extends JPanel {

    private final String title;
    private List<Double> data;
    private final Color lineColor;

    public StatChart(String title, Color lineColor) {
        this.title = title;
        this.lineColor = lineColor;
        setOpaque(false);
        setPreferredSize(new Dimension(300, 150));
    }

    public void setData(List<Double> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int padding = 30;

        // Draw Background
        g2.setColor(new Color(40, 40, 40, 180));
        g2.fillRoundRect(0, 0, w, h, 15, 15);
        g2.setColor(new Color(80, 80, 80));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 15, 15);

        // Draw Title
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.drawString(title, 10, 20);

        if (data == null || data.size() < 2) {
            g2.setColor(Color.GRAY);
            g2.drawString("Not enough data", w / 2 - 40, h / 2);
            return;
        }

        // Calculate Scale
        double max = data.stream().mapToDouble(d -> d).max().orElse(1.0);
        double min = data.stream().mapToDouble(d -> d).min().orElse(0.0);
        if (max == min)
            max = min + 1.0;

        double range = max - min;
        double xStep = (double) (w - 2 * padding) / (data.size() - 1);
        double yScale = (double) (h - 2 * padding) / range;

        // Draw Axes (Subtle)
        g2.setColor(new Color(100, 100, 100, 100));
        g2.drawLine(padding, h - padding, w - padding, h - padding);
        g2.drawLine(padding, padding, padding, h - padding);

        // Draw Line
        g2.setColor(lineColor);
        g2.setStroke(new BasicStroke(2f));

        for (int i = 0; i < data.size() - 1; i++) {
            int x1 = padding + (int) (i * xStep);
            int y1 = h - padding - (int) ((data.get(i) - min) * yScale);
            int x2 = padding + (int) ((i + 1) * xStep);
            int y2 = h - padding - (int) ((data.get(i + 1) - min) * yScale);
            g2.drawLine(x1, y1, x2, y2);
        }
    }
}
