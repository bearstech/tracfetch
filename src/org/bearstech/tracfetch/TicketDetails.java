package org.bearstech.tracfetch;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

interface HistoryCallbackHandler {
	void historyCallback();
}

/**
 * Screen that mainly shows the description of the ticket ;
 * will later also show more informations such as comments, ...
 */
public class TicketDetails extends Activity implements HistoryCallbackHandler {
	
	private RequestHandler reqHandler;
	
	private Ticket ticket = null;
	
	public void onCreate(Bundle savedInstanceState) {
		/* Initialisation and display */
		super.onCreate(savedInstanceState);
		
		Bundle bundle = this.getIntent().getExtras();

		ticket = new Ticket(bundle);
		reqHandler = RequestHandler.getInstance();
		// TODO ask for the comments of the ticket
		
		setContentView(R.layout.ticket_details);
		
		TextView desc = (TextView) findViewById(R.id.ticket_details_description);
		desc.setText(ticket.get("description"));
	}

	@Override
	public void historyCallback() {
		//TODO
	}
}
