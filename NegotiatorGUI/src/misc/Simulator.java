/**
 * ScenarioLauncher for the optimal bidding simulations.
 * 
 * 	Features:
 *	 	- Runs "ntournaments" tournaments. Each tournament has X sessions.
 * 	 		- For each tournament, it dynamically/randomly picks two agents from agentrepository.xml
 * 			- Loads two preferences' profile, 
 *  			- Stores the log in a specific location log/_log
 *  
 *  		For example if we run it for two agents and 3 tournaments at 2013-09-26 18.58.25, the result will be generated
 *  		and stored in 3 log files 2013-09-26 18.58.25__i.xml, with i=1,2,3.
 * 
 * Note:
 *     Before running ScenarioLauncher:
 * 	 	1. first run the SGG to generate the profiles
 * 	 	2. move them to domainrepository.xml (manually or automatically*)
 *	
 *  TODO deactivate the agents involving GUIs...
 *  TODO add the agents' names, rvB and rvA to the prefix of the log file name.
 *  TODO How to fix the number of rounds (X) in a session !?
 *  TODO (*)
 * 
 * @author rafik hadfi 
 **************************************************************************************************************/


/***

Make it in such a way that you specify the agents/domains/etc in separate files (launchagentrepository.xml, launchdomainrepository.xml, etc.?)
I guess it is better to make all combinations possible, and to specify the profiles for both A and B. We could make it most easily I think by:
1) Generate all combinations of all agents / profiles
2) Then filter out the combinations we do not want, e.g.: filterSelfPlay(), or filterNonEmptyAgreementZone().
About your remarks:
- I think the number of rounds should be also set in the TournamentConfiguration, no? The type of "protocolMode" should be discrete, 
and the rounds ("deadline") is another property.
- oneSidedBidding should be on for our experiments. The testing is almost finished, but I think it should work in most cases now.

______________________________________________________________________________________________________________________________
profiles generation : the loop (j=i+increment) method ? does it embed filterNonEmptyAgreementZone()
agents combinations : define the filterSelfPlay() or jut embed it with the loops ?

	here, or in sgg !?

 ****/

package misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import negotiator.AgentID;
import negotiator.DeadlineType;
import negotiator.Global;
import negotiator.NegotiationEventListener;
import negotiator.multipartyprotocol.MultiPartyProtocol;
import negotiator.protocol.Protocol;
import negotiator.protocol.alternatingoffers.AlternatingOffersProtocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.MultiPartyProtocolRepItem;
import negotiator.repository.PartyRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;
import negotiator.tournament.Tournament;
import negotiator.tournament.TournamentConfiguration;
import negotiator.tournament.TournamentRunner;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.tournament.VariablesAndValues.TournamentVariable;

public class Simulator
{
	public static void main(String[] args) throws Exception 
	{
	    
		try 
		{
			// globals
			
			String path 		  =  "file:/Users/rafik/Documents/workspace/NegotiatorGUI voor AAMAS competitie/";
			String ProtoName  =  "negotiator.protocol.alternatingoffers.AlternatingOffersProtocol";
			String outputFile =  "log/_" + Global.getOutcomesFileName();
			boolean trace 	  =  false;
			int ntournaments  =  4;  // number of tournaments
			int nsessions     =  6;  // number of sessions.

			// Loading the agents (classes) from repository
 
			Repository agentrepository       =   Repository.get_agent_repository();
			ArrayList<RepItem> agents_names  =   agentrepository.get_agent_repository().getItems();
	        Repository repAgent              =   Repository.get_agent_repository();
	        AgentRepItem[] agentsARI         =   new AgentRepItem[agents_names.size()];
	        
	        for(int i = 0 ; i < agents_names.size() ; i++)
	        {
	            if((agentsARI[i] = (AgentRepItem) repAgent.getItemByName(agents_names.get(i).getName())) == null)
	                throw new Exception("Unable to create agent " + agents_names.get(i).getName() + "!");
			    
	            if (trace)
		            System.out.println(" Agent " + i + " :  " + agentsARI[i].getName()  
					                    + "\n \t " + agentsARI[i].getDescription() 
					                    + "\n \t " + agentsARI[i].getClassPath()  
					                    + "\n \t " + agentsARI[i].getParams() 
					                    + "\n \t " + agentsARI[i].getVersion()); 
		    }		    
  
			// Loading the preferences' profiles from repository

	        Repository domainrepository        =  Repository.get_domain_repos();
	        ArrayList<RepItem> profiles_names  =  Repository.get_domain_repos().getItems();
	        DomainRepItem[] DomainsARI         =  new DomainRepItem[profiles_names.size()];
	       
	        for(int i = 0 ; i < profiles_names.size() ; i++)
	        {
	            if((DomainsARI[i] = (DomainRepItem) domainrepository.getItemByName(profiles_names.get(i).getName())) == null)
	                throw new Exception("Unable to create domain " + profiles_names.get(i).getName() + "!");
	        
		        if (trace)
		            System.out.println(" Domain " + i + " :  " + DomainsARI[i].getName()  
					                    + "\n \t " + DomainsARI[i].getClass()
					                    + "\n \t " + DomainsARI[i].getFullName()
					                    + "\n \t " + DomainsARI[i].getProfiles()
					                    + "\n \t " + DomainsARI[i].getURL()); 
	    		}
		        
			// Init tournaments

	        Protocol ns;
			Thread[] threads = new Thread[ntournaments];
			
			for ( int i = 1 ; i <= ntournaments ; i++ )
			{
			    // In the following, as an example, we randomly pick two different agents
			    // another way is to try all the combinations...

				String random_A = agentsARI[(new Random()).nextInt(agentsARI.length)].getClassPath(),
					   random_B = new String(random_A);
				
				while (random_B.equals(random_A))
					random_B = agentsARI[(new Random()).nextInt(agentsARI.length)].getClassPath();

				// ...same for the domains: pick two random preferences' profiles.
				
				int rand = (new Random()).nextInt(DomainsARI.length);
				String random_Domain = DomainsARI[rand].getURL().toString();
	            random_Domain = random_Domain.replaceAll("file:", "");  
					
	            System.out.println(" random_Domain : " + random_Domain );  
	            System.out.println(" Profiles : " + DomainsARI[rand].getProfiles() );  
	            
				String domainFile = path + random_Domain; 
				System.out.println(" domainFile: " + domainFile );
				List<String> profiles = Arrays.asList( path + DomainsARI[rand].getProfiles().toArray()[0], //"etc/templates/laptopdomain/laptop_buyer_utility.xml",
													   path + DomainsARI[rand].getProfiles().toArray()[1]);  //"etc/templates/laptopdomain/laptop_seller_utility.xml");
				System.out.println(" profiles:  profile 1 : " + profiles.get(0) + "\n\tprofile 2 : " + profiles.get(1));
				
				List<String> agents = Arrays.asList( random_A, random_B); 
				
				System.out.println(" agents:    agent 1 : " + agents.get(0) + "\n\tagent 2 : " + agents.get(1));

				outputFile = outputFile.replaceAll((i==1) ? ".xml" : "__(\\d+).xml", "__" + i + ".xml");						
				
				File outcomesFile = new File(outputFile);
				BufferedWriter out = new BufferedWriter(new FileWriter(outcomesFile, true));
				if (!outcomesFile.exists())
				{
					System.out.println("Creating log file " + outputFile );
					out.write("<a>\n");
				}
				
				out.close();
				System.out.println(" logfile: " + outputFile );
				Global.logPreset = outputFile;
				
				if (profiles.size() != agents.size())
					throw new IllegalArgumentException("The number of profiles does not match the number of agents!");
				
				ProtocolRepItem protocol        =   new ProtocolRepItem(ProtoName, ProtoName, ProtoName);
				DomainRepItem dom               =   new DomainRepItem(new URL(domainFile));
				ProfileRepItem[] agentProfiles  =   new ProfileRepItem[profiles.size()];

				System.out.println(" protocol name: " + protocol.getDescription() );

				for(int j = 0; j < profiles.size(); j++)
				{
					agentProfiles[j] = new ProfileRepItem(new URL(profiles.get(j)), dom);
					if(agentProfiles[j].getDomain() != agentProfiles[0].getDomain())
						throw new IllegalArgumentException("Profiles for agent 1 and agent " + (j+1) + " do not have the same domain. Please correct your profiles");
				}
				
				AgentRepItem[] agentsrep = new AgentRepItem[agents.size()];
				for(int j = 0; j<agents.size(); j++)
					agentsrep[j] = new AgentRepItem(agents.get(j), agents.get(j), agents.get(j));

//				Protocol ns = Global.createProtocolInstance(protocol, agentsrep, agentProfiles, null);

				TournamentConfiguration.addOption( "deadline",     			     180);
				TournamentConfiguration.addOption( "startingAgent",                0); 
				TournamentConfiguration.addOption( "accessPartnerPreferences",     0); 
				TournamentConfiguration.addOption( "appendModeAndDeadline",        0); 
				TournamentConfiguration.addOption( "disableGUI",                   0); 
				TournamentConfiguration.addOption( "logFinalAccuracy",             0); 
				TournamentConfiguration.addOption( "logNegotiationTrace",          0); 
				TournamentConfiguration.addOption( "allowPausingTimeline",         0); 
				TournamentConfiguration.addOption( "logDetailedAnalysis",          0); 
				TournamentConfiguration.addOption( "logCompetitiveness",           0); 
				TournamentConfiguration.addOption( "startingAgent",                0);
				TournamentConfiguration.addOption( "playAgainstSelf",              0);
				TournamentConfiguration.addOption( "playBothSides",                1);
				TournamentConfiguration.addOption( "oneSidedBidding",              1);	//
				TournamentConfiguration.addOption( "protocolMode",                 1);	//

		        // Constructing ns , and setting the nsessions
		        // {{
				
				java.lang.ClassLoader loader = ClassLoader.getSystemClassLoader();
				Class klass = loader.loadClass(protocol.getClassPath());
				Class[] paramTypes = { AgentRepItem[].class, ProfileRepItem[].class, HashMap[].class, int.class};
				Constructor constructor = klass.getConstructor(paramTypes);
				System.out.println(" \t   constructor : " + constructor);
				Object[] ns_args = {agentsrep,  agentProfiles, null, nsessions};
				Object object = constructor.newInstance(ns_args);
				System.out.println(" \t   object = " + object);
				System.out.println("_________");
				ns = (Protocol) object;

				// }}

				System.out.println(" \t   ns.getName()          = " + ns.getName()  );
				System.out.println(" \t   ns.getSessionNumber() = " + ns.getSessionNumber() );
				System.out.println(" \t   ns.getTotalSessions() = " + ns.getTotalSessions() );
		        

//                Class<Protocol> protocol_ = Global.getProtocolClass(protocol);
//                Class[] paramTypes = {  Tournament.class	 };
//                Method mthdGetTournamentSessions = protocol_.getMethod("getTournamentSessions", paramTypes);
//		        ArrayList<Protocol> sessions = (ArrayList<Protocol>)(mthdGetTournamentSessions.invoke(null, ns ));
//		        System.out.println(" sessions = " + sessions )	;		
//                System.out.println(" > " + TournamentConfiguration.getOptions())	;		
				
				System.out.println("======== Tournament " + i + "/" + ntournaments + " started ========{");
				// Set the tournament.
				//~~~~~ threads[i-1] = new Thread(ns);
				//~~~~~ threads[i-1].start();
				//~~~~~ threads[i-1].join(); // wait until the tournament finishes.
				//~~~~~ System.out.println("Thread " + i + " finished.");

				ns.startSession();
				
					
				System.out.println(" \t   ns.getName()          = " + ns.getName()  );
				System.out.println(" \t   ns.getSessionNumber() = " + ns.getSessionNumber() );
				System.out.println(" \t   ns.getTotalSessions() = " + ns.getTotalSessions() );
				System.out.println("======== Tournament " + i + "/" + ntournaments + " finished ========}");

			} // i

			System.out.println("\n" + ntournaments + " tournaments finished.");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	
	} // end main
	

} // end ScenarioLauncher
