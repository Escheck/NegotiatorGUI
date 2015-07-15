package negotiator.gui.progress;

import java.awt.BorderLayout;
import java.awt.Panel;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartyNegotiationSessionEvent;
import negotiator.events.NegotiationEvent;

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
	public void handleEvent(NegotiationEvent e) {
		if (e instanceof LogMessageEvent) {
			addLine(((LogMessageEvent) e).getMessage());
		} else if (e instanceof MultipartyNegotiationOfferEvent) {
			addLine(((MultipartyNegotiationOfferEvent) e).toString());

		} else if (e instanceof MultipartyNegotiationSessionEvent) {
			// quick hack, needs table-ization.
			addLine(((MultipartyNegotiationSessionEvent) e).toString());
		}

	}
}
