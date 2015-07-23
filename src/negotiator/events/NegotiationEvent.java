package negotiator.events;

import java.util.EventObject;

import negotiator.actions.Action;
import negotiator.actions.Offer;

/**
 * An abstract superclass for all events, both meta-events like
 * {@link SessionFailedEvent} and concrete {@link Action}s done by agents like
 * doing an {@link Offer} .
 * 
 * @author Mark
 *
 */
@SuppressWarnings("serial")
public abstract class NegotiationEvent extends EventObject {

	public NegotiationEvent(Object source) {
		super(source);
	}

}
