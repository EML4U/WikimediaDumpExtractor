package org.dice_research.eml4u.wikimediadumpextractor.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CategoryOverview {

	public static void main(String[] args) throws IOException {
		if (args.length == 3) {
			new CategoryOverview().execute(new File(args[0]), new File(args[1]), new File(args[2]));
		}
	}

	public static final boolean WRITE_FILE = true;
	public static final String SEPARATOR = ";";

	private void execute(File catSqlCountFileA, File catSqlCountFileB, File pageCatCountFile) throws IOException {
		Map<String, Integer> pageCatCountMap = readPageCatCount(pageCatCountFile, ";");
		Map<String, Integer> catSqlCountMapA = readCatSqlCount(catSqlCountFileA, ";", pageCatCountMap.keySet());
		Map<String, Integer> catSqlCountMapB = readCatSqlCount(catSqlCountFileB, ";", pageCatCountMap.keySet());

		StringBuilder sb = new StringBuilder();
		sb.append(catSqlCountFileA.getParentFile().getName());
		sb.append(SEPARATOR);
		sb.append(catSqlCountFileB.getParentFile().getName());
		sb.append(SEPARATOR);
		sb.append(pageCatCountFile.getName());
		sb.append(SEPARATOR);
		sb.append("a b");
		sb.append(SEPARATOR);
		sb.append("a c");
		sb.append(SEPARATOR);
		sb.append("b c");
		sb.append(SEPARATOR);
		sb.append("category");
		sb.append(System.lineSeparator());

		for (Entry<String, Integer> e : pageCatCountMap.entrySet()) {
			if (!catSqlCountMapA.containsKey(e.getKey())) {
				continue;
			}
			if (!catSqlCountMapB.containsKey(e.getKey())) {
				continue;
			}

			Integer a = catSqlCountMapA.get(e.getKey());
			Integer b = catSqlCountMapB.get(e.getKey());
			Integer c = e.getValue();

			double ratioAB = 1.0 * a / b;
			// if (ratioAB > 1) {
			// ratioAB = 1 / ratioAB;
			// }
			ratioAB = Math.round(ratioAB * 1000) / 1000.0;

			double ratioAC = 1.0 * a / c;
			// if (ratioAC > 1) {
			// ratioAC = 1 / ratioAC;
			// }
			ratioAC = Math.round(ratioAC * 1000) / 1000.0;

			double ratioBC = 1.0 * b / c;
			// if (ratioBC > 1) {
			// ratioBC = 1 / ratioBC;
			// }
			ratioBC = Math.round(ratioBC * 1000) / 1000.0;

			sb.append(a);
			sb.append(SEPARATOR);
			sb.append(b);
			sb.append(SEPARATOR);
			sb.append(c);
			sb.append(SEPARATOR);
			sb.append(ratioAB);
			sb.append(SEPARATOR);
			sb.append(ratioAC);
			sb.append(SEPARATOR);
			sb.append(ratioBC);
			sb.append(SEPARATOR);
			sb.append(e.getKey());
			sb.append(System.lineSeparator());
		}

		if (WRITE_FILE) {
			File outFile = new File(pageCatCountFile.getParentFile(), "category-overview.csv");
			Files.write(outFile.toPath(), sb.toString().getBytes());
			System.out.println(outFile.getAbsolutePath());
		} else {
			System.out.println(sb);
		}
	}

	/**
	 * Reads file consisting of lines like '123; Text'.
	 * 
	 * Based on {@link Files#readAllLines(java.nio.file.Path)}
	 */
	private Map<String, Integer> readPageCatCount(File file, String separator) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
			Map<String, Integer> result = new HashMap<>();
			for (;;) {
				String line = reader.readLine();
				if (line == null || line.isBlank())
					break;
				int index = line.indexOf(separator);
				result.put(line.substring(index + separator.length()).replaceAll(" ", "_"),
						Integer.valueOf(line.substring(0, index)));
			}
			return result;
		}
	}

	/**
	 * Reads file consisting of lines like '123; Text'.
	 * 
	 * Adds only elements with key are in keys.
	 * 
	 * Based on {@link Files#readAllLines(java.nio.file.Path)}
	 */
	private Map<String, Integer> readCatSqlCount(File file, String separator, Collection<String> keys)
			throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
			Map<String, Integer> result = new HashMap<>();
			for (;;) {
				String line = reader.readLine();
				if (line == null || line.isBlank())
					break;
				int index = line.indexOf(separator);
				String key = line.substring(index + separator.length());
				if (keys.contains(key)) {
					result.put(key, Integer.valueOf(line.substring(0, index)));
				}
			}
			return result;
		}
	}
}