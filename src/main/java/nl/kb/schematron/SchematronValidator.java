package nl.kb.schematron;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SchematronValidator {
	private static final SAXTransformerFactory factory = (SAXTransformerFactory)TransformerFactory.newInstance();
	private TransformerHandler startChain;
	private TransformerHandler endChain;
	public static final long initTime;
	private static final Templates t1;
	private static final Templates t2;
	private static final Templates t3;

	/**
	 * Initializes the Saxon conversion templates from XSLT files in resources
	 */
	static {
		long startTime = System.nanoTime();
		final Class clz = SchematronValidator.class;
		StreamSource iso_dsdl_include = new StreamSource(clz.getResourceAsStream("/iso_dsdl_include.xsl"));
		StreamSource iso_abstract_expand = new StreamSource(clz.getResourceAsStream("/iso_abstract_expand.xsl"));
		StreamSource iso_svrl_for_xslt1 = new StreamSource(clz.getResourceAsStream("/iso_svrl_for_xslt1.xsl"));
		factory.setURIResolver(new URIResolver() {
			public Source resolve(String href, String base) throws TransformerException {
				return new StreamSource(clz.getResourceAsStream("/" + href));
			}
		});
		try {
			t1 = factory.newTemplates(iso_dsdl_include);
			t2 = factory.newTemplates(iso_abstract_expand);
			t3 = factory.newTemplates(iso_svrl_for_xslt1);
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
		initTime = (System.nanoTime() - startTime) / 1000l / 1000l;
	}

	/**
	 * Initializes the XLST transformers for to generate the validation XSLT with.
	 */
	{
		try {
			startChain = factory.newTransformerHandler(t1);
			TransformerHandler middleChain = factory.newTransformerHandler(t2);
			endChain = factory.newTransformerHandler(t3);
			startChain.setResult(new SAXResult(middleChain));
			middleChain.setResult(new SAXResult(endChain));
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] schema = null;
	private final long loadTime;

	/**
	 * Loads a Schematron schema into an XSLT to validate against
	 * @param xmlSchema the schematron schema XML as a javax.xml.transform.Source object
	 * @throws TransformerException
	 * @throws IOException
	 */
	public SchematronValidator(StreamSource xmlSchema) throws TransformerException, IOException {
		long start = System.nanoTime();
		Transformer schemaTransformer = factory.newTransformer();
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		endChain.setResult(new StreamResult(result));
		schemaTransformer.transform(xmlSchema, new SAXResult(startChain));
		schema = result.toByteArray();
		loadTime = (System.nanoTime() - start) / 1000l / 1000l;
	}


	/**
	 * Validates an XML file against the loaded schematron schema
	 * @param xmlSource the XML as a javax.xml.transform.Source object
	 * @return a new SchematronResult object
	 * @throws TransformerException
	 * @throws XPathExpressionException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public SchematronResult validate(Source xmlSource) throws TransformerException,
				XPathExpressionException, IOException, SAXException, ParserConfigurationException {
		Transformer validationTransformer = factory.newTransformer(new StreamSource(new ByteArrayInputStream(schema)));
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		validationTransformer.transform(xmlSource, new StreamResult(result));

		return new SchematronResult(result.toString());
	}

	/**
	 * Get load time
	 * @return the time to load the schematron schema in the constructor
	 */
	public long getLoadTime() {
		return loadTime;
	}
}
