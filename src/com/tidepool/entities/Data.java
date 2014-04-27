package com.tidepool.entities;

import java.io.Serializable;
import java.util.Date;

public class Data implements Serializable {
	private long id;
	private Date time;
	private int bg;
	private int insulin;
	private long userId;
	private int chatId;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public int getBg() {
		return bg;
	}
	public void setBg(int bg) {
		this.bg = bg;
	}
	public int getInsulin() {
		return insulin;
	}
	public void setInsulin(int insulin) {
		this.insulin = insulin;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getChatId() {
		return chatId;
	}
	public void setChatId(int chatId) {
		this.chatId = chatId;
	}
	
	
	
	

}
