package misc;

import negotiator.DeadlineType;
import negotiator.config.Configuration;
import negotiator.config.MultilateralTournamentConfiguration;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.Protocol;
import negotiator.repository.*;
import negotiator.session.Session;
import negotiator.tournament.TournamentGenerator;
import negotiator.utility.TournamentIndicesGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dfesten on 2/19/2015.
 */
public class StudentConfiguration extends Configuration {

    public static final int AGENTS_PER_RUN = 3;
    public static final int PROFILES_PER_RUN = 3;
    public static final boolean REPETITION_ALLOWED = false;
    public static final int NUMBER_OF_RUNS = 1;
    public int runNumber = 0;
    private Session session = null;

    public StudentConfiguration(int runNumber) {
        this.runNumber = runNumber;
    }

    @Override
    public Session getSession() {
        if (session == null) session = new Session(getDeadlines());
        return session;
    }

    @Override
    public Protocol getProtocol() throws Exception {
        MultiPartyProtocolRepItem protocolRepItem = (MultiPartyProtocolRepItem) Repository.getMultiPartyProtocolRepository().getItems().get(0);
        return Configuration.createFrom(protocolRepItem);
    }

    @Override
    public HashMap<DeadlineType, Object> getDeadlines() {
        HashMap<DeadlineType, Object> deadlines = new HashMap<DeadlineType, Object>();
        deadlines.put(DeadlineType.TIME, StudentTestRunner.TIME_DEADLINE);
        return deadlines;
    }

    public static List<ProfileRepItem> getAllPartyProfileItems() {
        try {
            Repository domainrep = Repository.get_domain_repos();
            ArrayList<ProfileRepItem> profiles = new ArrayList<ProfileRepItem>();
            for (RepItem domain : domainrep.getItems()) {
                if (!(domain instanceof DomainRepItem))
                    throw new IllegalStateException("Found a non-DomainRepItem in domain repository:" + domain);
                for (ProfileRepItem profile : ((DomainRepItem) domain).getProfiles()) profiles.add(profile);
            }
            return profiles;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    @Override
    public List<ProfileRepItem> getPartyProfileItems() {
        return getAllPartyProfileItems().subList(runNumber * 3, runNumber * 3 + 3);
    }


    @Override
    public List<PartyRepItem> getPartyItems() {
        final ArrayList<RepItem> items = Repository.get_party_repository().getItems();
        ArrayList<PartyRepItem> parties = new ArrayList<PartyRepItem>();
        for (RepItem item : items) parties.add((PartyRepItem) item);
        return parties;
    }

    @Override
    public TournamentGenerator getPartiesGenerator() throws Exception {

        List<Integer> indices = new ArrayList<Integer>(getPartyItems().size());
        for (int i = 0; i < getPartyItems().size(); i++) indices.add(i);

        TournamentIndicesGenerator indicesGenerator = new TournamentIndicesGenerator(
                AGENTS_PER_RUN,
                PROFILES_PER_RUN,
                REPETITION_ALLOWED,
                indices);
        return new TournamentGenerator(this, indicesGenerator);
    }

    @Override
    public int getNumTournaments() {
        return NUMBER_OF_RUNS;
    }

    @Override
    public int getMediatorIndex() {
        return 0;
    }

    @Override
    public ProfileRepItem getMediatorProfile() {
        return null;
    }

    @Override
    public PartyRepItem getMediatorItem() {
        return null;
    }
}
