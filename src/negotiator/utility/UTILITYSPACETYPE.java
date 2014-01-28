package negotiator.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;

public enum UTILITYSPACETYPE 
{
		LINEAR,NONLINEAR;
	
	public static UTILITYSPACETYPE getUtilitySpaceType(String filename){
		
		try {
			SimpleDOMParser parser = new SimpleDOMParser();
	        BufferedReader file = new BufferedReader(new FileReader(new File(filename)));                  
	        SimpleElement root = parser.parse(file);
	        if (root.getAttribute("type").equals("nonlinear"))
	        	return NONLINEAR;
	        else return LINEAR;
		} catch(Exception e) {
			return null;
		}
	}
}
