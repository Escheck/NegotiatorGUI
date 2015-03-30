package negotiator.qualitymeasures;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import misc.Pair;
import negotiator.exceptions.Warning;

/**
 * Class used to write the results of quality measures to a csv-
 * formatted file.
 * 
 * @author Mark Hendrikx
 */
public class CSVlogger {
	
	private String path;
	private String agentA;
	private String agentB;
	private String spaceA;
	private String spaceB;
	private String LINE_SEPARATOR = System.getProperty("line.separator");
	private ArrayList<Pair<String, ArrayList<Double>>> dataToLog;
	private String domain;
	
	/**
	 * Creates a CSV logger
	 * @param path where the file should be stored (included extension)
	 * @param agentA name of agent A
	 * @param spaceA utility space of agent A
	 * @param agentB name of agent B
	 * @param spaceB utility space of agent B
	 */
	public CSVlogger(String path, String agentA, String spaceA, String agentB, String spaceB, String domain) {
		this.path = path;
		dataToLog = new ArrayList<Pair<String, ArrayList<Double>>>();
		this.agentA = agentA.replaceAll(",", " ");
		this.agentB = agentB.replaceAll(",", " ");
		this.spaceA = spaceA;
		this.spaceB = spaceB;
		this.domain = domain;
	}
	
	public CSVlogger(String path, Trace trace) {
		this.path = path;
		dataToLog = new ArrayList<Pair<String, ArrayList<Double>>>();
		this.agentA = trace.getAgent().replaceAll(",", " ");
		this.agentB = trace.getOpponent().replaceAll(",", " ");
		this.spaceA = trace.getAgentProfile();
		this.spaceB = trace.getOpponentProfile();
		this.domain = trace.getDomain();
	}

	/**
	 * Add an array with the results of a particular quality measures
	 * to the set of arrays which should be printed
	 * 
	 * @param name of the quality measure
	 * @param results of the quality measure
	 */
	public void addMeasure(String name, ArrayList<Double> results) {
		if (results.size() > 0) {
			dataToLog.add(new Pair<String, ArrayList<Double>>(name, results));
		}
	}
	
	/**
	 * Writes the quality measures to a compact log.
	 */
	public void writeToFileCompact(double timeOfAgreement, boolean agreement, int runNr) {
		// 1. check if there is information to be written. It is assumed that each
		// quality measure has the same amount of values
		if (dataToLog.size() > 0 && dataToLog.get(0).getSecond().size() > 0) {
			try {
				// 2. createFrom a writer
				BufferedWriter out = new BufferedWriter(new FileWriter(path, true));
				
				// 3. store general information to distinguish a particular match
				out.write(domain + LINE_SEPARATOR);
				out.write(agentA.trim() + LINE_SEPARATOR);
				out.write(spaceA.trim() + LINE_SEPARATOR);
				out.write(agentB.trim() + LINE_SEPARATOR);
				out.write(spaceB.trim() + LINE_SEPARATOR);
				out.write(timeOfAgreement + " " + LINE_SEPARATOR);
				out.write(agreement + LINE_SEPARATOR);
				out.write(runNr + LINE_SEPARATOR);
	
				// 4. write the names of each quality measure
				String namesLine = "";
				for (int i = 0; i < dataToLog.size() - 1; i++) {
					namesLine += (dataToLog.get(i).getFirst() + ",");
				}
				namesLine += (dataToLog.get(dataToLog.size() - 1).getFirst());
				out.write(namesLine + LINE_SEPARATOR);
				
				// 5. write the values of each quality measure
				for (int i = 0; i < dataToLog.get(0).getSecond().size(); i++) {
					String line = "";
					for (int b = 0; b < dataToLog.size() - 1; b++) {
						line += (dataToLog.get(b).getSecond().get(i) + ",");
					}
					line += (dataToLog.get(dataToLog.size() - 1).getSecond().get(i));
					out.write(line + LINE_SEPARATOR);
				}
				out.close();
			} catch (Exception e) {
				new Warning("Exception during writing s:"+e);
				e.printStackTrace();
			}
		} else {
			System.out.println("No data to log");
		}
	}
	
	/**
	 * Writes the quality measures to a pivot-table compatible log.
	 * Information is duplicated many times, and therefore this format should only
	 * be used as final output.
	 * @param firstEntry 
	 */
	public void writeToFilePivotCompatible(int runNr, double sampleSpeed, boolean firstEntry) {
		// 1. check if there is information to be written. It is assumed that each
		// quality measure has the same amount of values
		if (dataToLog.size() > 0 && dataToLog.get(0).getSecond().size() > 0) {
			try {
				// 2. createFrom a writer
				BufferedWriter out = new BufferedWriter(new FileWriter(path, true));
				
				// 3. store general information to distinguish a particular match
				String[] domainInfo = domain.split("/");
				domain = domainInfo[domainInfo.length - 1];
				String[] spaceAInfo = spaceA.split("/");
				spaceA = spaceAInfo[spaceAInfo.length - 1];
				String[] spaceBInfo = spaceB.split("/");
				spaceB = spaceBInfo[spaceBInfo.length - 1];
				
				// 4. write the names of each quality measure
				if (firstEntry) {
					String namesLine = "domain,agentA,spaceA,agentB,spaceB,run,time,";
					for (int i = 0; i < dataToLog.size() - 1; i++) {
						namesLine += (dataToLog.get(i).getFirst() + ",");
					}
					namesLine += (dataToLog.get(dataToLog.size() - 1).getFirst());
					out.write(namesLine + LINE_SEPARATOR);
				}

				// 5. write the values of each quality measure
				String extraInfo = domain + "," + agentA + "," + spaceA + "," + agentB + "," + spaceB + "," + runNr + ",";
				int currentSample = 0;
				for (int i = 0; i < dataToLog.get(0).getSecond().size(); i++) {
					String time = (currentSample * sampleSpeed) + "000";
					currentSample++;
					String line = extraInfo + time.substring(0, 4) + ",";
					for (int b = 0; b < dataToLog.size() - 1; b++) {
						line += (dataToLog.get(b).getSecond().get(i) + ",");
					}
					line += (dataToLog.get(dataToLog.size() - 1).getSecond().get(i));
					out.write(line + LINE_SEPARATOR);
				}
				out.flush();
				out.close();
			} catch (Exception e) {
				new Warning("Exception during writing s:"+e);
				e.printStackTrace();
			}
		} else {
			System.out.println("No data to log");
		}
	}
}