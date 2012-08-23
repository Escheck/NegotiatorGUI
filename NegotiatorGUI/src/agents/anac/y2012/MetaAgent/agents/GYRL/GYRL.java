package agents.anac.y2012.MetaAgent.agents.GYRL;

import java.io.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import negotiator.*;
import negotiator.actions.*;
import negotiator.issue.*;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.utility.UtilitySpace;

///Final Project In The Course: Information In Decision Making Processess
///Done By: Guy Gabay 		ID: 303125462
///			Rotem Mesika	ID: 303064927
///			Yaniv Mazliah 	ID: 036857357
///			Lina Shlangman	ID: 306551375 
///December 2010

public class GYRL extends Agent 
{
	//define variable to hold the last action of the other agent
    private Action actionOfPartner=null;
    
    private int sessionNumber;			
    private int sessionTotalNumber;
    //private Timeline timeline;
    private ArrayList<Bid> offeredBids;
    private double MaxUtility;
    private double MinUtility;
    private HashMap<AgentParameterVariable, AgentParamValue> params;
    boolean initMe=false;
    /**
     * init is called when a next session starts with the same opponent.
     */
    public void init(int sessionNumberP, int sessionTotalNumberP, Date startTimeP, Integer totalTimeP, UtilitySpace us) 
    {
    	super.init();
        super.internalInit(sessionNumberP, sessionTotalNumberP, startTimeP, totalTimeP, timeline, us, params);
        super.setName("GYRL 2.0");
        initMe=false;
    }

	public void ReceiveMessage(Action opponentAction) 
	{
        actionOfPartner = opponentAction;
    }
	public Action chooseAction()
	{
		//get the normalized time
		//System.out.println("GYRL: start choose action");
		double time=timeline.getTime();
		//The limit percent that we will recieve an offer above 
		double AcceptanceLimit=(2*time*time)-(2*time)+1;
		if(AcceptanceLimit>0.9)
			AcceptanceLimit=0.9;		
		double offerLimit=AcceptanceLimit;
		if(time>0.98)
		{
			//if we reach 98% of time than we take down our limit to 0.49*Avg-Utility
			AcceptanceLimit=0.5;
			offerLimit=0.6;
		}
		if((time>0.96) && (time<0.98))
		{
			offerLimit=0.75;
			AcceptanceLimit=0.75;
		}
		//System.out.println("GYRL:offer limit ="+offerLimit);
		//System.out.println("GYRL:accept limit ="+AcceptanceLimit);
		if(initMe==false)
		{
			//System.out.println("GYRL:init me");
			try 
			{
				Bid maxUtility = this.utilitySpace.getMaxUtilityBid();
				MaxUtility = this.utilitySpace.getUtility(maxUtility);
				MinUtility = MaxUtility;		
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
			initMe=true;
		}
		Action action = null;
		Bid bid2offer = new Bid();
		try 
		{
			//If we start the negotiation, we will offer the bid with 
			//the maximum utility for us
			if(actionOfPartner==null)
			{
			//	System.out.println("GYRL: max offer");
				bid2offer = this.utilitySpace.getMaxUtilityBid();
				action = new Offer(this.getAgentID(), bid2offer);
			}
			else 
			{
				//if we got an offer from the other agent
				if(actionOfPartner instanceof Offer)
				{
		        	//check offer and response.
					double offeredUtility =  this.utilitySpace.getUtility(((Offer) actionOfPartner).getBid());
					if(MaxUtility==MinUtility)
					{
						MinUtility=offeredUtility;
						action= new Offer(this.getAgentID(),this.utilitySpace.getMaxUtilityBid());
					}			
					else
					{
						if(offeredUtility>=(MinUtility+(MaxUtility-MinUtility)*AcceptanceLimit))	
						{
							action=new Accept(this.getAgentID());
						}
						else
						{
							Bid newOffer=this.utilitySpace.getDomain().getRandomBid();
							while(this.utilitySpace.getUtility(newOffer)<=(MinUtility+(MaxUtility-MinUtility)*offerLimit))
							{
								newOffer=this.utilitySpace.getDomain().getRandomBid();
							}
							action = new Offer(this.getAgentID(), newOffer);
						}
					}
				} //end if()
			}//end else
		} //end try
		catch (Exception e) 
		{ 
			//System.out.println("GYRL : Exception in ChooseAction:"+e.getMessage());
			action=new Accept(this.getAgentID()); // best guess if things go wrong.
		}
		return action;
	} //end function chooseAction()
} //end class

