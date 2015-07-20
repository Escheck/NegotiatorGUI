package negotiator.xml.multipartyrunner;

import misc.Progress;
import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.NegotiationEvent;

import java.io.IOException;

/**
 * Object that listens to MultipartyNegotiationEvents and increases it's internal counter on each event.
 * It will run in a separate thread and report progress to the System.out channel (Console by default).
 */
public class ProgressReporter extends Thread implements MultipartyNegotiationEventListener {

    /** The progress object used to internally keep track of current state */
    private Progress progress;

    /**
     * Initialize a new instance of the ProgressReporter class.
     * @param numNegotiations The total amount of negotiations to do. Progress will increase up to this number.
     */
    public ProgressReporter(int numNegotiations) {
        progress = new Progress(numNegotiations);
    }

    /**
     * Report progress to the System.out channel. This might run indefinitely so caller should take care of an interrupt
     * request when they are done with reporting progress. For timing to be correct, this should be called as late as
     * possible, but just before the actual negotiation runner starts.
     *
     * N.B. This is an implementation of thread main loop.
     * Call by using the .start() method from an instantiated ProgressReporter
     *
     * {@inheritDoc}
     */
    @Override
    public void run() {
        super.run();
        progress.start();
        System.out.println(String.format("Running %d negotiations", progress.getTotal()));
        while (!progress.isDone()) {
            try {
                progress.printProgressToConsole(Progress.ALL);
                Thread.sleep(250);
            } catch (InterruptedException e) {
                // do nothing
            }
        }
    }

    /**
     * Called when done with reporting progress. This will stop the thread.
     * Also this will clean up any progress report that is still on the command line and print the done message.
     * For timing to be correct, this should be called as soon as possible after negotiation is finished.
     *
     * {@inheritDoc}
     */
    @Override
    public void interrupt() {
        super.interrupt();
        try {
            String data = String.format("\rRan %d negotiations in %s\n", progress.getTotal(), progress.getNowPretty(true));
            System.out.print("                                                                                       ");
            System.out.write(data.getBytes());
        } catch (IOException e) {
            // do nothing
        }
    }

    /**
     * Increase progress counter
     *
     * {@inheritDoc}
     */
    @Override
    public void handleEvent(NegotiationEvent e) {
        progress.increment();
    }
}
