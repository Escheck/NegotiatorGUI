package negotiator.logging;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartyNegotiationSessionEvent;
import negotiator.events.NegotiationEvent;

/**
 * Creates a file logger which wil log the inputted messages to a file
 */
public class FileLogger implements MultipartyNegotiationEventListener,
		Closeable {
	// The internal print stream used for file writing
	PrintStream ps;

	public FileLogger(String fileName) throws FileNotFoundException {
		ps = new PrintStream(fileName);
	}

	@Override
	public void handleOfferActionEvent(MultipartyNegotiationOfferEvent evt) {
		// not used
	}

	@Override
	public void handleLogMessageEvent(LogMessageEvent evt) {
		ps.println(evt.getMessage());
	}

	@Override
	public void handleMultipartyNegotiationEvent(
			MultipartyNegotiationSessionEvent evt) {
		// not used
	}

	@Override
	public void close() throws IOException {
		ps.close();
	}

	@Override
	public void handleEvent(NegotiationEvent e) {
		if (e instanceof LogMessageEvent) {
			ps.println(((LogMessageEvent) e).getMessage());
		}

	}
}
