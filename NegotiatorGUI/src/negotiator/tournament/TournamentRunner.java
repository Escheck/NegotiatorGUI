package negotiator.tournament;

import java.util.ArrayList;
import negotiator.exceptions.Warning;

/**
 * TournamentRunner is a class that runs a tournament.
 * Use with new Thread(new TournamentRunner(tournament)).start();
 */
public class TournamentRunner implements Runnable {
    Tournament tournament;
	
    public TournamentRunner(Tournament t) throws Exception {
    	tournament=t;
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
    		ArrayList<NegotiationSession2> sessions=tournament.getSessions();
			for (NegotiationSession2 s: sessions) {
				s.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
			}
    	} catch (Exception e) { new Warning("Fatail error cancelled tournament run:"+e); }
    }

}
