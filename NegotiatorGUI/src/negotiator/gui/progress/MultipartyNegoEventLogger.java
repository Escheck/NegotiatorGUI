package negotiator.gui.progress;

import static java.lang.String.format;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartyNegotiationSessionEvent;
import negotiator.events.NegotiationEvent;
import negotiator.gui.negosession.MultiNegoSessionUI;
import negotiator.logging.CsvLogger;
import negotiator.session.MultipartyNegoEventLoggerData;
import negotiator.session.TournamentManager;

/**
 * Logger for MultiPartyNegotiationEvents. Currently only for hook into the
 * {@link TournamentManager} but may be generalizable and eg used in
 * {@link MultiNegoSessionUI}.
 * 
 * @author W.Pasman 18jun15
 *
 */
public class MultipartyNegoEventLogger implements
		MultipartyNegotiationEventListener {

	private MultipartyNegoEventLoggerData data = new MultipartyNegoEventLoggerData();

	public MultipartyNegoEventLogger(String name, int numAgents)
			throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");

		data.logger = new CsvLogger(format("log/tournament-%s-%s.log.csv",
				dateFormat.format(new Date()), name));

		data.logger.logLine(CsvLogger.getDefaultHeader(numAgents));
	}

	@Override
	public void handleOfferActionEvent(MultipartyNegotiationOfferEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleLogMessageEvent(LogMessageEvent evt) {
		data.logger.logLine(evt.getMessage());

	}

	@Override
	public void handleMultipartyNegotiationEvent(
			MultipartyNegotiationSessionEvent evt) {
		// TODO Auto-generated method stub
	}

	@Override
	public void handleEvent(NegotiationEvent e) {
		if (e instanceof LogMessageEvent) {
			data.logger.logLine(((LogMessageEvent) e).getMessage());

		}
	}

}