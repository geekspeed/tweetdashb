package com.ebay.app.raptor.sentirest.resources.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface TweetStore {

	public boolean initStore() ;
	public void insertTweet(HashMap<String, String> dataMap) throws Exception ;
	
	public void insertTweets(List<HashMap<String,String>> tweets) throws Exception;
	
	public List<Tweet> getTweets(String keyword, String value)
			throws Exception;
	List<Tweet> getTweets(Date startTime, Date endTime)
			throws Exception;
	
	
}
