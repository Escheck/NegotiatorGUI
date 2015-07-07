package negotiator.xml.multipartyrunner;

import com.sun.javafx.binding.StringFormatter;
import negotiator.MultipartyNegotiationEventListener;
import negotiator.events.LogMessageEvent;
import negotiator.events.MultipartyNegotiationOfferEvent;
import negotiator.events.MultipartyNegotiationSessionEvent;
import negotiator.exceptions.NegotiationPartyTimeoutException;
import negotiator.gui.progress.MultipartyNegoEventLogger;
import negotiator.logging.CsvLogger;
import negotiator.qualitymeasures.CSVlogger;
import negotiator.session.InvalidActionError;
import negotiator.session.SessionManager;
import negotiator.session.TournamentManager;
import org.jfree.data.io.CSV;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


public class MultiPartyXMLRunner {

    public static final int DOTS_PER_LINE = 50; // percentages will add another 5 columns
    static PrintStream orgErr = System.err;
    static PrintStream orgOut = System.out;

    public static void main(String[] args) {

        // repl
        System.out.println("MultipartyXMLRunner Commandline tool 1.0");
        String fileName = null;
        if (args.length != 0) {
            fileName = args[0];
            System.out.println("provided filename as argument: " + fileName);
        } else {
            fileName = getFileName();
        }

        // loading xmlObj
        System.out.println("Loading " + fileName);
        XmlObject xmlObj = load(fileName);

        // creating log file
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        CsvLogger csvLogger = null;
        try {
            String logfileName = String.format("logs/Log-XmlRunner-%s.csv", dateFormat.format(new Date()));
            System.out.println("Creating logfile at: " + logfileName);
            csvLogger = new CsvLogger(logfileName);
        } catch (IOException e) {
            System.err.println("Unable to create logfile.");
            System.err.println("---");
            e.printStackTrace();
            System.exit(1);
        }

        // create log file header
        String defaultHeader = CsvLogger.getDefaultHeader(0);
        String adjustedHeader = "Protocol"
                + CsvLogger.DELIMITER
                + defaultHeader.substring(defaultHeader.indexOf(CsvLogger.DELIMITER) + 1);
        csvLogger.logLine(adjustedHeader);

        // run sessions in a loop
        int runNumber = 0;
        long start = System.nanoTime();
        int totalRuns = xmlObj.getNumberOfRuns();
        System.out.println(String.format("Running %d negotiations (%d per line)", totalRuns, DOTS_PER_LINE));
        useConsoleOut(false);
        for (RunConfiguration runConfiguration : xmlObj) {

            try {
                String result = runConfiguration.run();
                csvLogger.logLine(result);
                printProgress(++runNumber, totalRuns);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long stop = System.nanoTime();

        // console write system exit strings
        try {
            useConsoleOut(true);
            int fillerNumber = runNumber;
            while (fillerNumber % DOTS_PER_LINE != 0) {
                fillerNumber++;
                System.out.print(" ");
            }
            if (fillerNumber != runNumber) System.out.println(" 100%");
            System.out.println(String.format("Negotiations took %s to complete.", TournamentManager.prettyTimeSpan(stop-start)));
            csvLogger.close();
        } catch (IOException e) {
            System.err.println("Unable to close fileWriter for log file");
        } finally {
            useConsoleOut(true);
        }
    }

    private static String getFileName() {
        File f = null;
        String filename = null;

        System.out.println("We found the the following xml files in the current root directory:");
        File[] xmlFiles = (new File("./xml-runner")).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });
        System.out.print((char) 27 + "[34m");
        for (File xmlFile : xmlFiles) System.out.print(xmlFile.getName() + " ");
        System.out.println((char) 27 + "[0m");

        System.out.print("Please provide the xml file you'd like to run [relative to ./xml-runner]: ");

        while (f == null || !f.exists()) {
            if (f != null) {
                System.err.println("Xml File could not be found: " + f.getName());
                System.out.print("Please provide the xml file you'd like to run [relative to ./xml-runner]: ");
            }
            Scanner sc = new Scanner(System.in);
            filename = "xml-runner/" + sc.nextLine();
            f = new File(filename);
        }
        return filename;
    }

    private static XmlObject load(String filename) {
        try {
            JAXBContext jc = JAXBContext.newInstance(XmlObject.class);

            Unmarshaller unmarshaller = jc.createUnmarshaller();
            File xml = new File(filename);
            return (XmlObject) unmarshaller.unmarshal(xml);

        } catch (JAXBException e) {
            System.err.println("Error while reading xml file");
            System.err.println(e.getMessage());
            System.err.println("---");
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    private static void printProgress(int current, int total) {
        useConsoleOut(true);
        System.out.print(".");
        if (current % DOTS_PER_LINE == 0) {
            int perc = (int) Math.round(100 * ((double) current / (double) total));
            System.out.println(String.format(" %3d%%", perc));
        }
        useConsoleOut(false);
    }

    /**
     * Silences or restores the console output. This can be useful to suppress
     * output of foreign code, like submitted agents
     * <p/>
     * FIXME redundant code, copy of SessionManager#useConsoleOut.
     *
     * @param enable Enables console output if set to true or disables it when set
     *               to false
     */
    private static void useConsoleOut(boolean enable) {

        if (enable) {
            System.setErr(orgErr);
            System.setOut(orgOut);
        } else {
            System.setOut(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException { /* no-op */
                }
            }));
            System.setErr(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) throws IOException { /* no-op */
                }
            }));
        }
    }
}
