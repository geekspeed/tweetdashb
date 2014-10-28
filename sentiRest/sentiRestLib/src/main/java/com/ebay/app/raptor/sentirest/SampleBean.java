package com.ebay.app.raptor.sentirest;

public class SampleBean implements IBean {

	private boolean testBool;
	
	public SampleBean(){
		testBool = true;
	}
	public boolean getBool(){
		return testBool;
	}
}
