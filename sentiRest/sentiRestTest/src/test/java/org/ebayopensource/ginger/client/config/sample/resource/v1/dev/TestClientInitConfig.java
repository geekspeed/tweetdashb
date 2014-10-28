package org.ebayopensource.ginger.client.config.sample.resource.v1.dev;

import org.ebayopensource.ginger.client.config.DefaultInitGingerClientConfig;

/**
 * Ginger client init configuration for <code>dev</code> environment.
 *
 */
public class TestClientInitConfig extends DefaultInitGingerClientConfig {

	/**
	 * The end-point for the server - all targets will be built on a URL prefixed by this.
	 * 
	 * @see org.ebayopensource.ginger.client.config.InitGingerClientConfig#getEndPoint()
	 */
	@Override
	public String getEndPoint() {
		return "http://localhost:8080/sentirest/sampleResource/v1";
	}

	/**
	 * Maximum number of milliseconds allowed to establish a connection with the server.
	 */
	@Override
	public int getConnectTimeout() {
		return 100;
	}

	/**
	 * Maximum number of milliseconds allowed to perform a READ from the server.
	 */
	@Override
	public int getReadTimeout() {
		return 2000;
	}

	/**
	 * Number of threads in the pool - there can be only this many standing concurrent invocations
	 * through this client.
	 * 
	 * @return 1 - for this sample code, we only make one invocation at a time. 
	 */
	@Override
	public int getThreadPoolSize() {
		return 1;
	}
	
}
