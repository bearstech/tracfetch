package org.bearstech.tracfetch;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class EditTrac extends Activity {

	private TextView hostname, path, username, password, filter;
	private int position;
	private boolean edit = false;

	public void onCreate(Bundle savedInstanceState) {
		/* Initialisation and display */
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_trac);
		
		Bundle bundle = this.getIntent().getExtras();
		
		hostname = (TextView) findViewById(R.id.hostname_edit);
		path = (TextView) findViewById(R.id.path_edit);
		filter = (TextView) findViewById(R.id.filter_edit);
		username = (TextView) findViewById(R.id.login_edit);
		password = (TextView) findViewById(R.id.password_edit);
		
		
		edit = bundle.getBoolean("edit", false);
		if (edit) {
			// We are editing ...
			position = bundle.getInt("position");
			Trac trac = DataHolder.getInstance().getTracs().get(position);
			
			// ... so we can fill the fields already
			hostname.setText(trac.hostname);
			path.setText(trac.xmlrpcPath);
			filter.setText(trac.filter);
			username.setText(trac.username);
			password.setText(trac.password);
		}


		Button validate = (Button) findViewById(R.id.validate_add_trac);
		validate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				validateTrac();
			}
		});
	}

	@SuppressWarnings("unused")
	private void validateTrac() {
		String url = hostname.getText().toString() + path.getText().toString();
		
		try {
			URL _url = new URL(url);
		} catch (MalformedURLException e) {
			error("The hostname/prefix couple you entered is not valid (it doesn't appear to be an URL)");
			return;
		}

		if (edit) {
			DataHolder.getInstance().getTracs().set(
					position,
					new Trac(hostname.getText(), path.getText(), filter.getText(), username
							.getText(), password.getText()));
		} else {
			DataHolder.getInstance().getTracs().add(
					new Trac(hostname.getText(), path.getText(), filter.getText(), username
							.getText(), password.getText()));
		}
		
		// We're done ; let's go back to the main activity
		finish();
	}

	private void error(String string) {
		Log.d("TRAC - EDIT", string);
		// TODO Toast
	}
}
