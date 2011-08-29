package thread;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import queue.PageBuffer;
import queue.PageToRetrieve;
import data.Page;

/**
 * Threaded! 
 * 
 * While running, this thread constantly checks for new URLs in a "URL queue".
 * Once it gets a URL, it retrieves the HTML body (text) for that URL and
 * places the result in the "page queue".
 * 
 * Runs until the "stop()" method is called.
 * 
 * @author Derv
 * @version 1
 */
public class PageRetriever extends Thread {
	/** The queue to get URLs from. */
	private final PageToRetrieve urlQueue;
	
	/** The queue to put the Page objects into. */
	private final PageBuffer pageQueue;
	
	/** Flag which tells whether or not the thread is currently retrieving a page. */
	private boolean busy;
	
	/** Flag which tells the thread to stop. */
	private boolean stop;
	
	/** 
	 * Constructor.
	 * 
	 * @param url_queue The queue of URLs to grab. 
	 * @param page_queue The queue of web pages to put the downloaded page bodies into.
	 */
	public PageRetriever(final PageToRetrieve url_queue, final PageBuffer page_queue) {
		this.urlQueue  = url_queue;
		this.pageQueue = page_queue;
		
		stop = false;
	}
	
	/** @return Whether or not this thread is currently retrieving a page. */
	public boolean isBusy() { return this.busy; }
	
	/** 
	 * Code which runs in it's own thread. 
	 * This method will constantly check the urlQueue for new Page objects,
	 * then download the HTML source body for the given URL,
	 * then place the result back into the Page object,
	 * an then place the Page object in the pageQueue.
	 */
	public void run() {
		
		// Loop until our 'stop' flag is true.
		while (!stop) {
			try {
				
				// Grab the next URL to retrieve from the queue.
				// If there is no URL, we will catch NoSuchElementException
				final String url = urlQueue.remove();
				
				busy = true; // At this point, we have begun grabbing the page.
				
				final String source = getHTML(url);
				
				if ("".equals(source)) // If the page is empty or unretrievable, don't bother adding it.
					continue;
				
				// Set the body and add the Page object to the pageQueue.
				final Page page = new Page(url, source);
				pageQueue.add(page);
				
				busy = false; // At this point, we are done grabbing the page.
				
			} catch (NoSuchElementException nse) {
				// NoSuchElementException is thrown when the queue is empty.
				busy = false;
				continue;
			}
		}
	}
	
	/** Tells current thread to stop. */
	public void stopThread() {
		stop = true;
	}
	
	/**
	 * Helper method. Retrieves HTML text from a given URL.
	 * Catches all exceptions.
	 * 
	 * @return HTML code (body) for a URL, 
	 * 			or empty string ("") if page could not be retrieved 
	 */
	private String getHTML(final String theURL) {
		// Result is a StringBuilder (uses less memory than a String)
		StringBuilder result = new StringBuilder();
        
        try {
        	// Open new connection
        	final URLConnection uc = new URL(theURL).openConnection();
        	
        	// The "timeouts" below are how long the socket waits before stopping
        	// These timeouts prevent us from being stuck on a page that never loads.
        	// The waiting times are in milliseconds
        	
			uc.setConnectTimeout(15 * 1000); // wait a maximum of 15 seconds before giving up.
			uc.setReadTimeout(15 * 1000);    // wait a maximum of 15 seconds for data to be sent.
			
			// The User-agent property of our request tells the web server what kind of computer we are running
			// Some websites block certain user-agents, so I will use the Mozilla Firefox user agent.
			// This makes the web server *think* we are using Firefox.
			
			uc.setRequestProperty("User-Agent", 
				"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.13) " +
				"Gecko/20101203 Firefox/3.6.13");
			
			// I like to use Scanner to read input streams (it's simple).
			final Scanner in = new Scanner(uc.getInputStream());
			
	        while (in.hasNextLine())          // Read every line from the web page input stream.
	        	result.append(in.nextLine()); // Add every line to the string buffer.
	        
	        in.close(); // Close the input stream.
	        
			// Lots of exceptions can occur.
        } catch (final FileNotFoundException fnf)  { 
        	System.err.println("*** FileNotFoundException: " + theURL);
        } catch (final UnknownHostException uhe)   {
        	System.err.println("*** UnknownHostException: " + theURL);
        } catch (final MalformedURLException mue)  { mue.printStackTrace();
        } catch (final SocketTimeoutException ste) { ste.printStackTrace();
        } catch (final IOException ioe)            { ioe.printStackTrace();
        }
        
        return result.toString();
	}
}
