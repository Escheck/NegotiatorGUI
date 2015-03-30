package negotiator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import negotiator.protocol.OldProtocol;
import negotiator.repository.AgentRepItem;
import negotiator.repository.DomainRepItem;
import negotiator.repository.ProfileRepItem;
import negotiator.repository.ProtocolRepItem;
import negotiator.repository.RepItem;
import negotiator.repository.Repository;
import negotiator.tournament.Tournament;
import negotiator.tournament.VariablesAndValues.AgentParamValue;
import negotiator.tournament.VariablesAndValues.AgentParameterVariable;
import negotiator.tournament.VariablesAndValues.AgentValue;
import negotiator.tournament.VariablesAndValues.AgentVariable;
import negotiator.tournament.VariablesAndValues.ProfileValue;
import negotiator.tournament.VariablesAndValues.ProfileVariable;
import negotiator.tournament.VariablesAndValues.ProtocolValue;
import negotiator.tournament.VariablesAndValues.ProtocolVariable;
import negotiator.tournament.VariablesAndValues.TotalSessionNumberValue;
import negotiator.tournament.VariablesAndValues.TotalSessionNumberVariable;

/**
 *
 * @author Liviu
 */
public class CSVLoader
{
    ArrayList<OldProtocol> sessions = new ArrayList<OldProtocol>();
    String filePath = "";
    
    public CSVLoader(String csvFilePath) throws Exception
    {
        filePath = csvFilePath;
        
        try
        {
            FileReader fr = new FileReader(csvFilePath);
            BufferedReader br = new BufferedReader(fr);

            String line;

            //read each line from script file
            while((line = br.readLine()) != null)
            {
                line = line.trim();
                String[] tokens = line.split("\t");

                //ignore commented line
                if((tokens.length == 0)||(line.startsWith(";")))
                {
                    //in case the line br an comment
                    continue;
                }

                if(tokens.length < 5)
                {
                    System.out.println("Invalid line " + line + " parsed as " + Arrays.toString(tokens));
                    break;
                }

                String protocol = tokens[0].trim();
                String domain = tokens[1].trim();
                String[] agentsA = tokens[2].split(",");
                String[] agentsB = tokens[3].split(",");
                String[] profiles = tokens[4].split(",");
                
                ArrayList<HashMap<String, String>> agentParamsA = new ArrayList();
                // add one parrameters hashmap for each agent at side A
                for(int i = 0; i < agentsA.length; i++)
                {
                    agentParamsA.add(new HashMap<String, String>());
                }
                
                ArrayList<HashMap<String, String>> agentParamsB = new ArrayList();
                // add one parrameters hashmap for each agent at side B
                for(int i = 0; i < agentsB.length; i++)
                {
                    agentParamsB.add(new HashMap<String, String>());
                }
                
                if(tokens.length > 6)
                {
                    String[] allAgentAParams = tokens[5].split(",");
                    //for each agent's parameters (separated by ,)
                    for(int i = 0; i < allAgentAParams.length; i++)
                    {
                        String[] stringAgentParams = allAgentAParams[i].split(";"); 
                        //for each parameter of a certain agent separated by ;
                        for(int j = 0; j < stringAgentParams.length; j++)
                        {
                            String keyValuePair[] = stringAgentParams[j].split("=");
                            if(keyValuePair.length > 2)
                                throw new Exception("Syntax error in agent parameters list!");
                            agentParamsA.get(i).put(keyValuePair[0], keyValuePair[1]);
                        }
                    }
                    
                    String[] allAgentBParams = tokens[6].split(",");
                    //for each agent's parameters (separated by ,)
                    for(int i = 0; i < allAgentBParams.length; i++)
                    {
                        String[] stringAgentParams = allAgentBParams[i].split(";"); 
                        //for each parameter of a certain agent separated by ;
                        for(int j = 0; j < stringAgentParams.length; j++)
                        {
                            String keyValuePair[] = stringAgentParams[j].split("=");
                            if(keyValuePair.length > 2)
                                throw new Exception("Syntax error in agent parameters list!");
                            agentParamsB.get(i).put(keyValuePair[0], keyValuePair[1]);
                        }
                    }
                }
                
                HashMap<String, String> params = new HashMap<String, String>();
                
                if(tokens.length > 7)
                {
                    String[] stringParams = tokens[7].split(";");
                    for(int i = 0; i < stringParams.length; i++)
                    {
                        String keyValuePair[] = stringParams[i].split("=");
                        if(keyValuePair.length > 2)
                            throw new Exception("Syntax error in parameters list!");
                        params.put(keyValuePair[0], keyValuePair[1]);
                    }
                }
                
                loadSessions(protocol, domain, agentsA , agentsB, profiles, agentParamsA, agentParamsB, params);
            }
            
            br.close();
            fr.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new Exception("Batch file error!");
        }
    }
    
    public ArrayList<OldProtocol> getSessions()
    {
        return sessions;
    }
    
    public String getFilePath()
    {
        return filePath;
    }
    
    private void loadSessions(String protocol, String domain, String agentsA[], String agentsB[], String profiles[], ArrayList<HashMap<String, String>> agentParamsA, ArrayList<HashMap<String, String>> agentParamsB, HashMap<String, String> parameters) throws Exception
    {
        //load protocol
        Repository repProtocol = Repository.getProtocolRepository();
        ProtocolRepItem protocolRI = (ProtocolRepItem) repProtocol.getItemByName(protocol);
        repProtocol = null;

        if(protocolRI == null)
        {
            throw new Exception("Unable to createFrom protocol: " + protocol);
        }

        //load domain
        Repository repDomain = Repository.get_domain_repos();
        RepItem domainRI = repDomain.getItemByName(domain);
        repDomain = null;

        if(domainRI == null)
        {
            throw new Exception("Unable to find domain: " + domain);
        }

        //load agents for side A
        Repository repAgent = Repository.get_agent_repository();
        AgentRepItem[] agentsARI = new AgentRepItem[agentsA.length];
        for(int i = 0; i < agentsA.length; i++)
        {
            agentsARI[i] = (AgentRepItem) repAgent.getItemByName(agentsA[i]);
            if(agentsARI[i] == null)
            {
                throw new Exception("Unable to createFrom agent " + agentsA[i] + "!");
            }
        }

        ////load agents for side B
        AgentRepItem[] agentsBRI = new AgentRepItem[agentsB.length];
        for(int i = 0; i < agentsB.length; i++)
        {
            agentsBRI[i] = (AgentRepItem) repAgent.getItemByName(agentsB[i]);
            if(agentsBRI[i] == null)
            {
                throw new Exception("Unable to createFrom agent " + agentsB[i] + "!");
            }
        }

        // load profiles (this is global, not for both sides separately)
        ArrayList<ProfileRepItem> profileArray = ((DomainRepItem)domainRI).getProfiles();
        ProfileRepItem[] profilesRI = new ProfileRepItem[profiles.length];
        for(int i = 0; i < profiles.length; i++)
        {
            for(ProfileRepItem prf: profileArray)
            {
                if(prf.getName().equals(profiles[i]))
                    profilesRI[i] = prf;
            }

            if(profilesRI[i] == null)
            {
                throw new Exception("Unable to createFrom profile: " + profiles[i]);
            }
         }

         //load agent parameters for side A
         HashMap<AgentParameterVariable, AgentParamValue>[] agentParamsAp = new HashMap[agentsARI.length];
         for(int i = 0; i < agentsARI.length; i++)
         {
             agentParamsAp[i] = new HashMap<AgentParameterVariable, AgentParamValue>();
         }

         //in case of multi-negotiation line
         if(parameters.get("tournament") != null)
         {
            loadMultipleSessions(protocolRI, agentsARI, agentsBRI, profilesRI, agentParamsA, agentParamsB, parameters);
         }
         else //only for one negotiation
         {
             loadOneSession(protocolRI, agentsARI, agentsBRI, profilesRI, agentParamsA, agentParamsB, parameters);
         }
    }
    
    private void loadMultipleSessions(ProtocolRepItem protocol, AgentRepItem agentsA[], AgentRepItem agentsB[], ProfileRepItem profiles[], ArrayList<HashMap<String, String>> agentParamsA, ArrayList<HashMap<String, String>> agentParamsB, HashMap<String, String> parameters) throws Exception
    {
        //get tournament related arguments
        String[] tournamentParams = parameters.get("tournament").split(",");
        //remove tournament parameter when createFrom protocol
        parameters.remove("tournament");
            
        //parse tournament arguments list and extract options
        boolean selfplay = false;
        boolean play_both_sides = false;
        HashSet<Integer> partiesHash = new HashSet<Integer>();
            
        for(int i = 0; i < tournamentParams.length; i++)            
        {
            if(Character.isDigit(tournamentParams[i].charAt(0)))
            {
                String[] intervals = tournamentParams[i].split("-");
                int n1 = Integer.parseInt(intervals[0]), n2;
                switch(intervals.length)
                {
                    case 1: //is only a number
                        partiesHash.add(n1);
                        break;
                    case 2: //is an inteval
                        n1 = Integer.parseInt(intervals[0]);
                        n2 = Integer.parseInt(intervals[1]);
                        if(n1 > n2)
                            throw new Exception("In tournament property the left side of the interval must be smaller than the right one!");                           
                                                      
                        for(int j = n1; j <= n2; j++)
                            partiesHash.add(j);
                        break;
                    default: //error
                            
                        break;
                }
            }
            if(tournamentParams[i].equals("all_combinations"))
            {
                partiesHash.add(2);
            }
            if(tournamentParams[i].equals("selfplay"))
                selfplay = true;
            else if(tournamentParams[i].equals("play_both_sides"))
            {
                play_both_sides = true;
            }
        }
        //end parsing tournament parameters
        
        //createFrom tournament object
        Tournament t = new Tournament();
        ProtocolVariable protocolVariable = new ProtocolVariable();
        protocolVariable.addValue(new ProtocolValue(protocol));
        t.getVariables().add(protocolVariable);
                
        AgentVariable agentVariableA = new AgentVariable();
        agentVariableA.setSide("A");
        for(AgentRepItem aRI : agentsA)
        {
            agentVariableA.addValue(new AgentValue(aRI));
        }
        t.getVariables().add(agentVariableA);
                
        ProfileVariable profileVariable = new ProfileVariable();
        for(ProfileRepItem pRI : profiles)
        {
            profileVariable.addValue(new ProfileValue(pRI));
        }
        t.getVariables().add(profileVariable);
                
        AgentVariable agentVariableB = new AgentVariable();
        agentVariableB.setSide("B");
        for(AgentRepItem aRI : agentsB)
        {
            agentVariableB.addValue(new AgentValue(aRI));  
        }
        t.getVariables().add(agentVariableB);
             
        TotalSessionNumberVariable totalVariable = new TotalSessionNumberVariable();
        if(parameters.containsKey("total_sessions"))
            totalVariable.addValue(new TotalSessionNumberValue(Integer.parseInt(parameters.get("total_sessions"))));
        else
            totalVariable.addValue(new TotalSessionNumberValue());
        t.getVariables().add(totalVariable);

        sessions.addAll(t.getSessions());
    }
    
    private void loadOneSession(ProtocolRepItem protocol, AgentRepItem agentsA[], AgentRepItem agentsB[], ProfileRepItem profiles[], ArrayList<HashMap<String, String>> agentParamsA, ArrayList<HashMap<String, String>> agentParamsB, HashMap<String, String> parameters) throws Exception
    {
        //createFrom the new list of agents - concatenate agentsA and agentsB lists
        int totalNumberOfAgents = agentsA.length + agentsB.length;
        AgentRepItem[] agentsRI = new AgentRepItem[totalNumberOfAgents];
        System.arraycopy(agentsA, 0, agentsRI, 0, agentsA.length);
        System.arraycopy(agentsB, 0, agentsRI, agentsA.length, agentsB.length);
            
        HashMap<AgentParameterVariable, AgentParamValue>[] agentParamsp = new HashMap[totalNumberOfAgents];
        for(int i = 0; i < agentParamsp.length; i++)
        {
            agentParamsp[i] = new HashMap<AgentParameterVariable, AgentParamValue>();
        }
           
        if(profiles.length != agentsRI.length)
        {
            throw new Exception("Invalid file - non-equal number of profiles and agents");
        }

        // Try createFrom the protocol instance
        try
        {
            OldProtocol ns = Global.createProtocolInstance(protocol, agentsRI, profiles, agentParamsp);
            sessions.add(ns);
        }
        catch(Exception e)
        {
            throw new Exception("Cannot createFrom protocol!");
        }
    }
}
