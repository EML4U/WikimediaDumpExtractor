package org.dice_research.eml4u.wikimediadumpextractor.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.dice_research.eml4u.wikimediadumpextractor.CategoryParser;

/**
 * Generates overview of overlap of files created by
 * {@link CountCategoriesInTextfiles} and {@link CategoryParser}.
 *
 * @author Adrian Wilke
 */
public class CategoryOverlap {

	public static void main(String[] args) throws IOException {
		new CategoryOverlap().execute(new File(args[0]), new File(args[1]));
	}

	private Map<String, Integer> countedCategories = new HashMap<>();
	private Map<String, Integer> parsedCategories = new HashMap<>();
	private boolean categoryParserFirstLine = true;
	private char separator = '\t';
	private boolean replaceWhitespaces = true;

	private void execute(File countedCategoriesFile, File categoryParserFile) throws IOException {
		readCountedCategories(countedCategoriesFile);
		readCategoryParser(categoryParserFile);

		StringBuilder sb = new StringBuilder();
		for (Entry<String, Integer> entry : parsedCategories.entrySet()) {
			if (countedCategories.containsKey(entry.getKey())) {
				sb.append(entry.getValue()).append(separator);
				sb.append(countedCategories.get(entry.getKey())).append(separator);
				sb.append(entry.getKey()).append(separator);
				sb.append(System.lineSeparator());
			}
		}
		System.out.println(sb);
	}

	private void readCountedCategories(File file) throws IOException {
		try (Stream<String> stream = Files.lines(file.toPath())) {
			stream.forEach(this::parseCountedCategories);
		}
	}

	private void parseCountedCategories(String line) {
		// Format:
		// 2; Some, title
		String[] parts = line.split("; ");
		String title = parts[1];
		if (replaceWhitespaces) {
			title = title.replaceAll(" ", "_");
		}
		countedCategories.put(title, Integer.valueOf(parts[0]));
	}

	private void readCategoryParser(File file) throws IOException {
		try (Stream<String> stream = Files.lines(file.toPath())) {
			stream.forEach(this::parseCategoryParser);
		}
	}

	private void parseCategoryParser(String line) {
		// Format:
		// 21,"Some,_title"

		if (categoryParserFirstLine) {
			categoryParserFirstLine = false;
			return;
		}

		String[] parts = line.split(",\"");
		parsedCategories.put(parts[1].substring(0, parts[1].length() - 1), Integer.valueOf(parts[0]));
	}
}