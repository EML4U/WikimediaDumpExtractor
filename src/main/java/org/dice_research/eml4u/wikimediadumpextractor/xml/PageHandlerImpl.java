package org.dice_research.eml4u.wikimediadumpextractor.xml;

import java.io.File;

/**
 * Gets parsed pages from {@link XmlParser} and passes it to
 * {@link XmlExecutor}.
 *
 * @author Adrian Wilke
 */
public class PageHandlerImpl implements PageHandler {

	@Override
	public void handlePage(Page page) {
		XmlExecutor.getInstance().submit(page.setVars(category, search, outDirectory));
	}

	// ---------------------------------------------------------------------------
	// TODO old code
	// ---------------------------------------------------------------------------

	public PageHandlerImpl setVars(String category, String search, File outDirectory) {
		this.category = category;
		this.search = search;
		this.outDirectory = outDirectory;
		return this;
	}

	private String category;
	private String search;
	private File outDirectory;

	// ---------------------------------------------------------------------------

}