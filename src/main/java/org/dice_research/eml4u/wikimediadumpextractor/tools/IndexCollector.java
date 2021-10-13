package org.dice_research.eml4u.wikimediadumpextractor.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.dice_research.eml4u.wikimediadumpextractor.xml.Index;
import org.dice_research.eml4u.wikimediadumpextractor.xml.IndexPage;

public class IndexCollector {

	public Set<Integer> collectIds(File linesWithIndexFiles) throws IOException {
		Set<Integer> ids = new TreeSet<>();
		for (IndexPage indexPage : collectIndexPages(
				collectIndexFiles(linesWithIndexFiles, linesWithIndexFiles.getParentFile()))) {
			ids.add(indexPage.id);
		}
		return ids;
	}

	public Set<IndexPage> collectIndexPages(File linesWithIndexFiles) throws IOException {
		return collectIndexPages(collectIndexFiles(linesWithIndexFiles, linesWithIndexFiles.getParentFile()));
	}

	public Set<IndexPage> collectIndexPages(File linesWithIndexFiles, File directoryWithIndexFiles) throws IOException {
		return collectIndexPages(collectIndexFiles(linesWithIndexFiles, directoryWithIndexFiles));
	}

	public List<File> collectIndexFiles(File linesWithIndexFiles, File directoryWithIndexFiles) throws IOException {
		List<File> indexFiles = new LinkedList<>();
		for (String indexFile : Files.readAllLines(linesWithIndexFiles.toPath())) {
			if (!indexFile.isBlank()) {
				indexFiles.add(new File(directoryWithIndexFiles, indexFile));
			}
		}
		return indexFiles;
	}

	public Set<IndexPage> collectIndexPages(List<File> indexFiles) throws IOException {
		Set<IndexPage> indexPages = new HashSet<>();
		for (File indexFile : indexFiles) {
			indexPages.addAll(new Index(indexFile).getIndexPages());
		}
		return indexPages;
	}

}