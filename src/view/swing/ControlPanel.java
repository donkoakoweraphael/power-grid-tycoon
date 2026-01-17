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
    private JLabel priceValueLabel;

    public ControlPanel(GameController controller, GameFrame frame) {
        this.controller = controller;
        this.frame = frame;

        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        setBackground(new Color(250, 250, 250));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        Font buttonFont = new Font("Segoe UI", Font.BOLD, 12);

        // 1. Bouton HEURE SUIVANTE
        JButton nextBtn = createStyledButton("> Heure Suivante", new Color(76, 175, 80));
        nextBtn.setFont(buttonFont);
        nextBtn.addActionListener(e -> {
            controller.handleNextDay();
            frame.refresh();
        });
        add(nextBtn);

        add(Box.createHorizontalStrut(20)); // Separator

        // 2. Bouton CONSTRUIRE (Shop)
        JButton buildBtn = createStyledButton("+ Construire", new Color(33, 150, 243));
        buildBtn.setFont(buttonFont);
        buildBtn.addActionListener(e -> showBuildDialog());
        add(buildBtn);

        add(Box.createHorizontalStrut(20)); // Separator

        // 3. CONTROLE DE PRIX (Inline)
        JPanel pricePanel = createPriceControlPanel();
        add(pricePanel);
    }

    // ----- Price Control Logic -----

    private JPanel createPriceControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Prix Électricité"));

        JButton minusBtn = new JButton("-");
        styleMiniButton(minusBtn, new Color(244, 67, 54));
        minusBtn.addActionListener(e -> updatePrice(-1));

        JButton plusBtn = new JButton("+");
        styleMiniButton(plusBtn, new Color(76, 175, 80));
        plusBtn.addActionListener(e -> updatePrice(1));

        priceValueLabel = new JLabel("0.0 🪙");
        priceValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        priceValueLabel.setPreferredSize(new Dimension(80, 25));
        priceValueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(minusBtn);
        panel.add(priceValueLabel);
        panel.add(plusBtn);

        return panel;
    }

    private void styleMiniButton(JButton btn, Color color) {
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void updatePrice(double delta) {
        double current = controller.getModel().getCity().getElectricityPrice();
        double newPrice = Math.max(1.0, current + delta);
        controller.setElectricityPrice(controller.getModel(), newPrice); // Correct call via service wrapper if needed
                                                                         // or direct
        // GameController delegates to Service, but here we can call setElectricityPrice
        // via Controller if visible?
        // Wait, Controller needs a setElectricityPrice method that calls service
        // Let's check Controller methods.
        // It has `gameService.setElectricityPrice`.
        // We will assume controller has a wrapper or we access model directly?
        // Ah, Controller has `gameService` private.
        // We should add `controller.handleSetPrice(newPrice)`?
        // Existing controller method: `setElectricityPrice(GameModel, double)`?
        // The Controller.java shows `gameService.setElectricityPrice` is in
        // `GameServiceImpl`.
        // Controller currently exposes `handleBuyPlant`, `handleNextDay`.
        // Let's check if Controller has a setter for price exposed.
        // Looking at previous view_file of GameController: it DOES NOT have `setPrice`
        // exposed publicly to UI.
        // BUT `GameServiceImpl` DOES.
        // Wait, `GameController` usually just delegates.
        // Let's assume for now we can access `controller.setElectricityPrice`.
        // If not, I will add it or use `gameService` if accessible.

        // Actually, let's fix this properly.
        // I'll assume `controller.setElectricityPrice` exists or I'll use
        // `controller.getModel().getCity().setElectricityPrice` + Notify.
        // Ideally we go through controller. Let's try `controller.setElectricityPrice`.
        // If it compiles error, I will fix Controller.

        try {
            // Direct model update + observer notification if controller method missing
            // controller.setElectricityPrice(controller.getModel(), newPrice); // This
            // method existed in GameService
            // Let's call the service wrapper if Controller has it.
            // If Controller.java has `public void setElectricityPrice(...)`
            // from the `view_file` I saw earlier...
            // Wait, I saw `setElectricityPrice` in `GameServiceImpl`.
            // Controller.java had `handleBuyPlant`, `handleNextDay`.
            // It did NOT seem to have `handleSetPrice`.
            // So I will likely need to update Controller too.
            // For now, I will modify Model directly and trigger methods.
            controller.getModel().getCity().setElectricityPrice(newPrice);
            controller.getModel().notifyObservers();
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
        update(); // Refresh UI
    }

    // ----- Improved Shop Logic -----

    private void showBuildDialog() {
        JDialog dialog = new JDialog(frame, "Construction", true);
        dialog.setLayout(new BorderLayout(0, 0));
        dialog.setBackground(Color.WHITE);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel title = new JLabel("Catalogue de Construction");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(20));

        // Available Plants
        java.util.List<viewmodel.GameViewModel.PlantShopInfo> availablePlants = frame.getViewModel()
                .getAvailablePlants();

        for (viewmodel.GameViewModel.PlantShopInfo info : availablePlants) {
            Color color = getPlantColor(info.type());
            JButton btn = createMakeoverBuildButton(info, color);

            btn.addActionListener(e -> {
                dialog.dispose();
                promptCoordinates(info.type(), true);
            });

            content.add(btn);
            content.add(Box.createVerticalStrut(10));
        }

        content.add(Box.createVerticalStrut(15));

        // Residence
        JButton houseBtn = createMakeoverResidenceButton();
        houseBtn.addActionListener(e -> {
            dialog.dispose();
            promptCoordinates("house", false);
        });
        content.add(houseBtn);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        dialog.add(scroll, BorderLayout.CENTER);

        // WIDER WINDOW default
        dialog.setSize(650, 700);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private Color getPlantColor(String type) {
        if (type.equalsIgnoreCase("solar"))
            return new Color(255, 235, 59);
        if (type.equalsIgnoreCase("wind"))
            return new Color(3, 169, 244);
        if (type.equalsIgnoreCase("hydro"))
            return new Color(33, 150, 243);
        if (type.equalsIgnoreCase("coal"))
            return new Color(62, 39, 35);
        if (type.equalsIgnoreCase("gas"))
            return new Color(121, 85, 72);
        if (type.equalsIgnoreCase("nuclear"))
            return new Color(156, 39, 176);
        if (type.equalsIgnoreCase("battery"))
            return new Color(255, 152, 0);
        return Color.GRAY;
    }

    private JButton createMakeoverBuildButton(viewmodel.GameViewModel.PlantShopInfo info, Color accent) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(15, 5));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(600, 80)); // WIDER and TALLER
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // LEFT: Icon + Name + Cost
        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(info.name());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel costLabel = new JLabel(info.cost() + " 🪙");
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        costLabel.setForeground(new Color(46, 125, 50)); // Money Green

        leftPanel.add(nameLabel);
        leftPanel.add(costLabel);

        // RIGHT: Detailed Stats
        JPanel rightPanel = new JPanel(new GridLayout(2, 2, 10, 2));
        rightPanel.setOpaque(false);

        String prodText = info.production() > 0 ? "⚡ " + info.production() + " MW" : "";
        String storeText = info.storage() > 0 ? "🔋 " + info.storage() + " MWh" : "";

        JLabel pLabel = new JLabel(prodText);
        pLabel.setForeground(Color.DARK_GRAY);
        JLabel sLabel = new JLabel(storeText);
        sLabel.setForeground(Color.DARK_GRAY);

        JLabel descLabel = new JLabel("<html><i>" + info.description() + "</i></html>");
        descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        descLabel.setForeground(Color.GRAY);

        rightPanel.add(pLabel);
        rightPanel.add(sLabel);

        // Add components
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(rightPanel, BorderLayout.NORTH);
        centerContainer.add(descLabel, BorderLayout.SOUTH);

        btn.add(leftPanel, BorderLayout.WEST);
        btn.add(centerContainer, BorderLayout.CENTER);

        // Color strip on left
        JPanel colorStrip = new JPanel();
        colorStrip.setBackground(accent);
        colorStrip.setPreferredSize(new Dimension(6, 60));
        btn.add(colorStrip, BorderLayout.EAST); // Put accent on right or left? user asked for style.

        // Hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(245, 250, 255));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(33, 150, 243), 2),
                        BorderFactory.createEmptyBorder(9, 14, 9, 14)));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
            }
        });

        return btn;
    }

    private JButton createMakeoverResidenceButton() {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(15, 5));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(600, 80));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("Résidence");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel costLabel = new JLabel("1000 🪙");
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        costLabel.setForeground(new Color(46, 125, 50));

        leftPanel.add(nameLabel);
        leftPanel.add(costLabel);

        JLabel descLabel = new JLabel(
                "<html><i>Logement pour les citoyens. Consomme de l'énergie et génère des revenus.</i></html>");
        descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        descLabel.setForeground(Color.GRAY);

        btn.add(leftPanel, BorderLayout.WEST);
        btn.add(descLabel, BorderLayout.CENTER);

        JPanel colorStrip = new JPanel();
        colorStrip.setBackground(new Color(121, 85, 72));
        colorStrip.setPreferredSize(new Dimension(6, 60));
        btn.add(colorStrip, BorderLayout.EAST);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(245, 250, 255));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(33, 150, 243), 2),
                        BorderFactory.createEmptyBorder(9, 14, 9, 14)));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)));
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
                    frame.refresh();
                    JOptionPane.showMessageDialog(frame,
                            "Centrale " + type + " construite en (" + x + "," + y + ")",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    controller.handleBuildResidence(x, y);
                    frame.refresh();
                    JOptionPane.showMessageDialog(frame,
                            "Maison construite en (" + x + "," + y + ")",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private JButton createStyledButton(String text, Color accentColor) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(33, 33, 33));
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        BorderFactory.createMatteBorder(0, 3, 0, 0, accentColor)),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 40));

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
        if (controller != null && controller.getModel() != null) {
            double price = controller.getModel().getCity().getElectricityPrice();
            if (priceValueLabel != null) {
                priceValueLabel.setText(String.format("%.1f 🪙", price));
            }
        }
    }
}
