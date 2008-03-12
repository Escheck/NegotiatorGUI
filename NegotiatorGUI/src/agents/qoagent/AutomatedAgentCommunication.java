import java.io.*;
import java.net.*;

/*
 * Created on 30/05/2004
 */

/**
 * @author raz
 * @version 1.0
 *
 * AutomatedAgentCommunication class: 
 * In charge of handling the connection between the agent
 * and the server:
 * 1. Creates the socket.
 * 2. Reads from the socket (data from server)
 * 3. Writes to socket (data to other agent via server)
 * 
 * This class is responsible to call AutomatedAgentMessages class to:
 * 1. Parse incoming messages
 * 2. Write messages to server in the given format
 * @see AutomatedAgent
 * @see AutomatedAgentMessages
 */
public class AutomatedAgentCommunication implements Runnable {

	private int m_nPort;
	private Socket m_socket = null;
	private String m_sIpAddress;
	private PrintWriter m_out = null;
	private BufferedReader m_in = null;
	private AutomatedAgent m_agent = null;
	private boolean m_bNegotiationEnded;

	/**
	 * Initializes the AutomatedAgentCommunication class.
	 * The socket is opened on "127.0.0.1" - assuming the server
	 * runs on the same machine where the AutomatedAgent runs.
	 * @param agent - saves the agent in the member variable
	 * @param nPort - the port of the server
	 */
	public AutomatedAgentCommunication(AutomatedAgent agent, int nPort)
	{
		m_agent = agent;
		m_bNegotiationEnded = false;
		
		m_nPort = nPort;
		m_sIpAddress = "127.0.0.1";
		
		try
		{
			m_socket = new Socket(m_sIpAddress, m_nPort);

			m_out = new PrintWriter(m_socket.getOutputStream(), true);
			m_in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
		}
		catch(UnknownHostException e)
		{
			System.out.println("[AA]UnknownHostException: " + e.getMessage() + " [AutomatedAgentCommunication::AutomatedAgentCommunication(59)]");
			System.err.println("[AA]UnknownHostException: " + e.getMessage() + " [AutomatedAgentCommunication::AutomatedAgentCommunication(59)]");
			System.exit(1);
		}
		catch(IOException e)
		{
			System.out.println("[AA]IO Error: " + e.getMessage() + " [AutomatedAgentCommunication::AutomatedAgentCommunication(65)]");
			System.err.println("[AA]IO Error: " + e.getMessage() + " [AutomatedAgentCommunication::AutomatedAgentCommunication(65)]");
			System.exit(1);
		}		
	}
	
	/**
	 * Sends the message to the server and then increments the
	 * message id
	 * @param sMsg - the message to send to the server
	 * @see AutomatedAgent
	 * @see AutomatedAgentMessages
	 */
	public void printMsg(String sMsg)
	{
		System.out.println("[AA]COMM:OUT------ " + sMsg);
		
		m_out.println(sMsg);
		
		// need to increment message id
		m_agent.incrementMsgId();
	}
	
	/**
	 * Reads a message from the server.
	 * @return line - the message from the server
	 * @see AutomatedAgent
	 */
	public String readMsgLine()
	{
		String line = "";
		
		try 
		{
			line = m_in.readLine();
		}
		catch (IOException e)
		{
			System.out.println("[AA]IO Error: " + e.getMessage() + " [AutomatedAgentCommunication::readMsgLine(105)]");
			System.err.println("[AA]IO Error: " + e.getMessage() + " [AutomatedAgentCommunication::readMsgLine(105)]");
			System.exit(1);
		}
		
		if (line != null)
		{
			System.out.println("[AA]COMM:IN------ " + line);
		}
		
		return line;
	}
	
	/** 
	 * Reads messages from the server until the negotiation ends
	 * @see java.lang.Runnable#run()
	 * @see AutomatedAgent
	 */
	public void run()
	{
		String sServerLine;

		while((sServerLine=readMsgLine())!=null && !m_bNegotiationEnded)
		{
			m_agent.receivedMessage(sServerLine);
		}
		
		System.out.println("[AA]Negotiation Ended");
		System.err.println("[AA]Negotiation Ended");
	}
	
	/** 
	 * Called when the negotiation ends.
	 * Sets m_bNegotiationEnded to true, causing run to stop.
	 * @see AutomatedAgentCommunication#run
	 */
	public void endNegotiation()
	{
		m_bNegotiationEnded = true;
	}
}
