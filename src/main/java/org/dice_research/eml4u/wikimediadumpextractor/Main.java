package org.dice_research.eml4u.wikimediadumpextractor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Wikimedia dump extractor.
 * 
 * Main entry point, parses arguments and starts execution.
 *
 * @author Adrian Wilke
 */
public class Main {

	public static final int DEFAULT_THREADS = 3;
	public static final int DEFAULT_MIN_CATEGORY_SIZE = 10000;
	public static final String MODE_PAGES = "pages";
	public static final String MODE_SEARCH = "search";
	public static final String MODE_CATEGORIES = "categories";

	/**
	 * Main entry point.
	 */
	public static void main(String[] args) throws Exception {

		// Check if arguments were specified

		if (args.length == 0) {
			printHelp();
			System.exit(1);
		}

		// Mode pages

		if (args[0].equals(MODE_PAGES) || args[0].equals(MODE_SEARCH)) {
			long time = System.currentTimeMillis();

			File inFile = new File(args[1]);
			if (!inFile.canRead()) {
				System.err.println("Can not read file: " + inFile.getAbsolutePath());
				System.exit(1);
			}

			File outDirectory = new File(args[2]);
			if (outDirectory.exists() && !outDirectory.isDirectory()) {
				System.err.println("Not a directory: " + outDirectory.getAbsolutePath());
				System.exit(1);
			} else if (!outDirectory.exists()) {
				outDirectory.mkdirs();
			}

			String category = null;
			String search = null;
			if (args[0].equals(MODE_PAGES)) {
				if (args[3].startsWith("Category:")) {
					category = args[3];
				} else {
					category = "Category:" + args[3];
				}
			} else if (args[0].equals(MODE_SEARCH)) {
				search = args[3];
			}

			int threads = DEFAULT_THREADS;
			if (args.length > 4) {
				threads = Integer.parseInt(args[4]);
			}

			extractPages(inFile, outDirectory, category, search, threads);
			System.out.println("Seconds:  " + (System.currentTimeMillis() - time) / 1000.0);
		}

		// Mode categories

		else if (args[0].equals(MODE_CATEGORIES)) {

			File inFile = new File(args[1]);
			if (!inFile.canRead()) {
				System.err.println("Can not read file: " + inFile.getAbsolutePath());
				System.exit(1);
			}

			int minCategorySize = DEFAULT_MIN_CATEGORY_SIZE;
			if (args.length > 2) {
				minCategorySize = Integer.parseInt(args[2]);
			}

			printCategories(inFile, minCategorySize);
		}

		System.exit(0);
	}

	private static void printHelp() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Usage: java -jar WikimediaDumpExtractor.jar");

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(" ");
		stringBuilder.append(MODE_PAGES);
		stringBuilder.append("      <input XML file> <output directory> <category>   [number of threads, default ");
		stringBuilder.append(DEFAULT_THREADS);
		stringBuilder.append("]");

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(" ");
		stringBuilder.append(MODE_SEARCH);
		stringBuilder.append("     <input XML file> <output directory> <searchterm> [number of threads, default ");
		stringBuilder.append(DEFAULT_THREADS);
		stringBuilder.append("]");

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(" ");
		stringBuilder.append(MODE_CATEGORIES);
		stringBuilder.append(" <input SQL file> [minimum category size, default ");
		stringBuilder.append(DEFAULT_MIN_CATEGORY_SIZE);
		stringBuilder.append("]");

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("https://github.com/EML4U/WikimediaDumpExtractor");
		System.err.println(stringBuilder.toString());
	}

	private static void extractPages(File inFile, File outDirectory, String category, String search, int threads)
			throws Exception {
		System.out.println("In:       " + inFile.getAbsolutePath());
		System.out.println("Category: " + category);
		System.out.println("Search:   " + search);
		System.out.println("Threads:  " + threads);

		PageHandlerFactory pageHandlerFactory = new PageHandlerFactory();
		pageHandlerFactory.setCategory(category);
		pageHandlerFactory.setSearch(search);
		pageHandlerFactory.setOutDirectory(outDirectory);

		XmlParser xmlParser = new XmlParser();
		xmlParser.setPageHandlerFactory(pageHandlerFactory);
		xmlParser.setExecutorService(Executors.newFixedThreadPool(threads));
		xmlParser.extract(inFile.getAbsolutePath());

		System.out.println("Out:      " + outDirectory.getAbsolutePath());
	}

	private static void printCategories(File inFile, int minCategorySize) throws IOException {
		new CategoryParser().printCategories(inFile, minCategorySize);
	}
}