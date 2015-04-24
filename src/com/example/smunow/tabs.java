package com.example.smunow;



import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
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
		twitterSpec.setIndicator(TWITTER_SPEC);
		Intent twitterIntent = new Intent(tabs.this, twitStream.class);
		twitterSpec.setContent(twitterIntent);

		// Event Stream Tab
		TabSpec streamSpec = tabHost.newTabSpec(STREAM_SPEC);
		streamSpec.setIndicator(STREAM_SPEC);
		Intent streamIntent = new Intent(tabs.this, Stream.class);
		streamSpec.setContent(streamIntent);

		// Saved Events Tab
		TabSpec savedSpec = tabHost.newTabSpec(SAVED_SPEC);
		savedSpec.setIndicator(SAVED_SPEC);
		Intent savedIntent = new Intent(tabs.this, SavedList.class);
		savedSpec.setContent(savedIntent);

		// Adding all TabSpec to TabHost
		tabHost.addTab(streamSpec); 
		tabHost.addTab(twitterSpec); 
		tabHost.addTab(savedSpec); 
	}
	@Override
	public void onPause(){
		super.onPause();
		new updateDB().execute();
	}

	private class updateDB extends AsyncTask<Void, Void, Void> {
		private ArrayList<Event> deleted;
		private ArrayList<Event> saved;
		private httpHandler parser;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			parser = s.getParser();
			deleted = s.getDeleted();
			saved = s.getSaved();

		}
		@Override
		protected Void doInBackground(Void... screenNames) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if(saved != null){
				for (int i = 0; i < saved.size(); i++){
					params.add(new BasicNameValuePair("userID", Integer.toString(s.getUid())));
					params.add(new BasicNameValuePair("request","save event"));
					params.add(new BasicNameValuePair("eventID", Integer.toString(saved.get(i).getID())));
					parser.makeRequest("http://52.10.7.245/api.php", "POST", params);
					params.clear();
				}
			}
			return null;
		}

		// onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
		@Override
		protected void onPostExecute(Void v) {
			s.emptySaved();

		}
	}

}
