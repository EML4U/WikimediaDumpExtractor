package org.dice_research.eml4u.wikimediadumpextractor.xml;

import java.io.File;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import org.dice_research.eml4u.wikimediadumpextractor.xml.XmlParser.MaxPagesException;

/**
 * Uses all available processors except one to provide an executor service.
 * 
 * Implemented as Singleton, use {@link #getInstance()} to get the instance.
 *
 * @author Adrian Wilke
 */
public class XmlExecutor {

	/**
	 * Singleton, see
	 * https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples#thread-safe-singleton
	 */
	private static XmlExecutor instance;

	private int workers;
	private ForkJoinPool forkJoinPool;
	private CompletionService<Page> completionService;

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
	}

	public static synchronized XmlExecutor getInstance() {
		if (instance == null) {
			instance = new XmlExecutor();
		}
		return instance;
	}

	// TODO
	public void execute(File file) {

		XmlParser xmlParser = new XmlParser();

		takeCompleted(false);
		try {

			xmlParser.setMaxPages(0).extract(file, new PageHandlerImpl().setVars(category, search, outDirectory));
		} catch (MaxPagesException e) {
			// Thrown if number of pages limited by user
			System.out.println(e.getMessage());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		while (forkJoinPool.hasQueuedSubmissions()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}

		System.out.println("Page handling: " + (1.0 * xmlParser.getDurationHandling() / (1000)));
		System.out.println("Page parsing: " + (1.0 * xmlParser.getDurationParsing() / (1000)));
		System.out.println("ParsedPages: " + xmlParser.getNumberOfParsedPages());
	}

	public void submit(Page page) {
		completionService.submit(page);
	}

	/**
	 * Uses an estimate of the total number of tasks currently held in queues by
	 * worker threads. If this number is larger than the number of used workers, the
	 * tread pool is rated as full.
	 */
	public boolean isFullEstimation() {

		// TODO
		long y = forkJoinPool.getQueuedTaskCount();
		boolean x = y > workers;
		if (x) {
			System.out.println(y + " forkJoinPool.getQueuedTaskCount()");
		}
		// System.out.println(forkJoinPool.getStealCount());
		return x;

		// return forkJoinPool.getQueuedTaskCount() > workers;
	}

	/**
	 * TODO Could be used if results have to be collected.
	 * 
	 * TODO Construct to ensure call of every return value required. Currently
	 * execution stops when program stops; some pages are not processed here.
	 */
	private void takeCompleted(boolean run) {
		if (run) {
			new Thread(new Runnable() {
				public void run() {
					try {
						while (true) {
							Future<Page> pageFuture = XmlExecutor.getInstance().completionService.take();
							// Will return null if nothing found
							if (pageFuture.get() != null) {
								System.err.println(pageFuture.get());
							}
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}).start();
		}
	}

	// ---------------------------------------------------------------------------
	// TODO old code
	// ---------------------------------------------------------------------------

	public XmlExecutor setVars(String category, String search, File outDirectory) {
		this.category = category;
		this.search = search;
		this.outDirectory = outDirectory;
		return this;
	}

	private String category;
	private String search;
	private File outDirectory;

	// ---------------------------------------------------------------------------

// TODO from old parser endDoc, creates index file, could be integrated into takeCompleted()
// see https://github.com/EML4U/WikimediaDumpExtractor/blob/aadb4ce8be00c4a0093423b04318585cbc440e12/src/main/java/org/dice_research/eml4u/wikimediadumpextractor/XmlParser.java#L90
}