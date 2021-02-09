package org.dice_research.eml4u.moveequalfilenames;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Move Equal Filenames.
 * 
 * Main entry point.
 * 
 * Moves files, if their names are contained in every directory.
 *
 * @author Adrian Wilke
 */
public class Main {

	/**
	 * Main entry point.
	 */
	public static void main(String[] args) {
		Main main = new Main();

		if (args.length != 2) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Usage: java -jar MoveEqualFilenames.jar <input directories> <output directory>");
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append("       <input directories> must be separated by |");
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append("https://github.com/EML4U/WikimediaDumpExtractor");
			System.err.println(stringBuilder.toString());
			System.exit(1);
		}

		String[] inputDirectories = args[0].split("\\|");
		List<File> directories = new LinkedList<>();
		for (String inputDirectory : inputDirectories) {
			File directory = new File(inputDirectory.trim());
			if (!directory.canRead()) {
				System.err.println("Can not read directory: " + directory.getAbsolutePath());
				System.exit(1);
			}
			directories.add(directory);
		}

		File targetDirectory = new File(args[1]);

		long time = System.currentTimeMillis();
		System.out.println("Input directories: " + directories);

		List<String> filenames = main.getFilenamesContainedInAllDirectories(directories);

		Map<File, Map<File, Integer>> results = null;
		try {
			results = main.moveFiles(directories, filenames, targetDirectory);
		} catch (IOException e) {
			System.err.println("Could not move files: " + e.getMessage());
			System.exit(1);
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (Entry<File, Map<File, Integer>> result : results.entrySet()) {
			for (Entry<File, Integer> entry : result.getValue().entrySet()) {
				stringBuilder.append(entry.getValue());
				stringBuilder.append(" ");
				stringBuilder.append(entry.getKey());
				stringBuilder.append(" <- ");
				stringBuilder.append(result.getKey());
				stringBuilder.append(System.lineSeparator());
			}
		}
		System.out.print(stringBuilder.toString());
		System.out.println("Output directory:  " + targetDirectory);
		System.out.println("Seconds:           " + (System.currentTimeMillis() - time) / 1000.0);
		System.exit(0);
	}

	private FileFilter noDirectoryFilter = new FileFilter() {
		public boolean accept(File file) {
			if (file.isFile()) {
				return true;
			} else {
				return false;
			}
		}
	};

	public Map<File, Map<File, Integer>> moveFiles(List<File> directories, List<String> filenames, File targetDirectory)
			throws IOException {
		Map<File, Map<File, Integer>> results = new HashMap<>();
		for (File directory : directories) {
			File targetSubDirectory = new File(targetDirectory, directory.getName());
			if (!targetSubDirectory.exists()) {
				targetSubDirectory.mkdirs();
			}
			int i = 0;
			for (String filename : filenames) {
				Files.move(new File(directory.getAbsoluteFile(), filename).toPath(),
						new File(targetSubDirectory.getAbsoluteFile(), filename).toPath(),
						StandardCopyOption.ATOMIC_MOVE);
				i++;
			}
			Map<File, Integer> resultsCounter = new HashMap<>();
			resultsCounter.put(targetSubDirectory, i);
			results.put(directory.getAbsoluteFile(), resultsCounter);
		}
		return results;
	}

	public List<String> getFilenamesContainedInAllDirectories(List<File> directories) {

		// To only traverse directories once:
		// Create index with directories and contained files
		Map<File, List<String>> index = new HashMap<>();
		for (File directory : directories) {
			index.put(directory, new LinkedList<>());
			List<String> list = index.get(directory);
			for (File file : directory.listFiles(noDirectoryFilter)) {
				list.add(file.getName());
			}
		}

		// Use smallest directory to reduce comparisons
		File smallestDirectory = index.keySet().iterator().next();
		for (Entry<File, List<String>> entry : index.entrySet()) {
			if (entry.getValue().size() < index.get(smallestDirectory).size()) {
				smallestDirectory = entry.getKey();
			}
		}

		// Non-smallest directories to avoid index comparisons
		List<File> otherDirectories = new ArrayList<File>(directories.size() - 1);
		for (File directory : directories) {
			if (!directory.equals(smallestDirectory)) {
				otherDirectories.add(directory);
			}
		}

		// Add to results, if file contained in all directories
		List<String> files = new LinkedList<>();
		fileLoop: for (File file : smallestDirectory.listFiles()) {
			for (File otherDirectory : otherDirectories) {
				if (!index.get(otherDirectory).contains(file.getName())) {
					continue fileLoop;
				}
			}
			files.add(file.getName());
		}
		return files;
	}

	// Note: Not used
	public String getCommonPrefix(List<File> directories) {
		String commonPrefix = directories.get(0).getAbsolutePath();
		if (directories.size() > 1) {
			for (int i = 0; i < directories.size() - 1; i++) {
				String prefix = getCommonPrefix(directories.get(i).getAbsolutePath(),
						directories.get(i + 1).getAbsolutePath());
				if (prefix.length() < commonPrefix.length()) {
					commonPrefix = prefix;
				}
			}
		}
		return commonPrefix;
	}

	// Note: Not used
	private String getCommonPrefix(String a, String b) {
		// Based on https://stackoverflow.com/a/8033983
		int minLength = Math.min(a.length(), b.length());
		for (int i = 0; i < minLength; i++) {
			if (a.charAt(i) != b.charAt(i)) {
				return a.substring(0, i);
			}
		}
		return a.substring(0, minLength);
	}
}