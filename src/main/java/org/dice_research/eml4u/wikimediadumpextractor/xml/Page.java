package org.dice_research.eml4u.wikimediadumpextractor.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dice_research.eml4u.wikimediadumpextractor.content.Text;

/**
 * Wikipedia XML page element. Created by {@link XmlParser}.
 * 
 * Instances will be called by {@link XmlExecutor} using {@link #call()} for
 * parallel processing.
 *
 * @author Adrian Wilke
 */
public class Page implements Callable<Page> {

	private Integer id; // e.g. https://en.wikipedia.org/?curid=
	private Text text;
	private String title;

	public Page(Integer id, String text, String title) {
		this.id = id;
		this.text = new Text(text);
		this.title = title;
	}

	public Page(String id, String text, String title) {
		this(Integer.valueOf(id), text, title);
	}

	public String getTitle() {
		return title;
	}

	public Integer getId() {
		return id;
	}

	public Text getText() {
		return text;
	}

	@Override
	public String toString() {
		return title;
	}

	// ---------------------------------------------------------------------------
	// TODO old code
	// ---------------------------------------------------------------------------

	/**
	 * Called from {@link XmlExecutor} to process data in parallel.
	 */
	@Override
	public Page call() throws Exception {

		// Note: Use System.out.println(Thread.currentThread().getId()); to check, if
		// different threads are used

		String filename = getFilename(title);

		if (isInCategory() || containsSearchTerm()) {

			File outFile = new File(outDirectory, filename);
			try {
				writeFile(outFile, text.getText());
			} catch (IOException e) {
				System.err.println("Could not write file: " + outFile.getAbsolutePath());
			}

			return this;

		} else {
			return null;
		}
	}

	// ---------------------------------------------------------------------------
	// TODO old code
	// ---------------------------------------------------------------------------

	public Page setVars(String category, String search, File outDirectory) {
		this.category = category;
		this.search = search;
		this.outDirectory = outDirectory;
		return this;
	}

	private String category;
	private String search;
	private File outDirectory;

	private boolean isInCategory() {
		if (this.category == null) {
			return false;
		}

		// Escape ':' in categroy
		String category = this.category.replace(":", "[:]");
		// [[Category:XYZ]] or [[Category:XYZ|Xyz]]
		Pattern pattern = Pattern.compile("\\[\\[" + category + "(\\]\\]|\\|)");
		Matcher matcher = pattern.matcher(text.getText());
		return matcher.find();
	}

	private boolean containsSearchTerm() {
		if (this.search == null) {
			return false;
		}

		Pattern pattern = Pattern.compile(this.search, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text.getText());
		return matcher.find();
	}

	private String getFilename(String title) {
		return title.replaceAll("[^A-Za-z0-9 -]+", " ").trim().replaceAll("[ ]+", "_") + ".txt";
	}

	private void writeFile(File file, String content) throws IOException {
		StringReader stringReader = new StringReader(content);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			bufferedWriter.write(line);
			bufferedWriter.write(System.getProperty("line.separator"));
		}
		bufferedWriter.close();
		bufferedReader.close();
	}
}