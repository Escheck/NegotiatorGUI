/**
 * Simulator for the optimal bidding experiments.
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

package misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import negotiator.Global;
import negotiator.protocol.Protocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;
import negotiator.tournament.TournamentConfiguration;

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
			int ntournaments  =  1;  // number of tournaments
			int nsessions     =  6;  // number of sessions.
			boolean trace 	  =  false, first = true;
	     
			// tournaments variables
	        Protocol ns = null;
			Set<Set<RepItem>> AgentsCombinations = null;
			Iterator<Set<RepItem>> combination = null;
			List<String> profiles = null;
	        Thread[] threads = new Thread[ntournaments];

			// Loading the agents (classes) from repository

	        Repository repAgent              =   Repository.get_agent_repository();
			ArrayList<RepItem> agents_names  =   repAgent.get_agent_repository().getItems();
	        ArrayList<String> UIbasedAgents  =   new ArrayList<String>(Arrays.asList("UIAgent Extended", "UIAgent"));
	        for ( String s : UIbasedAgents )					// removing the UI-based agents..
    	            for(int i = 0 ; i < agents_names.size() ; i++)
    	            		if (s.equals(agents_names.get(i).toString()))
    	            			agents_names.remove(i);
	    	    AgentRepItem[] agentsARI         =   new AgentRepItem[agents_names.size()];
	        for(int i = 0 ; i < agentsARI.length ; i++)
	        {
	        		if((agentsARI[i] = (AgentRepItem) repAgent.getItemByName(agents_names.get(i).getName())) == null)
		           throw new Exception("Unable to create agent " + agents_names.get(i).getName() + "!");
		        if (trace)
			      System.out.println(" Agent " + i + ":  " + agentsARI[i].getName() + "\n \t " + agentsARI[i].getDescription()  + "\n \t " + agentsARI[i].getClassPath() + "\n \t " + agentsARI[i].getParams()       + "\n \t " + agentsARI[i].getVersion()); 
	        }		   
	          		
	        // Loading the preferences' profiles from repository
	       
	        Repository domainrepository        =  Repository.get_domain_repos();
	        ArrayList<RepItem> profiles_names  =  domainrepository.getItems();
	        DomainRepItem[] DomainsARI         =  new DomainRepItem[profiles_names.size()];
	        for(int i = 0 ; i < profiles_names.size() ; i++)
	        {
	        		if((DomainsARI[i] = (DomainRepItem) domainrepository.getItemByName(profiles_names.get(i).getName())) == null)
	               throw new Exception("Unable to create domain " + profiles_names.get(i).getName() + "!");
		        if (trace)
		           System.out.println(" Domain #" + i + "\n \t Domain name     :  " + DomainsARI[i].getName()  
		        		   								 + "\n \t Domain class    :  " + DomainsARI[i].getClass() 
		        		   								 + "\n \t Domain fullname :  " + DomainsARI[i].getFullName() 
		        		   								 + "\n \t Domain profiles :  " + DomainsARI[i].getProfiles() 
		        		   								 + "\n \t Domain URL      :  " + DomainsARI[i].getURL()); 
	    		}
	        
	        //  We can either pick two different agents randomly or try all the combinations
			// {{
	 	    	Set<RepItem> NamesSet = new HashSet<RepItem>();
		    	Iterator<RepItem> iter = agents_names.iterator();
		    while (iter.hasNext())  	NamesSet.add(iter.next());
			AgentsCombinations = SetTools.cartesianProduct(NamesSet, NamesSet);
            combination = AgentsCombinations.iterator();
            while (combination.hasNext())  // removing singletons
            		if ( combination.next().size() != 2)	  combination.remove();
            if (trace)
            {
               Iterator<Set<RepItem>> i = AgentsCombinations.iterator();
		       while (i.hasNext())  System.out.println(" > " + i.next() );
            }

            System.out.println(" Total [Agents] combinations  : " + AgentsCombinations.size() + 
            					  "\n Total [Preferences] profiles : " + profiles_names.size()); 
            // }}
            
 //####### tournament(s) ################################################
            
            int domcounter = 1;
			for ( int i = 1 ; i <= ntournaments ; i++ )
			{
				for ( DomainRepItem domain : DomainsARI  )
				{
					combination = AgentsCombinations.iterator();
							
					while (combination.hasNext()) 
					{
						
						if ( domcounter == 1000 ) break;
						
						String domainFile = path + domain.getURL().toString().replaceAll("file:", ""); 
						System.out.println(" domainFile: " + domainFile );

						profiles = Arrays.asList( path + domain.getProfiles().toArray()[0], path + domain.getProfiles().toArray()[1]);  
						System.out.println(" profiles:  profile 1 : " + profiles.get(0) + "\n\tprofile 2 : " + profiles.get(1));
						List<String> agents = Arrays.asList( combination.next().toArray()[0].toString(), combination.next().toArray()[1].toString()); 
						System.out.println(" agents:    agent 1 : " + agents.get(0) + "\n\tagent 2 : " + agents.get(1));

						if (first) {
							   outputFile = outputFile.replaceAll(".xml",   "__" + i + ".xml"); 
							   first = false;
						} else outputFile = outputFile.replaceAll("__(\\d+).xml", "__" + i + ".xml");

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
		
						if (true) // metho 1
						{
								ns = Global.createProtocolInstance(protocol, agentsrep, agentProfiles, null);
						}
						else
						{
						        // Constructing ns , and setting the nsessions
						        // {{
										java.lang.ClassLoader loader = ClassLoader.getSystemClassLoader();
										Class klass = loader.loadClass(protocol.getClassPath());
										Class[] paramTypes = { AgentRepItem[].class, ProfileRepItem[].class, HashMap[].class, int.class};
										Constructor constructor = klass.getConstructor(paramTypes);
										System.out.println(" \t   constructor : " + constructor);
										Object[] ns_args = {agentsrep,  agentProfiles, null, nsessions};
										Object object = constructor.newInstance(ns_args);
										System.out.println(" \t   object = " + object + "\n_________");
										ns = (Protocol) object;
								// }}
				
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
						}
						System.out.println("==============================================================================  " + domcounter + "  ================ " + AgentsCombinations.size() * profiles_names.size()); 

						domcounter++;
						if (1+3+3+4+4>0) continue;
						
						ns.startSession();
							
						System.out.println(" \t   ns.getName()          = " + ns.getName()  );
						System.out.println(" \t   ns.getSessionNumber() = " + ns.getSessionNumber() );
						System.out.println(" \t   ns.getTotalSessions() = " + ns.getTotalSessions() );
						System.out.println("======== Tournament " + i + "/" + ntournaments + " finished ========}");
					
					} // combination
				} // domain	
			} // i

			System.out.println("\n" + ntournaments + " tournaments finished.");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	} // end main
	
} // end Simulator
