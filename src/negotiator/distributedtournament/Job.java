package negotiator.distributedtournament;

import java.util.List;

import negotiator.protocol.Protocol;

/**
 * Describes a job: a partial tournament.
 * 
 * @author Mark Hendrikx
 * @version 17-12-11
 */
public class Job {

	// ID of the high-level job in the DB
	private int jobID;
	// ID of the low-level job in the DB
	private int sessionID;
	// Sessions to be ran for this Job
	private List<Protocol> sessions;

	public Job(int jobID, int sessionID, List<Protocol> sessions) {
		this.jobID = jobID;
		this.sessionID = sessionID;
		this.sessions = sessions;
	}

	public int getJobID() {
		return jobID;
	}

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}
	
	public int getSessionID() {
		return sessionID;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	public List<Protocol> getSessions() {
		return sessions;
	}

	public void setSessions(List<Protocol> sessions) {
		this.sessions = sessions;
	}
}
