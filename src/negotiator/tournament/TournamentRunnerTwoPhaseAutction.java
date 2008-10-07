package negotiator.tournament;

import java.util.ArrayList;

import negotiator.Agent;
import negotiator.NegotiationEventListener;
import negotiator.exceptions.Warning;

public class TournamentRunnerTwoPhaseAutction extends TournamentRunner {

	@Override
	public void run() {
		// TODO Auto-generated method stub
	   	try { 
    		ArrayList<NegotiationSession2> sessions=tournament.getSessions();
			for (NegotiationSession2 s: sessions) {
				//if (the_event_listener!=null) s.actionEventListener=the_event_listener;
				for (NegotiationEventListener list: negotiationEventListeners) s.addNegotiationEventListener(list);
				fireNegotiationSessionEvent(s);
				s.run(); // note, we can do this because TournamentRunner has no relation with AWT or Swing.
				
			}
			//determine winner
			double lMaxUtil;
			double lSecondPrice;
			Agent winner = null;
			for (NegotiationSession2 s: sessions) {
				s.getSessionRunner().getNegotiationOutcome().
				
			}
			NegotiationSession2 secondPhaseSession = new NegotiationSession2();
					
    	} catch (Exception e) { e.printStackTrace(); new Warning("Fatail error cancelled tournament run:"+e); }
  
	}

}
