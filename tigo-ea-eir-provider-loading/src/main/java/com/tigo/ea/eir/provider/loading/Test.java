package com.tigo.ea.eir.provider.loading;

import com.tigo.ea.util.dto.GenericDto;

public class Test {
	public static void main(String[] args) {
		GenericDto prueba = new GenericDto();
		prueba.put("codigo", "jorge");
		prueba.put("ciudad", null);
		System.out.println("-"+stringValue(prueba.get("nada"))+"-");
	}
	public static String stringValue(Object object) {
		try {
			return (object != null) ? object.toString() : "";
		} catch (Exception e) {
			return "";
		}
	}

}
