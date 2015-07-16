package negotiator.gui.progress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import negotiator.events.AgreementEvent;

/**
 * These keys are used as indicator for the returned data , such as in
 * {@link HashMap}s. See e.g. {@link AgreementEvent#getValues()}.
 * 
 * The keys also have a human-readable text version. This can be used as basis
 * for the column text strings for data display and logging.
 * 
 * The keys UTILS, FILES and AGENTS contain lists. The function
 * {@link AgreementEvent#getFlatMap()} and {@link AgreementEvent#getValues()}
 * will convert these to strings and extend these keys with the agent number,
 * and we then have eg 3 columns "Utility 1", "Utility 2" and "Utility 3" in the
 * table. In the map, the UTILS field will be an {@link List} with 3 values in
 * that case. see also {@link AgreementEvent#getKeys()}.
 * 
 * @author W.Pasman
 *
 */
public enum DataKey implements Comparable<DataKey> {
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

	/**
	 * 
	 * @return human-readable short description of this column
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return n human-readable short descriptions of this column. The
	 *         descriptions are just the name, but with an added number 1..n
	 */
	public List<String> getNames(int num) {
		List<String> names = new ArrayList<String>();
		for (int n = 1; n <= num; n++) {
			names.add(name + " " + n);
		}
		return names;
	}

};
