package view;

import controller.GameController;
import viewmodel.GameViewModel;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog for purchasing power plants (The Boutique).
 */
public class ShopDialog extends JDialog {

    private final GameController controller;

    public ShopDialog(Window owner, GameController controller) {
        super(owner, "Boutique - Power Plants", ModalityType.APPLICATION_MODAL);
        this.controller = controller;

        setupFrame();
        createComponents();
    }

    private void setupFrame() {
        setSize(500, 600);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(new Color(25, 25, 25));
        setLayout(new BorderLayout());
    }

    private void createComponents() {
        JPanel header = new JPanel();
        header.setBackground(new Color(40, 40, 40));
        JLabel title = new JLabel("CONSTRUCTION SHOP");
        title.setForeground(new Color(255, 193, 7));
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.add(title);
        add(header, BorderLayout.NORTH);

        JPanel panelShop = new JPanel();
        panelShop.setLayout(new BoxLayout(panelShop, BoxLayout.Y_AXIS));
        panelShop.setBackground(new Color(25, 25, 25));
        panelShop.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        List<GameViewModel.PlantShopInfo> available = controller.getViewModel().getAvailablePlants();
        for (GameViewModel.PlantShopInfo info : available) {
            panelShop.add(createShopWidget(info));
            panelShop.add(Box.createVerticalStrut(10));
        }

        JScrollPane scroll = new JScrollPane(panelShop);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JButton btnClose = new JButton("CLOSE");
        btnClose.addActionListener(e -> dispose());
        add(btnClose, BorderLayout.SOUTH);
    }

    private JPanel createShopWidget(GameViewModel.PlantShopInfo info) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(new Color(40, 40, 40));
        p.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        JLabel name = new JLabel(info.name());
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel specs = new JLabel(String.format("Cost: %.0f | Power: %.1f MW | Storage: %.0f MWh",
                info.cost(), info.production(), info.storage()));
        specs.setForeground(Color.GRAY);
        specs.setFont(new Font("SansSerif", Font.PLAIN, 12));

        left.add(name);
        left.add(specs);
        p.add(left, BorderLayout.CENTER);

        JButton btnBuy = new JButton("BUY");
        btnBuy.setBackground(new Color(0, 150, 136));
        btnBuy.setForeground(Color.WHITE);
        btnBuy.addActionListener(e -> {
            try {
                String id = "P-" + info.type().charAt(0) + "-" + System.currentTimeMillis() % 10000;
                controller.handleBuyPlant(info.type(), id);
                JOptionPane.showMessageDialog(this, info.type().toUpperCase() + " construction started!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Construction Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        p.add(btnBuy, BorderLayout.EAST);

        p.setMaximumSize(new Dimension(450, 65));
        return p;
    }
}
