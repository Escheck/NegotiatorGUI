package negotiator.events;

import static java.lang.String.format;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import negotiator.Bid;
import negotiator.analysis.MultilateralAnalysis;
import negotiator.gui.progress.DataKey;
import negotiator.logging.CsvLogger;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.MediatorProtocol;
import negotiator.protocol.MultilateralProtocol;
import negotiator.session.Session;

/**
 * Indicates that an agreement was reached.
 * 
 * Note. This code grew much more complex than anticipated. There were too many
 * layout constraints for rendering the results and this reflects in this code
 * (so maybe this class should be refactored or cleaned up).
 * 
 * Layout-ing of an AgreementEvent goes in a few steps
 * 
 * (1) {@link #getValues()} delivers a {@link Map} of key-value pairs, one for
 * each {@link DataKey}. Some Keys may have a {@link List} instead of a single
 * value; these are then the values for each of the N agents in the agreement.
 * 
 * (2) The {@link Map} may need conversion, to split the {@link List}s further
 * in separate fields. see {@link #getFlatMap()}.
 * 
 * 
 * (3) the flat {@link Map} can then be moved into a {@link SimpleTableModel} or
 * into a log file.
 * 
 * (3b) Alternatively, The flat {@link Map} can be converted into a list, see
 * {@link #getValuesList(Map)}, and converted to a string, for logging into a
 * plain text file.
 * 
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
	 * Convert the agreement into a hashmap of < {@link DataKey}, {@link Object}
	 * > pairs. Object will usually be a {@link String}, {@link Number} or
	 * {@link List}.
	 * 
	 * @return hashmap of agreement evaluations.
	 */
	public Map<DataKey, Object> getValues() {
		Map<DataKey, Object> values = new HashMap<DataKey, Object>();

		try {
			Bid agreement = protocol.getCurrentAgreement(session, parties);
			values.put(DataKey.RUNTIME, format("%.3f", runTime));
			values.put(DataKey.ROUND, "" + (session.getRoundNumber() + 1));

			// round / time
			if (session.getDeadlines().isRounds()) {
				values.put(DataKey.MAX_ROUNDS, ""
						+ session.getDeadlines().getTotalRounds());
			} else {
				values.put(DataKey.MAX_TIME, ""
						+ session.getDeadlines().getTotalTime());
			}

			// discounted and agreement
			boolean isDiscounted = false;
			for (NegotiationParty party : parties)
				isDiscounted |= party.getUtilitySpace().isDiscounted();
			values.put(DataKey.IS_AGREEMENT, agreement == null ? "No" : "Yes");
			values.put(DataKey.IS_DISCOUNT, isDiscounted ? "Yes" : "No");

			// number of agreeing parties
			List<NegotiationParty> agents = MediatorProtocol
					.getNonMediators(parties);
			values.put(DataKey.NUM_AGREE,
					"" + protocol.getNumberOfAgreeingParties(session, agents));

			// min and max utility
			List<Double> utils = CsvLogger.getUtils(parties, agreement);
			values.put(DataKey.MINUTIL, format("%.5f", Collections.min(utils)));
			values.put(DataKey.MAXUTIL, format("%.5f", Collections.max(utils)));

			// analysis (distances, social welfare, etc)
			MultilateralAnalysis analysis = new MultilateralAnalysis(session,
					parties, protocol);
			values.put(DataKey.DIST_PARETO,
					format("%.5f", analysis.getDistanceToPareto()));
			values.put(DataKey.DIST_NASH,
					format("%.5f", analysis.getDistanceToNash()));
			values.put(DataKey.DIST_SOCIAL_WELFARE,
					format("%.5f", analysis.getSocialWelfare()));

			// enumerate agents names, utils, protocols
			List<String> agts = new ArrayList<String>();

			String agentstr = "";
			for (NegotiationParty a : agents) {
				agts.add(a.getClass().getSimpleName());
			}
			values.put(DataKey.AGENTS, agts);

			values.put(DataKey.UTILS, utils);// format("%.5f ", util);

			List<String> files = new ArrayList<String>();
			for (NegotiationParty agent : agents) {
				File utilfile = new File(agent.getUtilitySpace().getFileName());
				files.add(utilfile.getName());
			}
			values.put(DataKey.FILES, files);

		} catch (Exception e) {
			values.put(DataKey.EXCEPTION, e.toString());
		}
		return values;

	}
}
