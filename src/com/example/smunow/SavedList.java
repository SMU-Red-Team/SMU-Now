package com.example.smunow;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter.SwipeActionListener;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

@SuppressWarnings("deprecation")

//creates and maintains the Saved Events screen
public class SavedList extends ListActivity{

	private Session s;
	private ListView lvName;
	private ArrayList<Event> events = new ArrayList<Event>();
	private ArrayAdapter<Event> adapter;
	private SwipeActionAdapter mAdapter;
	private Button logout;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved);
		s = Session.getSession();

		//determines if the user has logged in or not
		//either forces them to login or shows their saved events
		if(s.getUid() == 0)
			startActivity(new Intent(SavedList.this, logintab.class));
		else{

			//initializes all private attributes and creates a swipe adapter
			adapter = new EventListAdapter(SavedList.this, R.layout.eventlayout , events);
			logout = (Button)findViewById(R.id.logout);
			lvName = (ListView)findViewById(android.R.id.list);
			mAdapter = new SwipeActionAdapter(adapter);
			mAdapter.setListView(lvName);
			lvName.setAdapter(mAdapter);

			//adds background to swiping right to delete an event from saved list
			mAdapter.addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT,R.layout.swiperight);

			//gets all of the users saved events
			new DownloadSaved().execute();

			//waits for the user to log out to change screens
			logout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					startActivity(new Intent(SavedList.this, logintab.class));
				}
			});

			//listens for a user to click on a specific event in the list
			lvName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {

					//prints out the events description to the screen
					Toast.makeText(getBaseContext(),events.get(arg2).getDescription(), Toast.LENGTH_LONG).show();
				}
			});

			//waits for a user to swipe an event
			mAdapter.setSwipeActionListener(new SwipeActionListener(){

				@Override
				//tells the list that any item can be swiped
				public boolean hasActions(int position){
					return true;
				}

				@Override
				//tells when a swipe should not be acknowledged which is never
				public boolean shouldDismiss(int position, int direction){
					return false;
				}

				@Override
				//handles logic for specific swipes
				public void onSwipe(int[] positionList, int[] directionList){

					for(int i=0;i<positionList.length;i++) {

						int direction = directionList[i];
						int position = positionList[i];

						//switch statement to differentiate swipe actions
						switch (direction) {

						//does nothing for swiping left
						case SwipeDirections.DIRECTION_FAR_LEFT:
							break;
						case SwipeDirections.DIRECTION_NORMAL_LEFT:
							break;

							//deletes events for swiping right and alerts user
						case SwipeDirections.DIRECTION_FAR_RIGHT:
							s.addDeletedSaved(events.get(position));
							Toast.makeText(SavedList.this, events.get(position) + "has been deleted",Toast.LENGTH_SHORT).show();
							events.remove(position);
							break;
						case SwipeDirections.DIRECTION_NORMAL_RIGHT:
							s.addDeletedSaved(events.get(position));
							Toast.makeText(SavedList.this, events.get(position) + "has been deleted",Toast.LENGTH_SHORT).show();
							events.remove(position);
							break;
						}

						//notifies the adapter that some data has been potentially removed
						adapter.notifyDataSetChanged();
					}
				}
			});

		}

	}
	@Override
	public void onResume(){
		super.onResume();

		//determines if user events is being downloaded
		if (!s.visited()){

			//gets the newly saved events and adds them to the list
			ArrayList<Event> saved = s.getSaved();
			for(int i = 0; i < saved.size();i++){
				events.add(saved.get(i));
			}
			s.emptySaved();
			adapter.notifyDataSetChanged();
		}
		else{
			s.setVisited();
		}

	}

	@Override
	//updates the database when screen isn't being viewed
	protected void onPause(){
		super.onPause();
		new updateDB().execute();
	}

	//asynchronous class to download the users saved events
	private class DownloadSaved extends AsyncTask<Void, Void, Void> {

		private httpHandler parser;
		private ProgressDialog pDialog;

		protected void onPreExecute() {
			super.onPreExecute();
			parser = s.getParser();

			//lets the user know that their events are being loaded
			pDialog = new ProgressDialog(SavedList.this);
			pDialog.setMessage("Loading Saved Events ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... screenNames) {

			//assigns values to the parameters for the API and executes post
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userID", Integer.toString(s.getUid())));
			params.add(new BasicNameValuePair("request","saved events"));
			String json = parser.makeRequest("http://52.10.7.245/api.php", "POST", params);

			//determines if the returned info can be parsed
			JSONArray jArr = null;
			try {
				jArr = new JSONArray(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			//gets JSONObjects for each event retrieved and creates an event object with info
			JSONObject jObj = null;
			for(int i = 0; i < jArr.length();i++){
				try {
					jObj = new JSONObject(jArr.getString(i));
					events.add(new Event(jObj.getInt("eventID"),jObj.getString("name"),jObj.getString("description"),jObj.getString("startDate"),jObj.getString("startTime"), jObj.getInt("allDay")));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			pDialog.dismiss();
			
			//augments downloaded events with newly saved events
			ArrayList<Event> saved = s.getSaved();
			for (int i = 0 ; i < saved.size();i++){
				events.add(saved.get(i));
			}
			s.emptySaved();
			
			//updates list on main thread
			runOnUiThread(new Runnable() {
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
		}
	}
	
	//updates the users saved events by getting rid of events the user deleted
	private class updateDB extends AsyncTask<Void, Void, Void> {
		
		private ArrayList<Event> deleted;
		private httpHandler parser;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			//obtains info from the session
			parser= s.getParser();
			deleted = s.getDeletedSaved();

		}
		@Override
		protected Void doInBackground(Void... screenNames) {
			
			//creates parameters for deleting an event and executes the post
			for (int i = 0; i < deleted.size(); i++){
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userID", Integer.toString(s.getUid())));
				params.add(new BasicNameValuePair("request","delete saved event"));
				params.add(new BasicNameValuePair("eventID", Integer.toString(deleted.get(i).getID())));
				parser.makeRequest("http://52.10.7.245/api.php", "POST", params);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			
			//gets rid of deleted events so they wont be deleted twice
			s.emptySavedDeleted();
		}
	}

}
