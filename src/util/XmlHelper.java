package util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.StringReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * A thin wrapper for reading XML documents
 */
public class XmlHelper {

	public final Document doc;

	public XmlHelper(String raw) throws SAXException, IOException, ParserConfigurationException {
		this.doc = getXmlHandle(raw);
	}

	public String getAttribute(String a) throws SAXException, IOException, ParserConfigurationException {
		return getAttribute(doc, a);
	}

	public static String getAttribute(Document doc, String a) throws SAXException, IOException, ParserConfigurationException {
		NodeList nl = doc.getElementsByTagName(a);
		if(nl.getLength() != 1) {
			return null;
		}
		else {
			String content = nl.item(0).getTextContent();
			// System.out.println("Content of attribute " + a.name() + " is: " + content);
			return content;
		}
	}

	public static Document getXmlHandle(String raw) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true); 
		DocumentBuilder builder = dbf.newDocumentBuilder(); 
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(raw));
		Document xmlHandle =  builder.parse(is);
		return xmlHandle;
	}
}