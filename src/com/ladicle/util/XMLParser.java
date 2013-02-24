package com.ladicle.util;

import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class XMLParser {
	private static final XMLParser instance = new XMLParser();
	private static final Log log = new Log("XMLParser");
	private SAXBuilder saxBuilder;

	private XMLParser() {
		saxBuilder = new SAXBuilder();
	}

	public static XMLParser getInstance() {
		return instance;
	}

	public Element getRootElement(InputStream in) {
		Element root = null;

		try {
			Document doc = saxBuilder.build(in);
			root = doc.getRootElement();

		} catch (JDOMException e) {
			log.e(e.getMessage());
		} catch (IOException e) {
			log.e(e.getMessage());
		}

		return root;
	}
}
