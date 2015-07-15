package negotiator.events;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public String toStringOld() {
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

	public String toString() {
		String s = "";
		Map<Value, String> values = getValues();
		for (Value v : Value.values()) {
			s += ";";
			if (values.containsKey(v)) {
				s += values.get(v);
			}
		}
		return s.substring(1); // remove the extra first delimiter
	}

	/**
	 * Column values.
	 * 
	 * @author W.Pasman
	 *
	 */
	public enum Value {
		RUNTIME("Run time"), ROUND("Round"), EXCEPTION("Exception"), MAX_ROUNDS(
				"Max.rounds"), MAX_TIME("Max.time"), IS_AGREEMENT("Agree"), IS_DISCOUNT(
				"Discounted"), NUM_AGREE("#agreeing"), MINUTIL("min.util."), MAXUTIL(
				"max.util."), DIST_PARETO("Dist. to Pareto"), DIST_NASH(
				"Dist. to Nash"), DIST_SOCIAL_WELFARE("Dist. to Social Welfare"), AGENTS(
				"Agents"), UTILS("Utilities"), FILES("Agent files");

		String name;

		Value(String n) {
			name = n;
		}

		public String getName() {
			return name;
		}
	};

	/**
	 * Convert the agreement into a hashmap of < {@link Value}, {@link String} >
	 * pairs.
	 * 
	 * @return hashmap of agreement evaluations.
	 */
	public Map<Value, String> getValues() {
		Map<Value, String> values = new HashMap<AgreementEvent.Value, String>();

		try {
			Bid agreement = protocol.getCurrentAgreement(session, parties);
			values.put(Value.RUNTIME, format("%.3f", runTime));
			values.put(Value.ROUND, "" + (session.getRoundNumber() + 1));

			// round / time
			if (session.getDeadlines().isRounds()) {
				values.put(Value.MAX_ROUNDS, ""
						+ session.getDeadlines().getTotalRounds());
			} else {
				values.put(Value.MAX_TIME, ""
						+ session.getDeadlines().getTotalTime());
			}

			// discounted and agreement
			boolean isDiscounted = false;
			for (NegotiationParty party : parties)
				isDiscounted |= party.getUtilitySpace().isDiscounted();
			values.put(Value.IS_AGREEMENT, agreement == null ? "No" : "Yes");
			values.put(Value.IS_DISCOUNT, isDiscounted ? "Yes" : "No");

			// number of agreeing parties
			List<NegotiationParty> agents = MediatorProtocol
					.getNonMediators(parties);
			values.put(Value.NUM_AGREE,
					"" + protocol.getNumberOfAgreeingParties(session, agents));

			// min and max utility
			List<Double> utils = CsvLogger.getUtils(parties, agreement);
			values.put(Value.MINUTIL, format("%.5f", Collections.min(utils)));
			values.put(Value.MAXUTIL, format("%.5f", Collections.max(utils)));

			// analysis (distances, social welfare, etc)
			MultilateralAnalysis analysis = new MultilateralAnalysis(session,
					parties, protocol);
			values.put(Value.DIST_PARETO,
					format("%.5f", analysis.getDistanceToPareto()));
			values.put(Value.DIST_NASH,
					format("%.5f", analysis.getDistanceToNash()));
			values.put(Value.DIST_SOCIAL_WELFARE,
					format("%.5f", analysis.getSocialWelfare()));

			// enumerate agents names, utils, protocols
			values.put(Value.AGENTS, agents.toString());
			String utilstring = "";
			for (double util : utils)
				utilstring += format("%.5f", util);
			values.put(Value.UTILS, utilstring);

			String files = "";
			for (NegotiationParty agent : agents)
				files += CsvLogger.stripPath(agent.getUtilitySpace()
						.getFileName());
			values.put(Value.FILES, files);

		} catch (Exception e) {
			values.put(Value.EXCEPTION, e.toString());
		}
		return values;

	}
}
