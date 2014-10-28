package com.ebay.app.raptor.sentirest.resources.util;

public class TweetStoreFactory {

	public static TweetStore getTweetStore(TweetStoreType type) throws Exception {
	switch(type) {	
	case MONGO: return  MongoTweetStore.getInstance();
	 default: return MongoTweetStore.getInstance();
	}
		
	}
	

}
