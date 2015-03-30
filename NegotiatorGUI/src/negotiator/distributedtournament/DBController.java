package negotiator.distributedtournament;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import misc.Serializer;
import negotiator.Global;
import negotiator.exceptions.Warning;
import negotiator.protocol.OldProtocol;
import negotiator.tournament.Tournament;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

/**
 * Creates a DBControler. The DBController is used to manage the communication
 * with the database in case a distributed tournament is ran. In this implementation,
 * it is assumed that we are communicating with a busy webserver. Therefore, each
 * time a group of operations has been performed, the connection is closed.
 * 
 * @author Mark Hendrikx
 * @version 17-12-11
 */
public class DBController {

	/** Singleton pattern */
	private static DBController ref;
	/** Database connection */
	private static Connection conn = null;	
	/** URL of DB */
	private static String urlStored = "";
	/** username for DB */
	private static String userStored = "";
	/** password for DB */
	private static String passwordStored = "";
	/** how many outcomes may be stored in a single file. The
	 * problem with tools such as Excel, is that they can only
	 * import a limited amount of data. */
	private final int NUMBER_OUTCOMES_PER_LOG = 5000;
	
	private DBController() { }
	
	/**
	 * @return instance of the database controller with a connection
	 */
	public static DBController getInstance() {
		if (ref == null)
			ref = new DBController();
		try {
			// did we close the connection, or has the connection been closed
			if (conn != null && (conn.isClosed() || connectionClosed())) {
				reconnect();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ref;
	}
	
	/**
	 * Simplest method to detect if a connection has been closed.
	 * @return true if closed
	 */
	private static boolean connectionClosed() {
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(
				"SELECT sessionname " +
				"FROM Jobs ");
			
			preparedStatement.executeQuery();
		} catch (SQLException e) {
			return true;
		}
		return false;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	/**
	 * Connect to the database.
	 * 
	 * @param url with port (also supports properties: http://dev.mysql.com/doc/refman/5.0/en/connector-j-reference-configuration-properties.html)
	 * @param username of the database account
	 * @param password of the database account
	 * @return true if connection was succesfull
	 */
	public static boolean connect(String url, String username, String password) {
		String fullURL = "jdbc:mysql://" + url;
		String driver= "com.mysql.jdbc.Driver";
		urlStored = url;
		userStored = username;
		passwordStored = password;
		
		try {
			Class.forName(driver).newInstance();
			conn =  (Connection) DriverManager.getConnection(fullURL, username, password);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Reconnect to the database.
	 * 
	 * @return true if success
	 */
	public static boolean reconnect() {
		return connect(urlStored, userStored, passwordStored);
	}
	
	/**
	 * Creates a job by storing the tournament in the database, and
	 * splitting all sessions into smaller groups.
	 * 
	 * @param sessionName name of the session given to the tournament
	 * @param t tournament from which the jobs are derived.
	 */
	public void createJob(String sessionName, Tournament t) {
		
		try {
			// 1. serialize the tournament for storage in the DB
			Serializer<Tournament> serializeSession = new Serializer<Tournament>("");
			String serTournament = serializeSession.writeToString(t);
			
			// 2. get the sessions of the tournament. The order is important,
			// as the tournament creates many objects which are unserializable
			ArrayList<OldProtocol> sessions = t.getSessions();

			// 3. Store the full job
			int jobID = storeJob(sessionName, serTournament);
			
			// 4. Store the groups of sessions in the DB
			storeSessions(sessions, jobID, t.getRounds());
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}
	
	/**
	 * Method which divides all sessions into small groups.
	 * 
	 * @param sessions array of all sessions of the tournament
	 * @param jobID id of the highlevel job associated with the sessions
	 */
	private void storeSessions(ArrayList<OldProtocol> sessions, int jobID, int jobsize) {
		for (int i = 0; i < sessions.size(); i+= jobsize) {
			int max = i + jobsize - 1;
			if (max >= sessions.size()) {
				max = sessions.size() - 1;
			}
			storeSession(jobID, i, max);
		}
	}

	/**
	 * Returns the last issued job with the given session name.
	 * 
	 * @param sessionname name of the session.
	 * @return jobID if success, else 0
	 */
	public int getJobID(String sessionname) {
		int sessionID = 0;
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(
				"SELECT MAX(jobID) " +
				"FROM Jobs " +
				"WHERE sessionname =  ?");
			
			preparedStatement.setString(1, sessionname);
			ResultSet set = preparedStatement.executeQuery();
			if (set.next()) {
				sessionID = set.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sessionID;
	}
	/**
	 * Stores the full session in the database. Since the tournament
	 * is available, we only need to store where to start and end.
	 * 
	 * @param jobID ID of the highlevel job
	 * @param min which job should be started with
	 * @param max which job should be ended with
	 * @return ID of the session in the DB
	 */
	private int storeSession(int jobID, int min, int max) {
		int sessionID = -1;
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(
					"INSERT INTO Sessions values (default, ?, ?, ?, default, 0)",
					Statement.RETURN_GENERATED_KEYS);

			preparedStatement.setInt(1, jobID);
			preparedStatement.setInt(2, min);
			preparedStatement.setInt(3, max);
			preparedStatement.executeUpdate();
			ResultSet keys = preparedStatement.getGeneratedKeys();
			keys.next();  
			sessionID = keys.getInt(1); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sessionID;
	}

	/**
	 * Given the jobID of the tournament, the tournament is requested from
	 * the database and stored as a Tournament object.
	 * @param jobID of the tournament to be retrieved
	 * @return tournament object with the given jobID
	 */
	public Tournament getTournament(int jobID) {
		Tournament tournament = null;
		try {
			// 1. get the serialized tournament
			PreparedStatement preparedStatement = conn.prepareStatement(
					"SELECT tournament " +
					"FROM Jobs " +
					"WHERE jobID = ?");

			preparedStatement.setInt(1, jobID);
			
			// 2. Execute the query
			ResultSet set = preparedStatement.executeQuery();
			if (set.next()) {
				// 3. Deserialize the tournament
		        String tourStr = set.getString(1);
				Serializer<Tournament> unserializeTournament = new Serializer<Tournament>("");
		        tournament = unserializeTournament.readStringToObject(tourStr);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();
		return tournament;
	}
	
	/**
	 * Gets a group of sessions to run.
	 * 
	 * @param jobID id of the main job.
	 * @param sessions array of all jobs of the tournament.
	 * @return subset of the full array of jobs which still needs to be executed.
	 */
	public Job getJob(int jobID, ArrayList<OldProtocol> sessions) {
		Job job = null;
		try {
			// 1. Get the latest job with the given name. From this job,
			// select the group which was stored first and is not yet executed.
			// Finally, for this group of sessions retrieve all information.
			PreparedStatement preparedStatement = conn.prepareStatement(
					"SELECT Sessions.sessionID, jobLow, jobHigh " +
					"FROM Sessions " +
					"WHERE sessionID = " +
					"" +
					"(SELECT MIN(sessionID) " +
					"FROM Sessions JOIN Jobs ON Sessions.jobID = Jobs.jobID " +
					"WHERE Sessions.status = 0 AND Jobs.jobID = ?)");

			preparedStatement.setInt(1, jobID);
			
			// 2. Execute the query and check if there is still something to do
			ResultSet set = preparedStatement.executeQuery();
			if (set.next()) {
				int sessionID = set.getInt(1);
		        int jobLow = set.getInt(2);
		        int jobHigh = set.getInt(3);

		        // 3. Seems there is a job to be done. Store that you are busy fulfilling the request
		        preparedStatement = conn.prepareStatement("UPDATE Sessions SET status = 1 WHERE sessionID = ?");
				preparedStatement.setInt(1, sessionID);
				preparedStatement.executeUpdate();

		        try {
		        	// 4. Get the job and store it in a Job-object for easy access
		        	job = new Job(jobID, sessionID, sessions.subList(jobLow, jobHigh + 1));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// 3b. All jobs are finished or allocated
		close();
		return job;
	}
	
	/**
	 * Stores a high-level job request in the database.
	 * 
	 * @param sessionName identifier of the high-level job
	 * @param serTournament serialized tournament
	 * @return id of the job in the DB
	 */
	private int storeJob(String sessionName, String serTournament) {
		int jobID = -1;
		try {
			// 1. Insert a job in the DB
			PreparedStatement preparedStatement = conn.prepareStatement(
					"INSERT INTO Jobs values (default, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			preparedStatement.setString(1, sessionName);
			preparedStatement.setString(2, serTournament);
			preparedStatement.executeUpdate();
			
			// 2. Retrieve the jobID (row number) of the job in the DB
			ResultSet keys = preparedStatement.getGeneratedKeys();
			keys.next();  
			jobID = keys.getInt(1); 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return jobID;
	}

	/**
	 * Closes a database connection.
	 * 
	 * @return if the DB is successfully closed
	 */
	private boolean close() {
		if (conn != null) {
			try {
				if (!conn.isClosed())
					conn.close();
				return true;
			} catch (SQLException e) { }
		}
		return false;
	}

	/**
	 * Store the result of the group of sessions in the DB.
	 * 
	 * @param sessionID ID of the group
	 * @param outcome outcome for the group
	 */
	public void storeResult(int sessionID, String outcome) {
		byte[] compStr = new byte[0];
		try {
			compStr = compressBytes(outcome);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("UPDATE Sessions SET status = 2, result = ? WHERE sessionID = ?");
			preparedStatement.setBytes(1, compStr);
			preparedStatement.setInt(2, sessionID);
			preparedStatement.executeUpdate();
			close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the amount of jobs which are currently executed by
	 * other instances of Genius or need to be processed.
	 *
	 * @param jobID ID of the high-level job
	 * @return amount of jobs which are currently running
	 */
	public int getRunningSessions(int jobID) {
		int count = 0;
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(
					"SELECT COUNT(sessionID) " +
					"FROM Sessions JOIN Jobs ON Sessions.jobID = Jobs.jobID " +
					"WHERE Sessions.status <> 2 AND Sessions.jobID = ?");
			preparedStatement.setInt(1, jobID);
			ResultSet set = preparedStatement.executeQuery();
			set.next();
			count = set.getInt(1);
			close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * Returns if a sessionname exists.
	 * 
	 * @param sessionName name of the tournament.
	 * @return true if exists
	 */
	public boolean existsSessionName(String sessionName) {
		int count = 0;
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(
					"SELECT COUNT(sessionname) " +
				    "FROM Jobs " +
					"WHERE sessionname = ?");
			preparedStatement.setString(1, sessionName);
			ResultSet set = preparedStatement.executeQuery();
			set.next();
			count = set.getInt(1);
			close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return (count > 0);
	}
	
	/**
	 * Resets all sessions which were busy. This is to avoid a infinite waiting time
	 * when another computer has failed at processing a job (in which case it is still
	 * marked as busy).
	 * 
	 * @param jobID ID of the high-level job
	 */
	public void resetJobs(int jobID) {
		try {
			PreparedStatement preparedStatement = conn.prepareStatement("UPDATE Sessions SET status = 0 WHERE jobID = ? AND status = 1");
			preparedStatement.setInt(1, jobID);
			preparedStatement.executeUpdate();
			close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reconstructs the full log of outcomes by gluing all separate outcomes 
	 * of the job together. Note that the resulting log should be identical
	 * to running the full tournament on a single PC.
	 * 
	 * @param jobID ID of the job
	 */
	public void reconstructLog(int jobID) {
		// 1. Retrieve all outcomes from the DB.
		// Note the design decision to retrieve all at once to 
		// avoid overloading the DB with requests (there are more clients)
		ResultSet outcomes = getLog(jobID);
		
		//to get the amount of rows in the ResultSet
		int rowCount = 0;  
		try {
			rowCount = outcomes.last() ? outcomes.getRow() : 0;	// Determine number of rows  
			outcomes.beforeFirst();  
			System.out.println("Number of Logs: " + rowCount);// We want next() to go to first row  

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		
		for(int x = 0; x <(rowCount/NUMBER_OUTCOMES_PER_LOG) + 1; x++){

			// 2. Get the filename of the to-be-created log
			String outcomesFileName = Global.getDistributedOutcomesFileName();
			if (!(rowCount < NUMBER_OUTCOMES_PER_LOG)){
				outcomesFileName = outcomesFileName.replace(".xml", "-Part"+ (x+1) + ".xml");
			}

			File outcomesFile = new File(outcomesFileName);
			boolean exists = outcomesFile.exists();
	
			// 3. Store the outcomes retrieved from the DB in a log
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(outcomesFile, true));
				if (!exists) {
					out.write("<a>\n");
				}

				while (outcomes.getRow() / NUMBER_OUTCOMES_PER_LOG == x){
				//while (outcomes.next()) {		
					outcomes.next();
					try {
						//System.out.println(extractBytes(outcomes.getBytes(1)));
						out.write(extractBytes(outcomes.getBytes(1)));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					if(outcomes.isLast()){
						break;
					}
				} 
				if(!(outcomes.getRow()%NUMBER_OUTCOMES_PER_LOG==0) && !(outcomes.getRow()/NUMBER_OUTCOMES_PER_LOG == x)){
					System.out.println("Error writing Log: Outcome written in wrong file");
					System.out.println("Outcome Number: " + outcomes.getRow());
					System.out.println("Modulo Outcome Number: " + outcomes.getRow()/NUMBER_OUTCOMES_PER_LOG);
					System.out.println("File Number: " + x); 
				}
				//}
				out.write("</a>\n");
				out.close();
				System.out.println("closed Log file: " + outcomesFileName);
			} catch (Exception e) {
				new Warning("Exception during closing log:"+e);
				close();
				e.printStackTrace();
			}
		}
		close();
	}

	/**
	 * Gets all outcomes of a job from the DB, and glues them together.
	 * 
	 * @param jobID ID of the high-level job
	 * @return all outcomes glued together as a single string
	 */
	private ResultSet getLog(int jobID) {
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(
					"SELECT result " +
					"FROM Sessions " +
					"WHERE jobID = ? " +
					"ORDER BY sessionID ASC");

			preparedStatement.setInt(1, jobID);
			ResultSet set = preparedStatement.executeQuery();
			return set;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the amount of matches per session.
	 * 
	 * @param jobID ID of the high-level job
	 * @return amount of matches per session, or 5 if nothing was found
	 */
	public int getMatchesPerSession(int jobID) {
		PreparedStatement preparedStatement;
		int result = 5;
		try {
			preparedStatement = conn.prepareStatement(
					"SELECT jobHigh " +
					"FROM Sessions " +
					"WHERE jobID = ? AND jobLow = 0");
			preparedStatement.setInt(1, jobID);
			ResultSet set = preparedStatement.executeQuery();
			set.next();
			result = (set.getInt(1) + 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		close();
		return result;
	}

	/**
	 * @return small description of the DT functionality.
	 */
	public static String getDistributedTutorial() {
		String tutorial = "\n\n\n" +
			"############################################################################\n" +
			"# Entering Distributed Tournament Mode                                     #\n" +
			"############################################################################\n" +
			"# * Only 1 DT should be active per Genius, as the DB connection is shared  #\n" +
			"# * Start Genius multiple times to benefit from multiple cores on 1 PC     #\n" +
			"# * If the outcomes should not be split, increase NUMBER_OUTCOMES_PER_LOG  #\n" +
			"############################################################################\n\n\n";	
		return tutorial;
	}
	
	/**
	 * Compresses the data to be stored in the database. This can result in a compression rate
	 * of 1 / 60.
	 * 
	 * @param data to be compressed.
	 * @return compressed string
	 * @throws UnsupportedEncodingException if UTF-8 encoding is not supported.
	 * @throws IOException if there is a problem reading the string.
	 */
	public static byte[] compressBytes(String data) throws UnsupportedEncodingException, IOException {
        byte[] input = data.getBytes("UTF-8");
        Deflater df = new Deflater();
        df.setLevel(Deflater.BEST_COMPRESSION);
        df.setInput(input);
 
        ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
        df.finish();
        byte[] buff = new byte[1024];
        while(!df.finished())
        {
            int count = df.deflate(buff);
            baos.write(buff, 0, count);
        }
        baos.close();
        byte[] output = baos.toByteArray();
 
        return output;
    }
	
	/**
	 * Decompress a compressed string.
	 * 
	 * @param input compressed byte array which needs to be decompressed.
	 * @return decompressed string.
	 * @throws UnsupportedEncodingException if UTF-8 encoding is not supported.
	 * @throws IOException if there is a problem reading the byte array.
	 * @throws DataFormatException should not happen.
	 */
	public static String extractBytes(byte[] input) throws UnsupportedEncodingException, IOException, DataFormatException {
        Inflater ifl = new Inflater();
        ifl.setInput(input);
 
        ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
        byte[] buff = new byte[1024];
        while(!ifl.finished())
        {
            int count = ifl.inflate(buff);
            baos.write(buff, 0, count);
        }
        baos.close();
        byte[] output = baos.toByteArray();
 
        return new String(output);
    }
}