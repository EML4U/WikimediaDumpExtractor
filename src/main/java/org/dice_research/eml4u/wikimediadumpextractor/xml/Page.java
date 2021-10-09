package org.dice_research.eml4u.wikimediadumpextractor.xml;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.dice_research.eml4u.wikimediadumpextractor.Cfg;
import org.dice_research.eml4u.wikimediadumpextractor.content.Text;
import org.dice_research.eml4u.wikimediadumpextractor.io.FileUtils;
import org.dice_research.eml4u.wikimediadumpextractor.utils.Strings;

/**
 * Wikipedia XML page element. Created by {@link XmlParser}.
 * 
 * Instances will be called by {@link XmlExecutor} using {@link #call()} for
 * parallel processing.
 *
 * @author Adrian Wilke
 */
public class Page implements Callable<Page> {

	private Integer id; // e.g. https://en.wikipedia.org/?curid=
	private Text text;
	private String title;

	private Set<String> extractedCategories = null;
	private Set<String> extractedSearchTerms = null;
	private boolean fileWritten = false;

	public Page(Integer id, String text, String title) {
		this.id = id;
		this.text = new Text(text);
		this.title = title;
	}

	public Page(String id, String text, String title) {
		this(Integer.valueOf(id), text, title);
	}

	public String getTitle() {
		return title;
	}

	public Integer getId() {
		return id;
	}

	public String getText() {
		return text.toString();
	}

	/**
	 * Returns true if text is available and is no redirect.
	 */
	public boolean hasContent() {
		if (text == null) {
			return false;
		} else if (text.isRedirect()) {
			return false;
		} else {
			return true;
		}
	}

	public String getFilenameId() {
		// https://www.mediawiki.org/wiki/Manual:Short_URL
		// https://github.com/wikimedia/mediawiki
		return title.replaceAll("[^A-Za-z0-9 -]+", " ").trim().replaceAll("[ ]+", "_");
	}

	@Override
	public String toString() {
		return title;
	}

	private File getOutputDirectory() {
		String subDir = Cfg.INSTANCE.getAsFile(Cfg.INPUT_FILE).getName();
		int lastDotIndex = subDir.lastIndexOf(".");
		if (lastDotIndex > 0) {
			subDir = subDir.substring(0, lastDotIndex);
		}

		File directory = new File(Cfg.INSTANCE.getAsFile(Cfg.OUTPUT_DIR), subDir);
		directory.mkdirs();

		return directory;
	}

	/**
	 * Called from {@link XmlExecutor} to process data in parallel.
	 */
	@Override
	public Page call() throws Exception {

		// Note: Use System.out.println(Thread.currentThread().getId()); to check, if
		// different threads are used

		if (!getExtractedCategories().isEmpty() || !getExtractedSearchTerms().isEmpty()) {
			File outFile = new File(getOutputDirectory(), getFilenameId() + ".txt");
			try {
				FileUtils.stringToFile(outFile, getText());
				fileWritten = true;
			} catch (IOException e) {
				System.err.println("Could not write file: " + outFile.getAbsolutePath());
			}
			return this;
		} else {
			return null;
		}
	}

	public boolean wasFilewritten() {
		return fileWritten;
	}

	public Set<String> getExtractedCategories() {
		if (extractedCategories == null) {
			extractCategories();
		}
		return extractedCategories;
	}

	public Set<String> getExtractedSearchTerms() {
		if (extractedSearchTerms == null) {
			extractSearchTerms();
		}
		return extractedSearchTerms;
	}

	private void extractCategories() {
		if (!Cfg.INSTANCE.getAsString(Cfg.CATEGORIES).isBlank()) {
			extractedCategories = text.search(Strings.getCategories(), false, false);
		} else {
			extractedCategories = new HashSet<>();
		}
	}

	private void extractSearchTerms() {
		if (!Cfg.INSTANCE.getAsString(Cfg.SEARCH).isBlank()) {
			extractedSearchTerms = text.search(Strings.getSearchTerms(), true, true);
		} else {
			extractedSearchTerms = new HashSet<>();
		}
	}
}