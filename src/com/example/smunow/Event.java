package com.example.smunow;

//Event objects hold all relevant information for an obtained event
public class Event{
	
	private int ID, allDay;
	private String name, description, start, time;
	
	//constructor takes in all relevant info for an event
	public Event(int id, String name, String description, String start, String time, int allDay){
		ID = id;
		this.name = name;
		this.description = description;
		this.start = start;
		this.time = time;
		this.allDay= allDay;
	}
	
	//gets the event ID
	public int getID() {
		return ID;
	}
	
	//lets the system know if the event is all day or not
	public boolean allDay(){
		if(allDay == 0){
			return false;
		}
		else{
			return true;
		}
	}
	
	//gets the Event name
	public String getName() {
		return name;
	}
	
	//gets the description of the event
	public String getDescription() {
		return description;
	}
	
	//gets the start date for the event
	public String getStart() {
		return start;
	}
	
	//gets the start time for the event
	public String getTime() {
		return time;
	}
	
	//allows the object to be printed in a standard ListView adapter
	public String toString() {
		return name;
	}
	
}
