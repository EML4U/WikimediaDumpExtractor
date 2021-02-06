package org.dice_research.eml4u.wikimediadumpextractor;

import java.io.File;

/**
 * Creates {@link PageHandler} objects.
 * 
 * Usage: Use setters first.
 *
 * @author Adrian Wilke
 */
public class PageHandlerFactory {

	private String category;
	private File outDirectory;

	public PageHandlerFactory setCategory(String category) {
		this.category = category;
		return this;
	}

	public PageHandlerFactory setOutDirectory(File outDirectory) {
		this.outDirectory = outDirectory;
		return this;
	}

	public PageHandler create(String page, String title) {
		return new PageHandler(page, title, category, outDirectory);
	}
}