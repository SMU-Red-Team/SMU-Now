package com.example.smunow;

import java.util.ArrayList;

//Singleton to hold info that every activity and class needs
public class Session {
	private httpHandler parser = null;
	private static Session session = null;
	private int uid;
	private ArrayList<Event> deleted, saved, stagedSaved, deletedSaved;
	private boolean firstSaved;

	//private constructor to prevent instantiation
	private Session(){
		uid = 0;
		deleted = new ArrayList<Event>();
		saved = new ArrayList<Event>();
		stagedSaved = new ArrayList<Event>();
		deletedSaved = new ArrayList<Event>();
		firstSaved = true;
	}

	//returns a single instance of the HTTPHandler
	public httpHandler getParser(){
		if (parser == null){
			parser = new httpHandler();
		}
		return parser;
	}

	//returns a single instance of the Session object
	//every class gets same data if one instance
	public static Session getSession(){
		if(session == null){
			session = new Session();
		}
		return session;
	}

	//destroys all user relevant info in the session
	public void destroySession(){
		session = null;
		parser = null;
		deleted = new ArrayList<Event>();
		saved = new ArrayList<Event>();
		stagedSaved = new ArrayList<Event>();
		deletedSaved = new ArrayList<Event>();
		firstSaved = true;
		uid = 0;
	}

	//deletes the staged events
	public void emptyStaged(){
		stagedSaved.clear();
	}

	//sets the saved page view as unvisited
	public void setUnVisited(){
		firstSaved = true;
	}

	//sets the saved page view as visited
	public void setVisited(){
		firstSaved = false;
	}

	//returns whether the page is visited or not
	public boolean visited(){
		return firstSaved;
	}

	//adds and event to the deleted events list
	public void addDeleted(Event e){
		deleted.add(e);
	}

	//returns all events in the deleted events list
	public ArrayList<Event> getDeleted(){
		return deleted;
	}

	//adds an event to the saved events list
	public void addSaved(Event e){
		saved.add(e);
	}

	//gets the saved event list and adds saved to staged
	public ArrayList<Event> getSaved(){
		if (saved.size() > 0){
			for(int i = 0; i < saved.size(); i++)
				stagedSaved.add(saved.get(i));
		}
		return saved;
	}
	
	//gets saves staged for commit to database
	//adds saved events not yet in the staged list
	public ArrayList<Event> getStaged(){
		if (saved.size() > 0){
			for (int i = 0; i < saved.size(); i++){
				stagedSaved.add(saved.get(i));
			}
			saved = new ArrayList<Event>();
		}
		return stagedSaved;
	}
	
	//adds a deleted event from the users saved list
	public void addDeletedSaved(Event e){
		deletedSaved.add(e);
	}
	
	//gets the deleted events from a users saved list
	public ArrayList<Event> getDeletedSaved(){
		return deletedSaved;
	}
	
	//set the user id 
	public void setUid(int user){
		uid = user;
	}
	
	//gets the user's id
	public int getUid(){
		return uid;
	}
	
	//deletes the saved list
	public void emptySaved(){
		saved = new ArrayList<Event>();
	}
	
	//deletes all of the deleted events
	public void emptyDeleted(){
		deleted.clear();
	}
	
	//deletes all of the deleted events from saved list
	public void emptySavedDeleted(){
		deletedSaved.clear();
	}
}
