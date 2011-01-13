package negotiator.tournament;

import java.util.ArrayList;

import negotiator.Global;
import negotiator.NegotiationEventListener;
import negotiator.events.NegotiationSessionEvent;
import negotiator.exceptions.Warning;
import negotiator.gui.NegoGUIApp;
import negotiator.protocol.Protocol;
import negotiator.protocol.alternatingoffers.AlternatingOffersProtocol;

/**
 * TournamentRunner is a class that runs a tournament.
 * Use with new Thread(new TournamentRunner(tournament,ael)).start();
 * You can use a null action event listener if you want to.
 */
public class TournamentRunner implements Runnable {
    Tournament tournament;
    private boolean runSingleSession = false; 
    ArrayList<NegotiationEventListener> negotiationEventListeners = new ArrayList<NegotiationEventListener>();
	
    /** 
     * 
     * @param t the tournament to be run
     * @param ael the action event listener to use. If not null, the existing listener for each
     * 	session will be overridden with this listener.
     * @throws Exception
     */
    public TournamentRunner(Tournament t,NegotiationEventListener ael) throws Exception {
    	tournament=t;
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
    	this(t,ael);
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
    public void run() {
    	ArrayList<Protocol> sessions =null;
    	try { 
    		if(runSingleSession) {
    			sessions = new ArrayList<Protocol>();
    			sessions.add(tournament.getSessions().get(0));
    		}
    		else
    			sessions=tournament.getSessions();
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
    	} catch (Exception e) { e.printStackTrace(); new Warning("Fatal error cancelled tournament run:"+e); }
    	
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
    
    public void fireNegotiationSessionEvent(Protocol session ) {
    	for(NegotiationEventListener listener :  negotiationEventListeners) 
    		if(listener!=null)listener.handeNegotiationSessionEvent(new NegotiationSessionEvent(this,session));
    }

}
