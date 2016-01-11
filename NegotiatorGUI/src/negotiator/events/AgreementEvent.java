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
import negotiator.gui.progress.DataKeyTableModel;
import negotiator.logging.CsvLogger;
import negotiator.parties.NegotiationPartyInternal;
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
	List<NegotiationPartyInternal> parties;
	double runTime;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6630669386231073769L;

	public AgreementEvent(Object source, Session s, MultilateralProtocol pr,
			List<NegotiationPartyInternal> pa, double time) {
		super(source);
		session = s;
		protocol = pr;
		parties = pa;
		runTime = time;
	}

	/**
	 * Convert the agreement into a hashmap of < {@link DataKey}, {@link Object}
	 * > pairs. Object will usually be a {@link String}, {@link Number} or
	 * {@link List}. This data can be inserted directly into a
	 * {@link DataKeyTableModel}.
	 * 
	 * @return {@link Map} of agreement evaluations.
	 */
	public Map<DataKey, Object> getValues() {
		Map<DataKey, Object> values = new HashMap<DataKey, Object>();

		try {
			Bid agreement = protocol.getCurrentAgreement(session, parties);
			values.put(DataKey.RUNTIME, format("%.3f", runTime));
			values.put(DataKey.ROUND, "" + (session.getRoundNumber() + 1));

			// deadline
			values.put(DataKey.DEADLINE, session.getDeadlines().valueString());

			// discounted and agreement
			boolean isDiscounted = false;
			for (NegotiationPartyInternal party : parties)
				isDiscounted |= (party.getUtilitySpace().discount(1, 1) != 1);
			values.put(DataKey.IS_AGREEMENT, agreement == null ? "No" : "Yes");
			values.put(DataKey.IS_DISCOUNT, isDiscounted ? "Yes" : "No");

			// number of agreeing parties
			List<NegotiationPartyInternal> agents = MediatorProtocol
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
			for (NegotiationPartyInternal a : agents) {
				agts.add(a.getPartyId().toString());
			}
			values.put(DataKey.AGENTS, agts);

			values.put(DataKey.UTILS, utils);// format("%.5f ", util);

			List<String> files = new ArrayList<String>();
			for (NegotiationPartyInternal agent : agents) {
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
