package org.dice_research.eml4u.wikimediadumpextractor;

import java.io.File;
import java.io.IOException;

import org.dice_research.eml4u.wikimediadumpextractor.io.FileChecks;
import org.dice_research.eml4u.wikimediadumpextractor.xml.XmlExecutor;

/**
 * Wikimedia dump extractor.
 * 
 * Main entry point, parses arguments and starts execution.
 *
 * @author Adrian Wilke
 */
public class Main {

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

		// Mode pages / search

		if (args[0].equals(Cfg.MODE_PAGES) || args[0].equals(Cfg.MODE_SEARCH)) {
			Cfg.INSTANCE.set(Cfg.BEGIN_TIME, System.currentTimeMillis());
			Cfg.INSTANCE.set(Cfg.INPUT_FILE, FileChecks.checkFileIn(args[1], 1));
			Cfg.INSTANCE.set(Cfg.OUTPUT_DIR, FileChecks.checkDirectoryOut(args[2], 1));

			// TODO: allow both
			if (args[0].equals(Cfg.MODE_PAGES)) {
				Cfg.INSTANCE.set(Cfg.MODE, Cfg.MODE_PAGES);
				Cfg.INSTANCE.set(Cfg.CATEGORIES, args[3]);
			} else if (args[0].equals(Cfg.MODE_SEARCH)) {
				Cfg.INSTANCE.set(Cfg.MODE, Cfg.MODE_SEARCH);
				Cfg.INSTANCE.set(Cfg.SEARCH, args[3]);
			}

			System.out.println(Cfg.INSTANCE);

			XmlExecutor.getInstance().execute(Cfg.INSTANCE.getAsFile(Cfg.INPUT_FILE));

			Cfg.INSTANCE.set(Cfg.INFO_END_TIME, System.currentTimeMillis());
			Cfg.INSTANCE.set(Cfg.INFO_DURATION,
					Cfg.INSTANCE.getAsLong(Cfg.INFO_END_TIME) - Cfg.INSTANCE.getAsLong(Cfg.BEGIN_TIME));
			System.out.println(Cfg.INSTANCE);
		}

		// Mode categories

		else if (args[0].equals(Cfg.MODE_CATEGORIES)) {
			Cfg.INSTANCE.set(Cfg.MODE, Cfg.MODE_CATEGORIES);
			Cfg.INSTANCE.set(Cfg.INPUT_FILE, FileChecks.checkFileIn(args[1], 1));

			int minCategorySize = DEFAULT_MIN_CATEGORY_SIZE;
			if (args.length > 2) {
				minCategorySize = Integer.parseInt(args[2]);
			}

			printCategories(Cfg.INSTANCE.getAsFile(Cfg.INPUT_FILE), minCategorySize);
		}

		System.exit(0);
	}

	private static void printHelp() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Usage: java -jar WikimediaDumpExtractor.jar");

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(" ");
		stringBuilder.append(Cfg.MODE_PAGES);
		stringBuilder.append("      <input XML file> <output directory> <category>");

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(" ");
		stringBuilder.append(Cfg.MODE_SEARCH);
		stringBuilder.append("     <input XML file> <output directory> <searchterm>");

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(" ");
		stringBuilder.append(Cfg.MODE_CATEGORIES);
		stringBuilder.append(" <input SQL file> [minimum category size, default ");
		stringBuilder.append(DEFAULT_MIN_CATEGORY_SIZE);
		stringBuilder.append("]");

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("https://github.com/EML4U/WikimediaDumpExtractor");
		System.err.println(stringBuilder.toString());
	}

	private static void printCategories(File inFile, int minCategorySize) throws IOException {
		new CategoryParser().printCategories(inFile, minCategorySize);
	}
}