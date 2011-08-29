package queue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Queue which holds the URLs.  Ensures no duplicate URLs will be added.
 * 
 * Thread-safe!
 * 
 * @author Derv
 * @version 1
 */
@SuppressWarnings("serial")
public class PageToRetrieve extends LinkedList<String> {
	
	/** 
	 * Collection of URLs that have already been added to the queue previously.
	 * This field keeps track of URLs so we will not add duplicates.
	 * Primarily used by the add() method.
	 */
	private final Map<String, Boolean> alreadyAdded = new HashMap<String, Boolean>();
	
	/** Default Constructor. */
	public PageToRetrieve() {
		super();
	}
	
	/** 
	 * Thread-safe method to add a URL to the queue.
	 * Ensures that URLs will only be added once. 
	 * 
	 * @param url The URL to add.
	 * 
	 * @return "False" if unable to add (or URL has already been added), 
	 * 			otherwise "True".
	 */
	synchronized public boolean add(final String url) {
		// Check if this URL has already been added before.
		if (alreadyAdded.containsKey(url))
			return false;
		
		// Add URL to list of URLs already added.
		alreadyAdded.put(url, true);
		return super.add(url);
	}
	
	/**
	 * Thread-safe method to remove the next URL from the queue.
	 * 
	 * @return The next URL in the queue.
	 
	 * @throws NoSuchElementException If the queue is empty.
	 */
	synchronized public String remove() throws NoSuchElementException {
		return super.remove();
	}
}
