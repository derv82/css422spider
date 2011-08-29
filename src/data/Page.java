package data;

public class Page {
	/** http:// link to this page. */
	private String url;
	
	/** HTML source code of the page. */
	private String body;
	
	/**
	 * Constructor.
	 * @param url The URL of a web page.
	 */
	public Page(final String url) {
		this.url = url;
	}
	
	/**
	 * Constructor.
	 * @param url The URL of the web page.
	 * @param body The HTML source of the web page.
	 */
	public Page(final String url, final String body) {
		this.url = url;
		this.body = body;
	}
	
	
	// Accessors 
	
	/** @return The URL of this object. */
	public String getUrl()  { return url; }
	
	/** @return The HTML source of this object. */
	public String getBody() { return body; }
	
	
	// Mutators
	
	/** @param url The URL to the current page. */
	public void setUrl(final String url)   { this.url = url; }
	
	/** @param body The HTML source (body) of the page. */
	public void setBody(final String body) { this.body = body; }
}
