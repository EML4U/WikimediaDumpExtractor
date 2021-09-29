package org.dice_research.eml4u.wikimediadumpextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Parses category SQL files (e.g. enwiki-YYYYMMDD-category.sql).
 * 
 * Prints categories and related number of pages in CSV format.
 * 
 * Usage: {@link #printCategories(File, int)}.
 *
 * @author Adrian Wilke
 */
public class CategoryParser {

	/**
	 * Inner class to store sorted elements.
	 */
	private class Element implements Comparable<Element> {

		private String title;
		private int pages;

		public Element(String title, int pages) {
			this.title = title;
			this.pages = pages;
		}

		public void incrementPages() {
			this.pages++;
		}

		@Override
		public int compareTo(Element o) {
			int result = Integer.compare(o.pages, this.pages);
			if (result != 0) {
				return result;
			} else {
				return this.title.compareToIgnoreCase(o.title);
			}
		}

		@Override
		public String toString() {
			return pages + "," + title;
		}
	}

	private static final boolean LIMIT_TO_FIRST_LINE = false;
	private static final String LINE_START = "INSERT INTO `category` VALUES (";
	private static final String LINE_START_OLD = "INSERT INTO `categorylinks` VALUES ("; // e.g. enwiki-20080103

	private int mode = -1;
	private int mode_category_sql = 1;
	private int mode_categorylinks_sql = 2;

	private int minCategorySize;
	private SortedSet<Element> categories = new TreeSet<>();
	private Map<String, Element> categoriesMap = new HashMap<>(); // Used for old categorylinks.sql

	/**
	 * Prints categories found in the given file.
	 * 
	 * @param inFile          enwiki-YYYYMMDD-category.sql like to be found in dumps
	 * @param minCategorySize Minimum size of categories to print
	 */
	public void printCategories(File inFile, int minCategorySize) throws IOException {
		this.minCategorySize = minCategorySize;
		readFile(inFile);

		// Map to default SortedSet
		if (mode == mode_categorylinks_sql) {
			categories.addAll(categoriesMap.values());
		}

		System.out.println("Number of Pages, \"Wikipedia category title\"");
		for (Element element : categories) {
			System.out.println(element);
		}
	}

	/**
	 * Reads file line by line.
	 */
	@SuppressWarnings("unused")
	private CategoryParser readFile(File file) throws IOException {
		// Based on: https://www.baeldung.com/java-read-lines-large-file
		FileInputStream inputStream = null;
		Scanner scanner = null;
		int line = 0;
		try {
			inputStream = new FileInputStream(file);
			scanner = new Scanner(inputStream);
			while (scanner.hasNextLine()) {
				line++;
				boolean valuesLineParsed = handleLine(scanner.nextLine(), line);
				if (LIMIT_TO_FIRST_LINE && valuesLineParsed) {
					return this;
				}
			}
			if (scanner.ioException() != null) {
				throw scanner.ioException();
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (scanner != null) {
				scanner.close();
			}
		}
		return this;
	}

	/**
	 * Parses single line.
	 * 
	 * @return true, if line starts with {@value #LINE_START}.
	 */
	private boolean handleLine(String line, int lineNumber) {

		// Distinguish between 2 variants
		if (mode == -1 && line.startsWith(LINE_START)) {
			mode = mode_category_sql;
		} else if (mode == -1 && line.startsWith(LINE_START_OLD)) {
			mode = mode_categorylinks_sql;
		}

		// Newer data variant
		if (mode == mode_category_sql && line.startsWith(LINE_START)) {
			int partNumber = -1;
			for (String part : line.split("\\),\\(")) {
				partNumber++;

				// Special case: First element
				if (part.startsWith(LINE_START)) {
					part = part.substring(LINE_START.length());
				}
				// Special case: Last element
				if (part.endsWith(");")) {
					part = part.substring(0, part.length() - 2);
				}

				String[] values = part.split(",");

				if (values.length == 6) {
					values[1] = values[1] + values[2];
					values[2] = values[3];
					values[3] = values[4];
					values[4] = values[5];
					values[5] = "";
				}

				if (values.length > 5 && !values[5].isEmpty()) {
					System.err.println(
							"Too many elements, skipping line/part " + lineNumber + " " + partNumber + " " + part);
					continue;
				}

				// SQL table category elements:
				// `cat_id` int(10)
				// `cat_title` varbinary(255)
				// `cat_pages` int(11)
				// `cat_subcats` int(11)
				// `cat_files` int(11)
				Element element = new Element(values[1], Integer.parseInt(values[2]));
				if (element.pages >= minCategorySize) {
					categories.add(new Element(values[1].replace('\'', '"'), Integer.parseInt(values[2])));
				}
			}
			return true;
		}

		// Older data variant
		else if (mode == mode_categorylinks_sql && line.startsWith(LINE_START_OLD)) {
			int partNumber = -1;
			for (String part : line.split("\\),\\(")) {
				partNumber++;

				// Special case: First element
				if (part.startsWith(LINE_START_OLD)) {
					part = part.substring(LINE_START_OLD.length());
				}
				// Special case: Last element
				if (part.endsWith(");")) {
					part = part.substring(0, part.length() - 2);
				}

				String[] values = part.split(",");

				if (values.length == 6) {
					values[1] = values[1] + values[2];
					values[2] = values[3];
					values[3] = values[4];
					values[4] = values[5];
					values[5] = "";
				}

				if (values.length > 5 && !values[5].isEmpty()) {
					System.err.println(
							"Too many elements, skipping line/part " + lineNumber + " " + partNumber + " " + part);
					continue;
				}

				// SQL table category elements:
				// `cl_from` int(8) unsigned NOT NULL default '0',
				// `cl_to` varchar(255) binary NOT NULL default '',
				// `cl_sortkey` varchar(86) binary NOT NULL default '',
				// `cl_timestamp` timestamp(14) NOT NULL,

				String title = values[1].replace('\'', '"');
				if (categoriesMap.containsKey(title)) {
					categoriesMap.get(title).incrementPages();
				} else {
					categoriesMap.put(title, new Element(title, 1));
				}

			}
			return true;
		}

		else {
			return false;
		}
	}
}