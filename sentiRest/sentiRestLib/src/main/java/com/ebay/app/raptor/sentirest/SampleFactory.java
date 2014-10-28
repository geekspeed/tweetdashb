package com.ebay.app.raptor.sentirest;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.ebay.raptor.kernel.context.IRaptorContext;


/**
 * SampleFactory class created to demonstrate use of @Inject IRaptorContext and 
 * a provider of bean. For more information, visit: 
 * http://wiki2.arch.ebay.com/display/RAPTORDOCS/Dependency+Injection
 * @author manand
 *
 */

@Configuration
public class SampleFactory {

	@Inject IRaptorContext raptorContext;
		
	public SampleFactory() {		
	}
	
	/**
	 * creates a sample bean
	 */
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
	public IBean getSampleBean() {		
		if(raptorContext != null){
			System.out.println("raptorContext inject works!");
		}
		return new SampleBean();
	}

}
