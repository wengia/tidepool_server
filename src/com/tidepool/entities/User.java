package com.tidepool.entities;

import java.io.Serializable;
import java.util.Date;


@SuppressWarnings("serial")
public class User implements Serializable {
	private long id;
	private String email;
	private String username;
	private String password;
	private String confirmPwd;
	private String phoneNo;
	private Date dateOfBirth;
	private String gender;
	private String role;
	private double location_lat;
	private double location_lng;
	
	public double getLocation_lat() {
		return location_lat;
	}
	public void setLocation_lat(double location_lat) {
		this.location_lat = location_lat;
	}
	public double getLocation_lng() {
		return location_lng;
	}
	public void setLocation_lng(double location_lng) {
		this.location_lng = location_lng;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPwd() {
		return confirmPwd;
	}
	public void setConfirmPwd(String confirmPwd) {
		this.confirmPwd = confirmPwd;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public boolean validate() {
		if(email == null || username == null || password == null || role == null)
			return false;
		if(confirmPwd == null || !password.equals(confirmPwd))
			return false;
		
		return true;
	}
	
	

}
