package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import negotiator.xml.multipartyrunner.Runner;

import org.junit.Test;

public class multipartytest {

	@Test
	public void test1() throws IOException, NotEqualException, JAXBException {
		File output = File.createTempFile("temp-file-name", ".tmp");

		Runner.main(new String[] { "test/multitest.xml",
				output.getCanonicalPath() });
		equal(output, new File("test/multitestexpected.txt"));
	}

	/**
	 * Check the output log files line by line.
	 * 
	 * @param file1
	 * @param file2
	 * @return
	 * @throws IOException
	 * @throws NotEqualException
	 */
	private void equal(File file1, File file2) throws IOException,
			NotEqualException {
		BufferedReader br1 = new BufferedReader(new FileReader(file1));
		BufferedReader br2 = new BufferedReader(new FileReader(file2));

		int linenr = 1;
		String line1, line2;
		while ((line1 = br1.readLine()) != null) {
			line2 = br2.readLine();
			equal(linenr, line1, line2);
			linenr++;
		}

		br1.close();
		br2.close();
	}

	/**
	 * ignore run time(s), agent1, agent2, agent3, agent4. We ignore the agents
	 * because they contain memory address
	 */
	List<Integer> indicesToSkip = Arrays.asList(0, 12, 13, 14, 15);

	/**
	 * Check that given lines are equal. ";" is separator character.
	 * 
	 * @param linenr
	 * @param line1
	 * @param line2
	 * @throws NotEqualException
	 */

	private void equal(int linenr, String line1, String line2)
			throws NotEqualException {
		String[] elements1 = line1.split(";");
		String[] elements2 = line2.split(";");
		if (elements1.length != elements2.length) {
			throw new NotEqualException(
					"Lines do not contain the same number of results", linenr,
					"" + elements1.length, "" + elements2.length);
		}
		for (int i = 0; i < elements1.length; i++) {
			if (indicesToSkip.contains(i))
				continue;
			String elem1 = elements1[i];
			String elem2 = elements2[i];
			if (!elem1.equals(elem2)) {
				throw new NotEqualException("element " + (i + 1)
						+ " is not equal", linenr, elem1, elem2);
			}
		}
	}
}
