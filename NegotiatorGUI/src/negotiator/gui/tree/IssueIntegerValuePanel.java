package negotiator.gui.tree;

import javax.swing.JLabel;

import negotiator.issue.IssueInteger;
import negotiator.issue.Objective;
import negotiator.utility.EvaluatorInteger;

/**
 * 
 * @author Richard Noorlandt
 * 
 */

public class IssueIntegerValuePanel extends IssueValuePanel {

	// Attributes

	// Constructors
	public IssueIntegerValuePanel(NegotiatorTreeTableModel model,
			IssueInteger issue) {
		super(model, issue);
		init(issue);
	}

	// Methods
	private void init(IssueInteger issue) {
		String lowUtil = "";
		String uppUtil = "";
		if (model.getUtilitySpace() != null) {
			EvaluatorInteger eval = (EvaluatorInteger) model.getUtilitySpace()
					.getEvaluator(issue.getNumber());
			if (eval != null) {
				lowUtil = " ("
						+ String.format("%.3g%n", eval.getUtilLowestValue())
						+ ")";
				uppUtil = " ("
						+ String.format("%.3g%n", eval.getUtilHighestValue())
						+ ")";
			}
		}
		this.add(new JLabel("Min: " + issue.getLowerBound() + lowUtil
				+ " Max: " + issue.getUpperBound() + uppUtil));
	}

	public void displayValues(Objective node) {
		this.removeAll();
		init(((IssueInteger) node));
	}
}
