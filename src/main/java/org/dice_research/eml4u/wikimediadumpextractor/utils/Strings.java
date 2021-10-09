package org.dice_research.eml4u.wikimediadumpextractor.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.dice_research.eml4u.wikimediadumpextractor.Cfg;
import org.dice_research.eml4u.wikimediadumpextractor.Main;

public abstract class Strings {

	public static List<String> getCategories() {
		if (Cfg.INSTANCE.getAsString(Cfg.CATEGORIES) == null) {
			return new ArrayList<String>(0);
		} else {
			return Arrays.asList(Cfg.INSTANCE.getAsString(Cfg.CATEGORIES).split(Pattern.quote(Main.SEPARATOR)));
		}
	}

	public static List<String> getSearchTerms() {
		if (Cfg.INSTANCE.getAsString(Cfg.SEARCH) == null) {
			return new ArrayList<String>(0);
		} else {
			return Arrays.asList(Cfg.INSTANCE.getAsString(Cfg.SEARCH).split(Pattern.quote(Main.SEPARATOR)));
		}
	}
}