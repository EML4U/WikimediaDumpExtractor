package org.dice_research.eml4u.wikimediadumpextractor.tools;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.dice_research.eml4u.wikimediadumpextractor.Cfg;
import org.dice_research.eml4u.wikimediadumpextractor.io.FileChecks;
import org.dice_research.eml4u.wikimediadumpextractor.xml.Page;
import org.dice_research.eml4u.wikimediadumpextractor.xml.PageHandler;
import org.dice_research.eml4u.wikimediadumpextractor.xml.XmlParser;

/**
 * Checks first entries of XML dump for categories appearing multiple times.
 * 
 * Also searches for predefined terms.
 *
 * @author Adrian Wilke
 */
public class ExampleSearcher implements PageHandler {

	/**
	 * enwiki-20080103-pages-articles.xml
	 * 
	 * 103: Category:Social philosophy 2 [Anarchism 12, Altruism 336]
	 * 
	 * 125: [social philosophy] Anarchism 12, [altruism, social philosophy] Altruism
	 * 336, [altruism] Ayn Rand 339, [altruism] Atlas Shrugged 568
	 */
	public static final int MAX_PAGES = 125;
	public static final String[] SEARCH_TERMS = new String[] { "Altruism", "Social philosophy" };

	private int counter = 1;
	private Map<String, List<String>> cats = new HashMap<>();

	public static void main(String[] args) throws Exception {
		if (args.length >= 2) {
			ExampleSearcher exampleCreator = new ExampleSearcher();
			Cfg.INSTANCE.set(Cfg.INPUT_FILE, FileChecks.checkFileIn(args[0], 1));
			Cfg.INSTANCE.set(Cfg.OUTPUT_DIR, FileChecks.checkDirectoryOut(args[1], 1));
			new XmlParser().extract(new File(args[0]), exampleCreator);
		}
	}

	@Override
	public void handlePage(Page page) {
		for (String cat : page.getTextObject().getAllCategories()) {
			if (!cats.containsKey(cat)) {
				cats.put(cat, new LinkedList<>());
			}
			cats.get(cat).add(page.getTitle() + " " + page.getId());
		}
		if (counter++ >= MAX_PAGES) {
			System.out.println(this);
			System.exit(0);
		}

		Set<String> terms = page.getTextObject().search(Arrays.asList(SEARCH_TERMS), true, true);
		if (!terms.isEmpty()) {
			System.out.println(terms + " " + page.getTitle() + " " + page.getId());
		}
	}

	@Override
	public String toString() {
		Set<String> pages = new TreeSet<>();
		StringBuilder sb = new StringBuilder();
		sb.append(System.lineSeparator());
		for (Entry<String, List<String>> e : cats.entrySet()) {
			if (e.getValue().size() > 2) {
				pages.addAll(e.getValue());
				sb.append(e.getKey()).append(" ").append(e.getValue().size()).append(" ").append(e.getValue())
						.append(System.lineSeparator());
			}
		}
		sb.append(System.lineSeparator());
		for (Entry<String, List<String>> e : cats.entrySet()) {
			if (e.getValue().size() > 1 && e.getValue().size() < 3) {
				pages.addAll(e.getValue());
				sb.append(e.getKey()).append(" ").append(e.getValue().size()).append(" ").append(e.getValue())
						.append(System.lineSeparator());
			}
		}
		sb.append(System.lineSeparator());
		for (String page : pages) {
			sb.append(page);
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
}