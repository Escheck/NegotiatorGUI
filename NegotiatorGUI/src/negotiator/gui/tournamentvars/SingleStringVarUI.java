package negotiator.gui.tournamentvars;

import java.awt.Frame;
import java.awt.Panel;

import javax.swing.BoxLayout;
import javax.swing.JTextField;

import negotiator.gui.DefaultOKCancelDialog;

public class SingleStringVarUI extends DefaultOKCancelDialog 
{
	private JTextField textField;
	
	public SingleStringVarUI(Frame frame) {
		super(frame, "Number of sessions");
	}
	
	@Override
	public Panel getPanel() {
		textField = new JTextField();
		Panel panel = new Panel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.add(textField);
		return panel;

	}

	@Override
	public Object ok() {
		// TODO Auto-generated method stub
		return textField.getText();
	}

}
