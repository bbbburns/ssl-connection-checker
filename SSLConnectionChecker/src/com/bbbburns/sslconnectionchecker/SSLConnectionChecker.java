package com.bbbburns.sslconnectionchecker;

import java.io.IOException;
import java.net.InetAddress;
import org.xbill.DNS.*;

import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class SSLConnectionChecker extends ActionBarActivity {
	
	private static final String TAG = "SSLConnectionChecker";
	
	InetAddress address[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sslconnection_checker);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sslconnection_checker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class DNSLookupTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... site_addresses) {
			
			//doInBackground takes an array of parameters but I only have one
			//Take the first one.
			String site_address = site_addresses[0];
			
			
			//Resolve the name into an array of addresses
			
			//Use dnsjava instead of InetAddress to do the lookup
				
			Lookup lookup = null;
			try {
				lookup = new Lookup(site_address, Type.SRV);
			} catch (TextParseException e1) {
				Log.e (TAG, "DNS NAME PARSING FAILED: " + site_address);
				e1.printStackTrace();
			}

			Log.d (TAG, "Lookup Created. Running.");
			Record[] records = lookup.run();
		
			Log.d (TAG, "Lookup Result String is: " + lookup.getErrorString());
			if (lookup.getResult() == Lookup.SUCCESSFUL){
				if (records != null){
					for (int i=0; i < records.length; i++){
						Log.d (TAG, "Record String is: " + records[i].toString());
						Log.d (TAG, "Record rdata is: " + records[i].rdataToString());
					}
				}
			} else if (lookup.getResult() == Lookup.HOST_NOT_FOUND) {
				Log.e (TAG, "DNS Lookup Error - Not found.");
            } else if (lookup.getResult() == Lookup.TRY_AGAIN) {
            	Log.e (TAG, "DNS Lookup Error - Try Again");
            } else if (lookup.getResult() == Lookup.TYPE_NOT_FOUND) {
            	Log.e (TAG, "DNS Lookup Error - Type Not Found");
            } else if (lookup.getResult() == Lookup.UNRECOVERABLE) {
                Log.e (TAG, "DNS Lookup Error - Unrecoverable");
            }
			
			return null;
		}
		
	}
	
	/** Called when the user clicks the Check! button
	 *  Check the certificate at the address the user has entered
	 * @throws IOException 
	 */
	public void checkAddress (View view) throws IOException {
		//Check the certificates of the user provided address
		// 1. Resolve the name - bare bones working (needs wrappers and error checking)
		// 2. Ping the address (does this make sense)
		// 3. Connect via SSL
		// 4. Check if the cert CN matches
		// 5. Dump all cert contents to screen
		EditText editText = (EditText) findViewById(R.id.site_address);
		String site_address = editText.getText().toString();
		
		Log.d (TAG, "Someone has clicked Check! with name " + site_address);
		
		//We should create a graphical display of the checking process
		//Instead I'm just going to do the juicy bits and then log it
		
		Log.d (TAG, "Starting DNS Lookup");
		
		new DNSLookupTask().execute(site_address);
		
		Log.d (TAG, "Lookup now running in background");
		
		// Now see if we can Ping the addresses returned
		
		
		
	}
}
