package org.dice_research.eml4u.wikimediadumpextractor.utils;

/**
 * Regular expressions.
 * 
 * @see https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/regex/Pattern.html
 *
 * @author Adrian Wilke
 */
public abstract class RegEx {

	/**
	 * Gets category names in links. E.g. [[Category:XYZ]] or [[Category:XYZ|Xyz]].
	 */
	public static final String CATEGORY = "\\[\\[(Category:.*?)(\\]\\]|\\|)";

}