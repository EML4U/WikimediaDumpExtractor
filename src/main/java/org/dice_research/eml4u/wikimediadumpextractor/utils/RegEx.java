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

	/**
	 * (1.) replace non-filename-chars with whitespace. (2.) trim (3.) white spaces
	 * to underline.
	 */
	public static final String FILENAME_NOT_ALLOWED_TO_WHITESPACE = "[^A-Za-z0-9 -]+";
	public static final String FILENAME_WHITESPACES_TO_UNDERLINE = "[ ]+";

	/**
	 * Transforms string in filename format.
	 */
	public static String getFilenameString(String string) {
		return string.replaceAll(FILENAME_NOT_ALLOWED_TO_WHITESPACE, " ").trim()
				.replaceAll(FILENAME_WHITESPACES_TO_UNDERLINE, "_");
	}
}