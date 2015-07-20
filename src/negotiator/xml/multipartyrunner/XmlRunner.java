package negotiator.xml.multipartyrunner;

import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.NegotiationEvent;
import negotiator.gui.negosession.MultiPartyDataModel;
import negotiator.gui.progress.MultipartyNegoEventLogger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Runs a single or multiple negotiations from an xml file.
 * Can be run in sync by calling .run() or out of sync by calling .start()
 * An example xml file can be found at xml-runner/example.xml
 */
public class XmlRunner extends Thread {

    // holds the xml file as an object
    private XmlObject xml;

    // listens to the negotiations and reports progress to System.out in a seperate thread
    private ProgressReporter progress;

    // list of subscribers to the negotiation events
    private List<MultipartyNegotiationEventListener> listeners;

    /**
     * Initializes a new instance of the XmlRunner.
     * After initialization, you can add listeners extra listeners.
     * The runner comes with a built-in log listener and progress listener, which can be removed if required.
     * To start the negotiation, run the .run() method to run synchronized or .start() method for async running.
     *
     * @param inputFile The file to read configuration from
     * @param outputFile The file to write results to
     */
    public XmlRunner(String inputFile, String outputFile) {

        // init objects
        this.listeners = new ArrayList<MultipartyNegotiationEventListener>();
        xml = load(inputFile);
        progress = new ProgressReporter(xml.getNumberOfRuns());
        final int numAgents = xml.getMaxNumAgents();
        final MultipartyNegotiationEventListener logger = getLogger(outputFile, numAgents);

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
        if (listeners.contains(progress)) progress.start();

        // run each configuration in sequence
        for (RunConfiguration runConfiguration : xml) {
            try {
                NegotiationEvent event = runConfiguration.run();
                reportToListeners(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // if progress was started and is still running, interrupt it
        if (progress.isAlive()) progress.interrupt();
    }

    /**
     * Report some progress to the listeners
     * @param negotiationEvent The event that took place and should be reported to the listeners.
     */
    private void reportToListeners(NegotiationEvent negotiationEvent) {
        for (MultipartyNegotiationEventListener listener : listeners) {
            listener.handleEvent(negotiationEvent);
        }
    }

    /**
     * Adds a new listener to the list
     * @param listener The listener to add
     */
    public void addListener(MultipartyNegotiationEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener from the list
     * @param listener listener to remove
     */
    public void removeListener(MultipartyNegotiationEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Show the current listeners
     * @return An immutable list of current listeners
     */
    public List<MultipartyNegotiationEventListener> showListeners() {
        return Collections.unmodifiableList(listeners);

    }

    /**
     * Try loading the xml file. Will crash if not able to read the file
     * @param filename relative or absolute path to file
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
     * @param name Relative or absolute path to the file
     * @param numAgents Maximum number of agents that will appear in the log file
     * @return The logger event listener
     */
    private static MultipartyNegotiationEventListener getLogger(String name, int numAgents) {
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
