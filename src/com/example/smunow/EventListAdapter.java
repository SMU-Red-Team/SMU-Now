package com.example.smunow;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//special adapter used for putting data into ListView with 2 TextViews
public class EventListAdapter extends ArrayAdapter<Event>{
	
	private final Context context;
	private final ArrayList<Event> Events;
	private final int layoutResourceId;
	
	//constructor which takes in all relevant info for the ListView and initializes private variables
	public EventListAdapter(Context context, int layoutResourceId, ArrayList<Event> data) {
		super(context, layoutResourceId, data);
		this.context = context;
		this.Events = data;
		this.layoutResourceId = layoutResourceId;
	}
	
	//overrides the getView function to assign info to specific TextViews
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		//assigns view to the specially created layout XML file
		View row = convertView;
		ViewHolder holder = null;
		
		if(row == null)
		{
			//inflates the layout if one was not specified
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			
			//sets up TextViews in the holder class
			holder = new ViewHolder();
			holder.textView1 = (TextView)row.findViewById(R.id.eventName);
			holder.textView2 = (TextView)row.findViewById(R.id.eventTime);
			row.setTag(holder);
		}
		else
			
			//gets all tags from the layout
			holder = (ViewHolder)row.getTag();
		
		//sets the event name in the ListView
		holder.textView1.setText(Events.get(position).toString());
		
		//determines if the event is all day or not and assigns a time or ALL DAY
		if (!Events.get(position).allDay())
			holder.textView2.setText(Events.get(position).getTime());
		else
			holder.textView2.setText("ALL DAY");
		
		return row;
	}
	
	//holds all of the Layout Attributes that will be used
	static class ViewHolder
	{
		TextView textView1;
		TextView textView2;
	}
}
