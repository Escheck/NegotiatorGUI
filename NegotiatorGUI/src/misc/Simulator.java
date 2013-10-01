/*************************************************************************************************************
 * Simulator for the optimal bidding experiments.
 * 
 *	 - Generates all the combinations agents/profiles and runs trials
 * 	 - For each trial it stores the log in log/_log
 *     E.g., for two agents and 3 trials at 2013-09-26 18.58.25, the result will be generated 
 *     and stored in 3 log files: 2013-09-26 18.58.25__i.xml, with i=1,2,3.
 * 
 * 	 - Before running Simulator we first need to run SGG to generate the profiles
 *	
 *  TODO Fix the number of rounds (X) in a session !
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
			
			String path 		  =  "file:/Users/rafik/Documents/workspace/NegotiatorGUI voor AAMAS competitie/",
				   ProtoName  =  "negotiator.protocol.alternatingoffers.AlternatingOffersProtocol",
				   outputFile =  "log/_" + Global.getOutcomesFileName();
			boolean  trace    =  false, 
					 first    =  true,
					 all      =  true; // if == true, totaltrials==number of all possible combinations
			int nsessions     =   6,   // number of sessions.
				trial         =   1,   // number of trials ~ tournaments
            		totaltrials   =  10; 

			// Trials' variables
	        
            Protocol ns = null;
			Set<Set<RepItem>> AgentsCombinations = null;
			Iterator<Set<RepItem>> combination = null;
			List<String> profiles = null;

			// Loading the agents (classes) from repository

	        Repository repAgent              =   Repository.get_agent_repository();
			ArrayList<RepItem> agents_names  =   repAgent.get_agent_repository().getItems();
	        ArrayList<String> UIbasedAgents  =   new ArrayList<String>(Arrays.asList("UIAgent Extended", "UIAgent", "TAgent"));
	        for ( String s : UIbasedAgents )					// removing UI-based and experimental agents.
    	            for(int i = 0 ; i < agents_names.size() ; i++)
    	            		if (s.equals(agents_names.get(i).toString()))
    	            			agents_names.remove(i);
	    	    AgentRepItem[] agentsARI         =   new AgentRepItem[agents_names.size()];
	        for(int i = 0 ; i < agentsARI.length ; i++)
	        {
	        		if((agentsARI[i] = (AgentRepItem) repAgent.getItemByName(agents_names.get(i).getName())) == null)
		           throw new Exception("Unable to create agent " + agents_names.get(i).getName() + "!");
		        if (trace)
			       System.out.println(" Agent #" + i +  " : \n \t agent name     :  " + agentsARI[i].getName()  
														 + "\n \t descr class    :  " + agentsARI[i].getDescription()
														 + "\n \t class path     :  " + agentsARI[i].getClassPath()
														 + "\n \t param profiles :  " + agentsARI[i].getParams()
														 + "\n \t version        :  " + agentsARI[i].getVersion()); 
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

            System.out.println("\n Total [Agents] combinations  : " + AgentsCombinations.size() + 
            					  "\n Total [Preferences] profiles : " + profiles_names.size()); 
            // }}
            
            System.out.println("=========== runs ======================================================================================================================================================");

 //####### trials ################################################
            
            if (all) // all combinations
        			totaltrials = AgentsCombinations.size() * profiles_names.size();

            Thread[] threads = new Thread[totaltrials];

			for ( DomainRepItem domain : DomainsARI  )
			{
				combination = AgentsCombinations.iterator();
						
				while (combination.hasNext()) 
				{
					System.out.println("======== Trial " + trial + "/" + " started ==========={");

					String domainFile = path + domain.getURL().toString().replaceAll("file:", ""); 
					System.out.println(" domainFile: " + domainFile );

					profiles = Arrays.asList( path + domain.getProfiles().toArray()[0], path + domain.getProfiles().toArray()[1]);  
					System.out.println(" profiles:  profile 1 : " + profiles.get(0) + "\n\tprofile 2 : " + profiles.get(1));

					String AClassPath = combination.next().toArray()[0].toString(), 
						   BClassPath = combination.next().toArray()[1].toString();
					
			        for (int d=0; d<agentsARI.length; d++)
			        {
				        	if (agentsARI[d].getName().equals(AClassPath))
				        			AClassPath = agentsARI[d].getClassPath();
				        	else if (agentsARI[d].getName().equals(BClassPath))
			      				BClassPath = agentsARI[d].getClassPath();
			        	}
					
					List<String> agents = Arrays.asList( AClassPath, BClassPath); 
					System.out.println(" agents:    agent 1 : " + agents.get(0) + "\n\tagent 2 : " + agents.get(1));
					
					if (first) 
					{
						   outputFile = outputFile.replaceAll(".xml",   "__" + trial + ".xml"); 
						   first = false;
					}
					else outputFile = outputFile.replaceAll("__(\\d+).xml", "__" + trial + ".xml");

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
	
					System.out.print("Loading options..\n");

					TournamentConfiguration.addOption(  "deadline",     			      180 );
					TournamentConfiguration.addOption(  "startingAgent",                0  ); 
					TournamentConfiguration.addOption(  "accessPartnerPreferences",     0  ); 
					TournamentConfiguration.addOption(  "appendModeAndDeadline",        0  ); 
					TournamentConfiguration.addOption(  "disableGUI",                   0  ); 
					TournamentConfiguration.addOption(  "logFinalAccuracy",             0  ); 
					TournamentConfiguration.addOption(  "logNegotiationTrace",          0  ); 
					TournamentConfiguration.addOption(  "allowPausingTimeline",         0  ); 
					TournamentConfiguration.addOption(  "logDetailedAnalysis",          0  ); 
					TournamentConfiguration.addOption(  "logCompetitiveness",           0  ); 
					TournamentConfiguration.addOption(  "startingAgent",                0  );
					TournamentConfiguration.addOption(  "playAgainstSelf",              0  );
					TournamentConfiguration.addOption(  "playBothSides",                1  );
					TournamentConfiguration.addOption(  "oneSidedBidding",              1  );	//
					TournamentConfiguration.addOption(  "protocolMode",                 1  );	//
	
					if (false) // method 1
					{
							ns = Global.createProtocolInstance(protocol, agentsrep, agentProfiles, null);
							System.out.print("Negotiation session built: " + ns + "\n");
							ns.startSession();

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
							
							
							// Set the tournament.
							threads[trial-1] = new Thread(ns);
							threads[trial-1].start();
							System.out.println("Thread " + trial + " started");
							threads[trial-1].join(); // wait until the tournament finishes
							System.out.println("Thread " + trial + " finished");
					}

					System.out.println(" \t   ns.getName()          = " + ns.getName()  );
					System.out.println(" \t   ns.getSessionNumber() = " + ns.getSessionNumber() );
					System.out.println(" \t   ns.getTotalSessions() = " + ns.getTotalSessions() );
					System.out.println("======== Trial " + trial + "/" + totaltrials + " finished ========} \n");
				
					trial++;
					
					if (trial == totaltrials && all==false) 
					{
						System.out.println("\n" + trial + "/" + totaltrials + " trials finished.");
						System.exit(0);
					}
					
				} // combination
			} // domain	

			System.out.println("\n" + trial + " trials finished from " + AgentsCombinations.size() * profiles_names.size() + " combinations");

		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	} // end main
	
} // end Simulator
