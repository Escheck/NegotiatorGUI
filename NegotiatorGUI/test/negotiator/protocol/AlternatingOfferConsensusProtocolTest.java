package negotiator.protocol;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import negotiator.actions.Accept;
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
		when(session.getRoundNumber()).thenReturn(0); // an OFFER round

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
		when(session.getRoundNumber()).thenReturn(1); // an VOTING round.

		// the specs has poor type, that's why we have it here too.
		Collection<Class> acceptOrReject = new ArrayList<Class>(2);
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

}
