package com.ebay.app.raptor.sentirest.resources.util;

import java.util.Date;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

import twitter4j.Status;
@XmlRootElement
public class Tweet {
private String text;
private String latitude;
private String location;
private String country;
private String longitude;
//private String createdAt;
private String createdAt;
private String userName;
private int sentiValue;

public Tweet(){}


public String getLatitude() {
	return latitude;
}
public void setLatitude(String latitude) {
	this.latitude = latitude;
}

public String getLongitude() {
	return longitude;
}
public void setLongitude(String longitude) {
	this.longitude = longitude;
}
public String getText() {
	return text;
}
public void setText(String text) {
	this.text = text;
}


public String getCreatedAt() {
	return createdAt;
}
public void setCreatedAt(String createdAt) {
	this.createdAt = createdAt;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}

public String getLocation() {
	return location;
}


public void setLocation(String location) {
	this.location = location;
}


public String getCountry() {
	return country;
}


public void setCountry(String country) {
	this.country = country;
}


public HashMap<String, String> toMap () {
HashMap<String,String> tweetMap = new HashMap<String, String>();
if (getText() != null && !getText().isEmpty())
tweetMap.put("tweet",getText());
if(getUserName()!= null && !getUserName().isEmpty())
tweetMap.put("user", getUserName());
if(getLatitude()!= null && !getLatitude().isEmpty())
tweetMap.put("latitude", getLatitude());
if(getLongitude()!= null && !getLongitude().isEmpty())
tweetMap.put("longitude", getLongitude());
if(getCreatedAt()!= null)
tweetMap.put("createdAt", getCreatedAt());
if(getLocation() != null && !getLocation().isEmpty())
tweetMap.put("location", getLocation());
if(getCountry() != null && !getCountry().isEmpty())
tweetMap.put("country", getCountry());
tweetMap.put("sentiValue", new Integer(getSentiValue()).toString());
return tweetMap;
}

public int getSentiValue() {
	return sentiValue;
}

public void setSentiValue(int sentiValue) {
	this.sentiValue = sentiValue;
	
}

}



