package negotiator.qualitymeasures;

import java.util.ArrayList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

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
	public static void main(String[] args) throws Exception 
	{
		String domainLocation;
		String profileALocation;
		String profileBLocation;
		
//		domainLocation = args[0];
//		profileALocation = args[1];
//		profileBLocation = args[2];
//		outputLocation = args[3];
		
		
//		String path = "E:/ANAC original resources/etc/2010/EnglandZimbabwe/";
//		domainLocation = path + "EnglandZimbabwe_domain.xml";
//		profileALocation = path + "England.xml";
//		profileBLocation = path + "Zimbabwe.xml";
		
//		String path = "E:/ANAC original resources/etc/2010/ItexvsCypress/";
//		domainLocation = path + "ItexvsCypress_domain.xml";
//		profileALocation = path + "ItexvsCypress_Itex.xml";
//		profileBLocation = path + "ItexvsCypress_Cypress.xml";
		
//		String path = "E:/ANAC original resources/etc/2010/Thompson/";
//		domainLocation = path + "thompson_employment.xml";
//		profileALocation = path + "thompson_employer_utilityspace.xml";
//		profileBLocation = path + "thompson_employee_utilityspace.xml";
		
//		String path = "E:/ANAC original resources/etc/2010/Travel/";
//		domainLocation = path + "travel_domain.xml";
//		profileALocation = path + "travel_fanny.xml";
//		profileBLocation = path + "travel_chox.xml";
		
//		String path = "E:/ANAC original resources/etc/2011/final/Grocery/";
//		domainLocation = path + "grocery_domain.xml";
//		profileALocation = path + "grocery_domain_sam.xml";
//		profileBLocation = path + "grocery_domain_mary.xml";
		
		String path = "E:/ANAC original resources/etc/2011/final/Laptop/";
		domainLocation = path + "laptop_domain.xml";
		profileALocation = path + "laptop_buyer_utility.xml";
		profileBLocation = path + "laptop_seller_utility.xml";
		outputLocation = path;
		
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
