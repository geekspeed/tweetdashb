package com.ebay.app.raptor.sentirest.resources.util;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SamplePojo {

	private String name = "John";
	private int age = 21;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
	
}
