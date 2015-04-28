package com.example.smunow;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter.SwipeActionListener;
import com.wdullaer.swipeactionadapter.SwipeDirections;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

@SuppressWarnings("deprecation")

//creates and manages the event stream page
public class Stream extends Activity{

	private Session s;
	private ListView lvName;
	private ArrayList<Event> events = new ArrayList<Event>();
	private SwipeActionAdapter mAdapter;
	private EventListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stream);

		//sets up layout attributes and adapters
		s = Session.getSession();
		lvName = (ListView)findViewById(android.R.id.list);
		adapter=new EventListAdapter(Stream.this, R.layout.eventlayout, events);
		mAdapter = new SwipeActionAdapter(adapter);
		mAdapter.setListView(lvName);
		lvName.setAdapter(mAdapter);

		//adds a save and a delete background for swiping
		mAdapter.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT,R.layout.swipeleft)
		.addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT,R.layout.swiperight);

		//downloads all events for the events tab
		new downloadStream().execute();

		//waits for a list event to be clicked
		lvName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				//displays the description of the specific event to the user
				Toast.makeText(getBaseContext(),events.get(arg2).getDescription(), Toast.LENGTH_LONG).show();
			}
		});

		//gets swipe action event information
		mAdapter.setSwipeActionListener(new SwipeActionListener(){

			@Override
			//allows all events to be swipeable
			public boolean hasActions(int position){
				return true;
			}

			@Override
			//determines which event swipes should be acknowledged
			//all should so it returns false
			public boolean shouldDismiss(int position, int direction){
				return false;
			}

			@Override
			//determines what to do when an item is swiped
			public void onSwipe(int[] positionList, int[] directionList){

				//if the user swipes but isn't logged in, then forces login
				if(s.getUid() == 0){
					startActivity(new Intent(Stream.this, logintab.class));
					finish();
					return;
				}
				else{
					for(int i=0;i<positionList.length;i++) {

						int direction = directionList[i];
						int position = positionList[i];

						//different actions depending on swipe direction
						switch (direction) {

						//any swipe left saves an item
						//deletes to prevent duplicate swipes
						case SwipeDirections.DIRECTION_FAR_LEFT:
							s.addSaved(events.get(position));
							Toast.makeText(Stream.this, events.get(position) + " has been saved to your list",Toast.LENGTH_SHORT).show();
							events.remove(position);
							break;
						case SwipeDirections.DIRECTION_NORMAL_LEFT:
							s.addSaved(events.get(position));
							Toast.makeText(Stream.this, events.get(position) + " has been saved to your list",Toast.LENGTH_SHORT).show();
							events.remove(position);
							break;

							//any swipe right deletes and event
							//removes event from the list
						case SwipeDirections.DIRECTION_FAR_RIGHT:
							s.addDeleted(events.get(position));
							Toast.makeText(Stream.this, events.get(position) + " has been deleted",Toast.LENGTH_SHORT).show();
							events.remove(position);
							break;
						case SwipeDirections.DIRECTION_NORMAL_RIGHT:
							s.addDeleted(events.get(position));
							Toast.makeText(Stream.this, events.get(position) + " has been deleted",Toast.LENGTH_SHORT).show();
							events.remove(position);
							break;
						}

						//lets the adapter know that the events have changed
						adapter.notifyDataSetChanged();
					}
				}
			}
		});
	}

	@Override
	//when activity is out of view, update database
	protected void onPause(){
		super.onPause();
		new updateDB().execute();
	}

	//Asynchronous thread to download a stream of events
	private class downloadStream extends AsyncTask<Void, Void, Void> {

		private httpHandler parser;
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			parser = s.getParser();

			//lets the user know what's going on
			pDialog = new ProgressDialog(Stream.this);
			pDialog.setMessage("Loading Events ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... screenNames) {

			//creates a list of parameters based on if the user is logged in or not
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (s.getUid() != 0){
				params.add(new BasicNameValuePair("userID", Integer.toString(s.getUid())));
				params.add(new BasicNameValuePair("request","user events"));
			}
			else
				params.add(new BasicNameValuePair("request","get events"));

			//executes post 
			String json = parser.makeRequest("http://52.10.7.245/api.php", "POST", params);

			//attempts to parse the response
			JSONArray jArr = null;
			try {
				jArr = new JSONArray(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			//gets event information and adds events to the stream
			JSONObject jObj = null;
			for(int i = 0; i < jArr.length();i++){
				try {

					//creates an object to get the event info and make an object
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

			//tells the main thread to update the list
			runOnUiThread(new Runnable() {
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});

		}
	}

	//asynchronous task to update the deleted events whenever screen isn't being viewed
	private class updateDB extends AsyncTask<Void, Void, Void> {

		private ArrayList<Event> deleted;
		private httpHandler parser;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			//gets data from the scanner
			parser = s.getParser();
			deleted = s.getDeleted();

		}

		@Override
		protected Void doInBackground(Void... screenNames) {

			//creates parameters for deleting an event from the users list
			for (int i = 0; i < deleted.size(); i++){
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("userID", Integer.toString(s.getUid())));
				params.add(new BasicNameValuePair("request","delete event"));
				params.add(new BasicNameValuePair("eventID", Integer.toString(deleted.get(i).getID())));
				parser.makeRequest("http://52.10.7.245/api.php", "POST", params);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {

			//gets rid of deleted events so they aren't deleted twice
			s.emptyDeleted();
		}
	}
}
