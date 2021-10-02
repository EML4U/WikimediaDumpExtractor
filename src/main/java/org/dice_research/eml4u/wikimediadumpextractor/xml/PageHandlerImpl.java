package org.dice_research.eml4u.wikimediadumpextractor.xml;

/**
 * Gets parsed pages from {@link XmlParser} and passes it to
 * {@link XmlExecutor}.
 *
 * @author Adrian Wilke
 */
public class PageHandlerImpl implements PageHandler {

	@Override
	public void handlePage(Page page) {
		XmlExecutor.getInstance().submit(page);
	}

}