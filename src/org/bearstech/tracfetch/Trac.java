package org.bearstech.tracfetch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializable object (saved by DataHolder) that represents a trac. It has
 * default values for the URL and the request filter that can be overridden
 * using the appropriate constructor.
 */
public class Trac implements Serializable {

	private static final long serialVersionUID = 2188476323493110896L;

	protected String hostname = "";
	protected String xmlrpcPath = "";
	protected String filter = "";

	protected String username = "";
	protected String password = "";

	private List<Ticket> tickets;

	public Trac() {
		tickets = new ArrayList<Ticket>();
	}

	public Trac(CharSequence hostname, CharSequence path, CharSequence filter, CharSequence username,
			CharSequence password) {
		tickets = new ArrayList<Ticket>();
		
		this.hostname = hostname.toString();
		this.xmlrpcPath = path.toString();
		this.filter = filter.toString();
		this.username = username.toString();
		this.password = password.toString();
		
		if (this.username == null)
			this.username = "";
		if (this.password == null)
			this.password = "";

	}

	public boolean hasAuth() {
		return (!username.equals(""));
	}

	public String getURL() {
		return hostname.toString() + xmlrpcPath.toString();
	}

	protected void addTicket(Ticket t) {
		tickets.add(t);
	}

	protected List<Ticket> getTickets() {
		return tickets;
	}

}
