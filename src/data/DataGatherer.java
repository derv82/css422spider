package data;

import java.util.HashMap;
import java.util.Map;

/**
 * This class keeps track of all statistics found by the PageParser objects.
 * It receives data, combines it and manipulates it into a readable form, and then
 * passes this data onto a Reporter object, to be displayed to the user.
 * 
 * @author Derv
 * @version 1
 */
public class DataGatherer {
	/** Maximum number of pages to get. Required by this class because 
	 * the Reporter must have access to this number. */
	private final int pageLimit;
	
	/** Map of words and how often they appear. */
	private final Map<String, Integer> wordsTotal = new HashMap<String, Integer>();
	
	/** Total number of pages parsed. */
	private int pageCount = 0;
	
	/** Total number of words on all pages. */
	private int wordCount = 0;
	
	/** Total number of URLs found on each page. */
	private int urlCount = 0;
	
	/** Time since we started the data gathering process. */
	private long startTime;
	
	/** Total time spent parsing pages. Used to calculate "average parse time". */
	private long totalParseTime = 0;
	
	private final Reporter reporter = new Reporter();
	
	/** Constructor. */
	public DataGatherer(final int pageLimit) {
		this.pageLimit = pageLimit;
		this.startTime = System.currentTimeMillis();
	}
	
	/** @return True if the number of pages parsed is greater than 
	 * 			or equal to the "page limit, False otherwise.*/
	public boolean hitLimit() { return this.pageCount >= pageLimit; }
	
	/**
	 * Receives information about a parsed page, 
	 * combines it with existing information,
	 * and passes this onto the Reporter object.
	 * 
	 * @param url The URL of the page that was parsed.
	 * @param wordsOnPage A Map object holding the keywords (keys) 
	 * 		    and the number of times those words appeared on this page (value).
	 * @param wordCount How many words appeared on this page.
	 * @param urlCount How many URLs appeared on this page.
	 * @param parseTime How long it took to parse the page.
	 */
	synchronized public void parsed(final String url, final Map<String, Integer> wordsOnPage, 
			int wordCount, int urlCount, long parseTime) {
		
		// Don't bother parsing a page if we have hit the limit
		if (hitLimit()) {
			return;
		}
		
		this.pageCount++;            // Total # of pages + 1
		this.wordCount += wordCount; // Total # of words + this page's word count.
		this.urlCount += urlCount;   // Total # of URLs  + this page's URL count
		
		// Update total word count.
		for (String key : wordsOnPage.keySet()) {
			if (wordsTotal.containsKey(key)) {
				//final int value = wordsTotal.remove(key);
				//wordsTotal.put(key, value + wordsOnPage.get(key));
				wordsTotal.put(key, wordsTotal.get(key) + wordsOnPage.get(key));
			} else {
				wordsTotal.put(key, wordsOnPage.get(key));
			}
		}
		
		// Calculate the average parse time.
		this.totalParseTime += parseTime;
		final double avgParseTime = this.totalParseTime / this.pageCount;
		
		// Calculate total time the DataGatherer has been running.
		final long runningTime = System.currentTimeMillis() - this.startTime;
		
		// Send this information to the reporter.
		reporter.report(url, this.pageCount, wordCount / pageCount, urlCount / pageCount, 
				wordsTotal, pageLimit, avgParseTime, runningTime);
	}
}
