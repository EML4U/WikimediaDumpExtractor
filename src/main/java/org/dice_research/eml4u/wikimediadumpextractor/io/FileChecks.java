package org.dice_research.eml4u.wikimediadumpextractor.io;

import java.io.File;

/**
 * Methods checking file system.
 *
 * @author Adrian Wilke
 */
public abstract class FileChecks {

	public static File checkFileIn(String file, int exitStatus) {
		File f = new File(file);
		if (!f.isFile()) {
			System.err.println("Not a file: " + f.getAbsolutePath());
			System.exit(exitStatus);
		} else if (!f.canRead()) {
			System.err.println("Can not read file: " + f.getAbsolutePath());
			System.exit(exitStatus);
		}
		return f;
	}

	public static File checkDirectoryOut(String directory, int exitStatus) {
		File dir = new File(directory);
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				System.err.println("Not a directory: " + dir.getAbsolutePath());
				System.exit(exitStatus);
			} else if (!dir.canWrite()) {
				System.err.println("Could not write directory: " + dir.getAbsolutePath());
				System.exit(exitStatus);
			}
		} else if (!dir.exists()) {
			if (!dir.mkdirs()) {
				System.err.println("Could not create directory: " + dir.getAbsolutePath());
				System.exit(exitStatus);
			}
		}
		return dir;
	}
}