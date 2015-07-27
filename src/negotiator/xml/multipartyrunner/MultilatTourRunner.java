package negotiator.xml.multipartyrunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import negotiator.MultipartyNegotiationEventListener;
import negotiator.config.MultilateralTournamentConfiguration;
import negotiator.events.NegotiationEvent;
import negotiator.gui.negosession.MultiPartyDataModel;
import negotiator.gui.progress.MultipartyNegoEventLogger;

/**
 * Runs a multilateral tournament. It generates the sessions
 * 
 */
public class MultilatTourRunner extends Thread {

	// listens to the negotiations and reports progress to System.out in a
	// seperate thread
	private ProgressReporter progress;

	// list of subscribers to the negotiation events
	private List<MultipartyNegotiationEventListener> listeners;

	private MultilateralTournamentConfiguration configuration;

	/**
	 * Initializes a new instance of the Runner. After initialization, you can
	 * add listeners extra listeners. The runner comes with a built-in log
	 * listener and progress listener, which can be removed if required. To
	 * start the negotiation, run the .run() method to run synchronized or
	 * .start() method for async running.
	 *
	 * @param config
	 *            The run configuration.
	 * @param outputFile
	 *            The file to write results to
	 */

	public MultilatTourRunner(MultilateralTournamentConfiguration config,
			String outputFile) {
		// init objects
		this.configuration = config;
		this.listeners = new ArrayList<MultipartyNegotiationEventListener>();
		progress = new ProgressReporter(configuration.getNumSessions());
		final int numAgents = configuration.getNumAgentsPerSession();
		final MultipartyNegotiationEventListener logger = getLogger(outputFile,
				numAgents);

		// attach event listeners
		this.addListener(logger);
		this.addListener(progress);

	}

	/**
	 * Run the XmlRunner
	 */
	@Override
	public void run() {
		super.run();
		// if progress is a listener, start it
		if (listeners.contains(progress))
			progress.start();

		// just run it in this thread.
		new MultilatConfigurationRunner(configuration).run();

		// TODO run multiple tournaments using this code
		// run each configuration in sequence
		// for (RunConfiguration runConfiguration : xml) {
		// try {
		// NegotiationEvent event = runConfiguration.run();
		// reportToListeners(event);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// if progress was started and is still running, interrupt it
		if (progress.isAlive())
			progress.interrupt();
	}

	/**
	 * Report some progress to the listeners
	 * 
	 * @param negotiationEvent
	 *            The event that took place and should be reported to the
	 *            listeners.
	 */
	private void reportToListeners(NegotiationEvent negotiationEvent) {
		for (MultipartyNegotiationEventListener listener : listeners) {
			listener.handleEvent(negotiationEvent);
		}
	}

	/**
	 * Adds a new listener to the list
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addListener(MultipartyNegotiationEventListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener from the list
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeListener(MultipartyNegotiationEventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Show the current listeners
	 * 
	 * @return An immutable list of current listeners
	 */
	public List<MultipartyNegotiationEventListener> showListeners() {
		return Collections.unmodifiableList(listeners);

	}

	/**
	 * Gets the file logger for the negotiations
	 * 
	 * @param name
	 *            Relative or absolute path to the file
	 * @param numAgents
	 *            Maximum number of agents that will appear in the log file
	 * @return The logger event listener
	 */
	private static MultipartyNegotiationEventListener getLogger(String name,
			int numAgents) {
		try {
			final MultiPartyDataModel model = new MultiPartyDataModel(numAgents);
			final MultipartyNegoEventLogger logger = new MultipartyNegoEventLogger(
					name, numAgents, model);
			model.addTableModelListener(logger);
			return model;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
}
