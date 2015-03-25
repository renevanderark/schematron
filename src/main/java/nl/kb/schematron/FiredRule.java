package nl.kb.schematron;

import org.w3c.dom.Element;

public class FiredRule {
	String context;

	/**
	 * Loads FiredRule object from DOM Element
	 * @param item the DOM element representing a fired rule
	 */
	public FiredRule(Element item) {
		context = item.getAttribute("context");
	}

	@Override
	public String toString() {
		return "Fired rule: {" +
			"context='" + context + '\'' +
			'}';
	}

	/**
	 * Get the xpath context on which this rule was fired
	 * @return the context
	 */
	public String getContext() {
		return context;
	}
}
