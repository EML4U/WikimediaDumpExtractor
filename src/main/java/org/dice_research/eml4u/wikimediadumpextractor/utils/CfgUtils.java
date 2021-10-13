package org.dice_research.eml4u.wikimediadumpextractor.utils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.dice_research.eml4u.wikimediadumpextractor.Cfg;
import org.dice_research.eml4u.wikimediadumpextractor.Main;

public abstract class CfgUtils {

	private static Set<String> cachedCategories = null;
	private static Set<String> cachedSearchTerms = null;
	private static Set<String> cachedIds = null;
	private static File cachedOutputDirectoryJob = null;
	private static File cachedOutputDirectoryTexts = null;

	public static Set<String> getCategories() {
		if (cachedCategories != null) {
			return cachedCategories;
		}
		if (Cfg.INSTANCE.getAsString(Cfg.CATEGORIES) == null) {
			cachedCategories = new HashSet<String>(0);
		} else {
			cachedCategories = new HashSet<String>(
					Arrays.asList(Cfg.INSTANCE.getAsString(Cfg.CATEGORIES).split(Pattern.quote(Main.SEPARATOR))));
		}
		return cachedCategories;
	}

	public static Set<String> getSearchTerms() {
		if (cachedSearchTerms != null) {
			return cachedSearchTerms;
		}
		if (Cfg.INSTANCE.getAsString(Cfg.SEARCH) == null) {
			cachedSearchTerms = new HashSet<String>(0);
		} else {
			cachedSearchTerms = new HashSet<String>(
					Arrays.asList(Cfg.INSTANCE.getAsString(Cfg.SEARCH).split(Pattern.quote(Main.SEPARATOR))));
		}
		return cachedSearchTerms;
	}

	public static Set<String> getIds() {
		if (cachedIds != null) {
			return cachedIds;
		}
		if (Cfg.INSTANCE.getAsString(Cfg.IDS) == null) {
			cachedIds = new HashSet<String>(0);
		} else {
			cachedIds = new HashSet<String>(
					Arrays.asList(Cfg.INSTANCE.getAsString(Cfg.IDS).split(Pattern.quote(Main.SEPARATOR))));
		}
		return cachedIds;
	}

	public static File getOutputDirectoryJob() {
		if (cachedOutputDirectoryJob != null) {
			return cachedOutputDirectoryJob;
		}
		String subDir = Cfg.INSTANCE.getAsFile(Cfg.INPUT_FILE).getName();
		int lastDotIndex = subDir.lastIndexOf(".");
		if (lastDotIndex > 0) {
			subDir = subDir.substring(0, lastDotIndex);
		}

		File directory = new File(Cfg.INSTANCE.getAsFile(Cfg.OUTPUT_DIR), subDir);
		directory.mkdirs();

		cachedOutputDirectoryJob = directory;
		return cachedOutputDirectoryJob;
	}

	public static File getOutputDirectoryTexts() {
		if (cachedOutputDirectoryTexts != null) {
			return cachedOutputDirectoryTexts;
		}
		File directory = new File(getOutputDirectoryJob(), Cfg.DEFAULT_TEXT_SUBDIRECTORY);
		directory.mkdirs();

		cachedOutputDirectoryTexts = directory;
		return cachedOutputDirectoryTexts;
	}
}