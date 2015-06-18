package negotiator.gui.progress;

import java.awt.BorderLayout;
import java.awt.Panel;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartyNegotiationSessionEvent;

@SuppressWarnings("serial")
public class MultiPartyTournamentProgressUI extends Panel implements
		MultipartyNegotiationEventListener {

	JTextArea text = new JTextArea();
	JScrollPane scrollpane = new JScrollPane(text);

	public MultiPartyTournamentProgressUI() {
		setLayout(new BorderLayout());

		// init the table model.
		// init the progress panel with progress table
		text.setText("Multiparty Tournament in progress...");
		add(scrollpane, BorderLayout.CENTER);
	}

	/*************** implements MultipartyNegotiationEventListener *********************/

	@Override
	public void handleOfferActionEvent(MultipartyNegotiationOfferEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleLogMessageEvent(LogMessageEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleMultipartyNegotiationEvent(
			MultipartyNegotiationSessionEvent evt) {
		// TODO Auto-generated method stub

	}

}
