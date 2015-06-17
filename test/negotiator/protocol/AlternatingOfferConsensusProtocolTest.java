package negotiator.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import negotiator.Deadline;
import negotiator.actions.Accept;
import negotiator.actions.Action;
import negotiator.actions.OfferForVoting;
import negotiator.actions.Reject;
import negotiator.parties.NegotiationParty;
import negotiator.session.Round;
import negotiator.session.Session;
import negotiator.session.Turn;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AlternatingOfferConsensusProtocolTest {

	AlternatingOfferConsensusProtocol protocol;

	Turn turn = mock(Turn.class);

	private static int VOTING_ROUND = 0;
	private static int OFFER_ROUND = 1;
	private static int VOTING_ROUND_2 = 2;

	// Round round = mock(Round.class);

	Session session = mock(Session.class);

	@Before
	public void init() {

		protocol = mock(AlternatingOfferConsensusProtocol.class,
				Mockito.CALLS_REAL_METHODS);
		// when(protocol.createRound()).thenReturn(round);
	}

	@Test
	public void testGetRoundStructureVotingRound() {
		when(session.getRoundNumber()).thenReturn(VOTING_ROUND);

		List<NegotiationParty> parties = new ArrayList<NegotiationParty>();
		NegotiationParty party1 = mock(NegotiationParty.class);
		NegotiationParty party2 = mock(NegotiationParty.class);
		NegotiationParty party3 = mock(NegotiationParty.class);

		parties.add(party1);
		parties.add(party2);
		parties.add(party3);

		Round round = protocol.getRoundStructure(parties, session);

		// check that there were created for each party one turn for an offer
		verify(protocol, times(1)).createTurn(eq(party1),
				eq(OfferForVoting.class));
		verify(protocol, times(1)).createTurn(eq(party2),
				eq(OfferForVoting.class));
		verify(protocol, times(1)).createTurn(eq(party3),
				eq(OfferForVoting.class));

		// check that round contains 3 turns and that they are associated to our
		// parties.
		assertEquals(3, round.getTurns().size());
		assertEquals(party1, round.getTurns().get(0).getParty());
		assertEquals(party2, round.getTurns().get(1).getParty());
		assertEquals(party3, round.getTurns().get(2).getParty());

	}

	@Test
	public void testGetRoundStructureOfferRound() {
		when(session.getRoundNumber()).thenReturn(OFFER_ROUND);

		// the specs has poor type, that's why we have it here too.
		Collection<Class<? extends Action>> acceptOrReject = new ArrayList<Class<? extends Action>>(
				2);
		acceptOrReject.add(Accept.class);
		acceptOrReject.add(Reject.class);

		List<NegotiationParty> parties = new ArrayList<NegotiationParty>();
		NegotiationParty party1 = mock(NegotiationParty.class);
		NegotiationParty party2 = mock(NegotiationParty.class);
		NegotiationParty party3 = mock(NegotiationParty.class);

		parties.add(party1);
		parties.add(party2);
		parties.add(party3);

		Round round = protocol.getRoundStructure(parties, session);

		// check that everyone has 3 votes, one for each proposal.
		verify(protocol, times(3)).createTurn(eq(party1), eq(acceptOrReject));
		verify(protocol, times(3)).createTurn(eq(party2), eq(acceptOrReject));
		verify(protocol, times(3)).createTurn(eq(party3), eq(acceptOrReject));

		// check that round contains 9 turns (3 parties * 3 votes) and that they
		// are associated correctly to our parties.
		assertEquals(9, round.getTurns().size());
		assertEquals(party1, round.getTurns().get(0).getParty());
		assertEquals(party2, round.getTurns().get(1).getParty());
		assertEquals(party3, round.getTurns().get(2).getParty());
		assertEquals(party1, round.getTurns().get(3).getParty());
		assertEquals(party2, round.getTurns().get(4).getParty());
		assertEquals(party3, round.getTurns().get(5).getParty());
		assertEquals(party1, round.getTurns().get(6).getParty());
		assertEquals(party2, round.getTurns().get(7).getParty());
		assertEquals(party3, round.getTurns().get(8).getParty());

	}

	/**
	 * Call isFinished when in round 0 (initial situation). The round should not
	 * be finished, nothing happened yet. FAILS because
	 * get(session.getRoundNumber() - 2) in the isFinished code will give
	 * indexOutOfBoundsException.
	 */
	@Test
	public void isFinishedTestVoting() {
		List<NegotiationParty> parties = new ArrayList<NegotiationParty>();
		when(session.getRoundNumber()).thenReturn(VOTING_ROUND);
		when(session.getRounds()).thenReturn(new ArrayList<Round>());
		assertFalse(protocol.isFinished(session, parties));
	}

	/**
	 * Call isFinished when in round 1
	 */
	@Test
	public void isFinishedTestNonVoting() {
		List<NegotiationParty> parties = new ArrayList<NegotiationParty>();
		when(session.getRoundNumber()).thenReturn(OFFER_ROUND);
		assertFalse(protocol.isFinished(session, parties));
	}

	/**
	 * call isFinished when in round 2 The round should not be finished, nothing
	 * happened yet.
	 */
	@Test
	public void isFinishedTestVoting2() {
		List<NegotiationParty> parties = new ArrayList<NegotiationParty>();
		when(session.getRoundNumber()).thenReturn(VOTING_ROUND_2);

		Round round = mock(Round.class);
		ArrayList<Turn> turns = new ArrayList<Turn>();
		when(round.getTurns()).thenReturn(turns);
		ArrayList<Round> rounds = new ArrayList<Round>();
		rounds.add(round);
		when(session.getRounds()).thenReturn(rounds);
		when(session.getMostRecentRound()).thenReturn(round);

		assertFalse(protocol.isFinished(session, parties));
	}

	/**
	 * Check if code works with real round. Currently FAILS with
	 * indexOutOfBounds.
	 */
	@Test
	public void isFinishedTestVotingEndEndTest() {
		Session realsession = new Session(new Deadline(5, 5));
		Round round = new Round();
		round.addTurn(new Turn(null));
		List<NegotiationParty> parties = new ArrayList<NegotiationParty>();
		assertFalse(protocol.isFinished(realsession, parties));
	}
}
