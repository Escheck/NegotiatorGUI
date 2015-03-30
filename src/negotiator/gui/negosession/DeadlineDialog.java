package negotiator.gui.negosession;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import negotiator.DeadlineType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import static negotiator.DeadlineType.TIME;
import static negotiator.DeadlineType.ROUND;

public class DeadlineDialog extends JDialog {
    private JPanel contentPane;
    private JButton btnOk;
    private JButton btnCancel;
    private JTextField txtTime;
    private JTextField txtRounds;
    private JCheckBox chkTime;
    private JCheckBox chkRounds;
    private JLabel lblTime;
    private JLabel lblRounds;

    private HashMap<DeadlineType, Object> previousSettings;
    private HashMap<DeadlineType, Object> settings;


    public DeadlineDialog(Component parent, HashMap<DeadlineType, Object> deadlines) {
        this(parent);

        previousSettings = deadlines;
        settings = deadlines;
        updateTextFields();
    }

    public DeadlineDialog(Component parent) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnOk);
        setLocationRelativeTo(parent);

        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        chkTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableTextField(txtTime, chkTime.isSelected());
            }
        });
        chkRounds.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enableTextField(txtRounds, chkRounds.isSelected());
            }
        });
        txtTime.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (!txtTime.isEnabled()) {
                    txtTime.setEnabled(true);
                    txtTime.setText("");
                    txtTime.grabFocus();
                    chkTime.setSelected(true);
                }
            }
        });
        txtRounds.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (!txtRounds.isEnabled()) {
                    txtRounds.setEnabled(true);
                    txtRounds.setText("");
                    txtRounds.grabFocus();
                    chkRounds.setSelected(true);
                }
            }
        });
    }

    public HashMap<DeadlineType, Object> getDeadlines() {
        return settings;
    }

    private void updateTextFields() {
        if (settings.containsKey(TIME)) {
            txtTime.setText(settings.get(TIME).toString());
            txtTime.setEnabled(true);
            chkTime.setSelected(true);
        } else {
            txtTime.setText("Disabled");
            txtTime.setEnabled(false);
            chkTime.setSelected(false);
        }
        if (settings.containsKey(ROUND)) {
            txtRounds.setText(settings.get(ROUND).toString());
            txtRounds.setEnabled(true);
            chkRounds.setSelected(true);
        } else {
            txtRounds.setText("Disabled");
            txtRounds.setEnabled(false);
            chkRounds.setSelected(false);
        }
    }

    private void updateSettings() {
        settings.clear();
        if (chkTime.isSelected()) settings.put(TIME, Integer.parseInt(txtTime.getText()));
        if (chkRounds.isSelected()) settings.put(ROUND, Integer.parseInt(txtRounds.getText()));
    }

    private void enableTextField(JTextField field, boolean doEnable) {
        if (doEnable) {
            field.setText("");
            field.setEnabled(true);
        } else {
            field.setText("Disabled");
            field.setEnabled(false);
        }
    }

    private void onOK() {
        try {
            updateSettings();
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid number", "", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void onCancel() {
        settings = previousSettings;
        dispose();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnOk = new JButton();
        btnOk.setText("OK");
        panel2.add(btnOk, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        btnCancel = new JButton();
        btnCancel.setText("Cancel");
        panel2.add(btnCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        lblTime = new JLabel();
        lblTime.setText("Time (in seconds)");
        panel3.add(lblTime, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        lblRounds = new JLabel();
        lblRounds.setText("Rounds");
        panel3.add(lblRounds, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel3.add(spacer3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        txtTime = new JTextField();
        txtTime.setText("300");
        panel3.add(txtTime, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), null, 0, false));
        txtRounds = new JTextField();
        txtRounds.setEnabled(false);
        txtRounds.setText("Disabled");
        txtRounds.putClientProperty("html.disable", Boolean.FALSE);
        panel3.add(txtRounds, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(80, -1), null, 0, false));
        chkTime = new JCheckBox();
        chkTime.setSelected(true);
        chkTime.setText("Enabled");
        chkTime.putClientProperty("html.disable", Boolean.FALSE);
        panel3.add(chkTime, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chkRounds = new JCheckBox();
        chkRounds.setSelected(false);
        chkRounds.setText("Enabled");
        panel3.add(chkRounds, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }


    private class UpdateActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            updateTextFields();
        }
    }


}
