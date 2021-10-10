import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

import org.dice_research.eml4u.wikimediadumpextractor.Main;

/**
 * Reads first argument as file and uses first line not starting with '#' or
 * blank as arguments.
 *
 * @author Adrian Wilke
 */
public class WikimediaDumpExtractorRunner {

	/**
	 * Note: Does not work with white spaces
	 */
	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			String[] lines = Files.readString(new File(args[0]).toPath()).split(System.lineSeparator());
			for (String line : lines) {
				if (!line.startsWith("#") && !line.isBlank()) {
					args = line.split(" ");
					System.out.println(Arrays.toString(args));
					Main.main(args);
				}
			}
		}
	}
}