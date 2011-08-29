package queue;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import data.Page;

/**
 * Queue which holds Page objects.  Thread-safe!
 * 
 * @author Derv
 * @version 1
 */
//To avoid warnings about PageQueue not being serializable
@SuppressWarnings("serial") 
public class PageBuffer extends LinkedList<Page> {
	
	/** Default Constructor. */
	public PageBuffer() {
		super();
	}
	
	/** 
	 * Thread-safe method to add a page to the queue.
	 * 
	 * @param page The page to add.
	 */
	synchronized public boolean add(final Page page) {
		return super.add(page);
	}
	
	/**
	 * Thread-safe method to remove the next page from the queue.
	 * 
	 * @throws NoSuchElementException If the queue is empty.
	 */
	synchronized public Page remove() throws NoSuchElementException {
		return super.remove();
	}
}
