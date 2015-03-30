package negotiator.analysis;

import negotiator.Bid;
import negotiator.BidIterator;
import negotiator.Domain;
import negotiator.Timeline;
import negotiator.exceptions.AnalysisException;
import negotiator.parties.NegotiationParty;
import negotiator.protocol.MediatorProtocol;
import negotiator.protocol.Protocol;
import negotiator.session.Session;
import negotiator.utility.UtilitySpace;

import java.util.ArrayList;
import java.util.List;

/**
 * Start on analysis of the multi party tournament. Code in this class is mainly adapted from the
 * bilateral analysis which is in the other classes of this package (negotiator.analysis)
 *
 * @author David Festen
 */
public class MultilateralAnalysis
{
    /**
     * Maximum number of bids to analyse
     */
    public static final int ENUMERATION_CUTOFF = 100000;

    /**
     * List of all bid points in the domain.
     */
    private ArrayList<BidPoint> bidPoints;

    /**
     * Cached Pareto frontier.
     */
    private List<BidPoint> paretoFrontier = null; // null if not yet computed

    /**
     * Cached Nash solution. The solution is assumed to be unique.
     */
    private BidPoint nash = null; // null if not yet computed

    private BidPoint agreement = null;

    private final Session session;
    private final List<NegotiationParty> parties;
    private final Protocol protocol;
    private Timeline timeline;

    /**
     * Collection of utility spaces constituting the space.
     */
    private UtilitySpace[] utilitySpaces;

    /**
     * Domain of the utility spaces.
     *
     */
    private Domain domain;

    public MultilateralAnalysis(Session session, List<NegotiationParty> parties, Protocol protocol) throws Exception
    {
//        System.out.print("Generating analysis... ");

        this.session = session;
        this.parties = parties;
        this.protocol = protocol;
        this.timeline = session.getTimeline();
        initializeUtilitySpaces(getUtilitySpaces(parties));
        buildSpace(true);

        Bid finalBid = protocol.getCurrentAgreement(session, parties);
        Double[] utils = new Double[utilitySpaces.length];
        if (finalBid == null) for (int i = 0; i < utilitySpaces.length; i++) utils[i] = 0.0;
        else for (int i = 0; i < utilitySpaces.length; i++) {
            utils[i] = timeline == null ? utilitySpaces[i].getUtility(finalBid) : utilitySpaces[i].getUtilityWithDiscount(finalBid, timeline);
        }
        agreement = new BidPoint(finalBid, utils);

//        System.out.println("done");

    }

    public static ArrayList<double[][]> getPartyBidSeries(ArrayList<ArrayList<Double[]>> partyUtilityHistoryList)
    {

        ArrayList<double[][]> bidSeries = new ArrayList<double[][]>();
        double[][] product = new double[2][partyUtilityHistoryList.get(0).size()];
        try
        {

            for (int i = 0; i < partyUtilityHistoryList.size() - 1; i++)
            {

                double[][] xPartyUtilities = new double[2][partyUtilityHistoryList.get(i).size()];
                int index = 0;

                for (Double[] utilityHistory : partyUtilityHistoryList.get(i))
                {

                    xPartyUtilities[0][index] = utilityHistory[0];
                    xPartyUtilities[1][index] = utilityHistory[1];

                    product[0][index] = utilityHistory[0];
                    if (i == 0) // for the first agent
                        product[1][index] = utilityHistory[1];
                    else product[1][index] *= utilityHistory[1];
                    index++;
                }

                bidSeries.add(xPartyUtilities);
            }
            bidSeries.add(product);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return bidSeries;
    }

    public UtilitySpace[] getUtilitySpaces(List<NegotiationParty> parties)
    {
        List<NegotiationParty> agents = MediatorProtocol.getNonMediators(parties);
        UtilitySpace[] spaces = new UtilitySpace[agents.size()];
        for (int i = 0; i < agents.size(); i++) spaces[i] = agents.get(i).getUtilitySpace();
        return spaces;
    }

    /**
     * Create the space with all bid points from all the {@link UtilitySpace}s.
     *
     * @param excludeBids if true do not store the real bids.
     * @throws Exception if utility can not be computed for some point.
     */
    private void buildSpace(boolean excludeBids) throws Exception
    {


        bidPoints = new ArrayList<BidPoint>();
        BidIterator lBidIterator = new BidIterator(domain);

        // if low memory mode, do not store the actual. At the time of writing
        // this
        // has no side-effects
        int iterationNumber = 0;
        while (lBidIterator.hasNext())
        {
            if (++iterationNumber > ENUMERATION_CUTOFF) {
//                System.out.printf("Could not enumerate complete bid space, " +
//                        "enumerated first %d bids... ", ENUMERATION_CUTOFF);
                break;
            }
            Bid bid = lBidIterator.next();
            Double[] utils = new Double[utilitySpaces.length];
            for (int i = 0; i < utilitySpaces.length; i++)
            {
                utils[i] = timeline == null ? utilitySpaces[i].getUtility(bid) : utilitySpaces[i].getUtilityWithDiscount(bid, timeline) ;
            }
            if (excludeBids)
            {
                bidPoints.add(new BidPoint(null, utils));
            }
            else
            {
                bidPoints.add(new BidPoint(bid, utils));
            }
        }
    }

    /**
     * Returns the Pareto frontier. If the Pareto frontier is unknown, then it is
     * computed using an efficient algorithm. If the utility space contains more
     * than 500000 bids, then a suboptimal algorithm is used.
     *
     * @return The Pareto frontier. The order is ascending utilityA.
     * @throws Exception if the utility of a bid can not be calculated.
     */
    public List<BidPoint> getParetoFrontier() throws Exception
    {
        boolean isBidSpaceAvailable = !bidPoints.isEmpty();
        if (paretoFrontier == null)
        {
            if (isBidSpaceAvailable)
            {
                paretoFrontier = computeParetoFrontier(bidPoints).getFrontier();
                return paretoFrontier;
            }

            ArrayList<BidPoint> subPareto = new ArrayList<BidPoint>();
            BidIterator lBidIterator = new BidIterator(domain);
            ArrayList<BidPoint> tmpBidPoints = new ArrayList<BidPoint>();
            boolean isSplit = false;
            int count = 0;
            while (lBidIterator.hasNext() && count < ENUMERATION_CUTOFF)
            {
                Bid bid = lBidIterator.next();
                Double[] utils = new Double[utilitySpaces.length];
                for (int i = 0; i < utilitySpaces.length; i++)
                    utils[i] = timeline == null ? utilitySpaces[i].getUtility(bid) :  utilitySpaces[i].getUtilityWithDiscount(bid, timeline);
                tmpBidPoints.add(new BidPoint(bid, utils));
                count++;
                if (count > 500000)
                {
                    subPareto.addAll(computeParetoFrontier(tmpBidPoints)
                            .getFrontier());
                    tmpBidPoints = new ArrayList<BidPoint>();
                    count = 0;
                    isSplit = true;
                }
            }
            // Add the remainder to the sub-Pareto frontier
            if (tmpBidPoints.size() > 0)
                subPareto.addAll(computeParetoFrontier(tmpBidPoints)
                        .getFrontier());

            if (isSplit)
                paretoFrontier = computeParetoFrontier(subPareto).getFrontier(); // merge
                // sub-pareto's
            else
                paretoFrontier = subPareto;
        }
        return paretoFrontier;
    }

    /**
     * Private because it should be called only with the bids as built by
     * BuildSpace.
     *
     * @param points the ArrayList<BidPoint> as computed by BuildSpace and stored
     *               in bid points.
     * @return the sorted pareto frontier of the bid points.
     */
    private ParetoFrontier computeParetoFrontier(List<BidPoint> points)
    {
        ParetoFrontier frontier = new ParetoFrontier();
        for (BidPoint p : points)
            frontier.mergeIntoFrontier(p);

        frontier.sort();
        return frontier;
    }

    /**
     * Method which returns a list of the Pareto efficient bids.
     *
     * @return Pareto-efficient bids.
     * @throws Exception if the utility of a bid cannot be calculated
     */
    public List<Bid> getParetoFrontierBids() throws Exception
    {
        ArrayList<Bid> bids = new ArrayList<Bid>();
        List<BidPoint> points = getParetoFrontier();
        for (BidPoint p : points)
            bids.add(p.getBid());
        return bids;
    }

    /**
     * Initializes the utility spaces by checking if they are valid. This
     * procedure also clones the spaces such that manipulating them is not
     * useful for an agent.
     *
     * @param utilitySpaces to be initialized and validated.
     * @throws Exception if one of the utility spaces is null.
     */
    private void initializeUtilitySpaces(UtilitySpace[] utilitySpaces)
            throws Exception
    {
        this.utilitySpaces = utilitySpaces.clone();

        for (UtilitySpace utilitySpace : utilitySpaces)
            if (utilitySpace == null)
                throw new NullPointerException("util space is null");

        domain = this.utilitySpaces[0].getDomain();

        for (UtilitySpace space : utilitySpaces)
            space.checkReadyForNegotiation(domain);
    }

    public double getSocialWelfare() {
        double totalUtility = 0;
        List<NegotiationParty> agents = MediatorProtocol.getNonMediators(parties);
        for (NegotiationParty agent : agents) {
            totalUtility += agent.getUtilityWithDiscount(protocol.getCurrentAgreement(session, parties));
        }
        return totalUtility;
    }

    public double getDistanceToNash() throws Exception
    {
        return agreement.getDistance(getNashPoint());
    }

    public double getDistanceToPareto()
    {
        if (paretoFrontier == null)
        {
            try
            {
                paretoFrontier = getParetoFrontier();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        double distance = Double.POSITIVE_INFINITY;
        for (BidPoint paretoBid : paretoFrontier)
        {
            double paretoDistance = agreement.getDistance(paretoBid);
            if (paretoDistance < distance)
            {
                distance = paretoDistance;
            }
        }
        return distance;
    }

    public BidPoint getNashPoint() throws Exception
    {
        if (nash != null)
            return nash;
        if (getParetoFrontier().size() < 1)
            throw new AnalysisException(
                    "Nash product: Pareto frontier is unavailable.");
        double maxP = -1;
        double[] agentResValue = new double[utilitySpaces.length];
        for (int i = 0; i < utilitySpaces.length; i++)
            if (utilitySpaces[i].getReservationValue() != null)
                agentResValue[i] = utilitySpaces[i].getReservationValue();
            else
                agentResValue[i] = .0;
        for (BidPoint p : paretoFrontier)
        {
            double utilOfP = 1;
            for (int i = 0; i < utilitySpaces.length; i++)
                utilOfP = utilOfP * (p.getUtility(i) - agentResValue[i]);

            if (utilOfP > maxP)
            {
                nash = p;
                maxP = utilOfP;
            }
        }
        return nash;
    }

}
