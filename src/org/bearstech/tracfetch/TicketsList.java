package org.bearstech.tracfetch;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * Adapts the list of the tickets to a list view.
 */
public class TicketsList extends ListView {
	
	private BaseAdapter adapter;
	private final String[] from = {"id", "summary"};
	private final int[] to = {R.id.ticket_item_id, R.id.ticket_item_summary};
	private List<Ticket> tickets;

	public TicketsList(Context ctx) {
		super(ctx);
		// FIXME don't create the ArrayList here, rather make it part of the Trac class
		// But thath's not trivial
		this.tickets = new ArrayList<Ticket>();
		
		adapter = new SimpleAdapter(ctx, tickets, R.layout.tickets_list, from, to);
		this.setAdapter(adapter);
	}

	public Ticket get(int id) {
		return tickets.get(id);
	}

	public Ticket peek() {
		return tickets.get(tickets.size() - 1);
	}

	public List<Ticket> getTickets() {
		return tickets;
	}

	/**
	 * add and clear:
	 * when the dataset changes, let the adapter know or face bugs !
	 */
	public void add(Ticket t) {
		tickets.add(t);
		adapter.notifyDataSetChanged();
	}
	
	public void clear() {
		tickets.clear();
		adapter.notifyDataSetChanged();
	}
	
}