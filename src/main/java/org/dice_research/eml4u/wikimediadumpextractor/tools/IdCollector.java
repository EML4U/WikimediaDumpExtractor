package org.dice_research.eml4u.wikimediadumpextractor.tools;

import java.io.File;
import java.io.IOException;

public class IdCollector {

	public static void main(String[] args) throws IOException {

		File indexesFile = new File(args[0]);

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Integer id : new IndexCollector().collectIds(indexesFile)) {
			if (first) {
				first = false;
			} else {
				sb.append("|");
			}
			sb.append(id);
		}
		System.out.println(sb);
	}

}