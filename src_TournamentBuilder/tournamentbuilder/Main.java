/*
 * Main.java
 *
 * Created on 16 январь 2007 г., 20:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tournamentbuilder;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import negotiator.xml.*;

/**
 *
 * @author Dmytro Tykhonov
 * @author W.Pasman (modified 10dec07)
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
	
	static String RESULTSFILE="tournamentresults.xml";
	static String SCRIPTFILE="run_tournament.bat";
	static String COMPETITORFILE="src_TournamentBuilder/competition.xml";
	static String TOURNAMENTXMLDIR="tournamentxml";
	static String TOURNAMENTXMLPATH="etc/templates/"+TOURNAMENTXMLDIR;
	static String NEGOTEMPLATE="party"; 
		// leave out the ".xml" here, we use this for name generation too.
		// this xml file should be in the tournamentbuilder directory.
		// it is modified version of the domain template, prepared for the tournament.
	
    public static void main(String[] args) {
        // TODO code application logic here
    	 Boolean status = (new File(TOURNAMENTXMLDIR)).mkdir();
    	 if (status) System.out.print("created ");
    	 else System.out.print("reusing the existing ");
    	 System.out.println(TOURNAMENTXMLDIR+" directory");
    	 loadGroups(COMPETITORFILE);
    }
    
    
    public static void loadGroups(String fileName) {
        SimpleDOMParser parser = new SimpleDOMParser();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(SCRIPTFILE,false));
            out.write("if (-f "+RESULTSFILE+") echo \"warning: file "+RESULTSFILE+" already exists. appending\"\n");
            out.write("echo '<?xml-stylesheet type=\"text/xsl\" href=\"outcomes.xsl\"?>' >>"+RESULTSFILE+"\n");
            out.write("echo \"<Tournament>\" >>"+RESULTSFILE+"\n");
            
            BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));                  
            SimpleElement root = parser.parse(file);
            Object[] groups = root.getChildElements();
            for(int i=0; i<groups.length;i++) {
                SimpleElement group = (SimpleElement)(groups[i]);
                String agentAname = ((SimpleElement)(group.getChildByTagName("agent")[0])).getAttribute("name");
                String agentAclassname = ((SimpleElement)(group.getChildByTagName("agent")[0])).getAttribute("classname");
                for(int j=i;j<groups.length;j++) {

                    SimpleElement vsGroup = (SimpleElement)(groups[j]);
                    String agentBname = ((SimpleElement)(vsGroup.getChildByTagName("agent")[0])).getAttribute("name");
                    String agentBclassname = ((SimpleElement)(vsGroup.getChildByTagName("agent")[0])).getAttribute("classname");
//                    if (!(agentBclassname.equals("negotiation.group16.CurzonAgent")||(agentAclassname.equals("negotiation.group16.CurzonAgent")))) continue; 

                    if (i!=j) {                    
                        makeTemplate("src_TournamentBuilder/tournamentbuilder/"+NEGOTEMPLATE+".xml", 
                                     TOURNAMENTXMLPATH+"/"+NEGOTEMPLATE+"_"+agentAname+"_vs_"+agentBname+".xml",
                                     agentAclassname,
                                     agentBclassname);
           
                        //out.write("java -cp bin;. negotiator.Main \"etc/templates/tournament/ampo_vs_city_" + agentAname+"_vs_"+agentBname +".xml\" -b >output/ampo_vs_city_" + agentAname+"_vs_"+agentBname+".txt \n");
                        out.write("java -jar negosimulator.jar \"etc/templates/tournament/ampo_vs_city_" + agentAname+"_vs_"+agentBname +".xml\" -b >output/ampo_vs_city_" + agentAname+"_vs_"+agentBname+".txt -b\n");
                        out.write("cat outcomes.xml >>"+RESULTSFILE+"\n");
                        out.write("rm outcomes.xml\n");
                    }
                    /*
                    makeTemplate(NEGOTEMPLATE+".xml", 
                                 TOURNAMENTXMLPATH+"/"+NEGOTEMPLATE+"_"+agentBname+"_vs_"+agentAname+".xml",
                                 agentBclassname,
                                 agentAclassname);
					*/
            
                    out.write("java -cp negosimulator.jar;. negotiator.Main \"etc/templates/tournament/ampo_vs_city_" + agentBname+"_vs_"+agentAname +".xml\" -b >output/ampo_vs_city_" + agentBname+"_vs_"+agentAname+".txt \n");
                    
                }
            }
            out.write("echo \"</Tournament>\" >>"+RESULTSFILE+"\n");
            out.close();            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    
    private static void makeTemplate(String templateSourceName, String templateDestName, String agentAname, String agentBname) {
        SimpleDOMParser parser = new SimpleDOMParser();
        try {
            FileInputStream fis = new FileInputStream(templateSourceName);
            int x= fis.available();
            byte b[]= new byte[x];
            fis.read(b);
            String content = new String(b);
            String c1 = substitute(content, "@agentA", agentAname);
            String c2 = substitute(c1, "@agentB", agentBname);
            java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(new File(templateDestName)));
            out.print(c2);
            out.close();                
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public static String substitute( String name, String src, String dest ) {
        if( name == null || src == null || name.length() == 0 ) {
          return name;
        }

        if( dest == null ) {
          dest = "";
        }        
        int index = name.indexOf( src );
        if( index == -1 ) {
            return name;
        }

        StringBuffer buf = new StringBuffer();
        int lastIndex = 0;
        while( index != -1 ) {
            buf.append( name.substring( lastIndex, index ) );
            buf.append( dest );
            lastIndex = index + src.length();
            index = name.indexOf( src, lastIndex );
        }
        buf.append( name.substring( lastIndex ) );
        return buf.toString();
      }
    
}
