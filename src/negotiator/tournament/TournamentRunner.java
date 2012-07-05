package negotiator.tournament;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import negotiator.Global;
import negotiator.NegotiationEventListener;
import negotiator.distributedtournament.DBController;
import negotiator.distributedtournament.Job;
import negotiator.events.NegotiationSessionEvent;
import negotiator.exceptions.Warning;
import negotiator.gui.NegoGUIApp;
import negotiator.protocol.Protocol;
import negotiator.protocol.alternatingoffers.AlternatingOffersProtocol;
import negotiator.qualitymeasures.TournamentMeasures;

/**
 * TournamentRunner is a class that runs a tournament. It computes all combinations of the sessions from the {@link Tournament}
 * and then runs them sequentially.
 * Use with new Thread(new TournamentRunner(tournament,ael)).start();
 * You can use a null action event listener if you want to.
 */
public class TournamentRunner implements Runnable 
{
	private boolean runSingleSession = false; 
	private List<Protocol> sessions;
	ArrayList<NegotiationEventListener> negotiationEventListeners = new ArrayList<NegotiationEventListener>();
	
	// Options related to distributed tournaments
	private boolean distributed = false;
	private String sessionname = "";
	
	private int previousSession = -1;
	private int currentRunNr = 0;

	/** 
	 * 
	 * @param sessions the sessions to be run
	 * @param ael the action event listener to use. If not null, the existing listener for each
	 * 	session will be overridden with this listener.
	 * @throws Exception
	 */
	public TournamentRunner(List<Protocol> sessions, NegotiationEventListener ael) throws Exception {
		this.sessions = sessions;
		negotiationEventListeners.add(ael);

		if (sessions.size() == 1)
			runSingleSession = true;
	}
	
	/** 
	 * 
	 * @param t the tournament to be run
	 * @param ael the action event listener to use. If not null, the existing listener for each
	 * 	session will be overridden with this listener.
	 * @throws Exception
	 */
	public TournamentRunner(Tournament t,NegotiationEventListener ael) throws Exception {
		sessions = getSessionsFromTournament(t);
		negotiationEventListeners.add(ael);
	}
	
	public TournamentRunner(NegotiationEventListener ael) {
		negotiationEventListeners.add(ael);
	}
	
	/** 
	 * 
	 * @param t the tournament to be run
	 * @param ael the action event listener to use. If not null, the existing listener for each
	 * 	session will be overridden with this listener.
	 * @throws Exception
	 */
	public TournamentRunner(Tournament t,NegotiationEventListener ael, boolean runSingleSession) throws Exception {
		this(t, ael);
		this.runSingleSession = runSingleSession;
	}
	
	/**
	 * Enable distributed tournaments; which entails that a new job
	 * is loaded when jobs are still available.
	 * @param distributed
	 */
	public void setDistributed(boolean distributed, String sessionname) {
		this.distributed = distributed;
		this.sessionname = sessionname;
	}
	
	/**
	 * Warning. You can call run() directly (instead of using Thread.start() )
	 * but be aware that run() will not return until the tournament
	 * has completed. That means that your interface will lock up until the tournament is complete.
	 * And if any negosession uses modal interfaces, this will lock up swing, because modal
	 * interfaces will not launch until the other swing interfaces have handled their events.
	 * (at least this is my current understanding, Wouter, 22aug08).
	 * See "java dialog deadlock" on the web...
	 */
	public void run() {
		try {
			String log = "";
			if (distributed) {
				log = runDistributedTournament(0);
				NegoGUIApp.negoGUIView.getFrame().setVisible(true);
			} else {
				log = runTournament();
			}

			if (!Global.DISABLE_NORMAL_LOG) {
				if (Global.EXTENSIVE_OUTCOMES_LOG)
					AlternatingOffersProtocol.closeLog(true);
				AlternatingOffersProtocol.closeLog(false);
				
			}
			TournamentMeasures.process(log, Global.getTournamentOutcomeFileName());
			
			if (distributed) {
				JOptionPane.showMessageDialog(null, "Finished jobs of session: \"" + sessionname + "\".\nThe log is stored in the log directory.");
			}
			
			if (NegoGUIApp.getOptions().quitWhenTournamentDone)
			{
				System.out.println("Auto-quitting after the tournament is done.");
				System.exit(0);
			}
		} 
		catch (Exception e) { e.printStackTrace(); new Warning("Fatal error cancelled tournament run:"+e); }
	}

	private String runTournament() throws InterruptedException {
		for (int i = 0; i < sessions.size(); i++) {

			
			int runNr = determineRunNr(sessions.get(i));
			previousSession = sessions.get(i).hashCode();
			Protocol s = sessions.get(i);
			s.setRun(runNr);
			synchronized(this) { 
				for (NegotiationEventListener list: negotiationEventListeners) s.addNegotiationEventListener(list);
				s.setTournamentRunner(this);
				s.startSession(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
				wait();
			}
			
			if(!Global.REMEMBER_PREVIOUS_SESSIONS) {
				sessions.set(i, null);
				System.gc();
				System.gc();
				System.gc();
			}
		}
		return Global.getOutcomesFileName();
	}
	
	private int determineRunNr(Protocol protocol) {
		if (protocol.hashCode() == previousSession) {
			currentRunNr++;
		} else {
			currentRunNr = 0;
		}
		return currentRunNr;
	}

	private String runDistributedTournament(int previousOpenSessions) throws InterruptedException {
		Job job;
		int jobID = DBController.getInstance().getJobID(sessionname);

		// 1. If there is a job existing with the given sessionname (always after START, should be after JOIN)
		if (jobID > 0) {
			ArrayList<Protocol> sessions = new ArrayList<Protocol>();
			try {
				sessions = DBController.getInstance().getTournament(jobID).getSessions();
			} catch (Exception e) {
				e.printStackTrace();
			}
		// 2. Check if there are groups of sessions left to be processed
			job = DBController.getInstance().getJob(jobID, sessions);
			while (job != null) {
				StringBuilder builder = new StringBuilder();
				// 3. Run the matches and store the outcomes
				for (Protocol s: job.getSessions()) {
					int runNr = determineRunNr(s);
					s.setRun(runNr);
					synchronized(this) { 
						for (NegotiationEventListener list: negotiationEventListeners) s.addNegotiationEventListener(list);	
						s.setTournamentRunner(this);
						s.startSession();
						wait();
						
						// 4. Store outcomes of the job. Should be done here due to the implementation of logging
						if (s instanceof AlternatingOffersProtocol) {
							AlternatingOffersProtocol as = (AlternatingOffersProtocol) s;
							builder.append(as.getOutcome());
						}
						if(!Global.REMEMBER_PREVIOUS_SESSIONS) {
							sessions.set(sessions.indexOf(s), null);
							System.gc();
							System.gc();
							System.gc();
						}
					}
				}
				// 5. Store the outcome of the job and mark it done
				DBController.getInstance().storeResult(job.getSessionID(), builder.toString());
				job = DBController.getInstance().getJob(jobID, sessions);
			}
			// 6. Check for outstanding sessions. If outstanding sessions are detected, wait
			int openSessions = DBController.getInstance().getRunningSessions(jobID);
			if (openSessions > 0) {
				// as 1 computer can only claim 1 session, we just wait a full Job plus some extra time for DB storage
				
				int waitMS = DBController.getInstance().getMatchesPerSession(jobID) * 180000 + 5000;
				
				System.out.println("DT: waiting " + waitMS + " for " + openSessions + " tournaments to complete");
				Thread.sleep(waitMS);
	
				// If there are still outstanding jobs, this means that a pc did not complete its task
				// (or the DB takes a very long time (> 5 seconds) to store a result, but this is very unlikely)
				int nextOpenSessions = DBController.getInstance().getRunningSessions(jobID);
				
				// if the number of open sessions is equal to the number before the waiting periode, then
				// none of the clients acted and we can safely reset the jobs.
				if (openSessions == nextOpenSessions) {
					System.out.println("DT: Seems a computer crashed while processing a job. Remaining jobs are reset.");
					DBController.getInstance().resetJobs(jobID);
					runDistributedTournament(openSessions);
				} else {
					// the amount of open sessions changed; meaning that somebody completed a job (very unlikely) or
					// reset the tournament.
					System.out.println("DT: Another client acted during the waiting period. Resuming distributed tournament.");
					runDistributedTournament(openSessions);
				}
			} else {
				// 7. Retrieve the log from the database and save it
				DBController.getInstance().reconstructLog(jobID);
				DBController.getInstance().close();
			}
		} else {
			JOptionPane.showMessageDialog(null, "No job has been scheduled under the given sessionname.");
		}
		return Global.getDistributedOutcomesFileName();
	}

	private List<Protocol> getSessionsFromTournament(Tournament t) throws Exception
	{
		ArrayList<Protocol> sessions;
		if(runSingleSession) {
			sessions = new ArrayList<Protocol>();
			sessions.add(t.getSessions().get(0));
		}
		else {
			sessions=t.getSessions();
		}
		return sessions;
	}
	
	public void fireNegotiationSessionEvent(Protocol session ) {
		for(NegotiationEventListener listener :  negotiationEventListeners) 
			if(listener!=null)listener.handeNegotiationSessionEvent(new NegotiationSessionEvent(this,session));
	}
}