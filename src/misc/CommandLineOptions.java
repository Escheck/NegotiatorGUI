package misc;

import java.util.List;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class CommandLineOptions
{
	public boolean silent = false;
	/** Automatically open new tournament tab on start up */
	public boolean newTournament = false;
	/** Automatically start new tournament on start up */
	public boolean startTournament = false;
	/** Automatically quit after the tournament */
	public boolean quitWhenTournamentDone = false;
	public List<String> agents;
	public List<String> profiles;
	public String protocol = "negotiator.protocol.alternatingoffers.AlternatingOffersProtocol";
	public String domain;
	public String outputFile;
	
	public static void main(String[] args)
	{
		CommandLineOptions commandLineOptions = new CommandLineOptions();
        commandLineOptions.parse(new String [] {"-s", "-c", "foo", "-q"} );
        System.out.println(commandLineOptions.silent);
    }

	public void parse(String [] args)
	{
		OptionParser parser = new OptionParser( "stq:q::a:b:p:d:f:w:" );

        OptionSet options = parser.parse(args);

        if (options.has("s"))
        	startTournament = true;
        if (options.has("t"))
        	newTournament = true;
        if (options.has("q"))
        	quitWhenTournamentDone = true;
        if (options.has("a"))
        	agents = (List<String>) options.valuesOf("a");
        if (options.has("p"))
        	profiles = (List<String>) options.valuesOf("p");
        if (options.has("d"))
        	domain = (String) options.valueOf("d");
        if (options.has("f"))
        	outputFile = (String) options.valueOf("f");
                
//        System.out.println( options.has( "c" ) );
//        System.out.println( options.hasArgument( "c" ) );
//        System.out.println( options.valueOf( "c" ) );
//        System.out.println( options.valuesOf( "c" ) );
//
//        System.out.println( options.has( "q" ) );
//        System.out.println( options.hasArgument( "q" ) );
//        System.out.println( options.valueOf( "q" ) );
//        System.out.println( options.valuesOf( "q" ) );
	}

}
