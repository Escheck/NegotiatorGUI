package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import negotiator.Bid;
import negotiator.Deadline;
import negotiator.DeadlineType;
import negotiator.actions.ActionWithBid;
import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegoActionEvent;
import negotiator.events.MultipartySessionEndedEvent;
import negotiator.events.NegotiationEvent;
import negotiator.listener.Listener;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.protocol.MultilateralProtocol;
import negotiator.protocol.StackedAlternatingOffersProtocol;
import negotiator.repository.DomainRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.Repository;
import negotiator.session.ExecutorWithTimeout;
import negotiator.session.Session;
import negotiator.session.SessionManager;

public class StackedAlternatingOffersProtocolE2ETest {

	private static final String BID_PATTERN = "Bid [a: .., b: .., c: .., d: .., e: .., f: .., g: .., h: .. , ]";
	private static final String OFFER_PATTERN = "( Offer bid:" + BID_PATTERN + " ) ";
	private static final String ACCEPT_PATTERN = "( Accept bid: " + BID_PATTERN + ")";

	enum BidType {
		OFFER, ACCEPT, PLAIN_BID
	};

	private BidType getType(String preamble, String text) {
		if (match(preamble + OFFER_PATTERN, text)) {
			return BidType.OFFER;
		}
		if (match(preamble + ACCEPT_PATTERN, text)) {
			return BidType.ACCEPT;
		}
		if (match(preamble + BID_PATTERN, text)) {
			return BidType.PLAIN_BID;
		}
		throw new IllegalArgumentException("text " + text + " does not contain offer or accept");

	}

	/**
	 * Asserts that the text matches the pattern
	 * 
	 * @param pattern
	 *            the format for the {@link Pattern}.
	 * @param text
	 *            the text to match
	 */
	private void assertMatch(String pattern, String text) {
		if (!match(pattern, text)) {
			assertEquals(pattern, text); // generates error text 'expected ...
											// but found ...'
		}
	}

	/**
	 * check ift the text matches the pattern
	 * 
	 * @param pattern
	 *            the format for the {@link Pattern}. This patter can contain
	 *            chars like (, [ and ' ' which will be escaped before matching
	 *            is done. '*' and '.' will not be escaped
	 * @param text
	 *            the text to match
	 * @return true iff match
	 */
	private boolean match(String pattern, String text) {
		return Pattern.matches(escape(pattern), text);
	}

	private String escape(String text) {
		text = text.replaceAll(" ", "\\\\s*");
		text = text.replaceAll("\\,", "\\\\,");
		// notice, first arg is regexp for '('
		text = text.replaceAll("\\(", "\\\\(");
		// notice, first arg is regexp for ')'
		text = text.replaceAll("\\)", "\\\\)");
		text = text.replaceAll("\\:", "\\\\:");
		text = text.replaceAll("\\[", "\\\\[");
		text = text.replaceAll("\\]", "\\]");
		return text;
	}

	/**
	 * assert that the text is a bid.
	 * 
	 * @param preamble
	 *            the expected pattern before the bid text.
	 * @param text
	 */
	private void assertBidOrAccept(String preamble, String text) {
		getType(preamble, text); // any type goes.
	}

	private class myListener implements Listener<NegotiationEvent> {

		private List<Bid> offers = new ArrayList<Bid>();
		private List<String> logs = new ArrayList<String>();
		private List<MultipartySessionEndedEvent> events = new ArrayList<MultipartySessionEndedEvent>();

		public List<String> getLogs() {
			return logs;
		}

		public List<MultipartySessionEndedEvent> getEvents() {
			return events;
		}

		@Override
		public void notifyChange(NegotiationEvent e) {
			if (e instanceof LogMessageEvent) {
				logs.add(((LogMessageEvent) e).getMessage());
				System.out.println(((LogMessageEvent) e).getMessage());
			} else if (e instanceof MultipartyNegoActionEvent
					&& ((MultipartyNegoActionEvent) e).getAction() instanceof ActionWithBid) {
				offers.add(((ActionWithBid) ((MultipartyNegoActionEvent) e).getAction()).getBid());
			} else if (e instanceof MultipartySessionEndedEvent) {
				events.add((MultipartySessionEndedEvent) e);
			}

		}
	}

	/************************************************************************************************/
	/*
	 * the actual tests are below. We can't check creation of log files, as
	 * these are not created normally.
	 */

	/**
	 * End to end test of multiparty tournament. 3 parties boulware, conceder,
	 * random. Domain8 is used.
	 */
	@Test
	public void runMultiPartyNego1() throws Exception {

		/** set up a multiparty negotiation */
		final String[] partyclasses = { "negotiator.parties.BoulwareNegotiationParty",
				"negotiator.parties.ConcederNegotiationParty",
				"negotiator.parties.RandomCounterOfferNegotiationParty" };
		Deadline deadline = new Deadline(60, DeadlineType.ROUND);
		Session session = new Session(deadline);
		List<NegotiationPartyInternal> parties = new ArrayList<NegotiationPartyInternal>();

		// bad to have absolute ref. But there's no better function in
		// Repository...
		DomainRepItem domain8 = Repository.getDomainByName("file:etc/templates/Domain8/Domain8.xml");
		Repository party_rep = Repository.get_party_repository();

		for (int partynr = 0; partynr < 3; partynr++) {
			ProfileRepItem profileRepItem = domain8.getProfiles().get(partynr);
			PartyRepItem partyRepItem = party_rep.getPartyOfClass(partyclasses[partynr]);

			NegotiationPartyInternal negoparty = new NegotiationPartyInternal(partyRepItem, profileRepItem, session,
					null);
			parties.add(negoparty);
		}
		// maybe we can craete the parties directly, using this?
		// parties.add(new BoulwareNegotiationParty(utilitySpace, deadlines,
		// timeline, randomSeed));

		MultilateralProtocol protocol = new StackedAlternatingOffersProtocol();

		SessionManager manager = new SessionManager(parties, protocol, session,
				new ExecutorWithTimeout(1000 * deadline.getTimeOrDefaultTimeout()));

		myListener listener = new myListener();

		manager.addListener(listener);

		manager.runAndWait();

		/*********** and finally check the outcome **************/
		MultipartySessionEndedEvent lastEvent = listener.getEvents().get(listener.getEvents().size() - 1);
		assertNotNull(lastEvent.getAgreement());

		// check the logs file. It should be of this form
		// Starting negotiation session.
		// SOME NUMBER OF THIS {
		// Round N
		// Turn 1: Boulware#.* offerOrAccept
		// Turn 2: Conceder#.* offerOrAccept
		// Turn 3: Random#.* offerOrAccept
		// }
		// Round .*
		// Turn 1: .* (Accept)
		// Turn 2: .* (Accept)
		// Found an agreement: Bid[a: .., b: .., c: .., d: .., e: .., f: .., g:
		// .., h: .., ]
		// Finished negotiation session in .*s
		// SUMMARY TEXT LINE

		int line = 0;

		assertMatch("Starting .* session.*", listener.getLogs().get(line++));

		int rounds = (listener.getLogs().size() - 7) / 4;
		for (int n = 0; n < rounds; n++) {
			// System.out.println("check round " + (n + 1));
			assertMatch("Round " + (n + 1), listener.getLogs().get(line++));
			assertBidOrAccept(" Turn 1: Boulware\\S* ", listener.getLogs().get(line++));
			assertBidOrAccept(" Turn 2: Conceder\\S* ", listener.getLogs().get(line++));
			assertBidOrAccept(" Turn 3: Random\\S* ", listener.getLogs().get(line++));
		}
		// then the accept round
		assertMatch("Round " + (rounds + 1), listener.getLogs().get(line++));
		assertEquals(BidType.ACCEPT, getType(" Turn 1: \\S* ", listener.getLogs().get(line++)));
		assertEquals(BidType.ACCEPT, getType(" Turn 2: \\S* ", listener.getLogs().get(line++)));
		// then "found an agreement"
		assertEquals(BidType.PLAIN_BID, getType(" Found an agreement: ", listener.getLogs().get(line++)));
		assertMatch(" Finished negotiation session in \\S* s.", listener.getLogs().get(line++));
		// list line is the wrap-up. Not checked yet, pretty complex.
	}

}
