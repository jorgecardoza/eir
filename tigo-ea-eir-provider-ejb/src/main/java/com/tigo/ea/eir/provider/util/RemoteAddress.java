package com.tigo.ea.eir.provider.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class RemoteAddress {

	@SuppressWarnings("rawtypes")
	public static String getEndUserLocation() {
		String endUserLocation = "unknownHost";
		SOAPMessageContext context = AppContextUtil.getContext();
		//Si es un contexto web de donde proviene la invocación
		if (context != null) {			
			//Método new y mejorado
			Map map = (Map) context.get(MessageContext.HTTP_REQUEST_HEADERS);
			ArrayList l = (ArrayList) (map!=null ? map.get("X-Forwarded-For") : null);
			if(l!=null && l.size()>0){ 
					endUserLocation = (String) l.get(0);
			}
		}else{ //Si es un contexto local desde el mismo WLS
			try {
				endUserLocation = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
		}
		return endUserLocation;
	}
	
}
