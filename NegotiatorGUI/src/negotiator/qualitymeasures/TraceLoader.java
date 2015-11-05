package negotiator.qualitymeasures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import negotiator.Bid;
import negotiator.Domain;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.utility.AdditiveUtilitySpace;

/**
 * Simple parser which is designed to load CSV files containing the trace of the opponent and possibly
 * a the results of various quality measures. New quality measures can be easily added without having
 * to change the parser.
 * 
 * @author Mark Hendrikx
 */
public class TraceLoader {
	private enum Mode { DOMAIN, AGENTA, PREFPROFA, AGENTB, PREFPROFB, TIME, AGREEMENT, RUNNUMBER, HEADER, DATA }
	private Mode mode;
	private Trace currentTrace;
	private Domain currentDomain;
	private SortedOutcomeSpace currentOutcomeSpace;
	private AdditiveUtilitySpace myCurrentUtilSpace;
	
	public ArrayList<Trace> loadTraces(String mainDir, String logPath) {
		ArrayList<Trace> traces = null;
		try {	
			traces = process(mainDir, logPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return traces;
	}
	
	public ArrayList<Trace> process(String mainDir, String logPath) throws Exception {
		ArrayList<Trace> traces = new ArrayList<Trace>();
		BufferedReader parser = new BufferedReader(new FileReader(mainDir + "/" + logPath));
		mode = Mode.DOMAIN;
		String line = "";
		while ((line = parser.readLine()) != null) {
			
			if (mode.equals(Mode.DATA) && !Character.isDigit(line.charAt(0))) {
				mode = Mode.DOMAIN;
				traces.add(currentTrace);
				if (traces.size() % 10 == 0) {
					System.out.println("Loaded " + traces.size() + " traces");
				}
			}
			switch (mode) {
				case DOMAIN:
					currentTrace = new Trace();
					currentTrace.setDomain(line);
					currentDomain = new Domain(mainDir + "/" + currentTrace.getDomain());
					mode = Mode.AGENTA;
		        	break;
				case AGENTA:
					currentTrace.setAgent(line);
					mode = Mode.PREFPROFA;
		        	break;
				case PREFPROFA:
					currentTrace.setAgentProfile(line);
					myCurrentUtilSpace = new AdditiveUtilitySpace(currentDomain, mainDir + "/" + currentTrace.getAgentProfile());
					mode = Mode.AGENTB;
					break;
				case AGENTB:
					currentTrace.setOpponent(line);
					mode = Mode.PREFPROFB;
					break;
				case PREFPROFB:
					currentTrace.setOpponentProfile(line);
					AdditiveUtilitySpace opponentSpace = new AdditiveUtilitySpace(currentDomain, mainDir + "/" + currentTrace.getOpponentProfile());
					currentOutcomeSpace = new SortedOutcomeSpace(opponentSpace);
					mode = Mode.TIME;
					break;
				case TIME:
					currentTrace.setEndOfNegotiation(Double.parseDouble(line));
					mode = Mode.AGREEMENT;
					break;
				case AGREEMENT:
					currentTrace.setAgreement(line.equals("true"));
					mode = Mode.RUNNUMBER;
					break;
				case RUNNUMBER:
					currentTrace.setRunNumber(Integer.parseInt(line));
					mode = Mode.HEADER;
					break;
				case HEADER:
					parseHeader(currentTrace, line);
					mode = Mode.DATA;
					break;
				case DATA:
					parseData(currentTrace, line);
					break;
				default:
					break;
			}
		}
		if (currentTrace != null) {
			traces.add(currentTrace);
		}
		parser.close();
		return traces;
	}

	private void parseHeader(Trace trace, String line) {
		String[] headers = line.split(",");
		HashMap<Integer, String> legend = new HashMap<Integer, String>();
		for (int i = 0; i < headers.length; i++) {
			legend.put(i, headers[i]);
		}
		trace.setLegend(legend);
	}

	private void parseData(Trace trace, String line) {
		String[] data = line.split(",");
		double time = -1;
		int bidIndex = -1;
		for (int i = 0; i < data.length; i++) {
			String type = trace.getLegend().get(i);
			double value = Double.parseDouble(data[i]);
			if (type.equals("time")) {
				time = value;
			} else if (type.equals("bidindices")) {
				bidIndex = (int)value;
			} else {
				trace.getData().get(type).add(value);
			}
		}
		if (time >= 0 && bidIndex >= 0) {
			addBid(trace, time, bidIndex);
		} else {
			System.err.println("No bid found on this line; the data is in an incorrect format");
		}
	}
	
	private void addBid(Trace trace, double time, int bidIndex) {
		Bid bid = currentOutcomeSpace.getAllOutcomes().get(bidIndex).getBid();
		try {
			currentTrace.addBid(bidIndex, bid, myCurrentUtilSpace.getUtility(bid), time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}