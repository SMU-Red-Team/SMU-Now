package com.example.smunow;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")

//sets up all tabs for the main pages
public class tabs extends TabActivity{
	
	private static final String TWITTER_SPEC = "Twitter";
	private static final String STREAM_SPEC = "Events";
	private static final String SAVED_SPEC = "Saved";
	private Session s;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tablayout);
		s = Session.getSession();
		TabHost tabHost = getTabHost();

		// Twitter Tab
		TabSpec twitterSpec = tabHost.newTabSpec(TWITTER_SPEC);
		View twitterView = LayoutInflater.from(tabs.this).inflate(R.layout.twittertab, null);
		twitterSpec.setIndicator(twitterView);
		Intent twitterIntent = new Intent(tabs.this, twitStream.class);
		twitterSpec.setContent(twitterIntent);

		// Event Stream Tab
		TabSpec streamSpec = tabHost.newTabSpec(STREAM_SPEC);
		View streamView = LayoutInflater.from(tabs.this).inflate(R.layout.listlayout, null);
		streamSpec.setIndicator(streamView);
		Intent streamIntent = new Intent(tabs.this, Stream.class);
		streamSpec.setContent(streamIntent);

		// Saved Events Tab
		TabSpec savedSpec = tabHost.newTabSpec(SAVED_SPEC);
		View savedView = LayoutInflater.from(tabs.this).inflate(R.layout.savedtab, null);
		savedSpec.setIndicator(savedView);
		Intent savedIntent = new Intent(tabs.this, SavedList.class);
		savedSpec.setContent(savedIntent);

		// Adding all TabSpec to TabHost
		tabHost.addTab(streamSpec); 
		tabHost.addTab(twitterSpec); 
		tabHost.addTab(savedSpec); 
	}
	
	@Override
	//when tabs not viewed, update the database
	public void onPause(){
		super.onPause();
		upload();
	}
	
	public void upload() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		//only executes download if there is a connection to the internet
		if (networkInfo != null && networkInfo.isConnected()){
			new updateDB().execute();
		}
		else 
			Toast.makeText(tabs.this, "Could not connect to the internet", Toast.LENGTH_SHORT).show();
	}
	//asynchronous task which updates the database with saved events
	private class updateDB extends AsyncTask<Void, Void, Void> {
		
		private ArrayList<Event> saved;
		private httpHandler parser;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			//gets data from the session
			parser = s.getParser();
			saved = s.getStaged();
		}
		
		@Override
		protected Void doInBackground(Void... screenNames) {
			
			//creates parameters for posting saved events to the API
			for (int i = 0; i < saved.size(); i++){
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userID", Integer.toString(s.getUid())));
				params.add(new BasicNameValuePair("request","save event"));
				params.add(new BasicNameValuePair("eventID", Integer.toString(saved.get(i).getID())));
				parser.makeRequest("http://52.10.7.245/api.php", "POST", params);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			//gets rid of staged so the events can't be saved twice
			s.emptyStaged();
		}
	}
}
