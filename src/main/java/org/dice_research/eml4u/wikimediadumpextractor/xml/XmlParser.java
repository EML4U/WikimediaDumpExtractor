package org.dice_research.eml4u.wikimediadumpextractor.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * MediaWiki XML parser.
 * 
 * Extracts elements id, text and title from page elements and calls a
 * {@link PageHandler} instance on completed pages.
 * 
 * Usage: {@link #extract(File, PageHandler)}.
 * 
 * Setting a max pages value by {@link #setMaxPages(int)} will throw a
 * {@link MaxPagesException} if that number is reached.
 * 
 * The getter methods can be used for statistics.
 *
 * @author Adrian Wilke
 */
public class XmlParser extends DefaultHandler {

	public class MaxPagesException extends SAXException {

		private static final long serialVersionUID = 1L;

		public MaxPagesException(String message) {
			super(message);
		}
	}

	private static final String ELEMENT_ID = "id";
	private static final String ELEMENT_PAGE = "page";
	private static final String ELEMENT_REVISION = "revision";
	private static final String ELEMENT_TEXT = "text";
	private static final String ELEMENT_TITLE = "title";

	private boolean isId = false;
	private boolean isRevision = false;
	private boolean isText = false;
	private boolean isTitle = false;

	private StringBuilder idBuilder = new StringBuilder();
	private StringBuilder textBuilder = new StringBuilder();
	private StringBuilder titleBuilder = new StringBuilder();

	private PageHandler pageHandler;
	private int pageCounter = 0;
	private int maxPages = 0;

	private long startTimeParsing = 0;
	private long durationParsing = 0;
	private long durationHandling = 0;

	/**
	 * If set, the parser will throw a {@link SAXException} after max pages are
	 * parsed.
	 */
	public XmlParser setMaxPages(int maxPages) {
		this.maxPages = maxPages;
		return this;
	}

	/**
	 * Reads given {@link File} and calls given {@link PageHandler} on completed
	 * page elements.
	 */
	public int extract(File file, PageHandler pageHandler)
			throws MaxPagesException, IOException, ParserConfigurationException, SAXException {
		this.pageHandler = pageHandler;

		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxParserFactory.newSAXParser();
		saxParser.parse(file, this);

		return pageCounter;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals(ELEMENT_ID)) {
			isId = true;
		} else if (qName.equals(ELEMENT_PAGE)) {
			startTimeParsing = System.currentTimeMillis();
		} else if (qName.equals(ELEMENT_REVISION)) {
			isRevision = true;
		} else if (qName.equals(ELEMENT_TEXT)) {
			isText = true;
		} else if (qName.equals(ELEMENT_TITLE)) {
			isTitle = true;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (isId && !isRevision) {
			idBuilder.append(ch, start, length);
		} else if (isText) {
			textBuilder.append(ch, start, length);
		} else if (isTitle) {
			titleBuilder.append(ch, start, length);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.equals(ELEMENT_ID)) {
			isId = false;
		} else if (qName.equals(ELEMENT_PAGE)) {
			durationParsing += System.currentTimeMillis() - startTimeParsing;
			finishPage();
		} else if (qName.equals(ELEMENT_REVISION)) {
			isRevision = false;
		} else if (qName.equals(ELEMENT_TEXT)) {
			isText = false;
		} else if (qName.equals(ELEMENT_TITLE)) {
			isTitle = false;
		}
	}

	private void finishPage() throws SAXException {

		long startTimeHandling = System.currentTimeMillis();
		pageHandler.handlePage(new Page(idBuilder.toString(), textBuilder.toString(), titleBuilder.toString()));
		durationHandling += System.currentTimeMillis() - startTimeHandling;

		pageCounter++;
		if (maxPages > 0 && pageCounter >= maxPages) {
			throw new MaxPagesException("Parsed " + pageCounter + " pages.");
		}

		idBuilder = new StringBuilder();
		textBuilder = new StringBuilder();
		titleBuilder = new StringBuilder();
	}

	public long getDurationHandling() {
		return durationHandling;
	}

	public long getDurationParsing() {
		return durationParsing;
	}

	public int getNumberOfParsedPages() {
		return pageCounter;
	}
}