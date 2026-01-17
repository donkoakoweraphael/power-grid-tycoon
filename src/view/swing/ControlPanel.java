package view.swing;

import javax.swing.*;
import java.awt.*;
import controller.GameController;

/**
 * Panneau de controle avec les boutons d'action du jeu.
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
        
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
        
        // Bouton heure suivante
        JButton nextBtn = createStyledButton("> Heure Suivante", new Color(76, 175, 80));
        nextBtn.setFont(buttonFont);
        nextBtn.addActionListener(e -> {
            controller.handleNextDay();
            frame.refresh();
        });
        add(nextBtn);
        
        // Bouton de construction unifie
        JButton buildBtn = createStyledButton("+ Construire", new Color(33, 150, 243));
        buildBtn.setFont(buttonFont);
        buildBtn.addActionListener(e -> showBuildDialog());
        add(buildBtn);
    }
    
    private void showBuildDialog() {
        JDialog dialog = new JDialog(frame, "Construire un batiment", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setBackground(Color.WHITE);
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Choisissez un type de batiment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(20));
        
        // Section centrales electriques
        JLabel plantLabel = new JLabel("[CENTRALES ELECTRIQUES]");
        plantLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        content.add(plantLabel);
        content.add(Box.createVerticalStrut(10));
        
        String[] plants = {"Solar", "Wind", "Coal", "Gas", "Hydro", "Nuclear"};
        String[] plantIcons = {"[S]", "[W]", "[C]", "[G]", "[H]", "[N]"};
        int[] plantCosts = {800, 1000, 1500, 1200, 2000, 5000};
        
        for (int i = 0; i < plants.length; i++) {
            final String plantType = plants[i].toLowerCase();
            JButton btn = createBuildOptionButton(
                plantIcons[i] + " " + plants[i], 
                plantCosts[i] + " pieces",
                new Color(255, 193, 7)
            );
            btn.addActionListener(e -> {
                dialog.dispose();
                promptCoordinates(plantType, true);
            });
            content.add(btn);
            content.add(Box.createVerticalStrut(8));
        }
        
        content.add(Box.createVerticalStrut(10));
        
        // Section residences
        JLabel houseLabel = new JLabel("[RESIDENCES]");
        houseLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        content.add(houseLabel);
        content.add(Box.createVerticalStrut(10));
        
        JButton houseBtn = createBuildOptionButton("[R] Maison", "1000 pieces", new Color(121, 85, 72));
        houseBtn.addActionListener(e -> {
            dialog.dispose();
            promptCoordinates("house", false);
        });
        content.add(houseBtn);
        
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        dialog.add(scroll, BorderLayout.CENTER);
        
        dialog.pack();
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
    
    private JButton createBuildOptionButton(String name, String cost, Color color) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 0));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(350, 50));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(new Color(33, 33, 33));
        
        JLabel costLabel = new JLabel(cost);
        costLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        costLabel.setForeground(color);
        
        btn.add(nameLabel, BorderLayout.WEST);
        btn.add(costLabel, BorderLayout.EAST);
        
        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        
        return btn;
    }
    
    private void promptCoordinates(String type, boolean isPowerPlant) {
        String coords = JOptionPane.showInputDialog(frame, "Entrez les coordonnees (x,y):");
        if (coords != null && coords.contains(",")) {
            String[] parts = coords.split(",");
            try {
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                
                if (isPowerPlant) {
                    String id = type + "-" + System.currentTimeMillis();
                    controller.handleBuyPlant(type, id, x, y);
                } else {
                    controller.handleBuildResidence(x, y);
                }
                
                frame.refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur: " + ex.getMessage());
            }
        }
    }
    
    private JButton createStyledButton(String text, Color accentColor) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(33, 33, 33));  // Dark gray text
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createMatteBorder(0, 3, 0, 0, accentColor)  // Colored left border
            ),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 40));
        
        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        
        return btn;
    }
    
    public void update() {
        // Update button states if needed
    }
}
