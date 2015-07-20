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

	MultiPartyDataModel model;

	public MultipartyNegoEventLogger(String name, int numAgents,
			MultiPartyDataModel m) throws IOException {
		model = m;
		data.logger = new CsvLogger(name);
		logHeader();

	}

	/**
	 * write the header to the log file.
	 */
	private void logHeader() {
		List<String> headers = new ArrayList<String>();
		for (int col = 0; col < model.getColumnCount(); col++) {
			headers.add(model.getColumnName(col));
		}
		data.logger.logLine(Strings.join(headers, ";"));

	}

	/**
	 * Any insert in the model is caught here, to be logged. All values in the
	 * new row are added, converted to string
	 */
	@Override
	public void tableChanged(TableModelEvent evt) {
		if (evt.getType() == TableModelEvent.INSERT) {
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