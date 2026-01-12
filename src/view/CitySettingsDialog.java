package view;

import controller.GameController;
import javax.swing.*;
import java.awt.*;

/**
 * Dialog for city configuration (name, electricity price).
 */
public class CitySettingsDialog extends JDialog {

    private final GameController controller;
    private JTextField fieldName;
    private JSlider sliderPrice;
    private JLabel labelPriceValue;

    public CitySettingsDialog(Window owner, GameController controller) {
        super(owner, "City Configuration", ModalityType.APPLICATION_MODAL);
        this.controller = controller;

        setupFrame();
        createComponents();
    }

    private void setupFrame() {
        setSize(400, 350);
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(new Color(35, 35, 35));
        setLayout(new GridBagLayout());
    }

    private void createComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 30, 10, 30);

        JLabel title = new JLabel("CONFIGURATION", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        add(title, gbc);

        // Name
        add(new JLabel("City Name:"), gbc);
        fieldName = new JTextField(controller.getViewModel().getCityName());
        add(fieldName, gbc);

        // Price
        add(new JLabel("Electricity Price:"), gbc);
        double currentPrice = controller.getViewModel().getElectricityPrice();
        labelPriceValue = new JLabel(String.format("%.2f Coins/MWh", currentPrice));
        labelPriceValue.setForeground(Color.YELLOW);
        add(labelPriceValue, gbc);

        sliderPrice = new JSlider(1, 50, (int) currentPrice);
        sliderPrice.addChangeListener(e -> {
            labelPriceValue.setText(String.format("%d.00 Coins/MWh", sliderPrice.getValue()));
        });
        add(sliderPrice, gbc);

        add(Box.createVerticalStrut(20), gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = new JButton("Apply Changes");
        btnSave.setBackground(new Color(76, 175, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            controller.handleRenameCity(fieldName.getText());
            controller.handleSetPrice(sliderPrice.getValue());
            controller.handleSave("autosave");
            dispose();
        });

        buttons.add(btnCancel);
        buttons.add(btnSave);
        add(buttons, gbc);
    }
}
