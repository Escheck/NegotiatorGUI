package negotiator.gui.progress;

import static java.lang.String.format;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import joptsimple.internal.Strings;
import negotiator.events.AgreementEvent;
import negotiator.gui.negosession.MultiNegoSessionUI;
import negotiator.gui.negosession.MultiPartyDataModel;
import negotiator.logging.CsvLogger;
import negotiator.session.MultipartyNegoEventLoggerData;
import negotiator.session.TournamentManager;

/**
 * Logger for MultiPartyNegotiationEvents. Currently only for hook into the
 * {@link TournamentManager} but may be generalizable and eg used in
 * {@link MultiNegoSessionUI}. The logger simply listens to changes in the
 * tableModel.
 * 
 * 
 * @author W.Pasman 18jun15
 *
 */
public class MultipartyNegoEventLogger implements TableModelListener {

	private MultipartyNegoEventLoggerData data = new MultipartyNegoEventLoggerData();

	public MultipartyNegoEventLogger(String name, int numAgents)
			throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");

		data.logger = new CsvLogger(format("log/tournament-%s-%s.log.csv",
				dateFormat.format(new Date()), name));

		data.logger
				.logLine(Strings.join(AgreementEvent.getKeys(numAgents), ";"));
	}

	/**
	 * Any insert in the model is caught here, to be logged. All values in the
	 * new row are added, converted to string
	 */
	@Override
	public void tableChanged(TableModelEvent evt) {
		MultiPartyDataModel model = (MultiPartyDataModel) evt.getSource();
		if (evt.getType() == TableModelEvent.INSERT) {
			System.out.println("table changed:" + evt);
			int row = evt.getFirstRow();

			List<String> elements = new ArrayList<String>();
			for (int col = 0; col < model.getColumnCount(); col++) {
				Object value = model.getValueAt(row, col);
				elements.add(value == null ? "" : value.toString());
			}

			data.logger.logLine(Strings.join(elements, ";"));
		}
	}

}