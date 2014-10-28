package com.ebay.app.raptor.sentirest.resources.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TweetUtil {

	static List<Status> list = new ArrayList<Status>();
	static HashMap<String , Integer> tweetSentiMap = new HashMap<String , Integer>();
     
	
		
	
	
public static final int getSentimentOfTweet(final Status status) throws Exception {
	
    if(!(tweetSentiMap.size() > 0)) {
    	init();
    }
	//Remove all punctuation and new line chars in the tweet.
	final String tweet = status.getText().replaceAll("\\p{Punct}|\\n", " ").toLowerCase();
	//Splitting the tweet on empty space.
	final Iterable<String> words = Splitter.on(' ')
	.trimResults()
	.omitEmptyStrings()
	.split(tweet);
	int sentimentOfCurrentTweet = 0;
	//Loop thru all the words and find the sentiment of this tweet.
	for (final String word : words) {
	if(tweetSentiMap.containsKey(word)){
	sentimentOfCurrentTweet += tweetSentiMap.get(word);
	}
	}
	
	return sentimentOfCurrentTweet;
	}
	
	
	
	public static Tweet getTweet(Status status) throws Exception {
		Tweet tw = new Tweet();
		if(status != null) {
		tw.setText(status.getText());
		if(status.getGeoLocation() != null){
		tw.setLatitude(new Double(status.getGeoLocation().getLatitude()).toString());
		tw.setLongitude(new Double(status.getGeoLocation().getLongitude()).toString());
		}
		if(status.getUser() != null)
		tw.setUserName(status.getUser().getScreenName());
		if(status.getCreatedAt() != null) 

			tw.setCreatedAt(new Long(status.getCreatedAt().getTime()).toString());
		
		
		if(status.getPlace()!= null && status.getPlace().getFullName() != null )
			tw.setLocation(status.getPlace().getFullName());
		
		if(status.getPlace()!= null && status.getPlace().getFullName() != null )
			tw.setCountry(status.getPlace().getCountry());
		int sentiValue = getSentimentOfTweet(status);
		tw.setSentiValue(sentiValue);
		}
		return tw;
	}
	
	
	
	
public static Tweet getTweet(HashMap<String ,String> dataMap) {
	Tweet tw = new Tweet();
	if(dataMap != null && dataMap.size() > 0) {
	 	
	  if(dataMap.containsKey("tweet")) 
		  tw.setText(dataMap.get("tweet"));
	  if(dataMap.containsKey("user"))
		  tw.setUserName(dataMap.get("user"));
	  if(dataMap.containsKey("latitude"))
		  tw.setLatitude(dataMap.get("latitude"));
	  if(dataMap.containsKey("longitude"))
		  tw.setLongitude(dataMap.get("longitude"));
	  if(dataMap.containsKey("createdAt")) {
		  long time = new Long(dataMap.get("createdAt"));
		  Date d = new Date(time);
		  String createdAt = d.toString();
		  tw.setCreatedAt(createdAt);
	  }
	  if(dataMap.containsKey("location"))
		  tw.setLocation(dataMap.get("location"));
	  if(dataMap.containsKey("country"))
		  tw.setCountry(dataMap.get("country"));
	  if(dataMap.containsKey("sentiValue"))
		  tw.setSentiValue(Integer.valueOf(dataMap.get("sentiValue")));
	}
	return tw;
	}


	public static List<Tweet> searchTwitter(String keyword , String sinceDate , String untilDate) {
		List<Tweet> tweets = new ArrayList<Tweet>();
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("Y7S5yaBBSHCtmjZyVwqBDvXDM")
		  .setOAuthConsumerSecret("NTNCW5CyOQClcYAnDhzbj4m999CshxVelbJWrNVp6rZCEjKddi")
		  .setOAuthAccessToken("17062355-EgCaiuwj9OJetjPLWqL9BYE7CHadT56LF2l78Pbx5")
		  .setOAuthAccessTokenSecret("QexVJLXyJpdpMMOnZNzkDmFZNBymBLwtYkIr1NJjJqTzI");
		 Twitter twitter = new TwitterFactory(cb.build()).getInstance();
	        try {
	            Query query = new Query(keyword);
	            query.setSince(sinceDate);
	            query.setUntil(untilDate);
	            QueryResult result;
	            do {
	                result = twitter.search(query);
	                List<Status> twitterOut = result.getTweets();
	               // System.out.println("========================="+ tweets.size());
	                for (Status status : twitterOut) {
	                	try {
	                		
	                		Tweet tw = TweetUtil.getTweet(status);
	                		//Time format is in lon set it as readable string
	                	    long time = new Long(tw.getCreatedAt());
	              		  Date d = new Date(time);
	              		  String createdAt = d.toString();
	              		  tw.setCreatedAt(createdAt);
							tweets.add(tw);
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace(System.out);
							continue;
						}
	                   
	                }
	            } while ((query = result.nextQuery()) != null);
	          return tweets;
	        } catch (TwitterException te) {
	            te.printStackTrace();
	            System.out.println("Failed to search tweets: " + te.getMessage());
	           return tweets;
	        }
	    }
	


	public  static void  main(String args[]) throws FileNotFoundException {
		
	//	InputStream is1 = new FileInputStream(new File("C:\\sentiValue.properties"));
		//System.out.println(is1);
		//searchTwitter("apple");
		/*ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("Y7S5yaBBSHCtmjZyVwqBDvXDM")
		  .setOAuthConsumerSecret("NTNCW5CyOQClcYAnDhzbj4m999CshxVelbJWrNVp6rZCEjKddi")
		  .setOAuthAccessToken("17062355-EgCaiuwj9OJetjPLWqL9BYE7CHadT56LF2l78Pbx5")
		  .setOAuthAccessTokenSecret("QexVJLXyJpdpMMOnZNzkDmFZNBymBLwtYkIr1NJjJqTzI");
		   TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
	        StatusListener listener = new StatusListener() {
	        	
	        	private boolean isEbayRelated(String tweet) {
	        		if (tweet!= null) {
	        			if(tweet.contains("ebay") || tweet.contains("EBAY") || tweet.contains("eBay") ) {
	        				return true;
	        			}
	        		}
	        		
	        		return false;
	        	}
	            @Override
	            public void onStatus(Status status) {
	               
	            	if(isEbayRelated(status.getText())) {
	            	System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
	                list.add(status);
	            	}
	            }

	            @Override
	            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	            }

	            @Override
	            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
	            }

	            @Override
	            public void onScrubGeo(long userId, long upToStatusId) {
	                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	            }

	            @Override
	            public void onStallWarning(StallWarning warning) {
	                System.out.println("Got stall warning:" + warning);
	            }

	            @Override
	            public void onException(Exception ex) {
	                ex.printStackTrace();
	            }

			

			
	        };
	        twitterStream.addListener(listener);
	        twitterStream.sample();*/
	    }
		
	

	private static void init() throws Exception {
		
		try {
		    
			//final URL url = Resources.getResource("sentiValues.properties");
			//InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("sentiValue.properties");
			InputStream is1 = null;
							
			   is1 = TweetStoreFactory.getTweetStore(TweetStoreType.MONGO).getClass().getResourceAsStream("sentiValue.properties");
			   if(is1 == null)
				try {
					// Because of difference in behavior across Tomcat versions. Need to find flavour of loading resource that works 
					       //across tomcat versions
					is1 = new FileInputStream(new File("C:\\sentiValue.properties"));
					System.out.println("=====Read value from c::");
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					is1 = new FileInputStream(new File("/sentiValue.properties"));
					System.out.println("===READ from /");
				}
			
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
			    byte[] buffer = new byte[1024];
			    int length = 0;
			    while ((length = is1.read(buffer)) != -1) {
			        baos.write(buffer, 0, length);
			    }
			    String text = new String( baos.toByteArray(),"UTF-8");
			
			/*final String text = Resources.toString(url, Charsets.UTF_8);*/
			final Iterable<String> lineSplit = Splitter.on("\n").trimResults().omitEmptyStrings().split(text);
			List<String> tabSplit;
			for (final String str: lineSplit) {
				tabSplit = Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(str));
				try {
					tweetSentiMap.put(tabSplit.get(0), Integer.parseInt(tabSplit.get(1)));
					System.out.println(" Size is ==========" + tweetSentiMap.size());
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace(System.out);
					continue;
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable th) {
			// TODO Auto-generated catch block
			th.printStackTrace(System.out);
		}
	}
	}
