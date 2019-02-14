package net.c4analytics.htmlcleaner.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlValidator {
	private XmlValidator() {}
	
	private static DocumentBuilderFactory documentBuilderFactory = null;
	private static synchronized DocumentBuilderFactory getDocumentBuilderFactory() {
		if (documentBuilderFactory == null) {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setValidating(false);
		}
		return documentBuilderFactory;
	}
	public static Document getDocument(byte[] data) throws IOException {
		try {
			DocumentBuilder documentBuilder = getDocumentBuilderFactory().newDocumentBuilder();
			InputStream inputStream = new ByteArrayInputStream(data);
			InputSource inputSource = new InputSource(inputStream);
			return documentBuilder.parse(inputSource);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new IOException(e.getMessage());
		}
	}

}
