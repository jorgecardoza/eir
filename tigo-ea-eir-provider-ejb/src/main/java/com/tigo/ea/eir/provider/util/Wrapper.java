package com.tigo.ea.eir.provider.util;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

public class Wrapper<T> {
	
	private Object clazz;

	public Wrapper(Object clazz) {
		this.clazz = clazz;
	}

	@XmlAnyElement(lax = true)
	public Object getClazz() {
		return clazz;
	}
		
}
