package org.dice_research.eml4u.wikimediadumpextractor.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

import org.dice_research.eml4u.wikimediadumpextractor.io.FileFilters;

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
	public static final boolean WRITE_FILE = false;

	public static final String FILENAME = "categories-in-textfiles.csv";

	private String regexCategory = "\\[\\[" + "Category:" + "(.*)?" + "\\]\\]";
	private String regexCategoryTitle = "(.*)?" + "\\|";

	private Pattern patternCategory = Pattern.compile(regexCategory);
	private Pattern patternCategoryTitle = Pattern.compile(regexCategoryTitle);

	public static void main(String[] args) throws IOException {

		// Check directory parameter
		File directory = null;
		if (args.length == 0) {
			System.out.println("No directory given");
			System.exit(1);
		} else {
			directory = new File(args[0]);
			if (!directory.canRead()) {
				System.out.println("Can not read: " + args[0]);
				System.exit(1);
			}
		}

		// Get categories from files
		CountCategoriesInTextfiles instance = new CountCategoriesInTextfiles();
		Map<File, List<String>> filesToCategories = instance.getCategories(directory, MAX_FILES);

		// Count categories
		Map<String, Integer> counted = instance.countCategories(filesToCategories);

		// Sort by count
		counted = instance.sortByValue(counted);

		// Results to string
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Integer> e : counted.entrySet()) {
			sb.append(e.getValue());
			sb.append("; ");
			sb.append(e.getKey());
			sb.append(System.lineSeparator());
		}

		// Output
		if (WRITE_FILE) {
			File outFile = new File(directory.getParentFile(), FILENAME);
			Files.writeString(outFile.toPath(), sb.toString());
			System.out.println(outFile);
		} else {
			System.out.println(sb);
		}
	}

	/**
	 * Sorts map by value.
	 * 
	 * @see https://stackoverflow.com/a/23846961
	 */
	private LinkedHashMap<String, Integer> sortByValue(Map<String, Integer> map) {
		Stream<Map.Entry<String, Integer>> sortedStream = map.entrySet().stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
		return sortedStream
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	/**
	 * Count categories
	 * 
	 * @return
	 */
	private Map<String, Integer> countCategories(Map<File, List<String>> filesToCategories) {
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
	 * Gets files inside directory and extracts categories inside.
	 * 
	 * @param maxFiles 0 to process all
	 */
	private Map<File, List<String>> getCategories(File directory, int maxFiles) throws IOException {
		Map<File, List<String>> filesToCategories = new HashMap<>();

		// Go through text files
		int i = 0;
		for (File file : new FileFilters().getFiles(directory, "txt")) {
			if (maxFiles > 0 && i++ >= maxFiles) {
				break;
			}

			// Extract categories
			String text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
			List<String> categories = extractCategories(text, file);
			filesToCategories.put(file, categories);
		}
		return filesToCategories;
	}

	/**
	 * Extracts categories (e.g. "[[Category:Hey]])" from given string.
	 */
	private List<String> extractCategories(String string, File file) {
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

			if (category.length() <= MAX_CATEGORY_LENGTH) {
				// TODO check why the strings are not shorter
				categories.add(category);
			} else {
				// Long category names are typically WP internal notes
				if (category.contains("Archive")) {
					// An usual long category
					System.err.println("Skipping archive: " + category + " | " + file.getName());
				} else {
					// TODO If it occurs: examine
					System.out.println("Skipping: " + category + " | " + file.getName());
				}
			}

		}
		return categories;
	}

}