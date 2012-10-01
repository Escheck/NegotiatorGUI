package misc;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import negotiator.Global;
import negotiator.NegotiationEventListener;
import negotiator.events.ActionEvent;
import negotiator.events.BilateralAtomicNegotiationSessionEvent;
import negotiator.events.LogMessageEvent;
import negotiator.events.NegotiationSessionEvent;
import negotiator.protocol.Protocol;
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
			Global.logPrefix = options.outputFile;
			start(options.protocol, options.domain, options.profiles, options.agents, options.outputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void start(String p, String domainFile, List<String> profiles, List<String> agents, String outputFile) throws Exception {
		
		if (profiles.size() != agents.size())
			throw new IllegalArgumentException("Number of profiles does not match number of agents.");
		
		Protocol ns = null;

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
		
		final FileWriter fw = new FileWriter(outputFile+"/log.txt");
		ns.addNegotiationEventListener(new NegotiationEventListener() {
			
			public void handleActionEvent(ActionEvent evt) {
				try {
					fw.write(evt.toString());
					fw.write("\n");
					fw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			public void handleLogMessageEvent(LogMessageEvent evt) {
				// Nothing to be done
			}
			
			public void handleBlateralAtomicNegotiationSessionEvent(BilateralAtomicNegotiationSessionEvent evt) {
				// Nothing to be done
			}
			
			public void handeNegotiationSessionEvent(NegotiationSessionEvent evt) {
				// Nothing to be done
			}
		});
		ns.startSession();
	}
}