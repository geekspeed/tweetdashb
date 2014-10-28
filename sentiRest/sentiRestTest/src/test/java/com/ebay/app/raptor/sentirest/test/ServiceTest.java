package com.ebay.app.raptor.sentirest.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.Entity;

import org.ebayopensource.ginger.client.GingerClient;
import org.ebayopensource.ginger.client.GingerClientResponse;
import org.ebayopensource.ginger.client.GingerWebTarget;
import org.ebayopensource.ginger.client.internal.GingerClientManager;
import org.junit.Test;

import com.ebay.app.raptor.sentirest.test.util.SamplePojo;

/**
 * Tests for the sample service using Ginger client.
 * 
 * For more comprehensive Ginger client API sample, see
 * <a href="https://github.scm.corp.ebay.com/Ginger/Samples/tree/b1.2.0/client-api">client-api sample</a>.
 * 
 */
public class ServiceTest {

	private static final GingerClient CLIENT;

	static {
		try {
			CLIENT = GingerClientManager.get().getOrRegisterClient("TestClient", "sample.resource.v1", "dev");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	@Test
	public void testHello() throws Exception {
		GingerWebTarget webTarget = CLIENT.target("/hello");
		GingerClientResponse clientResponse = (GingerClientResponse) webTarget.request().get();
		
		assertEquals(200, clientResponse.getStatus());
		assertEquals("Hello, World!", clientResponse.getEntity(String.class));
	}

	@Test
	public void testEnv() throws Exception {
		GingerWebTarget webTarget = CLIENT.target("/environment");
		GingerClientResponse clientResponse = (GingerClientResponse) webTarget.request().get();
		
		assertEquals(200, clientResponse.getStatus());
		assertEquals("Environment: dev", clientResponse.getEntity(String.class));
	}

	@Test
	public void testUri() throws Exception {
		GingerWebTarget webTarget = CLIENT.target("/uriinfo/abc");
		GingerClientResponse clientResponse = (GingerClientResponse) webTarget.request().get();
		
		assertEquals(200, clientResponse.getStatus());
		assertTrue(clientResponse.getEntity(String.class).contains("Requested resource: abc"));
	}

	@Test
	public void testHeaders() throws Exception {
		GingerWebTarget webTarget = CLIENT.target("/headers");
		GingerClientResponse clientResponse = (GingerClientResponse) webTarget.request().get();
		
		assertEquals(200, clientResponse.getStatus());
		assertTrue(clientResponse.getEntity(String.class).contains("connection:keep-alive"));
	}

	@Test
	public void testSecurityCtx() throws Exception {
		GingerWebTarget webTarget = CLIENT.target("/security");
		GingerClientResponse clientResponse = (GingerClientResponse) webTarget.request().get();
		
		assertEquals(200, clientResponse.getStatus());
		assertTrue(clientResponse.getEntity(String.class).contains("Secure: false"));
	}

	@Test
	public void testServletCfg() throws Exception {
		GingerWebTarget webTarget = CLIENT.target("/servletconfig");
		GingerClientResponse clientResponse = (GingerClientResponse) webTarget.request().get();
		
		assertEquals(200, clientResponse.getStatus());
		assertEquals("Servlet name: " + "GingerServlet", clientResponse.getEntity(String.class));
	}

	@Test
	public void testServletCtx() throws Exception {
		GingerWebTarget webTarget = CLIENT.target("/servletcontext");
		GingerClientResponse clientResponse = (GingerClientResponse) webTarget.request().get();
		
		assertEquals(200, clientResponse.getStatus());
		assertTrue(clientResponse.getEntity(String.class).contains("Apache Tomcat"));
	}

	@Test
	public void testServletRequest() throws Exception {
		GingerWebTarget webTarget = CLIENT.target("/servletrequest");
		GingerClientResponse clientResponse = (GingerClientResponse) webTarget.request().get();
		
		assertEquals(200, clientResponse.getStatus());
		assertTrue(clientResponse.getEntity(String.class).contains("Path Info: /servletrequest"));
	}

	@Test
	public void testJson() throws Exception {
		Entity<SamplePojo> requestEntity = Entity.json(new SamplePojo());
		
		GingerWebTarget webTarget = CLIENT.target("/jsonXml");
		GingerClientResponse clientResponse = (GingerClientResponse) webTarget.request("application/json").post(requestEntity);
		SamplePojo responseEntity = clientResponse.getEntity(SamplePojo.class);
		
		assertEquals(200, clientResponse.getStatus());
		assertEquals("John", responseEntity.getName());
		assertEquals(21, responseEntity.getAge());
	}

	@Test
	public void testXml() throws Exception {
		Entity<SamplePojo> requestEntity = Entity.xml(new SamplePojo());
		
		GingerWebTarget webTarget = CLIENT.target("/jsonXml");
		GingerClientResponse clientResponse = (GingerClientResponse) webTarget.request("application/xml").post(requestEntity);
		SamplePojo responseEntity = clientResponse.getEntity(SamplePojo.class);
		
		assertEquals(200, clientResponse.getStatus());
		assertEquals("John", responseEntity.getName());
		assertEquals(21, responseEntity.getAge());
	}

	

}
