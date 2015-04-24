package com.example.smunow;

public class Event {
	private int ID, cID, lID, allDay, series;
	private String name, description, start, end, status, url, category, building, room;
	public Event(int id, int cid, int lid, String name, String description, String start, String end, String status, String url, String category){
		ID = id;
		cID = cid;
		lID = lid;
		this.name = name;
		this.description = description;
		this.start = start;
		this.end = end;
		this.status = status;
		this.url = url;
		this.category = category;
		this.allDay = 0;
		this.series = 0;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	
	public int getcID() {
		return cID;
	}
	public void setcID(int cID) {
		this.cID = cID;
	}
	
	public int getlID() {
		return lID;
	}
	public void setlID(int lID) {
		this.lID = lID;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getStart() {
		return start;
	}
	
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	
	public void setEnd(String end) {
		this.end = end;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getBuilding() {
		return building;
	}
	public void setBuilding(String building) {
		this.building = building;
	}
	
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	
	public int isAllDay() {
		return allDay;
	}
	public void setAllDay(int allDay) {
		this.allDay = allDay;
	}
	
	public int isSeries() {
		return series;
	}
	public void setSeries(int series) {
		this.series = series;
	}
	public String toString() {
        return name;
    }
}
