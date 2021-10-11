package org.dice_research.eml4u.wikimediadumpextractor.tools;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.dice_research.eml4u.wikimediadumpextractor.Main;

/**
 * Creates example results.
 *
 * @author Adrian Wilke
 */
public class ExampleRun {

	/**
	 * Typically, there is no need to run this again.
	 */
	public static boolean RUN = false;

	public static void main(String[] args) {

		if (!RUN) {
			return;
		}

		List<String> list = new LinkedList<>();
		list.add("pages");
		list.add(new File("src/test/resources/enwiki-20080103-pages-articles-example.xml").getAbsolutePath());
		list.add(new File("src/test/resources/").getAbsolutePath());
		list.add("Social philosophy");
		list.add("altruism");

		String[] arguments = list.toArray(new String[0]);
		System.out.println(Arrays.toString(arguments));
		// [pages,
		// /tmp/src/test/resources/enwiki-20080103-pages-articles-example.xml,
		// /tmp/src/test/resources,
		// Social philosophy,
		// altruism]

		Main.main(arguments);
	}

}