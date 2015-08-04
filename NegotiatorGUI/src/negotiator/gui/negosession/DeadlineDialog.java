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
 * @author W.Pasman
 *
 */
@SuppressWarnings("serial")
public class DeadlineDialog extends JDialog {

	private JPanel panel = new JPanel();
	private JButton btnOk = new JButton("Ok");
	private JButton btnCancel = new JButton("Cancel");

	private Deadline oldDeadline;
	private Deadline newDeadline;

	private final SpinnerNumberModel valuemodel = new SpinnerNumberModel(180,
			1, 10000, 10);
	private JSpinner spinner = new JSpinner(valuemodel);
	private JComboBox combobox = new JComboBox(DeadlineType.values());

	/**
	 * Edit existing deadline. Call {@link #getDeadlines()} after completion to
	 * get new value.
	 * 
	 * @param parent
	 * @param oldDeadl
	 *            existing deadline
	 */
	public DeadlineDialog(Component parent, Deadline oldDeadl) {
		this(parent);

		oldDeadline = oldDeadl;
		newDeadline = oldDeadl;

		valuemodel.setValue(oldDeadl.getValue());
		combobox.setSelectedItem(oldDeadl.getType());
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

	/**
	 * @return the new deadline as confirmed by the user. Returns old deadline
	 *         until the user presses OK.
	 */
	public Deadline getDeadline() {
		return newDeadline;
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
		newDeadline = getGuiSetting();
		dispose();
	}

	private void onCancel() {
		newDeadline = oldDeadline;
		dispose();
	}

}
