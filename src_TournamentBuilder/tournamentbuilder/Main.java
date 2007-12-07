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
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        loadGroups("src_TournamentBuilder/competition.xml");
    }
    public static void loadGroups(String fileName) {
        SimpleDOMParser parser = new SimpleDOMParser();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("run_tournament.bat"));
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
                        makeTemplate("etc/templates/ampo_vs_city_template.xml", 
                                     "etc/templates/tournament/ampo_vs_city_"+agentAname+"_vs_"+agentBname+".xml",
                                     agentAclassname,
                                     agentBclassname);
           
                        out.write("java -cp bin;. negotiator.Main \"etc/templates/tournament/ampo_vs_city_" + agentAname+"_vs_"+agentBname +".xml\" -b >output/ampo_vs_city_" + agentAname+"_vs_"+agentBname+".txt \n");
                    }
                   
                    makeTemplate("etc/templates/ampo_vs_city_template.xml", 
                                 "etc/templates/tournament/ampo_vs_city_"+agentBname+"_vs_"+agentAname+".xml",
                                 agentBclassname,
                                 agentAclassname);

            
                    out.write("java -cp bin;. negotiator.Main \"etc/templates/tournament/ampo_vs_city_" + agentBname+"_vs_"+agentAname +".xml\" -b >output/ampo_vs_city_" + agentBname+"_vs_"+agentAname+".txt \n");
                    
                }
            }
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
