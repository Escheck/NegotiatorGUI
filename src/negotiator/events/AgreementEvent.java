package negotiator.events;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import negotiator.Bid;
import negotiator.analysis.MultilateralAnalysis;
import negotiator.logging.CsvLogger;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.MediatorProtocol;
import negotiator.protocol.MultilateralProtocol;
import negotiator.session.Session;

/**
 * Indicates that an agreement was reached.
 * 
 * @author W.Pasman 15jul15
 *
 */
public class AgreementEvent extends NegotiationEvent {

	Session session;
	MultilateralProtocol protocol;
	List<NegotiationParty> parties;
	double runTime;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6630669386231073769L;

	public AgreementEvent(Object source, Session s, MultilateralProtocol pr,
			List<NegotiationParty> pa, double time) {
		super(source);
		session = s;
		protocol = pr;
		parties = pa;
		runTime = time;
	}

	/**
	 * just copy of
	 * {@link CsvLogger#getDefaultSessionLog(Session, MultilateralProtocol, List, double)}
	 * 
	 */
	public String toString() {
		List<String> values = new ArrayList<String>();

		try {
			Bid agreement = protocol.getCurrentAgreement(session, parties);
			values.add(format("%.3f", runTime));
			values.add("" + (session.getRoundNumber() + 1));

			// round / time
			if (session.getDeadlines().isRounds()) {
				values.add("Round");
				values.add("" + session.getDeadlines().getTotalRounds());
			} else {
				values.add("Time");
				values.add("" + session.getDeadlines().getTotalTime());
			}

			// discounted and agreement
			boolean isDiscounted = false;
			for (NegotiationParty party : parties)
				isDiscounted |= party.getUtilitySpace().isDiscounted();
			values.add(agreement == null ? "No" : "Yes");
			values.add(isDiscounted ? "Yes" : "No");

			// number of agreeing parties
			List<NegotiationParty> agents = MediatorProtocol
					.getNonMediators(parties);
			values.add(""
					+ protocol.getNumberOfAgreeingParties(session, agents));

			// min and max utility
			List<Double> utils = CsvLogger.getUtils(parties, agreement);
			values.add(format("%.5f", Collections.min(utils)));
			values.add(format("%.5f", Collections.max(utils)));

			// analysis (distances, social welfare, etc)
			MultilateralAnalysis analysis = new MultilateralAnalysis(session,
					parties, protocol);
			values.add(format("%.5f", analysis.getDistanceToPareto()));
			values.add(format("%.5f", analysis.getDistanceToNash()));
			values.add(format("%.5f", analysis.getSocialWelfare()));

			// enumerate agents names, utils, protocols
			for (NegotiationParty agent : agents)
				values.add("" + agent);
			for (double util : utils)
				values.add(format("%.5f", util));
			for (NegotiationParty agent : agents)
				values.add(CsvLogger.stripPath(agent.getUtilitySpace()
						.getFileName()));

		} catch (Exception e) {
			values.add("EXCEPTION OCCURRED");
		}

		return CsvLogger.join(values, ";");

	}
}
