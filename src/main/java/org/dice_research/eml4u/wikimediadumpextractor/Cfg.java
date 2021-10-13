package org.dice_research.eml4u.wikimediadumpextractor;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.dice_research.eml4u.wikimediadumpextractor.utils.Strings;

/**
 * Configuration singleton.
 *
 * @author Adrian Wilke
 */
public enum Cfg {

	// Singleton, see
	// https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples#enum-singleton
	// https://dzone.com/articles/java-singletons-using-enum
	INSTANCE;

	public static final String DEFAULT_TEXT_SUBDIRECTORY = "text";

	public static final String MODE = "mode";
	public static final String MODE_PAGES = "pages";
	public static final String MODE_CATEGORIES = "categories";

	public static final String BEGIN_TIME = "begin";
	public static final String CATEGORIES = "categories";
	public static final String INPUT_FILE = "input";
	public static final String OUTPUT_DIR = "output";
	public static final String SEARCH = "search";
	public static final String IDS = "ids";

	public static final String INFO_END_TIME = "end";
	public static final String INFO_DURATION = "duration";

	public static final String INFO_XML_TIME_PARSE = "time-parse";
	public static final String INFO_XML_TIME_EXTRACT = "time-extract";
	public static final String INFO_XML_READ_PAGES = "pages";
	public static final String INFO_XML_INDEX_BEGIN = "index-begin";
	public static final String INFO_XML_INDEX_END = "index-end";

	private Map<String, Object> map = new HashMap<>();

	public void set(String key, Object value) {
		map.put(key, value);
	}

	public Object get(String key) {
		return map.get(key);
	}

	public String getAsString(String key) {
		return (String) get(key);
	}

	public File getAsFile(String key) {
		return (File) get(key);
	}

	public long getAsLong(String key) {
		return (Long) get(key);
	}

	/**
	 * Provides ordered map with key titles.
	 */
	private Map<String, String> getInfoMap() {
		Map<String, String> map = new LinkedHashMap<>();

		map.put(INPUT_FILE, "Input file");
		map.put(OUTPUT_DIR, "Output directory");
		map.put(MODE, "Mode");
		map.put(CATEGORIES, "Categories");
		map.put(SEARCH, "Search terms");
		map.put(IDS, "IDs");
		map.put(BEGIN_TIME, "Begin time");
		map.put(INFO_END_TIME, "End time");
		map.put(INFO_DURATION, "Duration (sec)");
		map.put(INFO_XML_TIME_PARSE, "Reading XML (sec)");
		map.put(INFO_XML_TIME_EXTRACT, "Extracting XML (sec)");
		map.put(INFO_XML_INDEX_BEGIN, "Index size before");
		map.put(INFO_XML_INDEX_END, "Index size");
		map.put(INFO_XML_READ_PAGES, "XML pages read");

		int maxLength = getMaxValueLength(map);
		for (Entry<String, String> e : map.entrySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append(e.getValue()).append(": ");
			for (int i = 0; i < maxLength - e.getValue().length(); i++) {
				sb.append(" ");
			}
			e.setValue(sb.toString());
		}
		return map;
	}

	@Override
	public String toString() {
		Map<String, Object> values = new TreeMap<>(this.map);
		Map<String, String> info = getInfoMap();

		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> e : info.entrySet()) {
			if (values.containsKey(e.getKey()) && values.get(e.getKey()) != null) {
				sb.append(e.getValue());
				sb.append(formatValue(e.getKey(), values.get(e.getKey())));
				sb.append(System.lineSeparator());
				values.remove(e.getKey());
			}
		}

		int maxLength = getMaxValueLength(info);
		for (Entry<String, Object> e : values.entrySet()) {
			if (e.getValue() != null) {
				sb.append(e.getKey().substring(0, 1).toUpperCase() + e.getKey().substring(1));
				sb.append(": ");
				for (int i = 0; i < maxLength - e.getKey().toString().length() - 2; i++) {
					sb.append(" ");
				}
				sb.append(e.getValue());
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}

	private int getMaxValueLength(Map<?, String> map) {
		int maxLength = 0;
		for (Entry<?, String> e : map.entrySet()) {
			if (e.getValue().length() > maxLength) {
				maxLength = e.getValue().length();
			}
		}
		return maxLength;
	}

	private String formatValue(String key, Object value) {
		if (key.equals(Cfg.BEGIN_TIME) || key.equals(Cfg.INFO_END_TIME)) {
			return Strings.getReadableTimpestamp((Long) value);
		} else if (key.equals(Cfg.INFO_DURATION) || key.equals(Cfg.INFO_XML_TIME_EXTRACT)
				|| key.equals(Cfg.INFO_XML_TIME_PARSE)) {
			return String.valueOf((1.0 * Long.parseLong(value.toString())) / 1000);
		} else {
			return value.toString();
		}
	}
}