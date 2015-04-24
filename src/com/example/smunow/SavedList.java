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
import android.app.TabActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class SavedList extends ListActivity{
	private Session s;
	private ListView lvName;
	private ArrayList<Event> events = new ArrayList<Event>();
	private SwipeActionAdapter mAdapter;
	private ArrayAdapter<Event> adapter;
	private Button logout;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved);
		Log.d("this part is active", "herehehehhlkajsdf");
		s = Session.getSession();
		logout = (Button)findViewById(R.id.logout);
		lvName = (ListView)findViewById(android.R.id.list);
		if(s.getUid() == 0){
			startActivity(new Intent(SavedList.this, logintab.class));
			finish();
		}
		else{
			lvName = (ListView)findViewById(android.R.id.list);
			new DownloadSaved().execute();
			adapter = new ArrayAdapter<Event>(SavedList.this, android.R.layout.simple_list_item_1 , events);
			mAdapter = new SwipeActionAdapter(adapter);
			mAdapter.setListView(lvName);

			lvName.setAdapter(mAdapter);
			mAdapter.addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT,R.layout.swiperight);
			logout.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	s.destroySession();
	            	startActivity(new Intent(SavedList.this, logintab.class));
	            	finish();
	            }
	        });
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
					for(int i=0;i<positionList.length;i++) {
						int direction = directionList[i];
						int position = positionList[i];
						switch (direction) {
						case SwipeDirections.DIRECTION_FAR_LEFT:
							break;
						case SwipeDirections.DIRECTION_NORMAL_LEFT:
							break;
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
						adapter.notifyDataSetChanged();
					}
				}
			});
		}

	}
	@Override
	public void onResume(){
		super.onResume();
		if (adapter != null){
			Log.d("this part is active", "suck my dick");
			ArrayList<Event> saved = s.getSaved();
			for(int i = 0; i < saved.size();i++){
				events.add(saved.get(i));
			}
			adapter.notifyDataSetChanged();
		}

	}
	@Override
	protected void onPause(){
		super.onPause();
		Log.d("something", "executing this code");
		new updateDB().execute();
		new updateSaved().execute();
	}
	private class DownloadSaved extends AsyncTask<Void, Void, Void> {
		private httpHandler parser;
		private ProgressDialog pDialog;
		protected void onPreExecute() {
			super.onPreExecute();
			parser = s.getParser();
			pDialog = new ProgressDialog(SavedList.this);
			pDialog.setMessage("Loading Saved Events ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected Void doInBackground(Void... screenNames) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userID", Integer.toString(s.getUid())));
			params.add(new BasicNameValuePair("request","saved events"));
			String json = parser.makeRequest("http://52.10.7.245/api.php", "POST", params);
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
		@Override
		protected void onPostExecute(Void v) {
			pDialog.dismiss();
			ArrayList<Event> saved = s.getSaved();
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
	private class updateSaved extends AsyncTask<Void, Void, Void> {
		private ArrayList<Event> saved;
		private httpHandler parser;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			parser= s.getParser();
			saved = s.getSaved();

		}
		@Override
		protected Void doInBackground(Void... screenNames) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (int i = 0; i < saved.size(); i++){
				params.add(new BasicNameValuePair("userID", Integer.toString(s.getUid())));
				params.add(new BasicNameValuePair("request","save event"));
				params.add(new BasicNameValuePair("eventID", Integer.toString(saved.get(i).getID())));
				parser.makeRequest("http://52.10.7.245/api.php", "POST", params);
				params.clear();
			}
			return null;
		}

		// onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
		@Override
		protected void onPostExecute(Void v) {
			s.emptySaved();

		}
	}
	private class updateDB extends AsyncTask<Void, Void, Void> {
		private ArrayList<Event> deleted;
		private httpHandler parser;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			parser= s.getParser();
			deleted = s.getDeletedSaved();

		}
		@Override
		protected Void doInBackground(Void... screenNames) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (int i = 0; i < deleted.size(); i++){
				params.add(new BasicNameValuePair("userID", Integer.toString(s.getUid())));
				params.add(new BasicNameValuePair("request","delete saved event"));
				params.add(new BasicNameValuePair("eventID", Integer.toString(deleted.get(i).getID())));
				parser.makeRequest("http://52.10.7.245/api.php", "POST", params);
				params.clear();
			}
			return null;
		}

		// onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
		@Override
		protected void onPostExecute(Void v) {
			s.emptySavedDeleted();

		}
	}

}
