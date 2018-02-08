package genius.cli;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import listener.DefaultListenable;
import listener.Listenable;
import listener.Listener;
import negotiator.events.NegotiationEvent;
import negotiator.gui.negosession.MultiPartyDataModel;
import negotiator.gui.progress.MultipartyNegoEventLogger;

/**
 * Runs a single or multiple negotiations from an xml file. Can be run in sync
 * by calling .run() or out of sync by calling .start() An example xml file can
 * be found at xml-runner/example.xml
 */
public class XmlRunner extends Thread implements Listenable<NegotiationEvent> {

	// holds the xml file as an object
	private XmlObject xml;

	// listens to the negotiations and reports progress to System.out in a
	// seperate thread
	private ProgressReporter progress;

	private Listenable<NegotiationEvent> listeners = new DefaultListenable<NegotiationEvent>();

	/**
	 * Initializes a new instance of the XmlRunner. After initialization, you
	 * can add listeners extra listeners. The runner comes with a built-in log
	 * listener and progress listener, which can be removed if required. To
	 * start the negotiation, run the .run() method to run synchronized or
	 * .start() method for async running.
	 *
	 * @param inputFile
	 *            The file to read configuration from
	 * @param outputFile
	 *            The file to write results to
	 */
	public XmlRunner(String inputFile, String outputFile) {
		xml = load(inputFile);
		progress = new ProgressReporter(xml.getNumberOfRuns());
		final int numAgents = xml.getMaxNumAgents();
		final Listener<NegotiationEvent> logger = getLogger(outputFile, numAgents);

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
		// FIXME what's this??? Just start it, or not?
		// if progress is a listener, start it
		// if (listeners.contains(progress))
		// progress.start();

		// run each configuration in sequence
		for (RunConfiguration runConfiguration : xml) {
			try {
				NegotiationEvent event = runConfiguration.run();
				listeners.notifyChange(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// if progress was started and is still running, interrupt it
		if (progress.isAlive())
			progress.interrupt();
	}

	/**
	 * Adds a new listener to the list
	 * 
	 * @param listener
	 *            The listener to add
	 */
	@Override
	public void addListener(Listener<NegotiationEvent> listener) {
		listeners.addListener(listener);
	}

	/**
	 * Remove a listener from the list
	 * 
	 * @param listener
	 *            listener to remove
	 */
	@Override
	public void removeListener(Listener<NegotiationEvent> listener) {
		listeners.removeListener(listener);
	}

	// /**
	// * Show the current listeners
	// *
	// * @return An immutable list of current listeners
	// */
	// public List<MultipartyNegotiationEventListener> showListeners() {
	// return Collections.unmodifiableList(listeners);
	//
	// }

	/**
	 * Must implement, but we access the private implementation directly.
	 * External use should be prohibited.
	 */
	public void notifyChange(NegotiationEvent e) {
		throw new IllegalStateException();
	}

	/**
	 * Try loading the xml file. Will crash if not able to read the file
	 * 
	 * @param filename
	 *            relative or absolute path to file
	 * @return An XmlObject representing the file
	 */
	private static XmlObject load(String filename) {
		try {
			JAXBContext jc = JAXBContext.newInstance(XmlObject.class);

			Unmarshaller unmarshaller = jc.createUnmarshaller();
			File xml = new File(filename);
			return (XmlObject) unmarshaller.unmarshal(xml);

		} catch (JAXBException e) {
			System.err.println("Error while reading xml file");
			System.err.println(e.getMessage());
			System.err.println("---");
			e.printStackTrace();
			System.exit(1);
			return null;
		}
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
	private static Listener<NegotiationEvent> getLogger(String name, int numAgents) {
		try {
			final MultiPartyDataModel model = new MultiPartyDataModel(numAgents);
			final MultipartyNegoEventLogger logger = new MultipartyNegoEventLogger(name, numAgents, model);
			model.addTableModelListener(logger);
			return model;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
}
