package org.dice_research.eml4u.wikimediadumpextractor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

import org.dice_research.eml4u.wikimediadumpextractor.io.FileChecks;
import org.dice_research.eml4u.wikimediadumpextractor.utils.CfgUtils;
import org.dice_research.eml4u.wikimediadumpextractor.utils.Strings;
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
	public static void main(String[] args) {

		// Check if arguments were specified

		if (args.length == 0) {
			printHelp();
			System.exit(1);
		}

		Cfg.INSTANCE.set(Cfg.BEGIN_TIME, System.currentTimeMillis());
		String mode = args[0];

		// Mode pages / search

		if (mode.equals(Cfg.MODE_PAGES) && args.length >= 6) {

			// IO
			Cfg.INSTANCE.set(Cfg.INPUT_FILE, FileChecks.checkFileIn(args[1], 1));
			Cfg.INSTANCE.set(Cfg.OUTPUT_DIR, FileChecks.checkDirectoryOut(args[2], 1));

			// Parse configuration
			Cfg.INSTANCE.set(Cfg.CATEGORIES, args[3]);
			cleanCategories();
			Cfg.INSTANCE.set(Cfg.SEARCH, args[4]);
			cleanSearchTerms();
			Cfg.INSTANCE.set(Cfg.IDS, args[5]);

			// Print configuration overview
			System.out.println(Cfg.INSTANCE);

			// Execute
			try {
				XmlExecutor.getInstance().execute(Cfg.INSTANCE.getAsFile(Cfg.INPUT_FILE));
			} catch (Exception e) {
				System.err.println("Error: " + Strings.stackTraceToString(e));
			}

			// Print results
			Cfg.INSTANCE.set(Cfg.INFO_END_TIME, System.currentTimeMillis());
			Cfg.INSTANCE.set(Cfg.INFO_DURATION,
					Cfg.INSTANCE.getAsLong(Cfg.INFO_END_TIME) - Cfg.INSTANCE.getAsLong(Cfg.BEGIN_TIME));
			System.out.println(Cfg.INSTANCE);
			try {
				Files.write(new File(CfgUtils.getOutputDirectoryJob(),
						Strings.getReadableTimpestamp(Cfg.INSTANCE.getAsLong(Cfg.BEGIN_TIME)) + ".txt").toPath(),
						Cfg.INSTANCE.toString().getBytes());
			} catch (IOException e) {
				System.err.println("Error writing job overview: " + Strings.stackTraceToString(e));
			}
		}

		// Mode categories

		else if (mode.equals(Cfg.MODE_CATEGORIES) && args.length >= 3) {
			Cfg.INSTANCE.set(Cfg.MODE, Cfg.MODE_CATEGORIES);
			Cfg.INSTANCE.set(Cfg.INPUT_FILE, FileChecks.checkFileIn(args[1], 1));
			Cfg.INSTANCE.set(Cfg.OUTPUT_DIR, FileChecks.checkDirectoryOut(args[2], 1));

			System.out.println(Cfg.INSTANCE);

			int minCategorySize = DEFAULT_MIN_CATEGORY_SIZE;
			if (args.length >= 4) {
				minCategorySize = Integer.parseInt(args[3]);
			}

			File outFile = new File(Cfg.INSTANCE.getAsFile(Cfg.OUTPUT_DIR),
					"categories-in-sql." + minCategorySize + ".csv");

			try {
				Files.write(outFile.toPath(), new CategoryParser()
						.countCategories(Cfg.INSTANCE.getAsFile(Cfg.INPUT_FILE), minCategorySize).getBytes());
				System.out.println(outFile.getAbsolutePath());
			} catch (IOException e) {
				System.err.println("Error on counting categories: " + Strings.stackTraceToString(e));
			}
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
			// null to blank
			Cfg.INSTANCE.set(Cfg.CATEGORIES, "");
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
			// null to blank
			Cfg.INSTANCE.set(Cfg.SEARCH, "");
		}
	}

	private static void printHelp() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Usage: java -jar WikimediaDumpExtractor.jar");

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(" ");
		stringBuilder.append(Cfg.MODE_PAGES);
		stringBuilder.append("      <input XML file> <output directory> <categories> <search terms> <ids>");

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(" ");
		stringBuilder.append(Cfg.MODE_CATEGORIES);
		stringBuilder.append(" <input SQL file> <output directory> [minimum category size, default ");
		stringBuilder.append(DEFAULT_MIN_CATEGORY_SIZE);
		stringBuilder.append("]");

		stringBuilder.append(System.lineSeparator());
		stringBuilder
				.append("The values <categories> and <search terms> can contain multiple entries separated by '|'");
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("Website: https://github.com/EML4U/WikimediaDumpExtractor");
		System.out.println(stringBuilder.toString());
	}
}