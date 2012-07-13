package negotiator.qualitymeasures;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import negotiator.Domain;
import negotiator.analysis.BidPoint;
import negotiator.analysis.BidSpace;
import negotiator.exceptions.Warning;
import negotiator.utility.UtilitySpace;

public class SingleScenarioMeasures {


	private static Domain domain;
	private static String LINE_SEPARATOR = System.getProperty("line.separator");
	static String outputLocation;
	
	
	
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String domainLocation = args[0];
		String profileALocation = args[1];
		String profileBLocation = args[2];
		outputLocation = args[3];
		
		/*
		String domainLocation = "D:/etc/2010/EnglandZimbabwe/EnglandZimbabwe_domain.xml";
		String profileALocation = "D:/etc/2010/EnglandZimbabwe/England.xml";
		String profileBLocation = "D:/etc/2010/EnglandZimbabwe/Zimbabwe.xml";
		*/
		/*
		String domainLocation = "D:/etc/2010/Travel/travel_domain.xml";
		String profileALocation = "D:/etc/2010/Travel/travel_chox.xml";
		String profileBLocation = "D:/etc/2010/Travel/travel_fanny.xml";
		*/
		
		domain = new Domain(domainLocation);
		UtilitySpace utilSpaceA = new UtilitySpace(domain, profileALocation);
		UtilitySpace utilSpaceB = new UtilitySpace(domain, profileBLocation);
		BidSpace bidSpace = new BidSpace(utilSpaceA, utilSpaceB);
		process(bidSpace);
	}
	
	public static void process(BidSpace bidSpace) throws Exception {
		String[] domainNameSplit = domain.getName().split("/");
		String domainName = domainNameSplit[domainNameSplit.length - 1];
		System.out.println("domain: " + domainName);


		
		writeCSVtoFile(outputLocation + domainName + "-AllBids.csv", bidSpace.bidPoints);
		writeCSVtoFile(outputLocation  + domainName + "-ParetoBids.csv", bidSpace.getParetoFrontier());



		
		System.out.println("Finished Analyzing Domain");
		
	}
	
	private static void writeCSVtoFile(String logPath, ArrayList<BidPoint> list) {
		try {
			File log = new File(logPath);
			if (log.exists()) {
				log.delete();
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(log, true));
			out.write("Utility_A;Utility_B" + LINE_SEPARATOR);
			for(BidPoint bidPoint : list){
				out.write(bidPoint.utilityA + ";" + bidPoint.utilityB + LINE_SEPARATOR);
			}
			
			out.close();
		} catch (Exception e) {
			new Warning("Exception during writing s:" + e);
			e.printStackTrace();
		}
	}
}
