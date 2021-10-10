package org.dice_research.eml4u.wikimediadumpextractor.xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import org.dice_research.eml4u.wikimediadumpextractor.Cfg;
import org.dice_research.eml4u.wikimediadumpextractor.utils.CfgUtils;
import org.dice_research.eml4u.wikimediadumpextractor.xml.XmlParser.MaxPagesException;

/**
 * Uses all available processors except one to provide an executor service.
 * 
 * Implemented as Singleton, use {@link #getInstance()} to get the instance.
 *
 * @author Adrian Wilke
 */
public class XmlExecutor {

	// 0 to parse all
	public static final Integer MAX_PAGES = 100;

	// Singleton, see
	// https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples#thread-safe-singleton
	private static XmlExecutor instance;

	private int workers;
	private ForkJoinPool forkJoinPool;
	private CompletionService<Page> completionService;

	private PageIndex pageIndex;

	/**
	 * Code based on {@link Executors#newWorkStealingPool(int)}. Processors obtained
	 * based on {@link Executors#newWorkStealingPool()}.
	 */
	private XmlExecutor() {

		workers = Runtime.getRuntime().availableProcessors();
		if (workers > 1) {
			workers--;
		}

		forkJoinPool = new ForkJoinPool(workers, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
		completionService = new ExecutorCompletionService<>(forkJoinPool);

		try {
			pageIndex = new PageIndex();
			Cfg.INSTANCE.set(Cfg.INFO_XML_INDEX_BEGIN, pageIndex.getNumberOfPages());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static synchronized XmlExecutor getInstance() {
		if (instance == null) {
			instance = new XmlExecutor();
		}
		return instance;
	}

	public void execute(File file) {

		// New thread to monitor finished pages
		monitor();

		// Start parsing
		XmlParser xmlParser = new XmlParser();
		try {
			xmlParser.setMaxPages(MAX_PAGES).extract(file, new PageHandlerImpl());
		} catch (MaxPagesException e) {
			// Thrown if number of pages limited by user
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.err.print(e);
		}

		// Wait for pool
		while (forkJoinPool.hasQueuedSubmissions()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.print(e);
			}
		}

		// Write index
		try {
			pageIndex.write();
			// TODO
//			for (Entry<String, SearchIndex> entry : searchIndexes.entrySet()) {
//				entry.getValue().write();
//				System.out.println(entry.getKey() +" "+ entry.getValue().getNumberOfPages());
//			}
		} catch (IOException e) {
			System.err.print(e);
		}

		// Update configuration
		Cfg.INSTANCE.set(Cfg.INFO_XML_TIME_PARSE, xmlParser.getDurationParsing());
		Cfg.INSTANCE.set(Cfg.INFO_XML_TIME_EXTRACT, xmlParser.getDurationHandling());
		Cfg.INSTANCE.set(Cfg.INFO_XML_READ_PAGES, xmlParser.getNumberOfParsedPages());
		Cfg.INSTANCE.set(Cfg.INFO_XML_INDEX_END, pageIndex.getNumberOfPages());

	}

	public void submit(Page page) {
		completionService.submit(page);
	}

	public boolean isIndexed(int pageId) {
		return pageIndex.isIndexed(pageId);
	}

	// TODO
//	Map<String, SearchIndex> searchIndexes = new HashMap<>();

	/**
	 * Runs in endless loop and collects parsed pages, returned by
	 * {@link Page#call()}.
	 */
	private void monitor() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Future<Page> pageFuture = XmlExecutor.getInstance().completionService.take();
						Page page = pageFuture.get();
						if (page != null) {

							// File will not be written, if already in index
							if (page.wasFilewritten()) {
								pageIndex.addPage(page);
							}

//							// TODO
//							if (page.hasContent()) {
//								for (String term : CfgUtils.getSearchTerms()) {
//									term=term.toLowerCase();
//									if (!searchIndexes.containsKey(term)) {
//										searchIndexes.put(term,
//												new SearchIndex("searchindex-" + term + ".txt"));
//									}
//									searchIndexes.get(term).addPage(page);
//								}
//							}

							// TODO overview of extracted data
							// System.out.println(page.getExtractedCategories() + " " +
							// page.getExtractedSearchTerms()
							// + " " + page.wasFilewritten());
						}

					} catch (Exception e) {
						System.err.println("Monitor: " + e);
					}
				}
			}
		}).start();
	}

}