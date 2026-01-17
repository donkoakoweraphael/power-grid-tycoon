package view.swing;

import javax.swing.*;
import java.awt.*;
import model.GameModel;
import model.entity.Building;
import model.entity.PowerPlant;
import model.entity.Residence;

/**
 * Panneau de grille affichant la carte de la ville.
 */
public class GridPanel extends JPanel {

    private static final int CELL_SIZE = 75;
    private static final Color GRASS_COLOR = new Color(34, 139, 34);
    private static final Color GRID_COLOR = new Color(0, 100, 0);

    private GameModel model;
    private GameFrame frame;

    // Interaction State
    private String pendingBuildingType = null;
    private boolean pendingIsPowerPlant = false;
    private Point hoverCell = null;
    private boolean isMoveMode = false;
    private Point moveSource = null;

    public GridPanel(GameModel model, GameFrame frame) {
        this.model = model;
        this.frame = frame;

        int LABEL_OFFSET = 30;
        int gridSize = model.getCity().getWidth() * CELL_SIZE + LABEL_OFFSET;
        setPreferredSize(new Dimension(gridSize, gridSize));
        setBackground(Color.BLACK);

        // Ecouteur de clic souris
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Right click check first
                if (isMoveMode || pendingBuildingType != null) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        cancelConstructionMode();
                        return;
                    }
                }

                int LABEL_OFFSET = 30;
                int gridX = (e.getX() - LABEL_OFFSET) / CELL_SIZE;
                int gridY = (e.getY() - LABEL_OFFSET) / CELL_SIZE;

                if (gridX >= 0 && gridX < model.getCity().getWidth() &&
                        gridY >= 0 && gridY < model.getCity().getHeight()) {

                    if (pendingBuildingType != null) {
                        if (!model.getCity().isCellOccupied(gridX, gridY)) {
                            frame.finalizeConstruction(pendingBuildingType, pendingIsPowerPlant, gridX, gridY);
                            cancelConstructionMode();
                        } else {
                            Toolkit.getDefaultToolkit().beep();
                        }
                    } else if (isMoveMode) {
                        if (moveSource == null) {
                            if (model.getCity().isCellOccupied(gridX, gridY)) {
                                moveSource = new Point(gridX, gridY);
                            } else {
                                Toolkit.getDefaultToolkit().beep();
                            }
                        } else {
                            if (!model.getCity().isCellOccupied(gridX, gridY)) {
                                frame.finalizeMove(moveSource.x, moveSource.y, gridX, gridY);
                                resetMoveSelection();
                            } else {
                                Toolkit.getDefaultToolkit().beep();
                            }
                        }
                        repaint();
                    } else {
                        Building b = model.getCity().getGrid()[gridX][gridY];
                        frame.showBuildingInfo(b, gridX, gridY);
                    }
                }
            }
        });

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                if (pendingBuildingType != null || isMoveMode) {
                    int LABEL_OFFSET = 30;
                    int gridX = (e.getX() - LABEL_OFFSET) / CELL_SIZE;
                    int gridY = (e.getY() - LABEL_OFFSET) / CELL_SIZE;

                    if (gridX >= 0 && gridX < model.getCity().getWidth() &&
                            gridY >= 0 && gridY < model.getCity().getHeight()) {
                        hoverCell = new Point(gridX, gridY);
                    } else {
                        hoverCell = null;
                    }
                    repaint();
                }
            }
        });
    }

    public void startConstructionMode(String type, boolean isPowerPlant) {
        this.pendingBuildingType = type;
        this.pendingIsPowerPlant = isPowerPlant;
        this.isMoveMode = false;
        this.moveSource = null;
        this.hoverCell = null;
        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        repaint();
    }

    public void startMoveMode() {
        this.isMoveMode = true;
        this.pendingBuildingType = null;
        this.moveSource = null;
        this.hoverCell = null;
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        JOptionPane.showMessageDialog(frame,
                "Mode Deplacement Actif!\n1. Choisissez le batiment.\n2. Cliquez sur une case vide.", "Deplacement",
                JOptionPane.INFORMATION_MESSAGE);
        repaint();
    }

    public void resetMoveSelection() {
        this.moveSource = null;
        repaint();
    }

    public void cancelConstructionMode() {
        this.pendingBuildingType = null;
        this.isMoveMode = false;
        this.moveSource = null;
        this.hoverCell = null;
        setCursor(Cursor.getDefaultCursor());
        if (frame != null)
            frame.notifyMoveModeEnded();
        repaint();
    }

    public void setModel(GameModel model) {
        this.model = model;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int gridWidth = model.getCity().getWidth();
        int gridHeight = model.getCity().getHeight();

        int LABEL_OFFSET = 25; // Espace pour les etiquettes des axes

        // Dessiner les etiquettes des axes
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(Color.WHITE);

        // Indicateur d'axes (X horizontal/colonnes, Y vertical/lignes)
        g2d.drawString("Y \\ X", 2, 18);

        // Etiquettes axe X (horizontal - en haut)
        for (int x = 0; x < gridWidth; x++) {
            int px = LABEL_OFFSET + x * CELL_SIZE + CELL_SIZE / 2;
            g2d.drawString(String.valueOf(x), px - 4, 18);
        }

        // Etiquettes axe Y (vertical - à gauche)
        for (int y = 0; y < gridHeight; y++) {
            int py = LABEL_OFFSET + y * CELL_SIZE + CELL_SIZE / 2;
            g2d.drawString(String.valueOf(y), 8, py + 5);
        }

        // Dessiner les tuiles d'herbe avec motif en damier pour la profondeur
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                int px = LABEL_OFFSET + x * CELL_SIZE;
                int py = LABEL_OFFSET + y * CELL_SIZE;

                // Nuances d'herbe alternees pour la profondeur
                boolean isDark = (x + y) % 2 == 0;
                Color grassShade = isDark ? new Color(60, 179, 113) : // Medium sea green
                        new Color(46, 160, 100); // Slightly darker

                g2d.setColor(grassShade);
                g2d.fillRect(px, py, CELL_SIZE, CELL_SIZE);

                // Lignes de grille subtiles
                g2d.setColor(new Color(40, 120, 70, 100));
                g2d.drawRect(px, py, CELL_SIZE, CELL_SIZE);
            }
        }

        // Dessiner les batiments
        Building[][] grid = model.getCity().getGrid();
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                Building b = grid[x][y];
                if (b != null) {
                    drawBuilding(g2d, b, x, y, LABEL_OFFSET);
                }
            }
        }
        // Dessiner le highlight de survol (Construction / Deplacement)
        if (hoverCell != null && (pendingBuildingType != null || isMoveMode)) {
            drawHighlight(g2d, hoverCell.x, hoverCell.y, LABEL_OFFSET);
        }
    }

    private void drawHighlight(Graphics2D g2d, int x, int y, int offset) {
        int px = offset + x * CELL_SIZE;
        int py = offset + y * CELL_SIZE;

        boolean isOccupied = model.getCity().isCellOccupied(x, y);
        Color highlightColor = null;

        if (pendingBuildingType != null) {
            // MODE CONSTRUCTION
            // Libre = Vert, Occupe = Rouge
            highlightColor = !isOccupied ? new Color(76, 175, 80, 150) : new Color(244, 67, 54, 150);
        } else if (isMoveMode) {
            // MODE DEPLACEMENT
            if (moveSource == null) {
                // Etape 1 : Selection du batiment source
                // Occupe = Bleu (Selectionnable), Vide = Rien/Gris
                if (isOccupied) {
                    highlightColor = new Color(33, 150, 243, 150); // Blue for "Pick me"
                }
            } else {
                // Etape 2 : Selection de la destination
                // Libre = Vert, Occupe = Rouge
                highlightColor = !isOccupied ? new Color(76, 175, 80, 150) : new Color(244, 67, 54, 150);
            }
        }

        if (highlightColor != null) {
            g2d.setColor(highlightColor);
            g2d.fillRect(px, py, CELL_SIZE, CELL_SIZE);

            // Border for extra visibility
            g2d.setColor(highlightColor.darker());
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(px + 1, py + 1, CELL_SIZE - 2, CELL_SIZE - 2);
            g2d.setStroke(new BasicStroke(1));
        }
    }

    private void drawBuilding(Graphics2D g2d, Building b, int x, int y, int offset) {
        int px = offset + x * CELL_SIZE;
        int py = offset + y * CELL_SIZE;

        // Couleurs et style des batiments
        Color baseColor, accentColor;
        String label;

        if (b instanceof PowerPlant) {
            baseColor = new Color(255, 193, 7); // Amber
            accentColor = new Color(255, 152, 0); // Orange
            label = b.getGridCode() + "-" + b.getLevel();
        } else if (b instanceof Residence) {
            baseColor = new Color(121, 85, 72); // Brown
            accentColor = new Color(93, 64, 55); // Dark brown
            label = "H-" + b.getLevel(); // Residence might not have grid code yet, checking Building.java
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
        Color healthColor = healthPercent > 0.6 ? new Color(76, 175, 80)
                : healthPercent > 0.3 ? new Color(255, 193, 7) : new Color(244, 67, 54);
        g2d.setColor(healthColor);
        g2d.fillRect(barX, barY, (int) (barWidth * healthPercent), barHeight);
    }
}
