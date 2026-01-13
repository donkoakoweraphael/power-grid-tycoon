package view;

import controller.GameController;
import model.entity.PowerPlant;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Detailed information about a power plant.
 */
public class PlantInfoDialog extends JDialog {

    public PlantInfoDialog(Window owner, GameController controller, PowerPlant plant) {
        super(owner, "Plant Details - " + plant.getId(), ModalityType.APPLICATION_MODAL);

        setLayout(new BorderLayout());
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(new Color(30, 30, 30));
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel(plant.getClass().getSimpleName().toUpperCase());
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.YELLOW);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.add(title);
        main.add(Box.createVerticalStrut(15));

        List<String> details = controller.getViewModel().getPlantDetails(plant);
        for (String detail : details) {
            JLabel lbl = new JLabel(detail);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            main.add(lbl);
            main.add(Box.createVerticalStrut(5));
        }

        main.add(Box.createVerticalStrut(15));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actions.setOpaque(false);

        JButton btnToggle = new JButton(plant.getStatus().toString().equals("ACTIVE") ? "DEACTIVATE" : "ACTIVATE");
        btnToggle.addActionListener(e -> {
            controller.handleTogglePlant(plant);
            dispose();
        });

        JButton btnUpgrade = new JButton("UPGRADE (" + (int) plant.getUpgradeCost() + ")");
        btnUpgrade.setEnabled(plant.getLevel() < plant.getMaxLevel());
        btnUpgrade.addActionListener(e -> {
            controller.handleUpgradeBuilding(plant.getId());
            dispose();
        });

        actions.add(btnToggle);
        actions.add(btnUpgrade);
        main.add(actions);

        add(main, BorderLayout.CENTER);

        JButton btnClose = new JButton("CLOSE");
        btnClose.addActionListener(e -> dispose());
        add(btnClose, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }
}
