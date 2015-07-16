package negotiator.gui.progress;

import java.util.List;

import negotiator.events.AgreementEvent;

/**
 * The keys that can be used in logging, tables etc. This is also providing the
 * basis for the column names. the keys UTILS, FILES and AGENTS contain lists.
 * The function {@link AgreementEvent#getFlatMap()} and
 * {@link AgreementEvent#getValues()} will convert these to strings and extend
 * these keys with the agent number, and we then have eg 3 columns "Utility 1",
 * "Utility 2" and "Utility 3" in the table. In the map, the UTILS field will be
 * an {@link List} with 3 values in that case. see also
 * {@link AgreementEvent#getKeys()}.
 * 
 * @author W.Pasman
 *
 */
public enum DataKey {
	RUNTIME("Run time (s)"), ROUND("Round"), EXCEPTION("Exception"), MAX_ROUNDS(
			"Max.rounds"), MAX_TIME("Max.time"), IS_AGREEMENT("Agreement"), IS_DISCOUNT(
			"Discounted"), NUM_AGREE("#agreeing"), MINUTIL("min.util."), MAXUTIL(
			"max.util."), DIST_PARETO("Dist. to Pareto"), DIST_NASH(
			"Dist. to Nash"), DIST_SOCIAL_WELFARE("Dist. to Social Welfare"), AGENTS(
			"Names"), UTILS("Utility"), FILES("Profiles");

	String name;

	DataKey(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}
};
