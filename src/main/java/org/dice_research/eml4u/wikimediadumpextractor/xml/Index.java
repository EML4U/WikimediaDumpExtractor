package org.dice_research.eml4u.wikimediadumpextractor.xml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dice_research.eml4u.wikimediadumpextractor.utils.CfgUtils;

/**
 * Index for filenames and Wikipedia titles.
 *
 * @author Adrian Wilke
 */
public class Index {

	public static final String FILENEAME = "index.txt";

	private File file;
	private Map<Integer, IndexPage> index = new HashMap<>();

	public Index() throws IOException {
		this(FILENEAME);
	}

	public Index(String filename) throws IOException {
		this.file = new File(CfgUtils.getOutputDirectoryJob(), filename);
		file.getParentFile().mkdirs();
		if (file.exists()) {
			read();
		}
	}

	public Index(File file) throws IOException {
		this.file = file;
		file.getParentFile().mkdirs();
		if (file.exists()) {
			read();
		}
	}

	public void write() throws IOException {
		Files.write(file.toPath(), toString().getBytes());
	}

	private void read() throws IOException {
		add(file.toPath());
	}

	public Collection<IndexPage> getIndexPages() {
		return index.values();
	}

	public void add(Path path) throws IOException {
		List<String> lines = Files.readAllLines(path);
		String filename = null;
		String title = null;
		Integer id = null;
		for (int i = 0; i < lines.size(); i++) {
			switch (i % 3) {
			case 0:
				filename = lines.get(i);
				break;
			case 1:
				title = lines.get(i);
				break;
			case 2:
				id = Integer.valueOf(lines.get(i));
				if (!index.containsKey(id)) {
					index.put(id, new IndexPage(filename, title, id));
				}
				break;
			default:
				throw new IOException("Error in reading index.");
			}
		}
	}

	public boolean isIndexed(int pageId) {
		return index.containsKey(pageId);
	}

	public void addPage(Page page) {
		index.put(page.getId(), new IndexPage(page));
	}

	public int getNumberOfPages() {
		return index.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (IndexPage metaPage : index.values()) {
			sb.append(metaPage.toString());
		}
		return sb.toString();
	}
}