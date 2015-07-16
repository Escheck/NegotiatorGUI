package negotiator.events;

import static java.lang.String.format;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import joptsimple.internal.Strings;
import negotiator.Bid;
import negotiator.analysis.MultilateralAnalysis;
import negotiator.gui.progress.SimpleTableModel;
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
 * each {@link Key}. Some Keys may have a {@link List} instead of a single
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

	/**
	 * The values in the hash map. This is also providing the basis for the
	 * column names. the keys UTILS, FILES and AGENTS contain lists. The
	 * function {@link AgreementEvent#getFlatMap()} and
	 * {@link AgreementEvent#getValues()} will convert these to strings and
	 * extend these keys with the agent number, and we then have eg 3 columns
	 * "Utility 1", "Utility 2" and "Utility 3" in the table. In the map, the
	 * UTILS field will be an {@link List} with 3 values in that case. see also
	 * {@link AgreementEvent#getKeys()}.
	 * 
	 * @author W.Pasman
	 *
	 */
	public enum Key {
		RUNTIME("Run time (s)"), ROUND("Round"), EXCEPTION("Exception"), MAX_ROUNDS(
				"Max.rounds"), MAX_TIME("Max.time"), IS_AGREEMENT("Agreement"), IS_DISCOUNT(
				"Discounted"), NUM_AGREE("#agreeing"), MINUTIL("min.util."), MAXUTIL(
				"max.util."), DIST_PARETO("Dist. to Pareto"), DIST_NASH(
				"Dist. to Nash"), DIST_SOCIAL_WELFARE("Dist. to Social Welfare"), AGENTS(
				"Names"), UTILS("Utility"), FILES("Profiles");

		String name;

		Key(String n) {
			name = n;
		}

		public String getName() {
			return name;
		}
	};

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
	 * As {@link #getValues()} but returns a flat map with string,string pairs.
	 * The first string is the key, the second the value. All lists in the map
	 * (see {@link #getValues()}) are converted to a separate element in the
	 * list.
	 * 
	 */

	public Map<String, String> getFlatMap() {
		Map<Key, Object> values = getValues();
		Map<String, String> strings = new HashMap<String, String>();
		for (Key v : values.keySet()) {
			Object data = values.get(v);
			if (data instanceof List) {
				int n = 1;
				for (Object elt : (List) data) {
					strings.put(v.getName() + " " + n++, elt.toString());
				}
			} else {
				strings.put(v.getName(), data.toString());
			}
		}
		return strings;
	}

	/**
	 * Convert the values in the given {@link Map} to a fixed-order list. The
	 * order is as in {@link #getKeys()}. keys that are in the values map but
	 * not a know key are ignored. The list does not have to be complete.
	 * 
	 * For a single string you can just use {@link Strings#join(List, String)}
	 * eg <code>Strings.join(strings, ";")</code>
	 * 
	 * @param values
	 * @return
	 */
	public List<String> getValuesList(Map<String, String> values) {
		List<String> string = new ArrayList<String>();
		for (String k : getKeys(parties.size())) {
			if (values.containsKey(k)) {
				string.add(values.get(k).toString());
			} else {
				string.add("");
			}
		}
		return string;
	}

	/**
	 * returns a list of keys that can appear in the {@link HashMap} that is
	 * returned from {@link #getValues()}. Note, the {@link Key}s in the map are
	 * expanded, to have "1", "2", "3" etc behind their base key name. This can
	 * then be used as header above tables in log files and on screen.
	 * 
	 * This function is static because it needs to be called before the first
	 * negotiation happens.
	 * 
	 * @param numParties
	 *            the number of parties (agents) involved in this negotiation.
	 * @return keys for use in table headers.
	 */
	public static List<String> getKeys(int numParties) {
		List<String> keys = new ArrayList<String>();
		for (Key v : Key.values()) {
			if (v == Key.AGENTS || v == Key.FILES || v == Key.UTILS) {
				for (int n = 1; n <= numParties; n++) {
					keys.add(v.getName() + " " + n);
				}
			} else {
				keys.add(v.getName());
			}

		}
		return keys;
	}

	/**
	 * Convert the agreement into a hashmap of < {@link Key}, {@link Object} >
	 * pairs. Object will usually be a {@link String}, {@link Number} or
	 * {@link List}.
	 * 
	 * @return hashmap of agreement evaluations.
	 */
	public Map<Key, Object> getValues() {
		Map<Key, Object> values = new HashMap<AgreementEvent.Key, Object>();

		try {
			Bid agreement = protocol.getCurrentAgreement(session, parties);
			values.put(Key.RUNTIME, format("%.3f", runTime));
			values.put(Key.ROUND, "" + (session.getRoundNumber() + 1));

			// round / time
			if (session.getDeadlines().isRounds()) {
				values.put(Key.MAX_ROUNDS, ""
						+ session.getDeadlines().getTotalRounds());
			} else {
				values.put(Key.MAX_TIME, ""
						+ session.getDeadlines().getTotalTime());
			}

			// discounted and agreement
			boolean isDiscounted = false;
			for (NegotiationParty party : parties)
				isDiscounted |= party.getUtilitySpace().isDiscounted();
			values.put(Key.IS_AGREEMENT, agreement == null ? "No" : "Yes");
			values.put(Key.IS_DISCOUNT, isDiscounted ? "Yes" : "No");

			// number of agreeing parties
			List<NegotiationParty> agents = MediatorProtocol
					.getNonMediators(parties);
			values.put(Key.NUM_AGREE,
					"" + protocol.getNumberOfAgreeingParties(session, agents));

			// min and max utility
			List<Double> utils = CsvLogger.getUtils(parties, agreement);
			values.put(Key.MINUTIL, format("%.5f", Collections.min(utils)));
			values.put(Key.MAXUTIL, format("%.5f", Collections.max(utils)));

			// analysis (distances, social welfare, etc)
			MultilateralAnalysis analysis = new MultilateralAnalysis(session,
					parties, protocol);
			values.put(Key.DIST_PARETO,
					format("%.5f", analysis.getDistanceToPareto()));
			values.put(Key.DIST_NASH,
					format("%.5f", analysis.getDistanceToNash()));
			values.put(Key.DIST_SOCIAL_WELFARE,
					format("%.5f", analysis.getSocialWelfare()));

			// enumerate agents names, utils, protocols
			List<String> agts = new ArrayList<String>();

			String agentstr = "";
			for (NegotiationParty a : agents) {
				agts.add(a.getClass().getSimpleName());
			}
			values.put(Key.AGENTS, agts);

			values.put(Key.UTILS, utils);// format("%.5f ", util);

			List<String> files = new ArrayList<String>();
			for (NegotiationParty agent : agents) {
				File utilfile = new File(agent.getUtilitySpace().getFileName());
				files.add(utilfile.getName());
			}
			values.put(Key.FILES, files);

		} catch (Exception e) {
			values.put(Key.EXCEPTION, e.toString());
		}
		return values;

	}
}
