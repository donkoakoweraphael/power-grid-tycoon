package view.swing;

import javax.swing.*;
import java.awt.*;
import controller.GameController;

/**
 * Control panel with build buttons.
 */
public class ControlPanel extends JPanel {
    
    private GameController controller;
    private GameFrame frame;
    
    public ControlPanel(GameController controller, GameFrame frame) {
        this.controller = controller;
        this.frame = frame;
        
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        setBackground(new Color(250, 250, 250));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);
        
        // Next hour button
        JButton nextBtn = createStyledButton("Heure Suivante", new Color(76, 175, 80));
        nextBtn.setFont(buttonFont);
        nextBtn.addActionListener(e -> {
            controller.handleNextDay();
            frame.refresh();
        });
        add(nextBtn);
        
        // Build solar button
        JButton solarBtn = createStyledButton("Construire Solaire (800)", new Color(255, 193, 7));
        solarBtn.setFont(buttonFont);
        solarBtn.addActionListener(e -> {
            String coords = JOptionPane.showInputDialog(frame, "Entrez les coordonnees (x,y):");
            if (coords != null && coords.contains(",")) {
                String[] parts = coords.split(",");
                try {
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());
                    String id = "solar-" + System.currentTimeMillis();
                    controller.handleBuyPlant("solar", id, x, y);
                    frame.refresh();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Erreur: " + ex.getMessage());
                }
            }
        });
        add(solarBtn);
        
        // Build house button
        JButton houseBtn = createStyledButton("Construire Maison (1000)", new Color(121, 85, 72));
        houseBtn.setFont(buttonFont);
        houseBtn.addActionListener(e -> {
            String coords = JOptionPane.showInputDialog(frame, "Entrez les coordonnees (x,y):");
            if (coords != null && coords.contains(",")) {
                String[] parts = coords.split(",");
                try {
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());
                    controller.handleBuildResidence(x, y);
                    frame.refresh();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Erreur: " + ex.getMessage());
                }
            }
        });
        add(houseBtn);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 35));
        return btn;
    }
    
    public void update() {
        // Update button states if needed
    }
}
