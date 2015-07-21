package negotiator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import negotiator.actions.Action;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Value;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.parties.NegotiationParty;
import negotiator.session.Timeline;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;

/**
*
* @author Reyhan Aydogan
* Based on Agent Class written by Dmytro and Wouter
 *
 * @deprecated Use negotiator.parties.NegotiationParty instead
*/
public abstract class Party {

	protected AgentID partyID;   //unique id for each negotiating party
	protected UtilitySpace utilitySpace; 
	protected HashMap<AgentParameterVariable,AgentParamValue> parametervalues; // not used now
	protected ArrayList<Integer> partyListenerIndices;

	protected Random randomnr;
	private DeadlineType deadlineType;
	private int totalRoundOrTime;
	private Timeline timeline; 	
    private int round;
	private int sessionNo;
    
    
	//Check whether startTime and total time are necessary ?  If so, add them.
    
	
	public Party() {
		partyListenerIndices=new ArrayList<Integer>();
		setRound(1);
	}
	
	
	public static String getVersion()	{ 
		return "unknown";
	}

	public AgentID getPartyID() {
		return partyID;
	}

	public void setPartyID(AgentID partyID) {
		this.partyID = partyID;
	}


	public UtilitySpace getUtilitySpace() {
		return utilitySpace;
	}

	public void setUtilitySpace(UtilitySpace utilitySpace) {
		this.utilitySpace = utilitySpace;
	}

	public HashMap<AgentParameterVariable,AgentParamValue> getParametervalues()
	{
		return parametervalues;
	}
	
	public void setParametervalues(HashMap<AgentParameterVariable,AgentParamValue> parametervalues)
	{
		this.parametervalues=parametervalues;
	}
	
	
	
	 /**
     * This method is called by the environment (SessionRunner) every time before starting a new 
     * session after the internalInit method is called. User can override this method. 
     */
    public void init() {
    	randomnr= new Random(getSessionNo()); //Randomizer
    }
    
   
    /**
     * This method is called by the SessionRunner to initialize the agent with a new session information.
      * @param us utility space of the agent for the session
     * @param params parameters of the agent
     */
    public final void internalInit(int sessionNo, DeadlineType deadlineType, int totalRoundOrTime,UtilitySpace us, HashMap<AgentParameterVariable,AgentParamValue> params) {
    	this.setSessionNo(sessionNo);
    	this.setDeadlineType(deadlineType);
    	this.setTotalRoundOrTime(totalRoundOrTime);
        this.utilitySpace=us;
    	this.parametervalues=params;
    	this.setRound(1);
     	System.out.println("Party " + getPartyID() + " initted with parameters " + parametervalues);
        return;
    }
    
    
    
    
    /**
     * This function cleans up the remainders of the agent: open windows etc.
     * This function will be called when the agent is killed,
     * typically when it was timed out in a nego session.
     * The agent will not be able to do any negotiation actions here, just clean up.
     * To ensure that the agent can not sabotage the negotiation, 
     * this function will be called from a separate thread.
     * 
     * @author W.Pasman
     */
    public void cleanUp() {  }
    
    /**
     * A convenience method to get the utility of a bid. This method will take discount factors into account (if any), 
     * using the status of the current {@link #timeline}.
     * @see {@link UtilitySpace}.
     */
    public double getUtility(Bid bid)
    {
    	try {
			return utilitySpace.getUtility(bid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return (0.0);
		}
    }
    
    public double getUtilityWithDiscount(Bid bid)
    {
    	return utilitySpace.getUtilityWithDiscount(bid, timeline);
    }
    
    
    public abstract void ReceiveMessage(Action opponentAction);
      
    public abstract Action chooseAction(ArrayList<Class> validActions);


	public ArrayList<Integer> getPartyListenerIndices() {
		return partyListenerIndices;
	}


	public void setPartyListenerIndices(ArrayList<Integer> partyListenerIndices) {
		this.partyListenerIndices = partyListenerIndices;
	}
	
	public void addPartyListenerIndex(int index) {
		
		this.partyListenerIndices.add(new Integer(index));
	}


	public int getSessionNo() {
		return sessionNo;
	}


	public void setSessionNo(int sessionNo) {
		this.sessionNo = sessionNo;
	}


	public int getTotalRoundOrTime() {
		return totalRoundOrTime;
	}


	public void setTotalRoundOrTime(int totalRoundOrTime) {
		this.totalRoundOrTime = totalRoundOrTime;
	}


	public DeadlineType getDeadlineType() {
		return deadlineType;
	}


	public void setDeadlineType(DeadlineType deadlineType) {
		this.deadlineType = deadlineType;
	}
	
	public Timeline getTimeline() {
		return timeline;
	}
	
	public void setTimeline(Timeline timeline)  {
		this.timeline=timeline;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}
	
	protected Value getRandomValue(Issue currentIssue) throws Exception {
		
		Value currentValue=null;
		int index;
		
		switch(currentIssue.getType()) {
			case DISCRETE:
				IssueDiscrete lIssueDiscrete = (IssueDiscrete)currentIssue;
				index=randomnr.nextInt(lIssueDiscrete.getNumberOfValues());
				currentValue=lIssueDiscrete.getValue(index);
				break;
			case REAL:
				IssueReal lIssueReal = (IssueReal)currentIssue;
				index = randomnr.nextInt(lIssueReal.getNumberOfDiscretizationSteps());  // check this!
				currentValue=new ValueReal(lIssueReal.getLowerBound() + ((double)((lIssueReal.getUpperBound()-lIssueReal.getLowerBound()))/(lIssueReal.getNumberOfDiscretizationSteps()))*index);
			    break;
			case INTEGER:
				IssueInteger lIssueInteger = (IssueInteger)currentIssue;
				index =  randomnr.nextInt(lIssueInteger.getUpperBound()-lIssueInteger.getLowerBound()+1);
				currentValue= new ValueInteger(lIssueInteger.getLowerBound()+index);				
				break;
			default: throw new Exception("issue type "+currentIssue.getType()+" not supported");
			}			
		
		return currentValue;		
	}
	
	
	protected Bid generateRandomBid() throws Exception
	{
		Bid randomBid=null;	
		HashMap<Integer, Value> values = new HashMap<Integer, Value>(); // pairs <issuenumber,chosen value string>
		ArrayList<Issue> issues=utilitySpace.getDomain().getIssues();
				
		 	for(Issue currentIssue:issues) {				
				values.put(currentIssue.getNumber(),getRandomValue(currentIssue));
			}
			
		 	randomBid=new Bid(utilitySpace.getDomain(),values);		
			
		return randomBid;
	}


}
