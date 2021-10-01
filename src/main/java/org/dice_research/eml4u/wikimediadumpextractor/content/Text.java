package org.dice_research.eml4u.wikimediadumpextractor.content;

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

	private String text;

	public Text(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public boolean isRedirect() {
		return text.startsWith("#REDIRECT");
	}

	@Override
	public String toString() {
		return text;
	}
}