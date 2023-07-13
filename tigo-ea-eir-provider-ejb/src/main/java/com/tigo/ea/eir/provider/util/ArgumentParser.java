package com.tigo.ea.eir.provider.util;

import java.util.ArrayList;
import java.util.List;

public class ArgumentParser {

	public static List<Object> toList(Object ... args) {
		List<Object> arguments = new ArrayList<Object>();
		for (Object object : args) {
			arguments.add(object);
		}
		return arguments;
	} 
	
}
