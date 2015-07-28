package negotiator.xml.multipartyrunner;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.xml.bind.JAXBException;

import negotiator.config.MultilateralTournamentsConfiguration;
import negotiator.gui.About;

/**
 * Genius console entry point for running a
 * {@link MultilateralTournamentsConfiguration}.
 * <p>
 * Use this entry point to run genius negotiations from an xml file. Example xml
 * file can be found at xml-runner/example.xml
 * 
 * @author W.Pasman
 */
public class Runner {

	/**
	 * Genius console entry point
	 * <p>
	 * Use this entry point to run genius negotiations from an xml file. Example
	 * xml file can be found at xml-runner/example.xml
	 * <p>
	 * When no arguments are given, input and output file are prompted at the
	 * console. Input file is expected to be an .xml file and output is expected
	 * to be an .csv file, although you are free to give different extensions.
	 * 
	 * @param args
	 *            1st argument: input file or empty to be prompted. 2nd
	 *            argument: output file or empty to be prompted
	 * @throws JAXBException
	 */
	public static void main(String[] args) throws JAXBException {

		// print welcome message
		System.out
				.println("This is the Genius multilateral tournament runner command line tool");
		System.out.println("Currently you are using using Genius "
				+ About.VERSION);

		// request input and output files
		Scanner sc = new Scanner(System.in);
		String input = requestInputFile(args, sc);
		String output = requestOutputFile(args, sc);
		sc.close();

		// run xml configuration
		MultilateralTournamentsConfiguration config = MultilateralTournamentsConfiguration
				.load(new File(input));

		// HACK run first tournament only for now.
		new MultilatTourRunner(config.getTournaments().get(0), output).start();

		// tell user where results are stored
		String logLocation = System.getProperty("user.dir") + "/" + output;
		System.out.println("Logfile stored at " + logLocation);
	}

	/** Requests the input file from System.in (or from args[0] if defined) */
	private static String requestInputFile(String[] args, Scanner sc) {
		// init
		File f = null;
		String filename = null;

		// if in args
		if (args.length >= 1) {
			System.out.println("Input file: " + args[0]);
			return args[0];
		}

		// Request filename
		System.out.print("Provide path to xml input file: ");
		while (f == null || !f.exists()) {
			if (f != null) {
				System.out.println("Xml input file could not be found: "
						+ f.getName());
				System.out.print("Provide path to xml input file: ");
			}
			filename = sc.nextLine();
			f = new File(filename);
		}

		// return filename
		return filename;
	}

	/** Requests the output file from System.in (or from args[1] if defined) */
	private static String requestOutputFile(String[] args, Scanner sc) {
		// init
		File f = null;
		String filename = null;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String defaultName = String.format("logs/Log-XmlRunner-%s.csv",
				dateFormat.format(new Date()));

		// if in args
		if (args.length >= 2) {
			System.out.println("Output file: " + args[1]);
			return args[1];
		}

		// Request filename
		System.out.print(String.format(
				"Provide path to output logfile [default: %s]: ", defaultName));
		while (f == null) {
			filename = sc.nextLine();
			if (filename.isEmpty()) {
				filename = defaultName;
			}
			f = new File(filename);
		}

		// return filename
		return filename;
	}
}
