package negotiator.xml.multipartyrunner;

import static java.lang.Math.pow;
import static misc.ConsoleHelper.useConsoleOut;

import java.util.ArrayList;
import java.util.List;

import negotiator.Deadline;
import negotiator.config.MultilateralTournamentConfiguration;
import negotiator.events.AgreementEvent;
import negotiator.events.NegotiationEvent;
import negotiator.events.SessionFailedEvent;
import negotiator.exceptions.NegotiatorException;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.protocol.MultilateralProtocol;
import negotiator.repository.DomainRepItem;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.session.ExecutorWithTimeout;
import negotiator.session.RepositoryException;
import negotiator.session.Session;
import negotiator.session.SessionManager;
import negotiator.tournament.TournamentGenerator;

/**
 * Class that can take a {@link MultilateralTournamentConfiguration}, generate
 * the and run it. Similar to {@link RunConfiguration} but without the XML
 * serialization stuff for more flexibility, only half of the code and better
 * readability.
 * 
 * @author W.Pasman 27jul15
 *
 */
public class MultilatConfigurationRunner extends Thread {

	private MultilateralTournamentConfiguration configuration;
	/**
	 * The result of the run. non-null means we got a result and the run
	 * terminated.
	 */
	private NegotiationEvent result = null;

	public MultilatConfigurationRunner(
			MultilateralTournamentConfiguration config) {
		configuration = config;
	}

	// FIXME connect to GUI and runners.
	@Override
	public void run() {
		MultilateralProtocol protocol = generateProtocol(configuration
				.getProtocolItem());
		Deadline deadline = configuration.getDeadline();
		Session session = new Session(deadline);
		DomainRepItem domain = configuration.getPartyProfileItems().get(0)
				.getDomain();
		List<NegotiationPartyInternal> parties = generateParties(session,
				configuration.getPartyItems(),
				configuration.getPartyProfileItems(), protocol, domain);
		ExecutorWithTimeout executor = new ExecutorWithTimeout(
				deadline.getTimeOrDefaultTimeout());
		SessionManager sessionManager = new SessionManager(parties, protocol,
				session, executor);

		try {
			long start = System.nanoTime();
			useConsoleOut(false);
			sessionManager.run();
			useConsoleOut(true);
			long stop = System.nanoTime();
			result = new AgreementEvent(this, session, protocol, parties,
					(stop - start) / pow(10, 9));
		} catch (Exception e) {
			result = new SessionFailedEvent(this, e, e.getMessage());
		}

	}

	/**
	 * get the result of the run. null if the run did not yet terminate.
	 * 
	 * @return result of run, or null if still running. If the run fails, this
	 *         should return a {@link SessionFailedEvent}.
	 */
	public NegotiationEvent getResult() {
		return result;
	}

	/**
	 * Converts a protocol string to an actual protocol
	 * 
	 * @param protocol
	 *            the protocol string to convert
	 * @return Protocol
	 */
	private MultilateralProtocol generateProtocol(
			MultiPartyProtocolRepItem protocolRepItem) {
		try {
			return TournamentGenerator.createFrom(protocolRepItem);
		} catch (Exception e) {
			System.err.println("Error while generating protocol from xml");
			System.err.println("---");
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	/**
	 * Generates a list of negotiation parties
	 * 
	 * @param session
	 *            The actual session object
	 * @param parties
	 *            list of party strings
	 * @param profiles
	 *            list of profile strings
	 * @return list of parties
	 */
	private List<NegotiationPartyInternal> generateParties(Session session,
			List<PartyRepItem> parties, List<ProfileRepItem> profiles,
			MultilateralProtocol protocol, DomainRepItem domain) {
		if (parties.size() != profiles.size()) {
			throw new IllegalArgumentException(
					"error in the configuration: #parties must be equal to #profiles");
		}

		List<NegotiationPartyInternal> negotiationParties = new ArrayList<NegotiationPartyInternal>(
				parties.size());
		for (int i = 0; i < parties.size(); i++) {
			negotiationParties.add(generateParty(parties.get(i),
					profiles.get(i), protocol, domain, session));
		}
		return negotiationParties;
	}

	/**
	 * Generates a single negotiation party from a given session and string
	 * representations of the other parts.
	 * 
	 * @param partyRepItem
	 *            party string
	 * @param profileRepItem2
	 *            profile string
	 * @param protocol
	 *            protocol string
	 * @param cpDomain
	 *            domain string
	 * @param session
	 *            Session object
	 * @return Negotiation party
	 */
	private NegotiationPartyInternal generateParty(PartyRepItem partyRepItem,
			ProfileRepItem profileRepItem, MultilateralProtocol protocol,
			DomainRepItem domainRepItem, Session session) {
		try {
			return new NegotiationPartyInternal(partyRepItem, profileRepItem,
					session, null);
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (NegotiatorException e) {
			e.printStackTrace();
		}

		// if any exception hit -> exit
		// FIXME we should just cancel, not exit the system??
		System.exit(-1);
		return null;
	}

}
