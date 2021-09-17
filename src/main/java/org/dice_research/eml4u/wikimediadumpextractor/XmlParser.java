package org.dice_research.eml4u.wikimediadumpextractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * MediaWiki XML parser.
 * 
 * Usage: Use setters first.
 *
 * @author Adrian Wilke
 */
public class XmlParser extends DefaultHandler {

	private ExecutorService executorService;
	private PageHandlerFactory pageHandlerFactory;

	private StringBuilder pageBuilder = new StringBuilder();
	private StringBuilder titleBuilder = new StringBuilder();
	private boolean isText = false;
	private boolean isTitle = false;

	private List<Future<String>> parsedPages = new LinkedList<>();

	public XmlParser setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
		return this;
	}

	public XmlParser setPageHandlerFactory(PageHandlerFactory pageHandlerFactory) {
		this.pageHandlerFactory = pageHandlerFactory;
		return this;
	}

	public void extract(String file) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxParserFactory.newSAXParser();
		saxParser.parse(file, this);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("text")) {
			isText = true;
		} else if (qName.equals("title")) {
			isTitle = true;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (isTitle) {
			titleBuilder.append(ch, start, length);
		} else if (isText) {
			pageBuilder.append(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals("text")) {
			isText = false;

			parsedPages.add(
					executorService.submit(pageHandlerFactory.create(pageBuilder.toString(), titleBuilder.toString())));

			pageBuilder = new StringBuilder();
			titleBuilder = new StringBuilder();
		} else if (qName.equals("title")) {
			isTitle = false;
		}
	}

	@Override
	public void endDocument() throws SAXException {
		try {

			// Create index

			String descriptor = "";
			if (pageHandlerFactory.getCategory() != null) {
				descriptor = "_"
						+ pageHandlerFactory.getCategory().replaceAll("[: ]", "_").replaceAll("[^A-Za-z0-9_]", "");
			} else if (pageHandlerFactory.getSearch() != null) {
				descriptor = "_"
						+ pageHandlerFactory.getSearch().replaceAll("[: ]", "_").replaceAll("[^A-Za-z0-9_]", "");
			}

			FileOutputStream fileOutputStream = new FileOutputStream(
					new File(pageHandlerFactory.getOutDirectory().getParentFile(), "Index" + descriptor + ".txt"),
					true);

			SortedSet<String> sortedParsedPages = new TreeSet<>();
			for (Future<String> parsedPage : parsedPages) {
				if (parsedPage.get() != null) {
					sortedParsedPages.add(parsedPage.get());
				}
			}

			for (String parsedPage : sortedParsedPages) {
				fileOutputStream.write(parsedPage.getBytes());
			}

			fileOutputStream.close();
		} catch (Exception e) {
			throw new SAXException(e);
		}
	}
}