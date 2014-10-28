package org.elasticsearch.river.twitter;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Utility class for Elastic search usecase where the insights are generated for a given tweet.
 * @author vjambunathan
 *
 */
public class InsightUtil {
	
		//Keywords that NOC will be interested in
		private static List<String> keywords ;
		//http://10.9.200.207:8080/sentirest/sampleResource/v1/ebaystream/2014-7-31T00-00-00/2014-7-31T23-59-59/down 	
		// Phrases that will make sense for NOC
		private static List<String> downPhrases;
		
		private static List<String> slowPhrases;
		
		private static final int SLOWCOUNT = 10;
		
		private static final int DOWNCOUNT = 1;
		
		private static final String SITE_NOT_WORKING = " eBay site is not working ";
		private static final String SITE_NOT_WORKING_REGION = "eBay site of the following region is not working:";
		
		private static final String FLOW_NOT_WORKING = " flow of eBay site is not working ";
		private static final String NW_DETAILS = " Check the failures and CAL logs in the specified time lines and Alert the owning PD team immediately ";
		private static final String FLOW_SLOW_REGION = " flow for eBay site for the following region is slow. ";
		private static final String FLOW_SLOW = " flow for eBay site is slow ";
		private static final String FLOW_SW_DETAILS = " Check the CAL logs for the specified time lines.  Alert the PD team ";
		
		private static final String SITE_SLOW_REGION = " eBay site for the following region is slow. ";
		private static final String SITE_SLOW = " eBay site is slow. ";
		
		private static final String FLOW_NOT_WORKING_REGION = "flow of eBay site of the following region is not working";
		
		
		
		
		private static final String SITE_US = "US";
		private static final String SITE_UK = "UK";
		private static final String SITE_GERMANY = "DE";
		private static final String SITE_ITALY = "ITALY";
		private static final String SITE_INDIA = "INDIA";
		private static final String SITE_RUSSIA = "RUSSIA";
		private static final String SITE_AUSTRALIA = "AUSTRALIA";
		private static final String UNITED_STATES = "UNITED STATES";
		private static final String INDIA = "INDIA";
		private static final String UNITED_KINGDOM = "UNITED KINGDOM";
		private static final String GERMANY = "GERMANY";
		private static final String DEUTSCHELAND = "DEUTSCHELAND" ;
		private static final String EBAY_US = "EBAY US";
		//private static final CharSequence ebay_us = "eBay us";
		private static final String EBAY_UK = "EBAY.UK";
		private static final String EBAY_IN =  "EBAY.IN";
		private static final String EBAY_DE = "EBAY.DE";
		private static final String EBAY_ITALY = " EBAY.ITALY";
		private static final String EBAY_RUSSIA = "EBAY.RUSSIA";
		private static final String EBAY_AUSTRALIA = "EBAY.AUSTRALIA";
		
		private static final CharSequence SELL = "sell";
		private static final CharSequence BUY = "buy";
		private static final CharSequence CHECKOUT = "checkout";
		private static final CharSequence PAYMENT = "payment";
		private static final CharSequence SEARCH = "search";
		private static final String FLOW_SEARCH = "SEARCH";
		private static final CharSequence LOGIN = "login";
		private static final String FLOW_LOGIN = "LOGIN";
		private static final String FLOW_PAYMENT = "PAYMENT";
		private static final String FLOW_CHECKOUT = "CHECKOUT";
		private static final String FLOW_BUY = "BUYING";
		private static final String FLOW_SELL = "SELLING";
		private static final String ITALY = "ITALY";
		private static final String RUSSIA = "RUSSIA";
		private static final String AUSTRALIA = "AUSTRALIA";
		
		
		
		
		
		static {
			keywords = new ArrayList<String>();
			downPhrases = new ArrayList<String>();
			slowPhrases = new ArrayList<String>();
			
			keywords.add("down");
			keywords.add("slow");
			keywords.add("hack");
			
			downPhrases.add("Ebay is down");
			downPhrases.add("Ebay down");
			downPhrases.add("is Ebay down");
			downPhrases.add("is eBay down");
			downPhrases.add("eBay is down");
			downPhrases.add("#ebaydown");
			downPhrases.add("is down");
			
			slowPhrases.add("ebay is slow");
			slowPhrases.add("eBay is slow");
			slowPhrases.add("eBay slow");
			slowPhrases.add("ebay slow");
			slowPhrases.add("#ebayslow");
			slowPhrases.add("is slow");
		}

		
		
		public static List<String> getInsights(String tw) {
			List<String> combinedList = new ArrayList<String>(); 
			try{
			    String insight = "";
			    List<String> slowList = new ArrayList<String>();
			    List<String> downList = new ArrayList<String>();
			    
			   	for(String phrase : slowPhrases) {
					 if(tw.contains(phrase)) {
					   slowList =  buildInsightForSlowness(tw);
					    break;
					 }
			   	}
				combinedList.addAll(slowList);
			   	
			   	for(String phrase : downPhrases)
			   	 if(tw.contains(phrase)) {
			   		 downList = buildInsightForDown(tw);
			   		 break;
			   	 }
			   			   
			   	combinedList.addAll(downList);
			   	
			   	return combinedList;
			   		
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace(System.out);
				return combinedList;
			} 
	         
		}

   private static String getFlow(String text) {
		
		String flow = "GENERIC";
			
		if ( text!=null && !text.isEmpty()){
			if(text.toLowerCase().contains(SELL))
				flow = FLOW_SELL;
			else if (text.toLowerCase().contains(BUY))
				flow = FLOW_BUY;
			else if (text.toLowerCase().contains(CHECKOUT))
				flow = FLOW_CHECKOUT;
			else if (text.toLowerCase().contains(PAYMENT))
				flow = FLOW_PAYMENT;
			else if (text.toLowerCase().contains(SEARCH))
				flow = FLOW_SEARCH;
			else if (text.toLowerCase().contains(LOGIN))
				flow = FLOW_LOGIN;
			
			}
		
		return flow;
	}
	

   private static String getSite(String text) {
			
		String site = getSiteFromText(text);
		if (!site.isEmpty())
			return site;
		site = "GENERIC";
			
		return site;
		
	}

   
   private static String getSiteFromText(String text) {
		String site = "";
		if (text.toUpperCase().contains(EBAY_US) || text.toUpperCase().contains(UNITED_STATES) || text.toUpperCase().contains("US"))
			site = SITE_US;
		else if (text.toUpperCase().contains(EBAY_UK) || text.toUpperCase().contains(UNITED_KINGDOM) || text.toUpperCase().contains("UK"))
			site = SITE_UK;
		else if (text.toUpperCase().contains(EBAY_IN) || text.toUpperCase().contains(INDIA))
		    site = SITE_INDIA;
		else if (text.toUpperCase().contains(EBAY_DE) || text.toUpperCase().contains(DEUTSCHELAND))
		    site = SITE_GERMANY    	;
		else if (text.contains(EBAY_ITALY) || text.toUpperCase().contains(ITALY))
			site = SITE_ITALY;
		else if (text.contains(EBAY_RUSSIA) || text.contains(RUSSIA))
				site = SITE_RUSSIA;
		else if (text.contains(EBAY_AUSTRALIA) || text.contains(AUSTRALIA))
				site = SITE_AUSTRALIA;
		
		return site;
			
	}

   
   
		


		private static List<String> buildInsightForDown(String tweet) {
					
			
			String flow = getFlow(tweet);
			String site = getSite(tweet);
			String aggrString = "down" + " " + flow + " " + site;
			
			ArrayList<String> al = new ArrayList<String>();
			al.add(aggrString);
			
			return al;
			
		}


		


		private static List<String> buildInsightForSlowness(String tw) {
		
			
			String flow = getFlow(tw);
			String site = getSite(tw);
			String aggrString = "slow" + " " + flow + " " + site;
			
			ArrayList<String> al = new ArrayList<String>();
			al.add(aggrString);
			
			return al;
		}


		
		public static void main(String [] args) {

			  List<String> insights = InsightUtil.getInsights("is ebay slow? UK ?");
			  if(insights.size()!=0){
			   for(String s: insights) {
				   System.out.println(s);
				   System.out.println(s.split(" ")[0]);
			   System.out.println(s.split(" ")[1]);
			   System.out.println(s.split(" ")[2]);
			   }
			  }
		}
		
		
	}
	

