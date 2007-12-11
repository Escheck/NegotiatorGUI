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
import java.util.Random;

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
	static String NEGOTEMPLATE="party"; 			// ROOT will be prepended where necessary. ".xml" will be added at end
	static int LOW_UTIL_NR=11;
	static int HIGH_UTIL_NR=40;    // util files are named <NEGOTEMPLATE>_<XX>_utility.xml
									// with XX a number between LOW_UTIL_NR and HIGH_UTIL_NR inclusive.
		
	
	static String ROOT="src_TournamentBuilder/";	// root dir of the tournament builder.
	static String RESULTSFILE="tournamentresults.xml";
	static String SCRIPTFILE=ROOT+"run_tournament.bat";
	static String COMPETITORFILE=ROOT+"groups.xml";
	static String TOURNAMENTXMLDIR="tournamentxml"; // ROOT will be prepended where necessary. 
		// leave out the ".xml" here, we use this for name generation too.
		// this xml file should be in the src_Tournament directory.
		// it is modified version of the domain template, prepared for the tournament.
	static String UTILDIR="utility/";
	
    public static void main(String[] args) {
        // TODO code application logic here
    	 Boolean status = (new File(ROOT+TOURNAMENTXMLDIR)).mkdir();
    	 if (status) System.out.print("created ");
    	 else System.out.print("reusing the existing ");
    	 System.out.println(TOURNAMENTXMLDIR+" directory");
    	 loadGroups(COMPETITORFILE);
    }
    
    /*
     * Eindeloos geprutst met de class paths.
     * Uiteindelijk , voor onbekende reden zoekt java de utility/... files in de tournamentxml dir???
     * Verder, de agents kunnen niet via group2.YourAgent e.d. worden aangeroepen,
     * omdat de YourAgent niet in package group2 zit maar in root.
     * Dit zuigt wederom, betekent dat we alle class paths goed zouden moeten zetten. En
     * dan nog kunnen we conflicten krijgen. package XXX toevoegen en Hercompileren lijkt geen goed plan.
     * Dan maar alles in root dir dumpen???
     * 
     * 
     */
    public static void loadGroups(String fileName) {
        SimpleDOMParser parser = new SimpleDOMParser();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(SCRIPTFILE,false));
            out.write("#!/bin/tcsh\n");
            out.write("if (-f "+RESULTSFILE+") then \necho \"warning: file "+RESULTSFILE+" already exists. appending\"\nendif\n");
            out.write("echo '<?xml-stylesheet type=\"text/xsl\" href=\"outcomes.xsl\"?>' >>"+RESULTSFILE+"\n");
            out.write("echo \"<Tournament>\" >>"+RESULTSFILE+"\n");
            
            BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));                  
            SimpleElement root = parser.parse(file);
            Object[] groups = root.getChildElements();
            for(int i=0; i<groups.length-1;i++) {
                SimpleElement group = (SimpleElement)(groups[i]);
                String agentAname = ((SimpleElement)(group.getChildByTagName("agent")[0])).getAttribute("name");
                String agentAclassname = ((SimpleElement)(group.getChildByTagName("agent")[0])).getAttribute("classname");
                for(int j=i+1;j<groups.length;j++) {
                	if (i==j) continue; // just for sure. agent can't play against itself.
                	
                    SimpleElement vsGroup = (SimpleElement)(groups[j]);
                    String agentBname = ((SimpleElement)(vsGroup.getChildByTagName("agent")[0])).getAttribute("name");
                    String agentBclassname = ((SimpleElement)(vsGroup.getChildByTagName("agent")[0])).getAttribute("classname");

       
                	// each agt plays 5 times against an opponent, but with different util spaces.
                	// note, currently it CAN NOT learn because it needs restart to load another util space.
                    for (int round=0; round<5; round++) 
                    {
                        // pick random util spaces
                        int autil=LOW_UTIL_NR+new Random().nextInt(HIGH_UTIL_NR-LOW_UTIL_NR+1); // +1 because nextInt excludes the given value.
                        int butil;
                        do {
                        	butil=LOW_UTIL_NR+new Random().nextInt(HIGH_UTIL_NR-LOW_UTIL_NR+1); // +1 because nextInt excludes the given value.
                        } while (butil==autil);

		            	String instnegotemplate=TOURNAMENTXMLDIR+"/"+NEGOTEMPLATE+"_"+agentAname+"_vs_"+agentBname+"_"+round+".xml";
		                makeTemplate(ROOT+NEGOTEMPLATE+".xml",ROOT+instnegotemplate,
		                             agentAname,agentAclassname,UTILDIR+NEGOTEMPLATE+"_"+autil+"_utility.xml",
		                             agentBname,agentBclassname,UTILDIR+NEGOTEMPLATE+"_"+butil+"_utility.xml");
		   
		                //out.write("java -cp bin;. negotiator.Main \"etc/templates/tournament/ampo_vs_city_" + agentAname+"_vs_"+agentBname +".xml\" -b >output/ampo_vs_city_" + agentAname+"_vs_"+agentBname+".txt \n");
		                out.write("echo \"starting "+instnegotemplate+"\"\n");
		                out.write("java -jar negosimulator.jar \""+instnegotemplate+"\" -b\n");
		                out.write("cat outcomes.xml >>"+RESULTSFILE+"\n");
		                out.write("rm outcomes.xml\n");
                    }
                }
            }
            out.write("echo \"</Tournament>\" >>"+RESULTSFILE+"\n");
            out.close();            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    
    private static void makeTemplate(String templateSourceName, String templateDestName, 
    		String agentAname, String agentAClassName,String agentAUtility, 
    		String agentBname,String agentBClassName, String agentBUtility) {
        SimpleDOMParser parser = new SimpleDOMParser();
        try {
            FileInputStream fis = new FileInputStream(templateSourceName);
            int x= fis.available();
            byte b[]= new byte[x];
            fis.read(b);
            String content = new String(b);
            String c1 = substitute(content, "@agentA", agentAname);
            String c2 = substitute(c1, "@agentClassA", agentAClassName);
            String c3 = substitute(c2, "@utilityA", agentAUtility);
            String c4 = substitute(c3, "@agentB", agentBname);
            String c5 = substitute(c4, "@agentClassB", agentBClassName);
            String c6 = substitute(c5, "@utilityB", agentBUtility);

            java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(new File(templateDestName)));
            out.print(c6);
            out.close();                
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Replace in name all occurences of src with dest.
     * @param name string in which stuff has to be replaced
     * @param src what to replace. Only fixed string, no patterns.
     * @param dest every occurence of src is replaed with this.
     * @return new string with replacements.
     */
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
        boolean replaced=false;
        while( index != -1 ) {
            buf.append( name.substring( lastIndex, index ) );
            buf.append( dest );
            lastIndex = index + src.length();
            index = name.indexOf( src, lastIndex );
            replaced=true;
        }
        if (!replaced) System.out.println("warning: replace failed, no occurences of "+src+" in "+name);
        buf.append( name.substring( lastIndex ) );
        return buf.toString();
      }
    
}
