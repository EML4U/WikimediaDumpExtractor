package org.dice_research.eml4u.wikimediadumpextractor.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dice_research.eml4u.wikimediadumpextractor.Cfg;
import org.dice_research.eml4u.wikimediadumpextractor.io.FileFilters;
import org.dice_research.eml4u.wikimediadumpextractor.xml.Index;
import org.dice_research.eml4u.wikimediadumpextractor.xml.IndexPage;

/**
 * Goes through files, extracts categories and counts them.
 *
 * @author Adrian Wilke
 */
public class CountCategoriesInTextfiles {

	// Config: Set 0 to process all
	public static final int MAX_FILES = 0;

	// Config: Maximum string length of single categories
	public static final int MAX_CATEGORY_LENGTH = 110;

	// Config: Write file
	public static final boolean WRITE_FILE = true;

	public static final boolean VERBOSE = true;

	public static final String FILENAME_PREFIX = "categories-in-";
	public static final String FILENAME_SUFFIX = ".csv";
	public static final String FILENAME_DEFAULT = FILENAME_PREFIX + "textfiles" + FILENAME_SUFFIX;

	private String regexCategory = "\\[\\[" + "Category:" + "(.*?)" + "\\]\\]?";
	private String regexCategoryTitle = "(.*)?" + "\\|";

	private Pattern patternCategory = Pattern.compile(regexCategory);
	private Pattern patternCategoryTitle = Pattern.compile(regexCategoryTitle);

	/**
	 * Argument can be a directory or a file with file paths.
	 */
	public static void main(String[] args) throws IOException {

		if (args.length == 0) {
			System.out.println("No input given");
			System.exit(1);
		}

		CountCategoriesInTextfiles instance = new CountCategoriesInTextfiles();
		File file = new File(args[0]);
		String filename = null;
		Map<File, List<String>> filesToCategories = null;

		// Read all files in directory
		if (file.isDirectory()) {
			filename = FILENAME_DEFAULT;
			filesToCategories = instance.getCategoriesFromDirectory(file, MAX_FILES);
		}

		// Read files connected in input: Text file containing names of index files
		else {
			File jobDirectory = file.getParentFile();
			File textDirectory = new File(jobDirectory, Cfg.DEFAULT_TEXT_DIRECTORY);
			List<File> files = new LinkedList<>();
			for (String indexFile : Files.readAllLines(file.toPath())) {
				if (!indexFile.isBlank()) {
					for (IndexPage indexPage : new Index(new File(jobDirectory, indexFile)).getIndexPages()) {
						files.add(new File(textDirectory, indexPage.filename));
					}
					if (VERBOSE) {
						System.out.println(indexFile);
					}
				}
			}
			filename = FILENAME_PREFIX + file.getName() + FILENAME_SUFFIX;
			filesToCategories = instance.getCategoriesFromFiles(files);
		}

		// Count and write
		countAndWrite(filesToCategories, new File(file.getParentFile(), filename));
	}

	private static void countAndWrite(Map<File, List<String>> filesToCategories, File outFile) throws IOException {

		// Count categories
		Map<String, Integer> counted = countCategories(filesToCategories);

		// Results to string
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Integer> e : sortByValue(counted).entrySet()) {
			sb.append(e.getValue());
			sb.append(";");
			sb.append(e.getKey());
			sb.append(System.lineSeparator());
		}

		// Output
		if (WRITE_FILE) {
			Files.writeString(outFile.toPath(), sb.toString());
			System.out.println(outFile);
		} else {
			System.out.println(sb);
		}
	}

	/**
	 * Gets files inside directory and extracts categories inside.
	 * 
	 * @param maxFiles 0 to process all
	 */
	private Map<File, List<String>> getCategoriesFromDirectory(File directory, int maxFiles) throws IOException {
		List<File> files = new LinkedList<>();

		int i = 0;
		for (File file : new FileFilters().getFiles(directory, "txt")) {
			if (maxFiles > 0 && i++ >= maxFiles) {
				break;
			}
			files.add(file);
		}

		return getCategoriesFromFiles(files);
	}

	private Map<File, List<String>> getCategoriesFromFiles(List<File> files) throws IOException {
		Map<File, List<String>> filesToCategories = new HashMap<>();
		for (File file : files) {
			filesToCategories.put(file, getCategoriesFromFile(file));
		}
		return filesToCategories;
	}

	private List<String> getCategoriesFromFile(File file) throws IOException {
		return getCategoriesFromString(Files.readString(file.toPath()), file);
	}

	/**
	 * Extracts categories (e.g. "[[Category:Hey]])" from given text.
	 */
	private List<String> getCategoriesFromString(String string, File sourceFile) {
		List<String> categories = new LinkedList<>();

		// [[Category:Hey]]
		Matcher matcher = patternCategory.matcher(string);
		while (matcher.find()) {
			String category = matcher.group(1);

			// [[Category:Hey| ]]
			Matcher matcherTitle = patternCategoryTitle.matcher(category);
			if (matcherTitle.find()) {
				category = matcherTitle.group(1);
			}

			categories.add(category);
		}
		return categories;
	}

	/**
	 * Count categories
	 */
	private static Map<String, Integer> countCategories(Map<File, List<String>> filesToCategories) {
		Map<String, Integer> catCounter = new HashMap<>();
		for (List<String> categories : filesToCategories.values()) {
			for (String category : categories) {
				if (catCounter.containsKey(category)) {
					catCounter.put(category, catCounter.get(category) + 1);
				} else {
					catCounter.put(category, 1);
				}
			}
		}
		return catCounter;
	}

	/**
	 * Sorts map by value.
	 * 
	 * @see https://stackoverflow.com/a/23846961
	 */
	private static LinkedHashMap<String, Integer> sortByValue(Map<String, Integer> map) {
		Stream<Map.Entry<String, Integer>> sortedStream = map.entrySet().stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
		return sortedStream
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
}