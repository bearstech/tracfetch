package org.bearstech.tracfetch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;

import android.util.Log;

/**
 * Singleton that contains the methods for retrieving data by xmlrpc requests
 */
public class RequestHandler {

	// We don't want that the user asks for a ticket list while we are retrieving another one
	/* FIXME : we should deactivate the spinner, or he will be able to ask for another
	list and he won't get it because of this lock */
	private boolean ticketLock = false;

	private static RequestHandler instance = null;

	private XMLRPCClient client;
	
	// That is ugly :(
	private Trac trac;

	// Singleton
	public static RequestHandler getInstance() {
		if (instance == null)
			instance = new RequestHandler();
		return instance;
	}

	private RequestHandler() {
	}

	/**
	 * Gets a list of IDs matching the query filter
	 */
	public void ticketsStep1(final TicketsCallbackHandler cbHandler, final TicketsList tList) {

		XMLRPCMethod method = new XMLRPCMethod(client, "ticket.query",
		/* Register the callback */
		new XMLRPCMethodCallback() {
			List<Integer> ids = new ArrayList<Integer>();

			public void callFinished(Object result) {
				/* You fetch the IDs. Store them and... */
				Object[] tickets = (Object[]) result;
				for (Object ticket : tickets) {
					ids.add((Integer) ticket);
				}

				/* ...ask for the full tickets after that */
				ticketsStep2(ids, cbHandler, tList);
			}

			public void error(Exception e) {
				e.printStackTrace();
			};
		});

		/* Pack the arguments to the call */
		Object[] params = { trac.filter, };

		/* Call the method */
		method.call(params);
	}

	/**
	 * Gets the list of tickets which IDs were returned from askID's request
	 * @throws ConcurrentRequestError 
	 */
	public void askTickets(Trac trac, final TicketsList tList,
			final TicketsCallbackHandler cbHandler) throws ConcurrentRequestError {
		if (ticketLock)
			throw new ConcurrentRequestError();
		ticketLock = true;
		this.trac = trac;
		client = new XMLRPCClient(trac.getURL());
		if (trac.hasAuth()) {
			client.setBasicAuthentication(trac.username, trac.password);
			Log.d("TRAC", "hasAuth");
		}
		Log.d("TRAC - REQ", trac.getURL());
		
		/*
		 * We will use the multicall mechanism: First build the array of
		 * requests
		 */
		ticketsStep1(cbHandler, tList);
	}
	
	public void refreshTickets(final TicketsList tList, final TicketsCallbackHandler cbHandler) throws ConcurrentRequestError {
		askTickets(trac, tList, cbHandler);
	}
	
	/**
	 * Retrieves an array of the results to several requests at once.
	 */
	private void ticketsStep2(List<Integer> ids, final TicketsCallbackHandler cbHandler, final TicketsList tList) {

		/*
		 * We will use the multicall mechanism, so build the arguments structure first.
		 */
		Object[] signatures = new Object[ids.size()];
		int i = 0;
		for (Integer id : ids) {
			Map<String, Object> tmp = new HashMap<String, Object>();

			Object[] params = { id };
			tmp.put("methodName", "ticket.get");
			tmp.put("params", params);

			signatures[i] = tmp;
			i++;
		}

		XMLRPCMethod method = new XMLRPCMethod(client, "system.multicall",
				new XMLRPCMethodCallback() {
					@SuppressWarnings("unchecked")
					public void callFinished(Object answer) {
						Object[] results = (Object[]) answer;

						for (Object _ticket : results) {
							// Useless encapsulation, but, ... well ...
							Object[] ticket = (Object[]) ((Object[]) _ticket)[0];

							Object _attrs = ticket[3];
							Map<String, Object> attrs = (Map<String, Object>) _attrs;

							tList.add(new Ticket((Integer) ticket[0],
									(Date) ticket[1], (Date) ticket[2], attrs));
						}

						// Done. Now, call the function that displays all the
						// tickets
						cbHandler.ticketCallback();
						Log.d("TRAC - LOCK", "Unlocked");
						ticketLock = false;
					}

					public void error(Exception e) {
						e.printStackTrace();
					};
				});

		/* Call the method */
		Object[] params = { signatures, };
		method.call(params);
	}
	
	// TODO
	public void askComments(Ticket ticket /*, Trac trac?, cb ?*/) {
		
	}

}
