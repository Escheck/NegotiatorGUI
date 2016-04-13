package negotiator.logging;

import static java.lang.String.format;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import negotiator.Bid;
import negotiator.analysis.MultilateralAnalysis;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.protocol.MediatorProtocol;
import negotiator.protocol.MultilateralProtocol;
import negotiator.session.Session;
import negotiator.utility.AbstractUtilitySpace;

/**
 * Logger interface that writes the log to a comma separated value file (.csv
 * file) File is created upon logger interface creation and logger class should
 * be released (i.e. log.close() to release internal file handle when done with
 * logger). *
 *
 * @author David Festen
 */
public class CsvLogger implements Closeable {
	// use "," for english and ";" for dutch excel readable output
	public static final String DELIMITER = ";";

	// The internal print stream used for file writing
	PrintStream ps;

	// The buffer of objects to write (we only do a write call per complete
	// line)
	List<Object> buffer;

	// Flag to indicate if header is already printed (we will print the header
	// only once)
	boolean printedHeader;

	/**
	 * Initializes a new instance of the CsvLogger class. Initializing this
	 * class opens a print stream, the user of the instance should take care to
	 * close (i.e. logger.close()) this instance when done.
	 *
	 * @param fileName
	 *            The name of the file to log to (including the .csv extension)
	 * @throws FileNotFoundException
	 *             Thrown by the PrintStream if the location is not writable.
	 */
	public CsvLogger(String fileName) throws IOException {
		File file = new File(fileName);
		file.getParentFile().mkdirs();
		ps = new PrintStream(file);
		buffer = new ArrayList<Object>();

		// Used to tell excel to handle file correctly.
		logLine("sep=" + DELIMITER);
	}

	/**
	 * Helper method. Joins all the elements in the collection using the given
	 * delimiter. For each element the to string function is used to generate
	 * the string.
	 *
	 * @param s
	 *            Collection of objects to create string of
	 * @param delimiter
	 *            The delimiter used between object
	 * @return The string delimited with the given delimiter
	 */
	public static String join(Collection<?> s, String delimiter) {
		StringBuilder builder = new StringBuilder();
		Iterator<?> iterator = s.iterator();
		while (iterator.hasNext()) {
			builder.append(iterator.next());
			if (!iterator.hasNext()) {
				break;
			}
			builder.append(delimiter);
		}
		return builder.toString();
	}

	/**
	 * Log a given object. This actually just adds it to the buffer, to print to
	 * file, call logLine() afterwards.
	 *
	 * @param value
	 *            The object to log
	 */
	public void log(Object value) {
		buffer.add(value);
	}

	/**
	 * Logs a complete line to the file.
	 *
	 * @param values
	 *            zero or more objects to log, using ; delimiter
	 */
	public void logLine(Object... values) {
		buffer.addAll(Arrays.asList(values));
		String line = join(buffer, DELIMITER);
		ps.println(line);
		buffer.clear();
	}

	/**
	 * Generate default header
	 *
	 * @param numAgentsPerSession
	 *            The number of non-mediator agents in this session, used to
	 *            print correct amount of headers for the agent.
	 * @return The header string
	 * @deprecated see {@link AgreementEvent#getKeys(int).
	 */
	public static String getDefaultHeader(int numAgentsPerSession) {
		// TODO get non-mediators.
		// List<NegotiationParty> agentList =
		// MediatorProtocol.getNonMediators(numAgentsPerSession);

		List<String> values = new ArrayList<String>();
		values.add("Session");
		values.add("Time (s)");
		values.add("Rounds");
		values.add("DeadlineType");
		values.add("Deadline");
		values.add("Agreement");
		values.add("Discounted");
		values.add("Approval");
		values.add("Min. utility");
		values.add("Max. utility");
		values.add("Distance to pareto");
		values.add("Distance to Nash");
		values.add("Social welfare");

		for (int i = 1; i <= numAgentsPerSession; i++)
			values.add("Agent name " + i);

		for (int i = 1; i <= numAgentsPerSession; i++)
			values.add("Agent utility " + i);

		for (int i = 1; i <= numAgentsPerSession; i++)
			values.add("Agent profile " + i);

		return join(values, DELIMITER);
	}

	/**
	 * Log default session information. Seems applicable only when an agreement
	 * was reached.
	 */
	public static String getDefaultSessionLog(Session session,
			MultilateralProtocol protocol,
			List<NegotiationPartyInternal> parties, double runTime)
			throws Exception {
		List<String> values = new ArrayList<String>();

		try {
			Bid agreement = protocol.getCurrentAgreement(session, parties);
			values.add(format("%.3f", runTime));
			values.add("" + (session.getRoundNumber() + 1));

			// round / time
			values.add(session.getDeadlines().toString());

			// discounted and agreement
			boolean isDiscounted = false;
			for (NegotiationPartyInternal party : parties)
				isDiscounted |= (party.getUtilitySpace().discount(1, 1) != 1);
			values.add(agreement == null ? "No" : "Yes");
			values.add(isDiscounted ? "Yes" : "No");

			// number of agreeing parties
			List<NegotiationPartyInternal> agents = MediatorProtocol
					.getNonMediators(parties);
			values.add(""
					+ protocol.getNumberOfAgreeingParties(session, agents));

			// discounted min and max utility
			List<Double> utils = getUtils(parties, agreement, true);
			values.add(format("%.5f", Collections.min(utils)));
			values.add(format("%.5f", Collections.max(utils)));

			// analysis (distances, social welfare, etc)
			MultilateralAnalysis analysis = new MultilateralAnalysis(session,
					parties, protocol);
			values.add(format("%.5f", analysis.getDistanceToPareto()));
			values.add(format("%.5f", analysis.getDistanceToNash()));
			values.add(format("%.5f", analysis.getSocialWelfare()));

			// enumerate agents names, utils, protocols
			for (NegotiationPartyInternal agent : agents)
				values.add("" + agent);
			for (double util : utils)
				values.add(format("%.5f", util));
			for (NegotiationPartyInternal agent : agents) {
				String name = "-";
				if (agent.getUtilitySpace() instanceof AbstractUtilitySpace) {
					name = stripPath(((AbstractUtilitySpace) agent
							.getUtilitySpace()).getFileName());
				}
				values.add(name);

			}

		} catch (Exception e) {
			values.add("EXCEPTION OCCURRED");
		}

		return join(values, DELIMITER);
	}

	/**
	 * @param parties
	 *            the parties in the nego
	 * @param agreement
	 *            the reached agreement, or null if there was no agreement.
	 * @param discount
	 *            true iff you want the discounted utilities.
	 * @return list with (possibly discounted) utilities/res value of each of
	 *         the parties. Res value is only used if agreement=null.
	 * 
	 */
	public static List<Double> getUtils(List<NegotiationPartyInternal> parties,
			Bid agreement, boolean discount) {
		List<Double> utils = new ArrayList<Double>();
		double time = 0;

		if (parties.size() > 0) {
			time = parties.get(0).getTimeLine().getTime();
		}

		for (NegotiationPartyInternal agent : parties) {
			double util = getUtil(agent, agreement);
			if (discount) {
				util = agent.getUtilitySpace().discount(util, time);
			}
			utils.add(util);
		}
		return utils;
	}

	/**
	 * 
	 * @param agent
	 *            the agent for which to compute the utility
	 * @param agreement
	 *            the agreement, or null if there is no agreement in which case
	 *            the reservation value is used
	 * @return the undiscounted utility/reservation value of the given
	 *         agreement.
	 */
	private static double getUtil(NegotiationPartyInternal agent, Bid agreement) {
		if (agreement == null) {
			return agent.getUtilitySpace().getReservationValue();
		}
		return agent.getUtility(agreement);
	}

	/**
	 * Makes a string for the log file.
	 * 
	 * @param session
	 * @param protocol
	 * @param parties
	 * @param runTime
	 * @return
	 * @throws Exception
	 */
	public static String logSingleSession(Session session,
			MultilateralProtocol protocol,
			List<NegotiationPartyInternal> parties, double runTime)
			throws Exception {

		List<String> values = new ArrayList<String>();
		Bid agreement = protocol.getCurrentAgreement(session, parties);
		List<Double> utils = getUtils(parties, agreement, true);

		double minUtil = Collections.min(utils);
		double maxUtil = Collections.max(utils);

		MultilateralAnalysis analysis = new MultilateralAnalysis(session,
				parties, protocol);

		// check if at least one of the util spaces is discounted.
		boolean isDiscounted = false;
		for (NegotiationPartyInternal party : parties) {
			if (party.getUtilitySpace().discount(1, 1) != 1) {
				isDiscounted = true;
			}
		}

		values.add("Time (s):\t\t");
		values.add("" + runTime + "\n");

		values.add("Rounds:\t\t");
		values.add("" + (session.getRoundNumber()) + "\n");

		values.add("Agreement?:\t\t");
		values.add(agreement == null ? "No\n" : "Yes\n");

		values.add("Discounted?:\t\t");
		values.add(isDiscounted ? "Yes\n" : "No\n");

		values.add("" + protocol.getNumberOfAgreeingParties(session, parties)
				+ "\n");

		values.add("Min. utility:\t\t");
		values.add(String.format("%.5f\n", minUtil));

		values.add("Max. utility:\t\t");
		values.add(String.format("%.5f\n", maxUtil));

		values.add("Distance to pareto:\t");
		values.add(String.format("%.5f\n", analysis.getDistanceToPareto()));

		values.add("Distance to Nash:\t");
		values.add(String.format("%.5f\n", analysis.getDistanceToNash()));

		values.add("Social welfare:\t\t");
		values.add(String.format("%.5f\n", analysis.getSocialWelfare()));

		for (int i = 0; i < parties.size(); i++) {
			values.add(String.format("Agent utility:\t\t%.5f (%s)\n",
					utils.get(i), parties.get(i).getPartyId()));
		}

		return join(values, "");
	}

	public static String stripPath(String filenameWithPath) {
		String[] split = filenameWithPath.split("/");
		if (split.length < 2)
			return filenameWithPath;
		else
			return split[split.length - 2] + "/" + split[split.length - 1];
	}

	/**
	 * Closes this stream and releases any system resources associated with it.
	 * If the stream is already closed then invoking this method has no effect.
	 *
	 * @throws java.io.IOException
	 *             if an I/O error occurs
	 */
	@Override
	public void close() throws IOException {
		ps.close();
	}
}
