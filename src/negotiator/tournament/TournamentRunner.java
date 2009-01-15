package negotiator.tournament;

import java.util.ArrayList;

import negotiator.events.NegotiationSessionEvent;
import negotiator.exceptions.Warning;
import negotiator.protocol.MetaProtocol;
import negotiator.protocol.alternatingoffers.AlternatingOffersNegotiationSession;
import negotiator.NegotiationEventListener;

/**
 * TournamentRunner is a class that runs a tournament.
 * Use with new Thread(new TournamentRunner(tournament,ael)).start();
 * You can use a null action event listener if you want to.
 */
public class TournamentRunner implements Runnable {
    Tournament tournament;
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
    		ArrayList<AlternatingOffersNegotiationSession> sessions=tournament.getSessions();
			for (AlternatingOffersNegotiationSession s: sessions) {
				//if (the_event_listener!=null) s.actionEventListener=the_event_listener;
				for (NegotiationEventListener list: negotiationEventListeners) s.addNegotiationEventListener(list);				
				//fireNegotiationSessionEvent(s);
				s.setTournamentRunner(this);
				s.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
				
			}
    	} catch (Exception e) { e.printStackTrace(); new Warning("Fatail error cancelled tournament run:"+e); }
    }
    
    public void fireNegotiationSessionEvent(MetaProtocol session ) {
    	for(NegotiationEventListener listener :  negotiationEventListeners) 
    		if(listener!=null)listener.handeNegotiationSessionEvent(new NegotiationSessionEvent(this,session));
    }

}
