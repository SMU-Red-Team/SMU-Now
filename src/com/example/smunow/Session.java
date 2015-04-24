package com.example.smunow;

import java.util.ArrayList;

public class Session {
	private httpHandler parser = null;
	private static Session session = null;
	private int uid;
	private ArrayList<Event> deleted;
	private ArrayList<Event> saved;
	private ArrayList<Event> deletedSaved;
	private Session(){
		uid = 0;
		deleted = new ArrayList<Event>();
		saved = new ArrayList<Event>();
		deletedSaved = new ArrayList<Event>();
	}
	public httpHandler getParser(){
		if (parser == null){
			parser = new httpHandler();
		}
		return parser;
	}
	public static Session getSession(){
		if(session == null){
			session = new Session();
		}
		return session;
	}
	public void destroySession(){
		session = null;
		parser = null;
		deleted = null;
		saved = null;
		deletedSaved = null;
		uid = 0;
	}
	public void addDeleted(Event e){
		deleted.add(e);
	}
	public ArrayList<Event> getDeleted(){
		return deleted;
	}
	public void addSaved(Event e){
		saved.add(e);
	}
	public ArrayList<Event> getSaved(){
		return saved;
	}
	public void addDeletedSaved(Event e){
		deletedSaved.add(e);
	}
	public ArrayList<Event> getDeletedSaved(){
		return deletedSaved;
	}
	public void setUid(int user){
		uid = user;
	}
	public int getUid(){
		return uid;
	}
	public void emptySaved(){
		saved.clear();
	}
	public void emptyDeleted(){
		deleted.clear();
	}
	public void emptySavedDeleted(){
		deletedSaved.clear();
	}
}
