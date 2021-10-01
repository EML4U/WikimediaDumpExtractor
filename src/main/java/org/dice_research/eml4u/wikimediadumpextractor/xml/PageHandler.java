package org.dice_research.eml4u.wikimediadumpextractor.xml;

/**
 * Interface used to handle pages parsed by {@link XmlParser}.
 *
 * @author Adrian Wilke
 */
public interface PageHandler {

	void handlePage(Page page);

}