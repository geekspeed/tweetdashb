package org.elasticsearch.river.twitter;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import net.spy.memcached.MemcachedClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.InetSocketAddress;
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
public static double[] getLatLng(String location) throws Exception {
		/*String link = "http://maps.googleapis.com/maps/api/geocode/json?address="
				+ location + "&sensor=false";*/
		double[] coord = new double[2];
		if(!location.equals("")){
		String mpquest="http://open.mapquestapi.com/nominatim/v1/search.php?format=json&q="+location;
		Thread.sleep(900);
		JSONArray jsonObject = readJsonFromUrl(mpquest);
		
		if(jsonObject.length()!=0){
			
			coord[0]=Double.parseDouble(jsonObject.getJSONObject(0).get("lon").toString());
			coord[1]=Double.parseDouble(jsonObject.getJSONObject(0).get("lat").toString());
			
		}
		else{
			coord=new double[2];
		}
		
	/*	if (jsonObject.get("status").equals("OK")) {
			
			JSONObject res = jsonObject.getJSONArray("results").getJSONObject(0);
			System.out.println(res.getString("formatted_address"));
			JSONObject loc = res.getJSONObject("geometry").getJSONObject(
					"location");
			System.out.println("lat: " + loc.getDouble("lat") + ", lng: "
					+ loc.getDouble("lng"));
			
			coord.put(loc.getDouble("lng"));
			coord.put(loc.getDouble("lat"));
			System.out.println(coord+coord.toString());
			
		} else{
			coord=new JSONArray("[]");
			System.out.println(coord);
		}
		*/
		}else{
			coord=new double[2];
		}
		System.out.println(coord[0]+","+coord[1]);
		return coord;
	}
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	public static JSONArray readJsonFromUrl(String url) throws IOException,
			JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
//			System.out.println(jsonText);
			JSONArray json = new JSONArray(jsonText);
			return json;
		} finally {
			is.close();
		}
	}


public static double[] getLatLangCache(String loc) throws Exception {
		Connection connection = null;
		Statement stmt = null;
		String coordOut = "";
		String sql = new String();

		MemcachedClient memcached = new MemcachedClient(new InetSocketAddress(
				"10.9.246.145", 11211));
		double[] coord = new double[2];
		try {
			// the sql server driver string
			Class.forName("com.mysql.jdbc.Driver");

			// the sql server url
			String url = "jdbc:mysql://sjc-vepts-745:3306/tweetsenti";
			if (memcached.get(loc) != null) {
				coordOut = memcached.get(loc).toString();
//				System.out.println("here");
					coord[0] = Double.parseDouble(coordOut.split(",")[0]);
					coord[1] = Double.parseDouble(coordOut.split(",")[1]);
			} else {
			// get the sql server database connection
			connection = DriverManager.getConnection(url, "root", "");

			stmt = connection.createStatement();

			System.out.println(sql);
			String getSql = "SELECT coord FROM coordinate_cache WHERE location='"
					+ loc + "'";
			ResultSet rs = stmt.executeQuery(getSql);
			// STEP 5: Extract data from result set
//		/	System.out.println(memcached.get(""));
		
				if (rs.next()) {
					// entry found in db cache
					coordOut = rs.getString("coord");
					coord[0] = Double.parseDouble(coordOut.split(",")[0]);
					coord[1] = Double.parseDouble(coordOut.split(",")[1]);
					System.out.println(coordOut);
					
					if (rs.getString(1).equals("")) {
						memcached.set("empty", 0, coordOut);
					} else if (loc.length() > 249) {

					} else {
					
						memcached.set(loc, 0, coordOut);
					}
					rs.close();
				} else {
					// coord=getLatLng(loc);
					coord[1] = new Double(36.77873);
					coord[0] = new Double(-119.41797);
					String coStr = Double.toString(coord[0]) + ","
							+ Double.toString(coord[1]);
					System.out.println(coStr);
					sql = "INSERT INTO coordinate_cache " + "VALUES ('" + loc
							+ "','" + coStr + "')";
					stmt.executeUpdate(sql);

					if (rs.getString(1).equals("")) {
						memcached.set("empty", 0, coStr);
					} else if (loc.length() > 249) {

					} else {
						memcached.set(loc, 0, coStr);
					}

					// entry made in db
				}
				
			}
			memcached.shutdown();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			memcached.shutdown();
		} catch (SQLException e) {
			e.printStackTrace();
			memcached.shutdown();
		} finally {
			try {
				if (stmt != null)
					connection.close();
				memcached.shutdown();
			} catch (SQLException se) {
			}// do nothing
			try {
				if (connection != null)
					connection.close();
				memcached.shutdown();
			} catch (SQLException se) {
				se.printStackTrace();
				memcached.shutdown();
			}
		}
		return coord;
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
							
			   is1 = TweetUtil.class.getClass().getResourceAsStream("sentiValues.properties");
			   if(is1 == null)
				try {
					// Because of difference in behavior across Tomcat versions. Need to find flavour of loading resource that works 
					       //across tomcat versions
					is1 = new FileInputStream(new File("C:\\sentiValues.properties"));
					System.out.println("=====Read value from c::");
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
					is1 = new FileInputStream(new File("/home/msortur/tmp/sentiValues.properties"));
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
