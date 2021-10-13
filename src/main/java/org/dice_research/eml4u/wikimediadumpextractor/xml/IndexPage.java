package org.dice_research.eml4u.wikimediadumpextractor.xml;

public class IndexPage {

	public String filename;
	public String title;
	public Integer id;

	public IndexPage(Page page) {
		this.filename = page.getFilename();
		this.title = page.getTitle();
		this.id = page.getId();
	}

	public IndexPage(String filename, String title, Integer id) {
		this.filename = filename;
		this.title = title;
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(filename);
		sb.append(System.lineSeparator());
		sb.append(title);
		sb.append(System.lineSeparator());
		sb.append(id);
		sb.append(System.lineSeparator());
		return sb.toString();
	}
}