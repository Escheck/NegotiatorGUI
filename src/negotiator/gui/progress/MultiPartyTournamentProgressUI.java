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

	JTextArea textArea = new JTextArea();
	StringBuilder text = new StringBuilder();
	JScrollPane scrollpane = new JScrollPane(textArea);

	public MultiPartyTournamentProgressUI() {
		setLayout(new BorderLayout());
		addLine("Multiparty Tournament in progress...");

		// init the table model.
		// init the progress panel with progress table
		textArea.setText(text.toString());
		add(scrollpane, BorderLayout.CENTER);
	}

	/*************** implements MultipartyNegotiationEventListener *********************/

	private void addLine(String msg) {
		text.append(msg + "\n");
		textArea.setText(text.toString());
	}

	@Override
	public void handleOfferActionEvent(MultipartyNegotiationOfferEvent evt) {
		// quick hack, needs table-ization.
		addLine(evt.toString());
	}

	@Override
	public void handleLogMessageEvent(LogMessageEvent evt) {
		// quick hack, needs table-ization.
		addLine(evt.getMessage());
	}

	@Override
	public void handleMultipartyNegotiationEvent(
			MultipartyNegotiationSessionEvent evt) {
		// quick hack, needs table-ization.
		addLine(evt.toString());
	}

}
