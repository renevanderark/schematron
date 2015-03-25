package nl.kb.schematron;

import org.junit.Test;
import org.probatron.Session;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class SchematronValidatorTest {
	@Test
	public void testValidation() throws TransformerException, IOException {
		SchematronValidator defaultValidator =
			new SchematronValidator(new StreamSource(getClass().getResourceAsStream("/jp2-gvn-schema.sch")));
		try {
			SchematronResult result = defaultValidator.validate(
				new StreamSource(getClass().getResourceAsStream("/jp2-failed-test.xml")));

			assertFalse("The xml file jp2-failed-test.xml should fail the validation", result.isValid());

			assertEquals("There should be 8 failed assertions", 8, result.getFailedAssertions().size());

			Iterator<FailedAssertion> failedAssertionIterator = result.getFailedAssertions().iterator();

			FailedAssertion currentFailedAssertion = failedAssertionIterator.next();
			assertEquals("/jpylyzer/properties/contiguousCodestreamBox/siz", currentFailedAssertion.getLocation());
			assertEquals("xTsiz = '1024'", currentFailedAssertion.getTest());
			assertEquals("wrong X Tile size", currentFailedAssertion.getText());

			currentFailedAssertion = failedAssertionIterator.next();
			assertEquals("/jpylyzer/properties/contiguousCodestreamBox/siz", currentFailedAssertion.getLocation());
			assertEquals("yTsiz = '1024'", currentFailedAssertion.getTest());
			assertEquals("wrong Y Tile size", currentFailedAssertion.getText());

			currentFailedAssertion = failedAssertionIterator.next();
			assertEquals("/jpylyzer/properties/contiguousCodestreamBox/cod", currentFailedAssertion.getLocation());
			assertEquals("sop = 'yes'", currentFailedAssertion.getTest());
			assertEquals("no start-of-packet headers", currentFailedAssertion.getText());

			currentFailedAssertion = failedAssertionIterator.next();
			assertEquals("/jpylyzer/properties/contiguousCodestreamBox/cod", currentFailedAssertion.getLocation());
			assertEquals("eph = 'yes'", currentFailedAssertion.getTest());
			assertEquals("no end-of-packet headers", currentFailedAssertion.getText());

			currentFailedAssertion = failedAssertionIterator.next();
			assertEquals("/jpylyzer/properties/contiguousCodestreamBox/cod", currentFailedAssertion.getLocation());
			assertEquals("segmentationSymbols = 'yes'", currentFailedAssertion.getTest());
			assertEquals("no segmentation symbols", currentFailedAssertion.getText());

			currentFailedAssertion = failedAssertionIterator.next();
			assertEquals("/jpylyzer/properties/contiguousCodestreamBox/cod", currentFailedAssertion.getLocation());
			assertEquals("order = 'RPCL'", currentFailedAssertion.getTest());
			assertEquals("wrong progression order", currentFailedAssertion.getText());

			currentFailedAssertion = failedAssertionIterator.next();
			assertEquals("/jpylyzer/properties/contiguousCodestreamBox/cod", currentFailedAssertion.getLocation());
			assertEquals("layers = '8'", currentFailedAssertion.getTest());
			assertEquals("wrong number of layers", currentFailedAssertion.getText());

			currentFailedAssertion = failedAssertionIterator.next();
			assertEquals("/jpylyzer/properties/contiguousCodestreamBox/cod", currentFailedAssertion.getLocation());
			assertEquals("levels = '5'", currentFailedAssertion.getTest());
			assertEquals("wrong number of decomposition levels", currentFailedAssertion.getText());

			result = defaultValidator.validate(
				new StreamSource(getClass().getResourceAsStream("/jp2-correct-test.xml")));
			assertTrue("The xml file jp2-correct-test.xml should pass the validation", result.isValid());

			assertEquals("There should be N fired rules", 4, result.getFiredRules().size());
			Iterator<FiredRule> firedRuleIterator = result.getFiredRules().iterator();
			FiredRule currentFiredRule = firedRuleIterator.next();
			assertEquals("/", currentFiredRule.getContext());

			currentFiredRule = firedRuleIterator.next();
			assertEquals("/jpylyzer", currentFiredRule.getContext());

			currentFiredRule = firedRuleIterator.next();
			assertEquals("/jpylyzer/properties/contiguousCodestreamBox/siz", currentFiredRule.getContext());

			currentFiredRule = firedRuleIterator.next();
			assertEquals("/jpylyzer/properties/contiguousCodestreamBox/cod", currentFiredRule.getContext());

		} catch (TransformerException | IOException | ParserConfigurationException | SAXException | XPathExpressionException e) {
			fail("Failed to validate xml file");
		}
	}

	private File resourceToTempfile(String path) throws IOException {
		InputStream inputStream = getClass().getResourceAsStream(path);
		File tempFile = File.createTempFile(path.replace("/", ""), ".tmp");
		OutputStream outputStream = new FileOutputStream(tempFile);
		int read;
		byte[] bytes = new byte[1024];

		while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);
		}
		outputStream.close();
		inputStream.close();
		return tempFile;
	}

	private static class MultiRunner implements Runnable {
		private final SchematronValidator validator;
		public boolean failed = false;

		private MultiRunner(SchematronValidator validator) {
			this.validator = validator;
		}

		@Override
		public void run() {
			try {
				SchematronResult result = this.validator.validate(
					new StreamSource(getClass().getResourceAsStream("/jp2-failed-test.xml")));
				if(result.isValid()) { throw new Exception("this profile should not validate"); }
			} catch (Exception e) {
				e.printStackTrace();
				failed = true;
			}
		}
	}

	@Test
	public void testScalability() throws TransformerException, IOException {
		List<Thread> threads = new ArrayList<>();
		List<MultiRunner> multiRunners = new ArrayList<>();
		boolean failed = false;
		for(int i = 0; i < 1000; i++) {
			MultiRunner multiRunner = new MultiRunner(
				new SchematronValidator(new StreamSource(getClass().getResourceAsStream("/jp2-gvn-schema.sch"))));
			Thread t = new Thread(multiRunner);
			multiRunners.add(multiRunner);
			threads.add(t);
			t.start();
		}
		for(Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				failed = true;
			}
		}
		for(MultiRunner m : multiRunners) {
			if(m.failed) { failed = true; break; }
		}

		assertFalse("This point should be reached without exceptions", failed);
	}

	@Test
	public void benchmarkCompare() throws IOException, TransformerException, ParserConfigurationException,
				XPathExpressionException, SAXException {
		File schemaTemp = resourceToTempfile("/jp2-gvn-schema.sch");
		File xmlTemp = resourceToTempfile("/jp2-failed-test.xml");
		Session session = new Session();

		session.setSchemaDoc("file:" + schemaTemp.getAbsolutePath());

		long sum = 0;
		for(int i = 0; i < 50; i++) {
			long start = System.nanoTime();
			session.doValidation("file:" + xmlTemp.getAbsolutePath());
			sum += (System.nanoTime() - start) / 1000l / 1000l;
		}
		long probatronAverage = sum / 50;

		SchematronValidator defaultValidator =
			new SchematronValidator(new StreamSource(getClass().getResourceAsStream("/jp2-gvn-schema.sch")));

		sum = 0;
		for(int i = 0; i < 50; i++) {
			long start = System.nanoTime();
			defaultValidator.validate(new StreamSource(getClass().getResourceAsStream("/jp2-failed-test.xml")));
			sum += (System.nanoTime() - start) / 1000l / 1000l;
		}
		long myAverage = sum / 50;

		assertTrue("My average validation time should be lower than probatron's average validation time",
			myAverage < probatronAverage);
		System.out.println("Probatron average: " + probatronAverage + "ms");
		System.out.println("My average: " + myAverage + "ms");
		if(!schemaTemp.delete()) { }
		if(!xmlTemp.delete()) { }
	}
}
