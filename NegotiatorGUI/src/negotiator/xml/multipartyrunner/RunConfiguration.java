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

import javax.xml.bind.annotation.XmlElement;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

class RunConfiguration {

    public String run() {

        MultilateralProtocol protocol = generateProtocol();
        Deadline deadline = generateDeadline();
        Session session = new Session(deadline);
        List<NegotiationParty> parties = generateParties(session);
        ExecutorWithTimeout executor = new ExecutorWithTimeout(deadline.getTimeOrDefaultTimeout());
        SessionManager sessionManager = new SessionManager(parties, protocol, session, executor);

        try {
            sessionManager.run();
            long timeUsedInMs = deadline.getTimeOrDefaultTimeout() * 1000 - executor.getRemainingTimeMs();
            return mProtocol
                    + CsvLogger.DELIMITER
                    + CsvLogger.getDefaultSessionLog(session, protocol, parties, timeUsedInMs / 1000D);
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
}