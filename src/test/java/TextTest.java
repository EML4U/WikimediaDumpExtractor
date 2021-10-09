import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;

import org.dice_research.eml4u.wikimediadumpextractor.Cfg;
import org.dice_research.eml4u.wikimediadumpextractor.content.Text;
import org.dice_research.eml4u.wikimediadumpextractor.utils.Strings;

public class TextTest {

	public static void main(String[] args) throws IOException {
		testSearch();
	}

	public static void testCats() throws IOException {
		String string = Files.readString(new File("src/test/resources/anachism.txt").toPath());
		Text text = new Text(string);

		System.out.println(string);
		System.out.println("----------------------------------------");
		for (String cat : text.getAllCategories()) {
			System.out.println(cat);
		}
	}

	public static void testSearch() throws IOException {
		String string = Files.readString(new File("src/test/resources/anachism.txt").toPath());
		Text text = new Text(string);

		System.out.println(string);
		System.out.println("----------------------------------------");

		Cfg.INSTANCE.set(Cfg.SEARCH, "the|AnD");
		System.out.println(text.search(Strings.getSearchTerms(), true, true));

		System.out.println(text.search(new HashSet<>(Arrays.asList(new String[] { "the", "AnD" })), true, true));
	}
}