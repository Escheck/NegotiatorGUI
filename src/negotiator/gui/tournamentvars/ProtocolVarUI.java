package negotiator.gui.tournamentvars;

import java.awt.Frame;
import java.awt.Panel;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import negotiator.exceptions.Warning;
import negotiator.gui.DefaultOKCancelDialog;
import negotiator.repository.ProtocolRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;

/**
 * Open a UI and negotiate with user about which agents to use in tournament.
 * 
 * @author wouter
 * 
 */
public class ProtocolVarUI extends DefaultOKCancelDialog {

	private static final long serialVersionUID = -6106919299675060907L;
	ArrayList<ProtocolRadioButton> radioButtons; // copy of what's in the panel,
													// for easy check-out.

	public ProtocolVarUI(Frame owner) {
		super(owner, "Protocol Variable Selector");

	}

	public Panel getPanel() {
		radioButtons = new ArrayList<ProtocolRadioButton>();
		Panel protocolList = new Panel();
		protocolList.setLayout(new BoxLayout(protocolList, BoxLayout.Y_AXIS));
		ButtonGroup group = new ButtonGroup();

		Repository protocolRep = Repository.getProtocolRepository();
		for (RepItem agt : protocolRep.getItems()) {
			if (!(agt instanceof ProtocolRepItem))
				new Warning("there is a non-AgentRepItem in agent repository:"
						+ agt);
			ProtocolRadioButton cbox = new ProtocolRadioButton(
					(ProtocolRepItem) agt);
			radioButtons.add(cbox);
			protocolList.add(cbox);
			cbox.setSelected(true);
			group.add(cbox);
		}
		return protocolList;
	}

	public Object ok() {
		ArrayList<ProtocolRepItem> result = new ArrayList<ProtocolRepItem>();
		for (ProtocolRadioButton cbox : radioButtons) {
			if (cbox.isSelected())
				result.add(cbox.protocolRepItem);
		}
		return result;
	}
}

class ProtocolRadioButton extends JRadioButton {
	public ProtocolRepItem protocolRepItem;

	public ProtocolRadioButton(ProtocolRepItem protocolRepItem) {
		super("" + protocolRepItem.getName());
		this.protocolRepItem = protocolRepItem;
	}
}