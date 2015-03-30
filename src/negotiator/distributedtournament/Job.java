package negotiator.distributedtournament;

import java.util.List;
import negotiator.protocol.OldProtocol;

/**
 * Describes a job: a partial tournament.
 * 
 * @author Mark Hendrikx
 * @version 17-12-11
 */
public class Job {

	/** ID of the high-level job in the DB */
	private int jobID;
	/** ID of the low-level job in the DB. A job consists of multiple sessions. */
	private int sessionID;
	/** Sessions to be ran for this Job */
	private List<OldProtocol> sessions;

	/**
	 * Store the information 
	 * @param jobID ID of the main job.
	 * @param sessionID ID of the particular set of sessions of this job.
	 * @param sessions sessions to be processed as part of this sessionID.
	 */
	public Job(int jobID, int sessionID, List<OldProtocol> sessions) {
		this.jobID = jobID;
		this.sessionID = sessionID;
		this.sessions = sessions;
	}

	/**
	 * @return ID of the high-level job.
	 */
	public int getJobID() {
		return jobID;
	}
	
	/**
	 * @return ID of the partiular session of the job that should be run.
	 */
	public int getSessionID() {
		return sessionID;
	}

	/**
	 * @return sessions which are part of the session.
	 */
	public List<OldProtocol> getSessions() {
		return sessions;
	}
}