package org.dice_research.eml4u.wikimediadumpextractor.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * File system filters.
 *
 * @author Adrian Wilke
 */
public class FileFilters {

	public class FilenameSuffixFilter implements FilenameFilter {

		private String filenameSuffix;

		public FilenameSuffixFilter(String filenameSuffix) {
			this.filenameSuffix = filenameSuffix;
		}

		@Override
		public boolean accept(File dir, String name) {
			if (name.endsWith(filenameSuffix)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Gets files with suffix.
	 */
	public File[] getFiles(File directory, String filenameSuffix) {
		return directory.listFiles(new FilenameSuffixFilter(filenameSuffix));
	}
}
