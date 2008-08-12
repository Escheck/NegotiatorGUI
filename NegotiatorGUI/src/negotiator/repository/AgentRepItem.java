package negotiator.repository;

/**
 * This repository item contains all info about an agent that can be loaded.

 * 
 * @author wouter
 *
 */
public class AgentRepItem implements RepItem
{
	
	String agentName; /**  the key: unique name of the agent as it will be known in the nego system.
 * This is an arbitrary but unique label. */
	String classPath; /** file path including the class name */
	String version; /** the version, should match the version as specified by the agent */
	String description; /** description of this agent */
	
	public AgentRepItem(String aName, String cPath, String vers,String desc) {
		agentName=aName; 
		classPath=cPath;
		version=vers;
		description=desc;
	}
	
	public String getName() { return agentName; }
	public String getPath() { return classPath; }
	public String getVersion() { return version; }
	public String getDescription() { return description; }
}