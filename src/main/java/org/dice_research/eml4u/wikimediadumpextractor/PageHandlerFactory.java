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
	private String search;
	private File outDirectory;

	public PageHandler create(String page, String title) {
		return new PageHandler(page, title, category, search, outDirectory);
	}

	public String getCategory() {
		return category;
	}

	public String getSearch() {
		return search;
	}

	public File getOutDirectory() {
		return outDirectory;
	}

	public PageHandlerFactory setCategory(String category) {
		this.category = category;
		return this;
	}

	public PageHandlerFactory setSearch(String search) {
		this.search = search;
		return this;
	}

	public PageHandlerFactory setOutDirectory(File outDirectory) {
		this.outDirectory = outDirectory;
		return this;
	}
}