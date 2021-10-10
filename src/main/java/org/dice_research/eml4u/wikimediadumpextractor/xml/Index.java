package org.dice_research.eml4u.wikimediadumpextractor.xml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

	public class MetaPage {

		public String filename;
		public String title;
		public Integer id;

		public MetaPage(Page page) {
			this.filename = page.getFilename();
			this.title = page.getTitle();
			this.id = page.getId();
		}

		public MetaPage(String filename, String title, Integer id) {
			this.filename = filename;
			this.title = title;
			this.id = id;
		}
	}

	public static final String FILENEAME = "index.txt";

	private File file;
	private Map<Integer, MetaPage> index = new HashMap<>();

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

	public void write() throws IOException {
		Files.write(file.toPath(), toString().getBytes());
	}

	private void read() throws IOException {
		List<String> lines = Files.readAllLines(file.toPath());
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
				index.put(id, new MetaPage(filename, title, id));
				break;
			default:
				throw new IOException("Error in reading index.");
			// break;
			}
		}
	}

	public boolean isIndexed(int pageId) {
		return index.containsKey(pageId);
	}

	public void addPage(Page page) {
		index.put(page.getId(), new MetaPage(page));
	}

	public int getNumberOfPages() {
		return index.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (MetaPage metaPage : index.values()) {
			sb.append(metaPage.filename);
			sb.append(System.lineSeparator());
			sb.append(metaPage.title);
			sb.append(System.lineSeparator());
			sb.append(metaPage.id);
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
}