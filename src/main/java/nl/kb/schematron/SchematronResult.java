package nl.kb.schematron;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import net.sf.saxon.xpath.XPathFactoryImpl;

public class SchematronResult {
	private static final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	private static final XPathFactory xPathFactory = new XPathFactoryImpl();
	private final XPathExpression validateXpathExpression;
	private final XPathExpression firedRulesXpathExpression;
	private final DocumentBuilder documentBuilder;

	/**
	 * Initialize the xpath expressions to locate fired rules and failed assertions with in the result XML
	 */
	{
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			XPath xpath = xPathFactory.newXPath();
			xpath.setNamespaceContext(new SchematronNamespaceContext());
			validateXpathExpression = xpath.compile("//svrl:failed-assert");
			firedRulesXpathExpression = xpath.compile("//svrl:fired-rule");
		} catch (ParserConfigurationException | XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	private String xml = null;
	private List<FailedAssertion> failedAssertions = new ArrayList<>();
	private List<FiredRule> firedRules = new ArrayList<>();

	/**
	 * Parses the schematron result XML into a SchematronResult object
	 * @param resultXml the schematron result XML as a String
	 * @throws IOException
	 * @throws SAXException
	 * @throws XPathExpressionException
	 */
	public SchematronResult(String resultXml) throws IOException, SAXException, XPathExpressionException {
		xml = resultXml;
		Document resultDoc = documentBuilder.parse(new InputSource(new StringReader(resultXml)));
		DOMSource resultSource = new DOMSource(resultDoc);

		NodeList firedRulesList = (NodeList) firedRulesXpathExpression.evaluate(resultSource, XPathConstants.NODESET);
		for(int i = 0; i < firedRulesList.getLength(); i++) {
			firedRules.add(new FiredRule((Element) firedRulesList.item(i)));
		}
		NodeList failedAssertionList = (NodeList) validateXpathExpression.evaluate(resultSource, XPathConstants.NODESET);
		for(int i = 0; i < failedAssertionList.getLength(); i++) {
			failedAssertions.add(new FailedAssertion((Element) failedAssertionList.item(i)));
		}
	}

	/**
	 * Get the failed assertions
	 * @return a list of failed assertions registered by schematron
	 */
	public List<FailedAssertion> getFailedAssertions() {
		return failedAssertions;
	}

	/**
	 * Get the fired rules
	 * @return the list of rules which were fired by schematron
	 */
	public List<FiredRule> getFiredRules() {
		return firedRules;
	}

	/**
	 * To string method
	 * @return the xml-result from schematron
	 */
	@Override
	public String toString() {
		return xml;
	}

	/**
	 * Is this schematron result valid?
	 * @return true if there were no failed assertions
	 */
	public boolean isValid() {
		return failedAssertions.size() == 0;
	}
}
