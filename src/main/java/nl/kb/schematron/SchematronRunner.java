package nl.kb.schematron;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.FileInputStream;
import java.io.IOException;

public class SchematronRunner {

	/**
	 * Runs the SchematronValidator sequentially on multiple files
	 * @param args first argument is the schematron schema file, 2nd-nth argument are the xml files to be tested
	 * @throws IOException
	 * @throws TransformerException
	 * @throws XPathExpressionException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static void main(String[] args) throws IOException, TransformerException, XPathExpressionException, ParserConfigurationException, SAXException {
		if(args.length < 2) {
			return;
		}

		SchematronValidator validator = new SchematronValidator(new StreamSource(new FileInputStream(args[0])));
		System.out.println("Initialization took: " + validator.initTime + " miliseconds");
		System.out.println("Loading schema took: " + validator.getLoadTime() + " miliseconds");
		for(int i = 1; i < args.length; i++) {
			System.out.println("Validating: " + args[i]);
			long start = System.nanoTime();
			SchematronResult result = validator.validate(new StreamSource(new FileInputStream(args[i])));
			long measured = (System.nanoTime() - start) / 1000l / 1000l;
			System.out.println("VALIDATES: " + result.isValid() + " (took " + measured + " miliseconds)");

			for(FiredRule firedRule : result.getFiredRules()) {
				System.out.println(firedRule);
			}
			for(FailedAssertion failedAssertion : result.getFailedAssertions()) {
				System.out.println(failedAssertion);
			}
			System.out.println(result);
		}
	}
}
