package org.dice_research.eml4u.wikimediadumpextractor;

import java.io.File;
import java.util.concurrent.Executors;

/**
 * Wikimedia dump extractor.
 * 
 * Main entry point, parses arguments and starts execution.
 *
 * @author Adrian Wilke
 */
public class Main {

	/**
	 * Main entry point.
	 */
	public static void main(String[] args) throws Exception {

		long time = System.currentTimeMillis();
		XmlParser xmlParser = new XmlParser();
		PageHandlerFactory pageHandlerFactory = new PageHandlerFactory();
		xmlParser.setPageHandlerFactory(pageHandlerFactory);

		File inFile = null;
		File outDirectory = null;
		String category = null;
		int threads = 1;

		if (args.length < 3) {
			System.err.println(
					"Please provide: <input XML file> <output directory> <category> <optional number of threads>");
			System.exit(1);

		} else {
			inFile = new File(args[0]);
			if (!inFile.canRead()) {
				System.err.println("Can not read file: " + inFile.getAbsolutePath());
				System.exit(1);
			}

			outDirectory = new File(args[1]);
			if (outDirectory.exists() && !outDirectory.isDirectory()) {
				System.err.println("Not a directory: " + outDirectory.getAbsolutePath());
				System.exit(1);
			} else if (!outDirectory.exists()) {
				outDirectory.mkdirs();
			}
			pageHandlerFactory.setOutDirectory(outDirectory);

			if (args[2].startsWith("Category:")) {
				category = args[2];
			} else {
				category = "Category:" + args[2];
			}
			pageHandlerFactory.setCategory(category);

			if (args.length > 3) {
				threads = Integer.parseInt(args[3]);
			}
			xmlParser.setExecutorService(Executors.newFixedThreadPool(threads));
		}

		System.out.println("In:       " + inFile.getAbsolutePath());
		System.out.println("Category: " + category);
		System.out.println("Threads:  " + threads);

		xmlParser.extract(inFile.getAbsolutePath());

		System.out.println("Out:      " + outDirectory.getAbsolutePath());
		System.out.println("Seconds:  " + (System.currentTimeMillis() - time) / 1000.0);
		System.exit(0);
	}
}