package misc;

import java.net.URL;
import java.util.List;
import negotiator.Global;
import negotiator.protocol.OldProtocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;

/**
 * Class to allow Negotiations to be run from the command line, without the use of a GUI.
 * 
 * @author Colin R. Williams
 */
public class CommandLineRunner {

	/**
	 * Main method used to launch a negotiation specified as commandline commands.
	 * 
	 * @param args specification of the tournament parameters.
	 */
	public static void main(String[] args) {
		CommandLineOptions options = new CommandLineOptions();
		options.parse(args);
				
		try {
			start(options.protocol, options.domain, options.profiles, options.agents, options.outputFile);
			//start("negotiator.protocol.alternatingoffers.AlternatingOffersProtocol", "file:etc/templates/laptopdomain/laptop_domain.xml", profiles,
				//	agents,"log");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("ends");
	}

	//make it private later
	private static void start(String p, String domainFile, List<String> profiles, List<String> agents, String outputFile) throws Exception {
		
		Global.logPreset = outputFile;
		
		if (profiles.size() != agents.size())
			throw new IllegalArgumentException("Number of profiles does not match number of agents.");
		
		OldProtocol ns = null;

		ProtocolRepItem protocol = new ProtocolRepItem(p, p, p);
		
		DomainRepItem dom = new DomainRepItem(new URL(domainFile));
		
		ProfileRepItem[] agentProfiles = new ProfileRepItem[profiles.size()];
		for(int i = 0; i<profiles.size(); i++)
		{
			agentProfiles[i] = new ProfileRepItem(new URL(profiles.get(i)), dom);
			if(agentProfiles[i].getDomain() != agentProfiles[0].getDomain())
				throw new IllegalArgumentException("Profiles for agent 0 and agent " + i + " do not have the same domain. Please correct your profiles");
		}
		
		AgentRepItem[] agentsrep = new AgentRepItem[agents.size()];
		for(int i = 0; i<agents.size(); i++)
		{
			agentsrep[i] = new AgentRepItem(agents.get(i), agents.get(i), agents.get(i));
		}
		
		ns = Global.createProtocolInstance(protocol, agentsrep, agentProfiles, null);
		

		ns.startSession();
	}
}