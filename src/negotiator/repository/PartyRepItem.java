package negotiator.repository;

import negotiator.exceptions.Warning;
import java.net.URL;
import javax.xml.bind.annotation.*;

/**
 * This repository item contains all info about an agent that can be loaded.
 * 
 * @author Reyhan modifies the AgentRepItem
 *
 */

@XmlRootElement
public class PartyRepItem implements RepItem
{
	@XmlAttribute
	String partyName; /**  the key: short but unique name of the agent as it will be known in the nego system.
 						* This is an arbitrary but unique label for this TYPE of agent.
 						* Note that there may still be multiple actual agents of this type during a negotiation. */
	@XmlAttribute
	String classPath; /** file path including the class name */
	@XmlAttribute
	String description; /** description of this agent */

	@XmlAttribute  //RA: For multiparty negotiation, there are two type of agents: mediator and negotiating party
	Boolean isMediator; /** whether the party is a mediator */
	
	 	
	public PartyRepItem(){
	}
	
	public PartyRepItem(String aName, String cPath, String desc, Boolean isMed) {
		partyName=aName; 
		classPath=cPath;
		description=desc;
        isMediator=isMed;	
	}
	
		
	/**
	 * @returns true if partyName and classPath equal. Note that partyName alone is sufficient to be equal as keys are unique.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof PartyRepItem)) return false;
		return partyName.equals( ((PartyRepItem)o).partyName) && classPath.equals( ((PartyRepItem)o).classPath);
	}
	
	
	public String getName() { return partyName; }
	
	public String getClassPath() { return classPath; }
	
	public String getDescription() { return description; }
	
	public Boolean getIsMediator() {return isMediator; } //RA

		
	/** getVersion is bit involved, need to call the agent getVersion() to get it */
	private static final Class[] parameters = new Class[]{URL.class};
	public String getVersion() { 
	       try{
	    	 return ""+callStaticPartyFunction( "getVersion",new Object[0]);
		   } catch(Exception e){
	           new Warning("can't get version for "+partyName+" :",e); 
	       }  		
	       return "ERR";
		}
	
	/** 
	 * callAgentFunction can call a Static agent function without instantiating the agent. 
	 * This is used to get the version and parameters from the agent class in general.
	 * @return the object returned by that function
	 * @throws any exception that the function can throw, or failures
	 * by not finding the class, failure to load the description, etc.
	 * @param methodname contains the name of the method, eg "getVersion"
	 * @param params contains an array of parameters to the call, eg Object[0] for no parameters.
	 */
	public Object callStaticPartyFunction(String methodname, Object[] params) throws Exception {
		Class c=Class.forName(classPath);
		return c.getMethod(methodname).invoke(null, params);
	}
	
	public String toString() { return "PartyRepositoryItem["+partyName+","+classPath+","+description+", is mediator="+isMediator.toString()+"]"; } //RA
}