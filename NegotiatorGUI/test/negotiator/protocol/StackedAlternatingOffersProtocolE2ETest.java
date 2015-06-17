package negotiator.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import negotiator.Deadline;
import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartyNegotiationSessionEvent;
import negotiator.parties.NegotiationParty;
import negotiator.repository.DomainRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.Repository;
import negotiator.session.ExecutorWithTimeout;
import negotiator.session.Session;
import negotiator.session.SessionManager;
import negotiator.tournament.TournamentGenerator;

import org.junit.Test;

public class StackedAlternatingOffersProtocolE2ETest {

	@Test
	public void runMultiPartyNego1() throws Exception {

		/** set up a multiparty negotiation */
		final String[] partyclasses = {
				"negotiator.parties.BoulwareNegotiationParty",
				"negotiator.parties.ConcederNegotiationParty",
				"negotiator.parties.RandomCounterOfferNegotiationParty" };
		Deadline deadline = new Deadline(0, 60);
		Session session = new Session(deadline);
		List<NegotiationParty> parties = new ArrayList<NegotiationParty>();

		// bad to have absolute ref. But there's no better function in
		// Repository...
		DomainRepItem domain8 = Repository
				.getDomainByName("file:etc/templates/Domain8/Domain8.xml");
		Repository party_rep = Repository.get_party_repository();

		for (int partynr = 0; partynr < 3; partynr++) {
			ProfileRepItem profileRepItem = domain8.getProfiles().get(partynr);
			PartyRepItem partyRepItem = party_rep
					.getPartyOfClass(partyclasses[partynr]);

			NegotiationParty negoparty = TournamentGenerator.createFrom(
					partyRepItem, profileRepItem, session);

			parties.add(negoparty);
		}
		// maybe we can craete the parties directly, using this?
		// parties.add(new BoulwareNegotiationParty(utilitySpace, deadlines,
		// timeline, randomSeed));

		MultilateralProtocol protocol = new StackedAlternatingOffersProtocol();

		SessionManager manager = new SessionManager(parties, protocol, session,
				new ExecutorWithTimeout(
						1000 * deadline.getTimeOrDefaultTimeout()));

		myListener listener = new myListener();

		manager.addLoggingListener(listener);

		manager.run();

		/*********** and finally check the outcome **************/
		MultipartyNegotiationSessionEvent lastEvent = listener.getEvents().get(
				listener.getEvents().size() - 1);
		assertNotNull(lastEvent.getAgreement());

		// (Offer: Bid[a: .., b: .., c: .., d: .., e: .., f: .., g: .., h: ..,
		// ])
		// but with proper escape chars, flexibility of spacing etc.
		// String someOffer =
		// "\\s*\\(\\s*Offer:\\s*Bid\\[a\\:\\s*..,\\s*b\\:\\s*..,\\s*c\\:\\s*..,\\s*d\\:\\s*..,\\s*e\\:\\s*..\\s*f\\:\\s*..,\\s*g\\:\\s*..,\\s*h\\:\\s*..,\\s*\\]\\s*\\)\\s*";

		String someOffer = "\\s*\\(Offer\\:\\s*Bid\\[a\\:\\s*..,\\s*b\\:\\s*..,\\s*c\\:\\s*..,\\s*d\\:\\s*..,\\s*e\\:\\s*..,\\s*f\\:\\s*..,\\s*g\\:\\s*..,\\s*h\\:\\s*..,\\s*\\]\\)";
		// check the logs file. It should be of this form
		// Starting negotiation session.
		// SOME NUMBER OF THIS {
		// Round N
		// Turn 1: Boulware#.* (Offer: Bid[a: .., b: .., c: .., d: .., e: .., f:
		// .., g: .., h: .., ])
		// Turn 2: Conceder#.* (Offer: Bid[a: .., b: .., c: .., d: .., e: .., f:
		// .., g: .., h: .., ])
		// Turn 3: Random#.* (Offer: Bid[a: .., b: .., c: .., d: .., e: .., f:
		// .., g: .., h: .., ])
		// }
		// Round .*
		// Turn 1: .* (Accept)
		// Turn 2: .* (Accept)
		// Found an agreement: Bid[a: .., b: .., c: .., d: .., e: .., f: .., g:
		// .., h: .., ]
		// Finished negotiation session in .*s
		// SUMMARY TEXT LINE
		assertMatch("Starting .* session.*", listener.getLogs().get(0));

		int rounds = (listener.getLogs().size() - 7) / 4;
		for (int n = 0; n < rounds; n++) {
			System.out.println("check round " + (n + 1));
			assertMatch("Round " + (n + 1), listener.getLogs().get(4 * n + 1));
			assertMatch("Turn 1:\\s*Boulware\\S*" + someOffer, listener
					.getLogs().get(4 * n + 2));
			assertMatch("Turn 2:\\s*Conceder\\S*" + someOffer, listener
					.getLogs().get(4 * n + 3));
			assertMatch("Turn 3:\\s*Random\\S*" + someOffer, listener.getLogs()
					.get(4 * n + 4));
		}
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
		if (!Pattern.matches("\\s*" + pattern, text)) {
			assertEquals(pattern, text); // generates error text 'expected ...
											// but found ...'
		}
	}

	private class myListener implements MultipartyNegotiationEventListener {

		private List<MultipartyNegotiationOfferEvent> offers = new ArrayList<MultipartyNegotiationOfferEvent>();
		private List<String> logs = new ArrayList<String>();
		private List<MultipartyNegotiationSessionEvent> events = new ArrayList<MultipartyNegotiationSessionEvent>();

		@Override
		public void handleOfferActionEvent(MultipartyNegotiationOfferEvent evt) {
			offers.add(evt);
		}

		public List<String> getLogs() {
			return logs;
		}

		@Override
		public void handleLogMessageEvent(LogMessageEvent evt) {
			logs.add(evt.getMessage());
			System.out.println(evt.getMessage());
		}

		@Override
		public void handleMultipartyNegotiationEvent(
				MultipartyNegotiationSessionEvent evt) {
			events.add(evt);
		}

		public List<MultipartyNegotiationSessionEvent> getEvents() {
			return events;
		}

	}
}
