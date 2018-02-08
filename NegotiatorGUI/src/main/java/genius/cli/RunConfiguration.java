package genius.cli;

import static java.lang.Math.pow;
import static misc.ConsoleHelper.useConsoleOut;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import negotiator.AgentID;
import negotiator.Deadline;
import negotiator.events.AgreementEvent;
import negotiator.events.NegotiationEvent;
import negotiator.events.SessionFailedEvent;
import negotiator.exceptions.InstantiateException;
import negotiator.exceptions.NegotiatorException;
import negotiator.parties.NegotiationPartyInternal;
import negotiator.parties.SessionsInfo;
import negotiator.persistent.PersistentDataType;
import negotiator.protocol.MultilateralProtocol;
import negotiator.repository.DomainRepItem;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.session.ExecutorWithTimeout;
import negotiator.session.RepositoryException;
import negotiator.session.Session;
import negotiator.session.SessionManager;
import negotiator.session.TournamentManager;

/**
 * A single negotatiation configuration which can be run. Should contain all
 * information necessary to run. If anything changes in the gui, it should be
 * reflected here as well.
 */
class RunConfiguration {

	@XmlElement(name = "party")
	private List<String> mParties;

	@XmlElement(name = "domain")
	private String mDomain;

	@XmlElement(name = "profile")
	private List<String> mProfiles;

	@XmlElement(name = "protocol")
	private String mProtocol;

	@XmlElement
	private Deadline deadline;

	/**
	 * makes a deep copy of this object using different profile ordering.
	 * 
	 * @param profiles
	 *            The profile ordering to use
	 * @return A new RunConfiguration with the given profiles
	 */
	private RunConfiguration copy(List<String> profiles) {
		return new RunConfiguration(this, profiles);
	}

	/**
	 * needed by the JAXB xml reader
	 */
	private RunConfiguration() {
	}

	/**
	 * Initializes a new instance of the run configuration. Used to do a deep
	 * copy of a previous run confiuration.
	 *
	 * @param config
	 *            The RunConfiguration to copy
	 * @param profiles
	 *            The new profiles for this RunConfiguration
	 */
	private RunConfiguration(RunConfiguration config, List<String> profiles) {
		mParties = new ArrayList<String>(config.mParties);
		mDomain = config.mDomain;
		mProfiles = new ArrayList<String>(profiles);
		mProtocol = config.mProtocol;
		deadline = config.deadline;
	}

	/**
	 * Run this configuration. Uses {@link PersistentDataType#DISABLED}
	 *
	 * @return The NegotiationEvent spawned by running the negotiation
	 * @throws InstantiateException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public NegotiationEvent run() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			InstantiateException, IOException {

		MultilateralProtocol protocol = generateProtocol(mProtocol);
		SessionsInfo info = new SessionsInfo(protocol, PersistentDataType.DISABLED, true);
		Session session = new Session(deadline, info);
		List<NegotiationPartyInternal> parties = generateParties(session, mParties, mProfiles, mProtocol, mDomain,
				info);

		ExecutorWithTimeout executor = new ExecutorWithTimeout(deadline.getDefaultTimeout());
		SessionManager sessionManager = new SessionManager(parties, session, executor);
		try {
			long start = System.nanoTime();
			useConsoleOut(false);
			sessionManager.runAndWait();
			useConsoleOut(true);
			long stop = System.nanoTime();
			return new AgreementEvent(session, protocol, parties, (stop - start) / pow(10, 9));
		} catch (Exception e) {
			return new SessionFailedEvent(e, e.getMessage(), mParties);
		} finally {
			info.close();
		}
	}

	/**
	 * Generates all permutations by fixing the agents and swapping the profiles
	 *
	 * @return A list of all permutations of the current RunConfiguration
	 */
	public List<RunConfiguration> generatePermutations() {
		final ICombinatoricsVector<String> vector = Factory.createVector(mProfiles);
		final Generator<String> generator = Factory.createPermutationGenerator(vector);
		final List<RunConfiguration> permutations = new ArrayList<RunConfiguration>();
		for (ICombinatoricsVector<String> permutatedProfile : generator) {
			permutations.add(this.copy(permutatedProfile.getVector()));
		}
		return permutations;
	}

	/**
	 * Get the number of parties in this RunConfiguration
	 * 
	 * @return The number of parties.
	 */
	public int getNumParties() {
		return mParties.size();
	}

	/**
	 * Checks if the number of parties and profiles are equal. Throws an error
	 * if this is not the case
	 */
	private void checkSizes() {
		if (mParties.size() != mProfiles.size()) {
			System.err.println("malformed xml: there should be equal number of parties and profiles in each run");
			System.exit(1);
		}
	}

	/**
	 * Converts a protocol string to an actual protocol
	 * 
	 * @param protocol
	 *            the protocol string to convert
	 * @return Protocol
	 */
	private MultilateralProtocol generateProtocol(String protocol) {
		try {
			final MultiPartyProtocolRepItem protocolRepItem = new MultiPartyProtocolRepItem(protocol, protocol,
					protocol, false, false);
			return TournamentManager.getProtocol(protocolRepItem);
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
	 *            list of party strings. Each string is full.path.to.class or
	 *            file name.
	 * 
	 * @param profiles
	 *            the utility spaces
	 * @param protocol
	 *            the protocol
	 * @param domain
	 *            the domain
	 * @param info
	 *            the {@link SessionsInfo}
	 * @return list of parties
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InstantiateException
	 */
	private List<NegotiationPartyInternal> generateParties(Session session, List<String> parties, List<String> profiles,
			String protocol, String domain, SessionsInfo info)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, InstantiateException {
		checkSizes();
		List<NegotiationPartyInternal> negotiationParties = new ArrayList<NegotiationPartyInternal>(parties.size());
		for (int i = 0; i < parties.size(); i++) {
			negotiationParties.add(generateParty(parties.get(i), profiles.get(i), protocol, domain, session, info));
		}
		return negotiationParties;
	}

	/**
	 * Generates a single negotiation party from a given session and string
	 * representations of the other parts.
	 * 
	 * @param cpParty
	 *            full.path.to.class or file name.
	 * @param cpProfile
	 *            profile string
	 * @param cpProtocol
	 *            protocol string
	 * @param cpDomain
	 *            domain string
	 * @param session
	 *            Session object
	 * @param info
	 *            the {@link SessionsInfo}
	 * @return Negotiation party
	 * @throws InstantiateException
	 */
	private NegotiationPartyInternal generateParty(String cpParty, String cpProfile, String cpProtocol, String cpDomain,
			Session session, SessionsInfo info) throws InstantiateException {
		try {
			PartyRepItem partyRepItem = null;
			partyRepItem = new PartyRepItem(cpParty);

			DomainRepItem domainRepItem = new DomainRepItem(new URL(cpDomain));
			ProfileRepItem profileRepItem = new ProfileRepItem(new URL(cpProfile), domainRepItem);
			return new NegotiationPartyInternal(partyRepItem, profileRepItem, session, info,
					AgentID.generateID(partyRepItem.getUniqueName()));
		} catch (MalformedURLException e) {
			System.err.println("Malformed url for profile item:");
			System.err.println(cpProfile);
			System.err.println("---");
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (NegotiatorException e) {
			e.printStackTrace();
		}

		// if any exception hit -> exit
		System.exit(-1);
		return null;
	}

}