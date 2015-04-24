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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class Stream extends Activity{
	private Session s;
	private ListView lvName;
	private ArrayList<Event> events = new ArrayList<Event>();
	private SwipeActionAdapter mAdapter;
	private ArrayAdapter<Event> adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stream);
		s = Session.getSession();
		lvName = (ListView)findViewById(android.R.id.list);
		new downloadStream().execute();
		adapter = new ArrayAdapter<Event>(Stream.this, android.R.layout.simple_list_item_1 , events);
		mAdapter = new SwipeActionAdapter(adapter);
		mAdapter.setListView(lvName);

		lvName.setAdapter(mAdapter);
		mAdapter.addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT,R.layout.swipeleft)
		.addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT,R.layout.swiperight);

		lvName.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(getBaseContext(),events.get(arg2).getDescription(), Toast.LENGTH_LONG).show();
			}
		});
		mAdapter.setSwipeActionListener(new SwipeActionListener(){
			@Override
			public boolean hasActions(int position){
				// All items can be swiped
				return true;
			}

			@Override
			public boolean shouldDismiss(int position, int direction){
				// Only dismiss an item when swiping normal left
				return false;
			}

			@Override
			public void onSwipe(int[] positionList, int[] directionList){
				if(s.getUid() == 0){
					startActivity(new Intent(Stream.this, logintab.class));
					finish();
					return;
				}
				for(int i=0;i<positionList.length;i++) {
					int direction = directionList[i];
					int position = positionList[i];
					switch (direction) {
					case SwipeDirections.DIRECTION_FAR_LEFT:
						s.addSaved(events.get(position));
						Toast.makeText(Stream.this, events.get(position) + " has been saved to your list",Toast.LENGTH_SHORT).show();
						break;
					case SwipeDirections.DIRECTION_NORMAL_LEFT:
						s.addSaved(events.get(position));
						Toast.makeText(Stream.this, events.get(position) + " has been saved to your list",Toast.LENGTH_SHORT).show();
						break;
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
					adapter.notifyDataSetChanged();
				}
			}
		});
	}
	@Override
	protected void onPause(){
		super.onPause();
		new updateDB().execute();
	}
	private class downloadStream extends AsyncTask<Void, Void, Void> {
		private httpHandler parser;
		private ProgressDialog pDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			parser = s.getParser();
			pDialog = new ProgressDialog(Stream.this);
			pDialog.setMessage("Loading Events ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected Void doInBackground(Void... screenNames) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			if (s.getUid() != 0){
				Log.d("something", "here we go bitches");
				params.add(new BasicNameValuePair("userID", Integer.toString(s.getUid())));
				params.add(new BasicNameValuePair("request","user events"));
			}
			else{
				params.add(new BasicNameValuePair("request","get events"));
			}
			String json = parser.makeRequest("http://52.10.7.245/api.php", "POST", params);
			Log.d("Json result", json);
			JSONArray jArr = null;
			try {
				jArr = new JSONArray(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject jObj = null;
			for(int i = 0; i < jArr.length();i++){
				try {
					Log.d("JSONARRAY STRING", jArr.getString(i));
					jObj = new JSONObject(jArr.getString(i));
					Log.d("EVENT NAME", jObj.getString("name"));
					events.add(new Event(jObj.getInt("eventID"),jObj.getInt("contactID"),jObj.getInt("locationID"),jObj.getString("name"),jObj.getString("description"),jObj.getString("startDate"),jObj.getString("endDate"),jObj.getString("status"),jObj.getString("url"),jObj.getString("categorization")));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return null;
		}

		// onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
		@Override
		protected void onPostExecute(Void v) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					adapter.notifyDataSetChanged();
				}
			});

		}
	}
	private class updateDB extends AsyncTask<Void, Void, Void> {
		private ArrayList<Event> deleted;
		private httpHandler parser;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			parser = s.getParser();
			deleted = s.getDeleted();
			
		}
		@Override
		protected Void doInBackground(Void... screenNames) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (int i = 0; i < deleted.size(); i++){
				params.add(new BasicNameValuePair("userID", Integer.toString(s.getUid())));
				params.add(new BasicNameValuePair("request","delete event"));
				params.add(new BasicNameValuePair("eventID", Integer.toString(deleted.get(i).getID())));
				parser.makeRequest("http://52.10.7.245/api.php", "POST", params);
				params.clear();
			}
			return null;
		}

		// onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
		@Override
		protected void onPostExecute(Void v) {
			s.emptyDeleted();
		}
	}
}
