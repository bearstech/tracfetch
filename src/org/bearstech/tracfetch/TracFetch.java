package org.bearstech.tracfetch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Main activity. Initiate singleton objects, restores the saved data. Displays
 * a list of summaries
 */
public class TracFetch extends Activity implements TicketsCallbackHandler {

	/*
	 * Menu IDs
	 */
	final int TRAC_ADD = 0;
	final int REFRESH = 1;
	final int TRAC_EDIT = 2;

	private RequestHandler reqHandler;
	private DataHolder data;
	private TicketsList tickets;

	private Integer currentPosition = null;

	public static LinearLayout main;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle bundle) {
		/* Initialisation and display */
		super.onCreate(bundle);
		setContentView(R.layout.main);

		main = (LinearLayout) findViewById(R.id.layout_main);

		/**
		 * Start applicative logic
		 */
		tickets = new TicketsList(this);
		reqHandler = RequestHandler.getInstance();
		data = DataHolder.getInstance();

		data.restore(this);

		/**
		 * Handles the click on a list item (show the details of the selected
		 * ticket)
		 */
		((AdapterView<?>) tickets)
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(TracFetch.this,
								TicketDetails.class);
						Bundle bundle = new Bundle();

						// TODO Find a way to pass the reference to the ticket,
						// rather than a copy
						for (String key : tickets.get(position).keySet()) {
							bundle.putString(key, tickets.get(position)
									.get(key));
						}

						intent.putExtras(bundle);
						startActivity(intent);
					}

				});
		main.addView((ListView) tickets);
	}

	@Override
	public void ticketCallback() {
		// FIXME find something to put in here ... maybe a Toast ?
	}

	/**
	 * When the application goes to background, save the data: the application
	 * could close w/o warning. FIXME : maybe it's better to do it just after a
	 * new data is created
	 */
	@Override
	public void onPause() {
		super.onPause();
		data.save(this);
	}

	public void onResume() {
		super.onResume();

		/**
		 * Handles the choice of a trac
		 */
		Spinner spinner = (Spinner) findViewById(R.id.trac_chooser);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, data.getTracNames());
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(spinnerAdapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> adapter, View view,
					int position, long id) {
				/*
				 * If we don't empty the list and the listview, scrolling the
				 * latter will trigger an OutOfBound exception
				 */
				tickets.clear();
				Log.d("TRAC - ITEM", "Selected: "
						+ data.getTracs().get(position).getURL());
				try {
					reqHandler.askTickets(data.getTracs().get(position),
							tickets, TracFetch.this);
				} catch (ConcurrentRequestError e) {
					Log.w("TRAC", "Cannot hold two similar requests at once");
				}
				currentPosition = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	/**
	 * Create the menus
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, TRAC_ADD, 0, "Add a Trac");
		menu.add(0, REFRESH, 0, "Refresh this Trac");
		menu.add(0, TRAC_EDIT, 0, "Edit this Trac");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (TRAC_ADD):
			showEditTracActivity(false);
			return true;
		case (REFRESH):
			refresh();
			return true;
		case (TRAC_EDIT):
			showEditTracActivity(true);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void refresh() {
		try {
			reqHandler.refreshTickets(tickets, this);
		} catch (ConcurrentRequestError e) {
			e.printStackTrace();
		}
	}

	private void showEditTracActivity(boolean edit) {
		Intent intent = new Intent(TracFetch.this, EditTrac.class);
		Bundle bundle = new Bundle();

		if (edit) {
			if (currentPosition == null) {
				// TODO warning
				return;
			}
			bundle.putBoolean("edit", true);
			bundle.putInt("position", currentPosition);
		} else {
			bundle.putBoolean("edit", false);
		}

		intent.putExtras(bundle);
		startActivity(intent);
	}

}