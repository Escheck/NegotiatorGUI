package negotiator.gui.progress;

import static java.lang.String.format;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import joptsimple.internal.Strings;
import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.AgreementEvent;
import negotiator.events.LogMessageEvent;
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

		data.logger
				.logLine(Strings.join(AgreementEvent.getKeys(numAgents), ";"));
	}

	@Override
	public void handleEvent(NegotiationEvent e) {
		if (e instanceof LogMessageEvent) {
			data.logger.logLine(((LogMessageEvent) e).getMessage());

		}
		if (e instanceof AgreementEvent) {
			AgreementEvent e1 = (AgreementEvent) e;
			data.logger.logLine(Strings.join(e1.getValuesList(e1.getFlatMap()),
					";"));

		}
		// MultipartyNegotiationOfferEvent ignored.
		// MultipartyNegotiationSessionEvent ignored.
	}

}