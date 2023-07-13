package com.tigo.ea.eir.provider.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;

public class WrapperList<T> {

	private List<T> items;

	public WrapperList() {
		items = new ArrayList<T>();
	}

	public WrapperList(List<T> items) {
		this.items = items;
	}

	@XmlAnyElement(lax = true)
	public List<T> getItems() {
		return items;
	}

}
