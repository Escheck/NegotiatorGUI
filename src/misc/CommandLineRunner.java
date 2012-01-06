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
 * @author Colin R. Williams
 *
 * Class to allow Negotiations to be run from the command line, without the use of a GUI.
 */
public class CommandLineRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLineOptions options = new CommandLineOptions();
		options.parse(args);
		try {
			Global.logPrefix = options.outputFile;
			start(options.protocol, options.domain, options.profiles, options.agents, options.outputFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void start(String p, String domainFile, List<String> profiles, List<String> agents, String outputFile) throws Exception {
		
		Protocol ns = null;
		
		if (p.equals("negotiator.protocol.alternatingoffers.AlternatingOffersProtocol"))
		{
			ProtocolRepItem protocol = new ProtocolRepItem(p, p, p);
			
			DomainRepItem dom = new DomainRepItem(new URL(domainFile));
			
			ProfileRepItem[] agentProfiles = new ProfileRepItem[2];
			agentProfiles[0] = new ProfileRepItem(new URL(profiles.get(0)), dom);
			agentProfiles[1] = new ProfileRepItem(new URL(profiles.get(1)), dom);
			
			AgentRepItem[] agentsrep = new AgentRepItem[2];
			agentsrep[0] = new AgentRepItem(agents.get(0), agents.get(0), agents.get(0));
			agentsrep[1] = new AgentRepItem(agents.get(1), agents.get(1), agents.get(1));
					
			DomainRepItem domain = agentProfiles[0].getDomain();
			if (domain != agentProfiles[1].getDomain())
				throw new IllegalArgumentException("Profiles for agent A and B do not have the same domain. Please correct your profiles");
			
			ns = Global.createProtocolInstance(protocol, agentsrep, agentProfiles, null);
		}
		
		final FileWriter fw = new FileWriter(outputFile+"/log.txt");
		ns.addNegotiationEventListener(new NegotiationEventListener() {
			
			public void handleActionEvent(ActionEvent evt) {
				try {
					fw.write(evt.toString());
					fw.write("\n");
					fw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
