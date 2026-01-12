package view;

import javax.swing.*;
import java.awt.*;

/**
 * Custom dialog to choose the number of days to advance.
 * Only accepts positive integers.
 */
public class DaySkipDialog extends JDialog {

    private int selectedDays = 0;
    private final JSpinner spinner;

    public DaySkipDialog(Window owner) {
        super(owner, "Simulation Skip", ModalityType.APPLICATION_MODAL);

        setLayout(new BorderLayout());
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(new Color(30, 30, 30));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel label = new JLabel("Number of days to advance:");
        label.setForeground(Color.WHITE);
        main.add(label, gbc);

        gbc.gridy = 1;
        // JSpinner already has + and - buttons (up/down)
        SpinnerNumberModel model = new SpinnerNumberModel(7, 1, 365, 1);
        spinner = new JSpinner(model);
        spinner.setPreferredSize(new Dimension(80, 30));
        main.add(spinner, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);

        JButton btnOk = new JButton("CONFIRM");
        btnOk.addActionListener(e -> {
            selectedDays = (int) spinner.getValue();
            dispose();
        });

        JButton btnCancel = new JButton("CANCEL");
        btnCancel.addActionListener(e -> {
            selectedDays = 0;
            dispose();
        });

        buttons.add(btnCancel);
        buttons.add(btnOk);

        add(main, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    public int getSelectedDays() {
        return selectedDays;
    }
}
