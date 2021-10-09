package org.dice_research.eml4u.wikimediadumpextractor.content;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dice_research.eml4u.wikimediadumpextractor.utils.RegEx;
import org.dice_research.eml4u.wikimediadumpextractor.xml.Page;

/**
 * Text element.
 * 
 * In XML inside element mediawiki and element {@link Page}.
 * 
 * In files directly saved.
 *
 * @author Adrian Wilke
 */
public class Text {

	private static final Pattern PATTERN_CATEGORY = Pattern.compile(RegEx.CATEGORY);

	private String text;

	public Text(String text) {
		this.text = text;
	}

	public boolean isRedirect() {
		return text.startsWith("#REDIRECT");
	}

	@Override
	public String toString() {
		return text;
	}

	public Set<String> getAllCategories() {
		Set<String> cats = new HashSet<>();
		Matcher matcher = PATTERN_CATEGORY.matcher(text.toString());
		while (matcher.find()) {
			cats.add(matcher.group(1));
		}
		return cats;
	}

	public Set<String> search(Collection<String> terms, boolean caseInsensitive, boolean addLowerCase) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String term : terms) {
			if (first) {
				first = false;
			} else {
				sb.append("|");
			}
			sb.append(Pattern.quote(term));
		}

		Pattern pattern = null;
		if (caseInsensitive) {
			pattern = Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile(sb.toString());
		}
		Matcher matcher = pattern.matcher(text);
		Set<String> results = new TreeSet<>();
		while (matcher.find()) {
			if (addLowerCase) {
				results.add(matcher.group().toLowerCase());
			} else {
				results.add(matcher.group());
			}
		}

		return results;
	}
}