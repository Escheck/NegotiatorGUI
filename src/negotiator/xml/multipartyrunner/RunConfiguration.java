package negotiator.xml.multipartyrunner;


import negotiator.Deadline;
import negotiator.config.Configuration;
import negotiator.exceptions.NegotiationPartyTimeoutException;
import negotiator.exceptions.NegotiatorException;
import negotiator.logging.CsvLogger;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.MultilateralProtocol;
import negotiator.repository.DomainRepItem;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.session.*;
import negotiator.tournament.Tournament;
import negotiator.tournament.TournamentGenerator;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import javax.xml.bind.annotation.XmlElement;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.pow;

class RunConfiguration {

    public RunConfiguration() {
    }

    private RunConfiguration(List<String> parties, String domain, List<String> profiles, String protocol, String deadlineType, String deadlineValue) {
        mParties = new ArrayList<String>(parties);
        mDomain = domain;
        mProfiles = new ArrayList<String>(profiles);
        mProtocol = protocol;
        mDeadlineType = deadlineType;
        mDeadlineValue = deadlineValue;
    }

    public String run() {

        MultilateralProtocol protocol = generateProtocol();
        Deadline deadline = generateDeadline();
        Session session = new Session(deadline);
        List<NegotiationParty> parties = generateParties(session);
        ExecutorWithTimeout executor = new ExecutorWithTimeout(deadline.getTimeOrDefaultTimeout());
        SessionManager sessionManager = new SessionManager(parties, protocol, session, executor);

        try {
            long start = System.nanoTime();
            sessionManager.run();
            long stop = System.nanoTime();
            return mProtocol
                    + CsvLogger.DELIMITER
                    + CsvLogger.getDefaultSessionLog(session, protocol, parties, (stop - start) / pow(10, 9));
        } catch (InterruptedException e) {
            return "TIMEOUT";
        } catch (ExecutionException e) {
            return "TIMEOUT";
        } catch (NegotiationPartyTimeoutException e) {
            return "TIMEOUT";
        } catch (InvalidActionError invalidActionError) {
            return "INVALID ACTION";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    /**
     * Generates all permutatations by fixing the agents and swapping the profiles
     *
     * @return
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

    private void checkSizes() {
        if (mParties.size() != mProfiles.size()) {
            System.err.println("malformed xml: there should be equal number of parties and profiles in each run");
            System.exit(1);
        }
    }

    private MultilateralProtocol generateProtocol() {
        try {
            final MultiPartyProtocolRepItem protocolRepItem = new MultiPartyProtocolRepItem(mProtocol, mProtocol, mProtocol, false, false);
            return TournamentGenerator.createFrom(protocolRepItem);
        } catch (Exception e) {
            System.err.println("Error while generating protocol from xml");
            System.err.println("---");
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private Deadline generateDeadline() {
        int value = Integer.parseInt(mDeadlineValue);
        if (mDeadlineType.toLowerCase().equals("time")) {
            return new Deadline(value, -1);
        } else {
            return new Deadline(-1, value);
        }
    }

    private List<NegotiationParty> generateParties(Session session) {
        checkSizes();
        List<NegotiationParty> parties = new ArrayList<NegotiationParty>(mParties.size());
        for (int i = 0; i < mParties.size(); i++) {
            parties.add(generateParty(mParties.get(i), mProfiles.get(i), session));
        }
        return parties;
    }

    private NegotiationParty generateParty(String cpParty, String cpProfile, Session session) {
        try {
            PartyRepItem partyRepItem = new PartyRepItem(cpParty, mProtocol);
            DomainRepItem domainRepItem = new DomainRepItem(new URL(mDomain));
            ProfileRepItem profileRepItem = new ProfileRepItem(new URL(cpProfile), domainRepItem);
            return TournamentGenerator.createFrom(partyRepItem, profileRepItem, session);
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

    @XmlElement(name = "party")
    private List<String> mParties;

    @XmlElement(name = "domain")
    private String mDomain;

    @XmlElement(name = "profile")
    private List<String> mProfiles;

    @XmlElement(name = "protocol")
    private String mProtocol;

    @XmlElement(name = "deadline-type")
    private String mDeadlineType;

    @XmlElement(name = "deadline-value")
    private String mDeadlineValue;

    private RunConfiguration copy(List<String> profiles) {
        return new RunConfiguration(mParties, mDomain, profiles, mProtocol, mDeadlineType, mDeadlineValue);
    }
}