package com.ebay.app.raptor.sentirest.resources.util;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoTweetStore implements TweetStore {
	
	private DB tweetDb ;
	
	private DBCollection tweetColl;
	
	private static MongoTweetStore mongoStore = new MongoTweetStore();
	
	public static MongoTweetStore getInstance() {
		
		return mongoStore;
				
	}
	
	private  MongoTweetStore()  {
		 initStore();
		
	}

	@Override
	public void insertTweet(HashMap<String, String> dataMap) throws Exception {
		BasicDBObject document = getDBObject(dataMap);
		tweetColl.insert(document);

	}
	
	public BasicDBObject getDBObject(HashMap<String ,String> dataMap)  throws Exception {
		BasicDBObject document = new BasicDBObject();
		for(Entry<String, String> e : dataMap.entrySet()){
		document.put(e.getKey(),e.getValue());	
		}
	
		return document;
	}

	@Override
	public void insertTweets(List<HashMap<String, String>> tweets)
			throws Exception {
		BulkWriteOperation builder = tweetColl.initializeOrderedBulkOperation();
		for(HashMap<String, String> tweet: tweets) {
			BasicDBObject doc = getDBObject(tweet);
			builder.insert(doc);
			
		}
		BulkWriteResult result = builder.execute();
			
	}

	@Override
	public List<Tweet> getTweets(String keyword , String value)
			throws Exception {
		BasicDBObject query = new BasicDBObject(keyword , value);
		Cursor cursor = tweetColl.find(query);
		List<Tweet> tweetList = new ArrayList<Tweet>();

		try {
		   while(cursor.hasNext()) {
		       DBObject db = cursor.next();
		       HashMap<String, String>smap = getMapFromDBObject(db.toMap());
		       Tweet t = TweetUtil.getTweet(smap);
		       tweetList.add(t);
		   }
		} finally {
		   cursor.close();
		}
		return tweetList;
	}

	@Override
	public List<Tweet> getTweets(Date startDate , Date endDate)
			throws Exception {
		String start = Long.valueOf( startDate.getTime()).toString();
		String end = Long.valueOf(endDate.getTime()).toString();
		BasicDBObject query = new BasicDBObject("createdAt" , new BasicDBObject("$gte", start).append("$lt", end));
		
		Cursor cursor = tweetColl.find(query);
		List<Tweet> tweetList = new ArrayList<Tweet>();

		try {
		   while(cursor.hasNext()) {
		       DBObject db = cursor.next();
		       HashMap<String, String>smap = getMapFromDBObject(db.toMap());
		     Tweet t =  TweetUtil.getTweet(smap);
		     tweetList.add(t);
		   }
		} finally {
		   cursor.close();
		}
		return tweetList;
	}
	private static HashMap<String, String> getMapFromDBObject(Map map) {
		HashMap<String , String> strMap = new HashMap<String , String>();
		for(Object obj : map.keySet()) {
			strMap.put(obj.toString(),map.get(obj).toString());
		}
		
		return strMap;
	}

	@Override
	public boolean initStore() {
	    boolean init = false;
		MongoClient mongoClient;
		try {
		//	mongoClient = new MongoClient( "blr-appdv-043.corp.ebay.com" , 27017 );
			mongoClient = new MongoClient( "10.9.205.208" , 27017 );
		
		 tweetDb = mongoClient.getDB( "tweetdb" );
		
		 tweetColl = tweetDb.getCollection("tweets");
		
		} catch (Throwable e) {
			
			e.printStackTrace(System.out);
			return init;
		}
		init = true;
		return init ;
		
	}
	
	private static List<HashMap<String , String>>  get(Date startDate , Date endDate) throws UnknownHostException {
		
	String start = Long.valueOf( startDate.getTime()).toString();
	String end = Long.valueOf(endDate.getTime()).toString();
	BasicDBObject query = new BasicDBObject("time" , new BasicDBObject("$gte", start).append("$lt", end));
	//MongoClient mongoClient = new MongoClient( "blr-appdv-043.corp.ebay.com" , 27017 );
	MongoClient mongoClient = new MongoClient( "10.9.205.208" , 27017 ); 
	DB tryDb = mongoClient.getDB( "trydb" );
		
		DBCollection tryColl = tryDb.getCollection("try");
	Cursor cursor = tryColl.find(query);
	List<HashMap<String , String>> tweetList = new ArrayList<HashMap<String , String>>();

	try {
	   while(cursor.hasNext()) {
	       DBObject db = cursor.next();
	       HashMap<String, String>smap = getMapFromDBObject(db.toMap());
	       tweetList.add(smap);
	   }
	} finally {
	   cursor.close();
	}
	return tweetList;
}
	private static void printResults(List<HashMap<String , String>> list) {
		for(HashMap<String , String> hm: list) {
			for(String str : hm.values())
				System.out.println(str);
		}
	}
	public  static void  main(String[] args) throws Exception {
		MongoTweetStore str = new MongoTweetStore();
		MongoClient mongoClient;
		try {
			mongoClient = new MongoClient( "blr-appdv-043.corp.ebay.com" , 27017 );
		
		
		 DB tryDb = mongoClient.getDB( "trydb" );
		
		DBCollection tryColl = tryDb.getCollection("try");
		BasicDBObject doc = new BasicDBObject();
		BasicDBObject doc1 = new BasicDBObject();
		BasicDBObject doc2 = new BasicDBObject();
		BasicDBObject doc3 = new BasicDBObject();
		BasicDBObject doc4 = new BasicDBObject();
		Date predefined = new SimpleDateFormat("yyyy-MM-dd").parse("2010-01-01");
		Date predefined1 = new SimpleDateFormat("yyyy-MM-dd").parse("2011-01-01");
		Date predefined2 = new SimpleDateFormat("yyyy-MM-dd").parse("2012-01-01");
		Date predefined3 = new SimpleDateFormat("yyyy-MM-dd").parse("2013-01-01");
		Date predefined4 = new SimpleDateFormat("yyyy-MM-dd").parse("2014-01-01");
		
		/*
		doc.put("year", "2010");
		doc.put("time", new Long(predefined.getTime()).toString());
		
		doc1.put("year", "2011");
		doc1.put("time", new Long(predefined1.getTime()).toString());
		
		doc2.put("year", "2012");
		doc2.put("time", new Long(predefined2.getTime()).toString());
		
		doc3.put("year", "2013");
		doc3.put("time", new Long(predefined3.getTime()).toString());
		
		doc4.put("year", "2014");
		doc4.put("time", new Long(predefined4.getTime()).toString());
		
		tryColl.insert(doc);
		tryColl.insert(doc1);
		tryColl.insert(doc2);
		tryColl.insert(doc3);
		tryColl.insert(doc4);*/
		
		
		List<HashMap<String , String>> resultList = get(predefined , predefined4);
		System.out.println("==========Between 10 to 14");
		printResults(resultList);
		
		 resultList = get(predefined1 , predefined4);
		System.out.println("======Between 11 to 14");
		printResults(resultList);
		
		 resultList = get(predefined2 , predefined4);
		System.out.println("========Between 12 to 14");
		printResults(resultList);
		
		 resultList = get(predefined3 , predefined4);
		System.out.println("Between 13 to 14");
		printResults(resultList);
		
		} catch (Throwable e) {
			
			e.printStackTrace(System.out);
			
		}
		
		Date predefined = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2010-01-01T10:10:10");
		//2010-04-16T11:15:17
		/*List<HashMap<String, String>> list = new ArrayList<HashMap<String ,String>>();
		HashMap<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("name", "Jai");
		dataMap.put("age", "31");
		dataMap.put("company", "ebay");
		dataMap.put("mane", "jambumane");
		//str.insertTweet(dataMap);
		list.add(dataMap);
		HashMap<String, String> dataMap1 = new HashMap<String, String>();
		dataMap.put("name", "ram");
		dataMap.put("age", "35");
		dataMap.put("company", "wipro");
		dataMap.put("mane", "jambumane");
		//str.insertTweet(dataMap);
		list.add(dataMap1);
		str.insertTweets(list);
		List<HashMap<String , String>> tweets = str.getTweets("name", "ram");
		System.out.println("Size of the list for Vivek " + tweets.size());
		for(HashMap<String, String> tweet :tweets) {
			System.out.println(tweet);
		}
		
		 tweets = str.getTweets("name", "vinay");
		System.out.println("Size of the list for Vinay " + tweets.size());
		for(HashMap<String, String> tweet :tweets) {
			System.out.println(tweet);
		}
		
		tweets = str.getTweets("mane", "jambumane");
		System.out.println("Size of the list for jambumane " + tweets.size());
		for(HashMap<String, String> tweet :tweets) {
			System.out.println(tweet);
		}
		*/
	}

}
