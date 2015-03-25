package nl.kb.schematron;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class FailedAssertion {
	private String test;
	private String location;
	private String text;

	/**
	 * Load FailedAssertion object from DOM element representing a failed assertion
	 * @param assertElem the DOM element representing a failed assertion
	 */
	public FailedAssertion(Element assertElem) {
		test = assertElem.getAttribute("test");
		location = assertElem.getAttribute("location");
		NodeList children = assertElem.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			if(children.item(i).getNodeName().equalsIgnoreCase("svrl:text")) {
				text = children.item(i).getTextContent();
				break;
			}
		}
	}

	/**
	 * Get test
	 * @return the test which failed
	 */
	public String getTest() {
		return test;
	}

	/**
	 * Get location
	 * @return the xpath of the failed assertion
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Get text
	 * @return the failed assertion's text
	 */
	public String getText() {
		return text;
	}

	@Override
	public String toString() {
		return "Assertion failed: {" +
			"test='" + test + '\'' +
			", location='" + location + '\'' +
			", text='" + text + '\'' +
			'}';
	}
}
