package data;

import java.util.Map;

/**
 * Reports information about the parsed pages to the user via the console.
 * 
 * @author Derv
 * @version 1
 */
public class Reporter {
	
	/** Constructor, does nothing. */
	public Reporter() {
		/* Does nothing. */
	}
	
	/**
	 * Reports to the user statistics about a parsed page.
	 * Prints to System.out (console) using formatted text Strings.
	 * 
	 * @param url URL that was just parsed.
	 * @param pagesTotal How many pages have been parsed so far.
	 * @param avgWords Average number of words per page.
	 * @param avgURLs Average number of URLs per page.
	 * @param keywordCounts A Map containing the keywords (key) and the number
	 * 				of times they have appeared so far (values).
	 * @param pageLimit Maximum number of pages before the parser stops.
	 * @param avgParseTime Average time it takes to parse a page.
	 * @param runningTime Total running time of this program.
	 */
	public void report(final String url, final int pagesTotal, 
						final int avgWords, final int avgURLs, 
						final Map<String, Integer> keywordCounts, 
						final int pageLimit, final double avgParseTime, 
						final long runningTime) {
		
		// Print a large block of white space to differentiate reports.
		p("\n\n\n\n\n"); 
		
		p("Parsed: " + url);
		p("Pages Retrieved: " + pagesTotal);
		p("Average words per page: " + avgWords);
		p("Average URLs per page: " + avgURLs);
		
		// Print "columns" for keywords
		p("Keyword" + String.format("%15s", "") + 
				"Ave. hits per page" + String.format("%7s", "") + 
				"Total hits");
		
		// Print keywords.
		for (String key : keywordCounts.keySet()) {
			final int value = keywordCounts.get(key);
			final double avg = (double) value / pagesTotal;
			p("  " + key + String.format("%" + (24 - key.length()) + "s", "") +  // The keyword 
					String.format("%.3f", avg) +        // Average
					String.format("%21s", "") + value); // Total
		}
		
		p("\nPage limit: " + pageLimit);
		p("Average parse time per page: " + String.format("%.3f", (double) avgParseTime / 1000) + "msec");
		p("Total running time: " + String.format("%.3f", (double) runningTime / 1000) + "sec");
		
	}
	
	/**
	 * Simplified System.out.println.
	 * This is much easier to type than the whole "System.out.println".
	 * 
	 * @param txt The text to print.
	 */
	private void p(final Object txt) {
		System.out.println(txt);
	}
}
