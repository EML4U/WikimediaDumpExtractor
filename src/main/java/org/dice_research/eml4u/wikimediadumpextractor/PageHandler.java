package org.dice_research.eml4u.wikimediadumpextractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles XML page elements. Created by {@link PageHandlerFactory}.
 *
 * @author Adrian Wilke
 */
public class PageHandler implements Runnable {

	private static final boolean DEV_NO_WRITING = false;
	private static final boolean REPLACE = false;

	private String page;
	private String title;
	private String category;
	private File outDirectory;

	public PageHandler(String page, String title, String category, File outDirectory) {
		this.page = page;
		this.title = title;
		this.category = category;
		this.outDirectory = outDirectory;
	}

	@Override
	public void run() {
		if (isInCategory()) {
			if (DEV_NO_WRITING) {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(title);
				stringBuilder.append(System.lineSeparator());
				stringBuilder.append(replace(page));
				stringBuilder.append(System.lineSeparator());
				stringBuilder.append(System.lineSeparator());
				stringBuilder.append("----");
				stringBuilder.append(System.lineSeparator());
				System.out.println(stringBuilder);
			} else {
				File outFile = new File(outDirectory, getFilename(title));
				try {
					writeFile(outFile, replace(page));
				} catch (IOException e) {
					System.err.println("Could not write file: " + outFile.getAbsolutePath());
				}
			}
		}
	}

	private boolean isInCategory() {
		// Escape ':' in categroy
		String category = this.category.replace(":", "[:]");
		// [[Category:XYZ]] or [[Category:XYZ|Xyz]]
		Pattern pattern = Pattern.compile("\\[\\[" + category + "(\\]\\]|\\|)");
		Matcher matcher = pattern.matcher(page);
		return matcher.find();
	}

	private String replace(String string) {

		if (!REPLACE) {
			return string;
		}

		// Remove links, keep link titles
		// Does not work properly, e.g. in page "Autism" section "Autism rights
		// movement"
		// string = string.replaceAll("\\[\\[(.*?)\\|", "");

		// Remove links
		string = string.replaceAll("\\[\\[", "").replaceAll("\\]\\]", "");

		// Remove templates
		string = string.replaceAll("\\{\\{(.*?)\\}\\}", "");

		return string;
	}

	private String getFilename(String title) {
		return title.replaceAll("[^A-Za-z0-9 ]", "") + ".txt";
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