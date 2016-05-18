package test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import negotiator.xml.multipartyrunner.Runner;

import org.junit.Test;

public class multipartytest {

	@Test
	public void test1() throws JAXBException, IOException {
		File output = File.createTempFile("temp-file-name", ".tmp");

		Runner.main(new String[] { "test/multitest.xml",
				output.getCanonicalPath() });
		assertTrue("result differs from expected",
				equal(output, new File("test/multitestexpected.txt")));
	}

	private boolean equal(File file1, File file2) throws IOException {
		byte[] f1 = Files.readAllBytes(file1.toPath());
		byte[] f2 = Files.readAllBytes(file2.toPath());
		return Arrays.equals(f1, f2);
	}

}
