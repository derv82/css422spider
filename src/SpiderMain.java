import java.util.Scanner;

import model.SpiderModel;

/**
 * Main thread which starts off the Spider program.
 * 
 * @author Derv
 * @version 1
 */
public class SpiderMain {
	
	private final static Scanner console = new Scanner(System.in);
	
	/**
	 * Simplified System.out.print.
	 * This is much easier to type than the whole "System.out.print".
	 * 
	 * @param txt The text to print.
	 */
	private static void p(final Object txt) {
		System.out.print(txt);
	}
	
	/**
	 * Main method: where the magic happens.
	 * 
	 * @param args Arguments sent from command-line.
	 */
	public static void main(final String[] args) {
		
		final SpiderModel sm = new SpiderModel();
		
		/*
		sm.execute(50, // Maximum number of pages to parse 
				"http://faculty.washington.edu/gmobus/", // Seed URL.
				new String[] {"and", "the", "good", "difficult", "science", "warming"},     // Keywords
				5); // Number of threads to open.
		
		System.exit(0);
		*/
		
		p("    Web spider\n\n");
		p("  Scours a given website for words.\n\n");
		p("Enter seed URL: ");
		final String seedUrl = console.nextLine();
		
		p("Enter keywords separated by spaces: ");
		final String[] keywords = console.nextLine().split(" ");
		
		int pageLimit = -1;
		do {
			p("Enter maximum number of pages to parse (greater than 0): ");
			final String input = console.nextLine();
			try {
				pageLimit = Integer.parseInt(input);
			} catch (NumberFormatException nfe) { }
		} while (pageLimit < 1);
		
		int threadNum = -1;
		do {
			p("Enter number of threads to use (greater than 0): ");
			final String input = console.nextLine();
			try {
				threadNum = Integer.parseInt(input);
			} catch (NumberFormatException nfe) { }
		} while (threadNum < 1);
		
		
		
		sm.execute(pageLimit, seedUrl, keywords, threadNum);
		
		
	}
}
