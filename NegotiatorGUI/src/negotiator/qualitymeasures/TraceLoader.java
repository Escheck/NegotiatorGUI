package negotiator.qualitymeasures;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import negotiator.Bid;
import negotiator.Domain;
import negotiator.boaframework.SortedOutcomeSpace;
import negotiator.utility.UtilitySpace;

/**
 * Simple parser which is designed to load CSV files containing the trace of the opponent.
 * This type of log can be easily made by disabling the opponent model measures, or using an agent
 * without opponent model.
 * 
 * @author Mark Hendrikx
 */
public class TraceLoader {

	private ArrayList<Trace> traces = new ArrayList<Trace>();
	private enum Mode { DOMAIN, AGENTA, PREFPROFA, AGENTB, PREFPROFB, TIME, AGREEMENT, RUNNUMBER, HEADER, DATA }
	private Mode mode;
	private Trace currentTrace;
	private Domain currentDomain;
	private SortedOutcomeSpace currentOutcomeSpace;
	private UtilitySpace myCurrentUtilSpace;
	
	public ArrayList<Trace> loadTraces(String mainDir, String logPath) {
		try {	
			process(mainDir, logPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return traces;
	}
	
	public void process(String mainDir, String logPath) throws Exception {
		BufferedReader parser = new BufferedReader(new FileReader(mainDir + "/" + logPath));
		mode = Mode.DOMAIN;
		String line = "";
		while ((line = parser.readLine()) != null) {
			
			if (mode.equals(Mode.DATA) && !Character.isDigit(line.charAt(0))) {
				mode = Mode.DOMAIN;
				traces.add(currentTrace);
			}
			switch (mode) {
				case DOMAIN:
					currentTrace = new Trace();
					currentTrace.setDomain(mainDir + "/" + line);
					currentDomain = new Domain(currentTrace.getDomain());
					mode = Mode.AGENTA;
		        	break;
				case AGENTA:
					currentTrace.setAgent(line);
					mode = Mode.PREFPROFA;
		        	break;
				case PREFPROFA:
					currentTrace.setAgentProfile(mainDir + "/" + line);
					myCurrentUtilSpace = new UtilitySpace(currentDomain, currentTrace.getAgentProfile());
					mode = Mode.AGENTB;
					break;
				case AGENTB:
					currentTrace.setOpponent(line);
					mode = Mode.PREFPROFB;
					break;
				case PREFPROFB:
					currentTrace.setOpponentProfile(mainDir + "/" + line);
					UtilitySpace opponentSpace = new UtilitySpace(currentDomain, currentTrace.getOpponentProfile());
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
					mode = Mode.DATA;
					break;
				case DATA:
					parseBid(currentTrace, line);
					break;
				default:
					break;
			}
		}
		if (currentTrace != null) {
			traces.add(currentTrace);
		}
	}

	private void parseBid(Trace trace, String line) {
		String[] split = line.split(",");
		double time = Double.parseDouble(split[0]);
		int bidIndex = (int) Double.parseDouble(split[1]);
		Bid bid = currentOutcomeSpace.getAllOutcomes().get(bidIndex).getBid();
		try {
			currentTrace.addBid(bid, myCurrentUtilSpace.getUtility(bid), time);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}