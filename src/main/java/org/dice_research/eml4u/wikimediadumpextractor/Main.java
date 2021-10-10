package org.dice_research.eml4u.wikimediadumpextractor;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.dice_research.eml4u.wikimediadumpextractor.io.FileChecks;
import org.dice_research.eml4u.wikimediadumpextractor.xml.Page;
import org.dice_research.eml4u.wikimediadumpextractor.xml.XmlExecutor;
import org.dice_research.eml4u.wikimediadumpextractor.xml.XmlParser;

/**
 * Wikimedia dump extractor.
 * 
 * Main entry point, parses arguments and starts execution.
 * 
 * XML file parsing is controlled by {@link XmlExecutor}, XML files are parsed
 * by {@link XmlParser} and single pages processed in parallel in {@link Page}.
 * 
 * @author Adrian Wilke
 */
public class Main {

	public static final String SEPARATOR = "|";
	public static final int DEFAULT_MIN_CATEGORY_SIZE = 10000;

	/**
	 * Main entry point.
	 */
	public static void main(String[] args) throws Exception {

		// Check if arguments were specified

		if (args.length == 0) {
			printHelp();
			System.exit(1);
		}

		Cfg.INSTANCE.set(Cfg.BEGIN_TIME, System.currentTimeMillis());
		String mode = args[0];

		// Mode pages / search

		if (mode.equals(Cfg.MODE_PAGES) && args.length > 4) {

			// IO
			Cfg.INSTANCE.set(Cfg.INPUT_FILE, FileChecks.checkFileIn(args[1], 1));
			Cfg.INSTANCE.set(Cfg.OUTPUT_DIR, FileChecks.checkDirectoryOut(args[2], 1));

			// Parse configuration
			Cfg.INSTANCE.set(Cfg.CATEGORIES, args[3]);
			cleanCategories();
			Cfg.INSTANCE.set(Cfg.SEARCH, args[4]);
			cleanSearchTerms();

			// Print configuration overview
			System.out.println(Cfg.INSTANCE);

			// Execute
			XmlExecutor.getInstance().execute(Cfg.INSTANCE.getAsFile(Cfg.INPUT_FILE));

			// Print results
			Cfg.INSTANCE.set(Cfg.INFO_END_TIME, System.currentTimeMillis());
			Cfg.INSTANCE.set(Cfg.INFO_DURATION,
					Cfg.INSTANCE.getAsLong(Cfg.INFO_END_TIME) - Cfg.INSTANCE.getAsLong(Cfg.BEGIN_TIME));
			System.out.println(Cfg.INSTANCE);
		}

		// Mode categories
		// TODO re-check; add aggs length

		else if (mode.equals(Cfg.MODE_CATEGORIES)) {
			Cfg.INSTANCE.set(Cfg.MODE, Cfg.MODE_CATEGORIES);
			Cfg.INSTANCE.set(Cfg.INPUT_FILE, FileChecks.checkFileIn(args[1], 1));

			int minCategorySize = DEFAULT_MIN_CATEGORY_SIZE;
			if (args.length > 2) {
				minCategorySize = Integer.parseInt(args[2]);
			}

			printCategories(Cfg.INSTANCE.getAsFile(Cfg.INPUT_FILE), minCategorySize);
		}

		else {
			printHelp();
		}

		System.exit(0);
	}

	private static void cleanCategories() {
		String catString = Cfg.INSTANCE.getAsString(Cfg.CATEGORIES);
		if (catString != null && !catString.isBlank()) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String cat : catString.split(Pattern.quote(SEPARATOR))) {
				if (!cat.isBlank()) {
					if (first) {
						first = false;
					} else {
						sb.append(SEPARATOR);
					}
					if (!cat.startsWith("Category:")) {
						sb.append("Category:").append(cat.trim());
					} else {
						sb.append(cat.trim());
					}
				}
			}
			Cfg.INSTANCE.set(Cfg.CATEGORIES, sb.toString());
		} else {
			// Blank to null
			Cfg.INSTANCE.set(Cfg.CATEGORIES, null);
		}
	}

	private static void cleanSearchTerms() {
		String searchString = Cfg.INSTANCE.getAsString(Cfg.SEARCH);
		if (searchString != null && !searchString.isBlank()) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String term : searchString.split(Pattern.quote(SEPARATOR))) {
				if (!term.isBlank()) {
					if (first) {
						first = false;
					} else {
						sb.append(SEPARATOR);
					}
					sb.append(term.trim());
				}
			}
			Cfg.INSTANCE.set(Cfg.SEARCH, sb.toString());
		} else {
			// Blank to null
			Cfg.INSTANCE.set(Cfg.SEARCH, null);
		}
	}

	private static void printHelp() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Usage: java -jar WikimediaDumpExtractor.jar");

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(" ");
		stringBuilder.append(Cfg.MODE_PAGES);
		stringBuilder.append("      <input XML file> <output directory> <categories> <search terms>");

		// TODO re-check
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(" ");
		stringBuilder.append(Cfg.MODE_CATEGORIES);
		stringBuilder.append(" <input SQL file> [minimum category size, default ");
		stringBuilder.append(DEFAULT_MIN_CATEGORY_SIZE);
		stringBuilder.append("]");

		stringBuilder.append(System.lineSeparator());
		stringBuilder
				.append("The values <categories> and <search terms> can contain multiple entries separated by '|'");
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("Website: https://github.com/EML4U/WikimediaDumpExtractor");
		System.out.println(stringBuilder.toString());
	}

	private static void printCategories(File inFile, int minCategorySize) throws IOException {
		new CategoryParser().printCategories(inFile, minCategorySize);
	}
}