package org.dice_research.eml4u.wikimediadumpextractor.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dice_research.eml4u.wikimediadumpextractor.Cfg;

public class FileMover {

	public static void main(String[] args) throws IOException {
		new FileMover().execute(args);
	}

	private void execute(String[] args) throws IOException {
		File sourceDirA = new File(args[0]);
		File sourceDirB = new File(args[1]);
		File indexesFile = new File(args[2]);
		File copyToDir = new File(args[3]);

		File textDirA = new File(sourceDirA, Cfg.DEFAULT_TEXT_SUBDIRECTORY);
		File textDirB = new File(sourceDirB, Cfg.DEFAULT_TEXT_SUBDIRECTORY);
		Set<Integer> ids = getIds(indexesFile);
		File copyToDirA = new File(copyToDir, sourceDirA.getName());
		File copyToDirB = new File(copyToDir, sourceDirB.getName());
		copyToDirA.mkdirs();
		copyToDirB.mkdirs();

//		Map<Integer, File> idsToFilesA = getFilesForIds(textDirA, ids);
//		Map<Integer, File> idsToFilesB = getFilesForIds(textDirB, ids);

//		for (Entry<Integer, File> a : idsToFilesA.entrySet()) {
//			if (idsToFilesB.containsKey(a.getKey())) {
//				Files.copy(a.getValue().toPath(), new File(copyToDirA, a.getValue().getName()).toPath());
//				File fileB = idsToFilesB.get(a.getKey());
//				Files.copy(fileB.toPath(), new File(copyToDirB, fileB.getName()).toPath());
//			}
//		}

		// Direct directory, not subdir text
		Map<Integer, File> idsToFilesA = getFilesForIds(sourceDirA, ids);
		Map<Integer, File> idsToFilesB = getFilesForIds(sourceDirB, ids);
		
		// IDs instead of original file name
		for (Entry<Integer, File> a : idsToFilesA.entrySet()) {
			if (idsToFilesB.containsKey(a.getKey())) {
				Files.copy(a.getValue().toPath(), new File(copyToDirA, a.getKey().toString() + ".txt").toPath());
				File fileB = idsToFilesB.get(a.getKey());
				Files.copy(fileB.toPath(), new File(copyToDirB, a.getKey().toString() + ".txt").toPath());
			}
		}
	}

	private Set<Integer> getIds(File indexesFile) throws IOException {
		return new IndexCollector().collectIds(indexesFile);
	}

	private Map<Integer, File> getFilesForIds(File directory, Set<Integer> ids) {
		Map<Integer, File> map = new HashMap<>();
		for (File file : directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt") ? true : false;
			}
		})) {
			Integer id = filenameToId(file);
			if (ids.contains(id)) {
				map.put(id, file);
			}
		}
		return map;
	}

	private Integer filenameToId(File file) {
		String id = file.getName();
		id = id.substring(0, id.lastIndexOf(".txt"));
		id = id.substring(id.lastIndexOf(".") + 1);
		return Integer.valueOf(id);
	}

}