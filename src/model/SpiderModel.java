package model;

import queue.PageBuffer;
import queue.PageToRetrieve;
import thread.PageParser;
import thread.PageRetriever;
import data.DataGatherer;

/**
 * Model which contains all of the functionality of the Spider program.
 * This class manages all of the objects required by the Spider.
 * 
 * Basically, we can use this class to execute the 
 * spider program with one command: execute(...)
 * 
 * @author Derv
 * @version 1
 */
public class SpiderModel {
	
	/** Constructor, does nothing. */
	public SpiderModel() { }
	
	/** Creates all necessary objects and ensures they work together.
	 * 
	 * @param pageLimit Maximum number of pages to retrieve.
	 * @param seedURL   Starting webpage.
	 * @param keywords  Collection of Strings to look for and key count of.
	 * @param numberOfThreads Number of threads to create.
	 */
	public void execute(final int pageLimit, final String seedURL, 
			final String[] keywords, final int numberOfThreads) {
		
		// Initialize the queues
		final PageToRetrieve  ptr = new PageToRetrieve();
		final PageBuffer      pb  = new PageBuffer();
		
		// Initialize DataGatherer
		final DataGatherer dg = new DataGatherer(pageLimit);
		
		// Initialize the PageRetriever thread pool
		final PageRetriever[] prs = new PageRetriever[numberOfThreads];
		for (int i = 0; i < numberOfThreads; i++) {
			prs[i] = new PageRetriever(ptr, pb);
			prs[i].start();
		}
		
		// Initialize the PageParser thread pool
		final PageParser[] pps = new PageParser[numberOfThreads];
		for (int i = 0; i < numberOfThreads; i++) {
			pps[i] = new PageParser(ptr, pb, keywords, dg);
			pps[i].start();
		}
		
		// Add the initial URL to start from.
		ptr.add(seedURL);
		
		// Wait until we have hit the page limit (dg.hitLimit)
		//         OR the threads are done (isDone).
		
		do {
			
			// Wait 1 second before checking the threads.
			// This gives the threads some time to think.
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException ie) { }
			
		} while (!dg.hitLimit() && !isDone(prs, pps, ptr, pb));
		
		System.err.println("Done!");
		
		// At this point, the program is done collecting information.
		
		// Tell the threads to stop the threads
		for (PageRetriever pr : prs)
			pr.stopThread();
		for (PageParser pp : pps) 
			pp.stopThread();
		
		// Threads will stop on their own, and we're done!
	}
	
	/**
	 * Helper method which checks if the threads and queues have  
	 * run out of web pages to parse.
	 * 
	 * @param pr Array of PageRetriever threads to see if they are not "busy"
	 * @param pp Array of PageParser threads to see if they are not "busy"
	 * @param ptr PageToRetrieve queue, to check if it's empty.
	 * @param pb PageBuffer queue, to check if it's empty.
	 * @return "True" if queues are empty and none of threads are busy, 
	 * 			"False" otherwise
	 */
	private boolean isDone(final PageRetriever[] pr, final PageParser[] pp, 
							final PageToRetrieve ptr, final PageBuffer pb) {
		
		// If any of the page retriever threads are "busy", we are not done yet.
		for (PageRetriever p : pr)
			if (p.isBusy()) {
				return false;
			}
		
		// If any of the page parser threads are "busy", we are not done yet.
		for (PageParser p : pp)
			if (p.isBusy()) {
				return false;
			}
		
		// If either of the queues are not empty, we are not done yet!
		return (ptr.size() == 0) && (pb.size() == 0);
	}
}
