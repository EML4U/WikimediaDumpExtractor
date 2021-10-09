package org.dice_research.eml4u.wikimediadumpextractor.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * File utilities.
 *
 * @author Adrian Wilke
 */
public abstract class FileUtils {

//	public static void stringToFile(File file, String content) throws IOException {
//		StringReader stringReader = new StringReader(content);
//		BufferedReader bufferedReader = new BufferedReader(stringReader);
//		FileOutputStream fileOutputStream = new FileOutputStream(file);
//		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
//		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
//		String line;
//		while ((line = bufferedReader.readLine()) != null) {
//			bufferedWriter.write(line);
//			bufferedWriter.write(System.getProperty("line.separator"));
//		}
//		bufferedWriter.close();
//		bufferedReader.close();
//	}
}