package negotiator.tournament;

import java.util.ArrayList;
import java.util.List;

import negotiator.Global;
import negotiator.NegotiationEventListener;
import negotiator.events.NegotiationSessionEvent;
import negotiator.exceptions.Warning;
import negotiator.gui.NegoGUIApp;
import negotiator.protocol.Protocol;
import negotiator.protocol.alternatingoffers.AlternatingOffersProtocol;

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
	 * Warning. You can call run() directly (instead of using Thread.start() )
	 * but be aware that run() will not return until the tournament
	 * has completed. That means that your interface will lock up until the tournament is complete.
	 * And if any negosession uses modal interfaces, this will lock up swing, because modal
	 * interfaces will not launch until the other swing interfaces have handled their events.
	 * (at least this is my current understanding, Wouter, 22aug08).
	 * See "java dialog deadlock" on the web...
	 */
	public void run() 
	{
		try { 
			for (Protocol s: sessions) 
			{
				System.out.println("Starting negotiation " + s);
				//if (the_event_listener!=null) s.actionEventListener=the_event_listener;
				synchronized(this) { 
					for (NegotiationEventListener list: negotiationEventListeners) s.addNegotiationEventListener(list);				
					//fireNegotiationSessionEvent(s);
					s.setTournamentRunner(this);
					s.startSession(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
					wait();					
				}
			}

			System.out.println("Done with " + sessions.size() + " sessions.");
			if (Global.EXTENSIVE_OUTCOMES_LOG)
				AlternatingOffersProtocol.closeLog(true);
			AlternatingOffersProtocol.closeLog(false);

			if (NegoGUIApp.getOptions().quitWhenTournamentDone)
			{
				System.out.println("Auto-quitting after the tournament is done.");
				System.exit(0);
			}
		} 
		catch (Exception e) { e.printStackTrace(); new Warning("Fatal error cancelled tournament run:"+e); }
	}

	private List<Protocol> getSessionsFromTournament(Tournament t) throws Exception
	{
		ArrayList<Protocol> sessions;
		if(runSingleSession) {
			sessions = new ArrayList<Protocol>();
			sessions.add(t.getSessions().get(0));
		}
		else
			sessions=t.getSessions();
		return sessions;
	}

	public void fireNegotiationSessionEvent(Protocol session ) {
		for(NegotiationEventListener listener :  negotiationEventListeners) 
			if(listener!=null)listener.handeNegotiationSessionEvent(new NegotiationSessionEvent(this,session));
	}

}
