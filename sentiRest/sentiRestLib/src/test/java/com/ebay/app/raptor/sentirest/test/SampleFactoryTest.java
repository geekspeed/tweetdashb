package com.ebay.app.raptor.sentirest.test;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.ebay.raptor.config.RaptorContextConfig;
import com.ebay.raptor.kernel.context.IRaptorContext;
import com.ebay.raptor.kernel.context.RaptorContextFactory;
import com.ebay.raptor.kernel.util.MockRaptorContext;
import com.ebay.app.raptor.sentirest.IBean;
import com.ebay.app.raptor.sentirest.SampleFactory;
import com.ebay.app.raptor.sentirest.util.WebContextTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RaptorContextConfig.class, SampleFactory.class }, loader = AnnotationConfigContextLoader.class)
@TestExecutionListeners({ 
    WebContextTestExecutionListener.class, 
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class })
public class SampleFactoryTest {
      
      @Autowired
      IBean sampleBean;

      @Before
      public void setUp(){
      	IRaptorContext raptorContext  = new MockRaptorContext();
    	RaptorContextFactory.setCtx(raptorContext);     
      }
      
      @Test
      public void testSampleFactory() throws Exception {
    	  assertNotNull(sampleBean);
          System.out.println("sample bean " + sampleBean);
          System.out.println("sample bean bool " + sampleBean.getBool());
      }
      
      @After
      public void reset(){
    	  RaptorContextFactory.reset();
      }
}
