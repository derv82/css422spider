package thread;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import queue.PageBuffer;
import queue.PageToRetrieve;
import data.DataGatherer;
import data.Page;

/**
 * Threaded! 
 * 
 * While running, this thread constantly checks for new Pages in a Page Buffer.
 * Once it gets a Page, it parses the HTML body (text),
 * reports its findings to DataGatherer,
 * and adds new-found URLs to the URL queue. 
 * 
 * Runs until the "stop()" method is called.
 * 
 * @author Derv
 * @version 1
 */
public class PageParser extends Thread {
	
	/** The queue place found URLs into. */
	private final PageToRetrieve urlQueue;
	
	/** The queue to get Page objects from. */
	private final PageBuffer pageQueue;
	
	/** List of key words we are looking for in the pages. */
	private final String[] keywords;
	
	/** DataGatherer object we will report our findings to. */
	private final DataGatherer dataGatherer;
	
	/** Flag which tells whether or not the thread is currently parsing a page. */
	private boolean busy;
	
	/** Flag which tells the thread to stop. */
	private boolean stop;
	
	/** 
	 * Constructor.
	 * 
	 * @param url_queue The queue to place found URLs into. 
	 * @param page_queue The queue to grab Page objects from (that have already been retrieved).
	 */
	public PageParser(final PageToRetrieve url_queue, final PageBuffer page_queue, 
						final String[] keywords, final DataGatherer data_gatherer) {
		
		this.urlQueue     = url_queue;
		this.pageQueue    = page_queue;
		this.keywords     = keywords;
		this.dataGatherer = data_gatherer;
		
		stop = false;
	}
	
	
	/** @return Whether or not this thread is currently retrieving a page. */
	public boolean isBusy() { return this.busy; }
	
	/** 
	 * This method will constantly check the urlQueue for new Page objects,
	 * then download the HTML source body for the given URL,
	 * then place the result back into the Page object,
	 * an then place the Page object in the pageQueue.
	 */
	public void run() {
		
		// Loop until our 'stop' flag is true.
		while (!stop) {
			try {
				final Page page = pageQueue.remove();
				
				busy = true;
				
				parse(page);
				
				busy = false;
				
			} catch (NoSuchElementException nse) {
				// NoSuchElementException is thrown when the queue is empty.
				continue;
			}
		}
	}
	
	/**
	 * Tells current thread to stop.
	 * Does *not* immediately stop the thread!
	 */
	public void stopThread() {
		stop = true;
	}
	
	/**
	 * Helper method. Parses a given web page.
	 * Reports findings to dataGatherer.
	 * 
	 * @param page The Page object to parse.
	 */
	private void parse(final Page page) {
		
		// The map contains the keywords (String) and how frequently they appear (integer).
		final Map<String, Integer> keywordMap = new HashMap<String, Integer>();
		for (String keyword : keywords) {
			keywordMap.put(keyword, 0); // Adding each word to the map, initialized to zero.
		}
		
		final long startTime = System.currentTimeMillis(); // The time we started parsing.
		int wordCount = 0; // Total # of words found.
		int urlCount = 0;  // Total # of URLs found.
		
		// We will use a tokenizer to iterate over every "word" in the page body.
		final StringTokenizer tokens = new StringTokenizer(page.getBody());
		while (tokens.hasMoreTokens()) {
			
			wordCount++; // Increment total # of words on this page.
			
			final String token = tokens.nextToken(); // Current word
			
			// Check if this word is in the Map
			if (keywordMap.containsKey(token)) {
				// int value = keywordMap.remove(token); // Remove word from map
				// keywordMap.put(token, value + 1);     // Put word back in map, updating the count
				keywordMap.put(token, keywordMap.get(token) + 1);
			}
			
			// Check if this word is a link
			if (token.startsWith("href=\"")) {
				
				urlCount++;        // Increment URL counter
				
				// Extract everything between 'href="' and the next quote '"'
				if (token.indexOf('"', 6) < 0)
					continue;
				String url = token.substring(6, token.indexOf('"', 6));
				
				// Remove hash tag if needed
				if (url.indexOf('#') != -1)
					url = url.substring(0, url.indexOf('#'));
				
				final String homeUrl = page.getUrl(); // The current page's URL
				
				// Now we need to fix relative addressing...
				if (url.startsWith("../")) {
					// Relative addressing, stay within current URL's subdirectories
					url = homeUrl.substring(0, homeUrl.lastIndexOf('/')) + url.substring(2);
					
				} else if (url.startsWith("/")) {
					// Using / addressing. Similar to "./"
					url = homeUrl.substring(0, homeUrl.lastIndexOf('/')) + url;
					
				} else if (url.startsWith("http")) {
					// Don't need to change anything
					
				} else if (url.startsWith("./")) {
					// Using ./ relative addressing 
					url = homeUrl.substring(0, homeUrl.lastIndexOf('/')) + url.substring(2);
					
				} else {
					// Using either ./ address or no addressing at all.
					url = homeUrl.substring(0, homeUrl.lastIndexOf('/') + 1) + url;
				}
				
				// Ensure the URL uses the proper extension.
				if (url.endsWith(".html") || url.endsWith(".htm") || url.endsWith(".txt")) {
					
					// Try to add URL to the PageToRetrieve queue
					urlQueue.add(url);
				}
				
			}
		}
		
		// Total time taken to parse this page.
		final long timeTaken = System.currentTimeMillis() - startTime;
		
		// Send this data to the DataGatherer object.
		dataGatherer.parsed(page.getUrl(), keywordMap, wordCount, urlCount, timeTaken);
		
	}
	
}
