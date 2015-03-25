package nl.kb.schematron;

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;

class SchematronNamespaceContext implements NamespaceContext {
	@Override
	public String getNamespaceURI(String s) {
		switch (s) {
			case "xs":
				return "http://www.w3.org/2001/XMLSchema";
			case "schold":
				return "http://www.ascc.net/xml/schematron";
			case "svrl":
				return "http://purl.oclc.org/dsdl/svrl";
			case "sch":
				return "http://www.ascc.net/xml/schematron";
			case "iso":
				return "http://purl.oclc.org/dsdl/schematron";
		}
		return null;
	}

	@Override
	public String getPrefix(String s) {
		return null;
	}

	@Override
	public Iterator getPrefixes(String s) {
		return null;
	}
}
