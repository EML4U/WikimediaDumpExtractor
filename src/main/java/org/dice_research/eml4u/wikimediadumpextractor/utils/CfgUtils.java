package org.dice_research.eml4u.wikimediadumpextractor.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.dice_research.eml4u.wikimediadumpextractor.Cfg;
import org.dice_research.eml4u.wikimediadumpextractor.Main;

public abstract class CfgUtils {

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

	public static File getOutputDirectoryJob() {
		String subDir = Cfg.INSTANCE.getAsFile(Cfg.INPUT_FILE).getName();
		int lastDotIndex = subDir.lastIndexOf(".");
		if (lastDotIndex > 0) {
			subDir = subDir.substring(0, lastDotIndex);
		}

		File directory = new File(Cfg.INSTANCE.getAsFile(Cfg.OUTPUT_DIR), subDir);
		directory.mkdirs();

		return directory;
	}

	public static File getOutputDirectoryTexts() {
		File directory = new File(getOutputDirectoryJob(), Cfg.DEFAULT_TEXT_DIRECTORY);
		directory.mkdirs();
		return directory;
	}
}