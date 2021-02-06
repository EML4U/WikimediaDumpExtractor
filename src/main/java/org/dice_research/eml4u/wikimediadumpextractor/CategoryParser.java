package org.dice_research.eml4u.wikimediadumpextractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Parses category SQL files (e.g. enwiki-YYYYMMDD-category.sql).
 * 
 * Prints categories and related number of pages in CSV format.
 * 
 * Set threshold using {@link #MIN_CATEGORY_SIZE}.
 * 
 * (Not integrated into main jar)
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
	private static final int MIN_CATEGORY_SIZE = 10000;

	private SortedSet<Element> categories = new TreeSet<>();

	/**
	 * Main method to run.
	 * 
	 * @param args [0] enwiki-YYYYMMDD-category.sql like to be found in dumps
	 */
	public static void main(String[] args) throws IOException {
		CategoryParser categoryParser = new CategoryParser().readFile(new File(args[0]));
		System.out.println("Number of Pages, \"Wikipedia category title\"");
		for (Element element : categoryParser.categories) {
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
		try {
			inputStream = new FileInputStream(file);
			scanner = new Scanner(inputStream);
			while (scanner.hasNextLine()) {
				boolean valuesLineParsed = handleLine(scanner.nextLine());
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
	private boolean handleLine(String line) {
		if (line.startsWith(LINE_START)) {
			for (String part : line.split("\\),\\(")) {
				// Special case: First element
				if (part.startsWith(LINE_START)) {
					part = part.substring(LINE_START.length());
				}
				// Special case: Last element
				if (part.endsWith(");")) {
					part = part.substring(0, part.length() - 2);
				}

				String[] values = part.split(",");
				if (values.length > 5) {
					// TODO Special case to handle: Name contains ','
					continue;
				}

				// SQL table category elements:
				// `cat_id` int(10)
				// `cat_title` varbinary(255)
				// `cat_pages` int(11)
				// `cat_subcats` int(11)
				// `cat_files` int(11)
				Element element = new Element(values[1], Integer.parseInt(values[2]));
				if (element.pages >= MIN_CATEGORY_SIZE) {
					categories.add(new Element(values[1].replace('\'', '"'), Integer.parseInt(values[2])));
				}
			}
			return true;
		} else {
			return false;
		}
	}
}