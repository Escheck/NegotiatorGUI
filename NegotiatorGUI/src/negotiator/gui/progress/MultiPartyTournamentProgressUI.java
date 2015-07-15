package negotiator.gui.progress;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.AgreementEvent;
import negotiator.events.AgreementEvent.Value;
import negotiator.events.LogMessageEvent;
import negotiator.events.NegotiationEvent;
import negotiator.events.SessionFailedEvent;
import negotiator.events.SessionStartedEvent;
import negotiator.events.TournamentEndedEvent;

@SuppressWarnings("serial")
public class MultiPartyTournamentProgressUI extends Panel implements
		MultipartyNegotiationEventListener {

	Progress progress = new Progress();

	SimpleTableModel model;

	public MultiPartyTournamentProgressUI() {
		init();

		setLayout(new BorderLayout());

		add(progress, BorderLayout.NORTH);

		// table must be inside scrollpane, otherwise the headers do not show.
		JTable resultsTable = new JTable(model);
		resultsTable.setShowGrid(true); // no effect?
		resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane tableScrollPane = new JScrollPane(resultsTable);

		add(tableScrollPane, BorderLayout.CENTER);
	}

	private void init() {
		List<String> headers = new ArrayList<String>();
		for (Value v : AgreementEvent.Value.values()) {
			headers.add(v.getName());
		}
		model = new SimpleTableModel(headers);
	}

	/*************** implements MultipartyNegotiationEventListener *********************/

	@Override
	public void handleEvent(NegotiationEvent e) {
		if (e instanceof LogMessageEvent) {
			System.out.println(((LogMessageEvent) e).getMessage());
		} else if (e instanceof AgreementEvent) {
			model.addRow(convert(((AgreementEvent) e).getValues()));
		} else if (e instanceof TournamentEndedEvent) {
			progress.finish();
		} else if (e instanceof SessionStartedEvent) {
			SessionStartedEvent e1 = (SessionStartedEvent) e;
			progress.update(e1.getCurrentSession(), e1.getTotalSessions());
		} else if (e instanceof SessionFailedEvent) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put(AgreementEvent.Value.EXCEPTION.getName(),
					((SessionFailedEvent) e).toString());
			model.addRow(row);
		}

	}

	/**
	 * Convert a <Value,String> map to a <String,Object> map where the string is
	 * the human readable string of that value and the Object is just the
	 * string.
	 * 
	 * @param values
	 * @return <Value,String> map
	 */
	private Map<String, Object> convert(Map<Value, String> values) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Value v : values.keySet()) {
			map.put(v.getName(), values.get(v));
		}
		return map;
	}
}

/**
 * progress panel, shows progress bar and text n/N
 *
 */
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