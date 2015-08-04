package negotiator.gui.negosession;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import negotiator.Deadline;
import negotiator.DeadlineType;

/**
 * Dialog asking user for deadline (type, value).
 * 
 * @author W.Pasman (replaced ugly class generated with some GUI editor).
 *
 */
@SuppressWarnings("serial")
public class DeadlineDialog extends JDialog {

	private JPanel panel = new JPanel();
	private JButton btnOk = new JButton("Ok");
	private JButton btnCancel = new JButton("Cancel");

	private Deadline previousSettings;
	private Deadline settings;

	private final SpinnerNumberModel valuemodel = new SpinnerNumberModel(180,
			1, 10000, 10);
	private JSpinner spinner = new JSpinner(valuemodel);
	private JComboBox combobox = new JComboBox(DeadlineType.values());

	/**
	 * Edit existing deadline. Call {@link #getDeadlines()} after completion to
	 * get new value.
	 * 
	 * @param parent
	 * @param deadlines
	 *            existing deadline
	 */
	public DeadlineDialog(Component parent, Deadline deadlines) {
		this(parent);

		previousSettings = deadlines;
		settings = deadlines;

		valuemodel.setValue(deadlines.getValue());
		combobox.setSelectedItem(deadlines.getType());
	}

	public DeadlineDialog(Component parent) {
		panel.setLayout(new BorderLayout());
		setModal(true);
		setTitle("Enter Deadline");

		panel.add(spinner, BorderLayout.CENTER);

		panel.add(combobox, BorderLayout.EAST);

		setContentPane(panel);
		getRootPane().setDefaultButton(btnOk);
		setLocationRelativeTo(parent);

		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new FlowLayout());
		buttonpanel.add(btnOk);
		buttonpanel.add(btnCancel);

		panel.add(buttonpanel, BorderLayout.SOUTH);

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
	}

	public Deadline getDeadline() {
		return settings;
	}

	/**
	 * Get the setting as currently in the GUI.
	 * 
	 * @return
	 */
	private Deadline getGuiSetting() {
		return new Deadline((Integer) valuemodel.getValue(),
				(DeadlineType) combobox.getSelectedItem());
	}

	private void onOK() {
		settings = getGuiSetting();
		dispose();
	}

	private void onCancel() {
		settings = previousSettings;
		dispose();
	}

}
