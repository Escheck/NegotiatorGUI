package negotiator.gui.progress;

import java.awt.BorderLayout;
import java.awt.Panel;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.AgreementEvent;
import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartyNegotiationSessionEvent;
import negotiator.events.NegotiationEvent;
import negotiator.events.SessionStartedEvent;
import negotiator.events.TournamentEndedEvent;

@SuppressWarnings("serial")
public class MultiPartyTournamentProgressUI extends Panel implements
		MultipartyNegotiationEventListener {

	JTextArea textArea = new JTextArea();
	StringBuilder text = new StringBuilder();
	JScrollPane scrollpane = new JScrollPane(textArea);
	Progress progress = new Progress();

	public MultiPartyTournamentProgressUI() {
		setLayout(new BorderLayout());
		addLine("Multiparty Tournament in progress...");

		// init the table model.
		// init the progress panel with progress table
		textArea.setText(text.toString());
		add(progress, BorderLayout.NORTH);
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
		} else if (e instanceof AgreementEvent) {
			addLine(((AgreementEvent) e).toString());
		} else if (e instanceof TournamentEndedEvent) {
			progress.finish();
		} else if (e instanceof SessionStartedEvent) {
			SessionStartedEvent e1 = (SessionStartedEvent) e;
			progress.update(e1.getCurrentSession(), e1.getTotalSessions());
		}

	}
}

@SuppressWarnings("serial")
class Progress extends JPanel {
	private final int SCALE = 1000;
	private JProgressBar progressbar = new JProgressBar(0, SCALE);
	private JLabel label = new JLabel("starting tournament");

	public Progress() {
		setLayout(new BorderLayout());
		add(label, BorderLayout.EAST);
		add(progressbar, BorderLayout.CENTER);
	}

	/**
	 * Shows progress bar when n of total have been started. We are still
	 * working on the nth, even if it equals the total. Therefore the progress
	 * bar never will go exactly to 100%
	 * 
	 * @param n
	 * @param total
	 */
	public void update(int n, int total) {
		progressbar.setValue(Math.min(SCALE - 1,
				(int) (SCALE * n / (total + 1))));
		label.setText("" + n + "/" + total);
	}

	/**
	 * Set progress bar to 100% of total.
	 * 
	 * @param total
	 */
	public void finish() {
		progressbar.setValue(SCALE);

	}

}