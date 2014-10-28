package com.ebay.app.raptor.sentirest.resources;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.ebay.app.raptor.sentirest.resources.util.MongoTweetStore;
import com.ebay.app.raptor.sentirest.resources.util.SamplePojo;
import com.ebay.app.raptor.sentirest.resources.util.Tweet;
import com.ebay.app.raptor.sentirest.resources.util.TweetStore;
import com.ebay.app.raptor.sentirest.resources.util.TweetStoreFactory;
import com.ebay.app.raptor.sentirest.resources.util.TweetStoreType;
import com.ebay.app.raptor.sentirest.resources.util.TweetUtil;
import com.ebay.raptor.kernel.context.IRaptorContext;

@Component
@Scope("singleton")
@Path("/")
public class SampleResource {
	
	// Raptor/Spring cross-bundle injection
	@Inject IRaptorContext ctx;
	
	// JAX-RS context injection
	@Context UriInfo info;
	@Context HttpHeaders headers;
	@Context Request request;
	@Context SecurityContext secCtx;
	@Context Providers providers;
	
	// Servlet resource injection as defined by JAX-RS
	@Context ServletConfig servletConfig;
	@Context ServletContext servletContext;
	@Context HttpServletRequest servletRequest;
	@Context HttpServletResponse servletResponse;
	//@Context QueryParam queryParams;

	List<Tweet> tweetList = new ArrayList<Tweet>();
	Map<String,Tweet> ebayTweetMap = new ConcurrentHashMap<String,Tweet>();

	@GET
	@Path("/getVersion")
	public String getVer() {

		String version = this.servletConfig.getInitParameter("ServiceVersion");

		if (version == null) {
			version = "1.0.0";
		}
		return version;
	}
    @GET
    @Path("/hello")
    public String sayHello() { 	
        return "Hello, World!";
    }
    
    @GET
    @Path("/environment")
    public String showEnv() {
    	return "Environment: " + ctx.getDeploymentEnv().getPoolType().toString();    	
    }
    
    @GET
    @Path("/uriinfo/{x}")
    public String showUriInfo(@PathParam("x") String x) {
    	StringBuilder b = new StringBuilder();
    	b.append("URI Info: ").append(info.getRequestUri()).append("<br/>");
    	b.append("Requested resource: ").append(x);
    	return b.toString();
    }
    
    @GET
    @Path("/headers")
    public String showHeaders() {
    	MultivaluedMap<String, String> headerEntries = headers.getRequestHeaders();
    	StringBuilder b = new StringBuilder();
    	for (Map.Entry<String, List<String>> entry : headerEntries.entrySet()) {
    		String key = entry.getKey();
    		for (String value : entry.getValue()) {
    			b.append(key).append(':').append(value).append("<br/>");
    		}
    	}
    	return b.toString();   	
    }
    
    @GET
    @Path("/security")
    public String showSecurityContext() {
    	StringBuilder b = new StringBuilder();
    	b.append("Secure: ").append(secCtx.isSecure()).append("<br/>");
    	b.append("Authentication scheme: ").append(secCtx.getAuthenticationScheme()).append("<br/>");
    	b.append("User: ").append(secCtx.getUserPrincipal()).append("<br/>");    	
    	return b.toString();
    }
    
    @GET
    @Path("/servletconfig")
    public String showServletConfig() {
    	return "Servlet name: " + servletConfig.getServletName();
    }
    
    @GET
    @Path("/servletcontext")
    public String showServletContext() {
    	StringBuilder b = new StringBuilder();
    	b.append("Server info: ").append(servletContext.getServerInfo()).append("<br/>");
    	b.append("Major version: ").append(servletContext.getEffectiveMajorVersion()).append("<br/>");
    	b.append("Minor version: ").append(servletContext.getEffectiveMinorVersion()).append("<br/>");
    	return b.toString();
    }
    
    @GET
    @Path("/servletrequest")
    public String showServletRequest() {
    	StringBuilder b = new StringBuilder();
    	b.append("Request URI: ").append(servletRequest.getRequestURI()).append("<br/>");
    	b.append("Method: ").append(servletRequest.getMethod()).append("<br/>");
    	b.append("Protocol: ").append(servletRequest.getProtocol()).append("<br/>");
    	b.append("Path Info: ").append(servletRequest.getPathInfo()).append("<br/>");
    	return b.toString();
    }
    
    @POST
	@Path("/jsonXml")
	@Consumes({"application/json", "application/xml"})
	@Produces({"application/json", "application/xml"})
	public SamplePojo getJsonXml(SamplePojo pojo) {
		return pojo;
	}
	
	
    @GET
  	@Path("/ebaystream/{start}/{end}")
  	@Produces({"application/json", "application/xml"})
  //	public List<Tweet> getEbayStream(@QueryParam("start") String startTime, @QueryParam("end") String endTime) throws Exception {
	public List<Tweet> getEbayStream(@PathParam("start") String startTime, @PathParam("end") String endTime) throws Exception {
    	try {
			TweetStore store = TweetStoreFactory
						.getTweetStore(TweetStoreType.MONGO);
			Date startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").parse(startTime);
			Date endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").parse(endTime);
			return store.getTweets(startDate, endDate);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			throw e;
			
		}
    	
  	}
    
    @GET
  	@Path("/stream")
  
  	@Produces({"application/json", "application/xml"})
  	public List<Tweet> getStream() {
  	 	return new ArrayList<Tweet>(ebayTweetMap.values());
  	}
    
    @GET
  	@Path("/initStream")
  	public  String initStream() {
    initTwitterStream();
    return "Init done";
    }
    
    @GET
  	@Path("/searchTweet/{keyword}/{start}/{end}")
  	@Produces({"application/json", "application/xml"})
  	public  List<Tweet> searchTwitter(@PathParam("keyword") String keyword , @PathParam("start") String since, @PathParam("end") String until) {
    return TweetUtil.searchTwitter(keyword, since, until);
    
    }
    
    private void initTwitterStream() {
    
    	ConfigurationBuilder cb = new ConfigurationBuilder();
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
            	
            	  private void storeTweetsInMongo(Map<String, Tweet> ebayTweetMap) {
      				TweetStore store = null;
      				try {
      					store = TweetStoreFactory
      							.getTweetStore(TweetStoreType.MONGO);

      					List<HashMap<String, String>> tl = new ArrayList<HashMap<String, String>>();
      					for (Tweet tw : ebayTweetMap.values()) {
      						store.insertTweet(tw.toMap());
      					}
      				} catch (Exception e) {
      					
      					e.printStackTrace(System.out);
      				}
            	  }
            	 
                @Override
                public void onStatus(Status status) {
                 
                	/*if(isEbayRelated(status.getText())) {
                		System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());*/
                		if(ebayTweetMap.size() <= 30)
							try {
								ebayTweetMap.put(new Long(status.getId()).toString(),TweetUtil.getTweet(status));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace(System.out);
							}
						else {

                			//Store the data in Mongo ..
                			storeTweetsInMongo(ebayTweetMap);
                			//Possibility of loss of some tweets..ok for now ..Needs to be fixed
                			ebayTweetMap.clear();
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
            
            FilterQuery fq = new FilterQuery();
            
            String keywords[] = {"ebay" , "eBay" ,"EBAY" , "paypal" , "PAYPAL" , "PayPal" };

            fq.track(keywords);

            twitterStream.addListener(listener);
            twitterStream.filter(fq);  

          //  twitterStream.addListener(listener);
            //twitterStream.sample();
        }
    }
    
    
    

