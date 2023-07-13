package com.tigo.ea.eir.provider.util;

import javax.xml.ws.handler.soap.SOAPMessageContext;

public class AppContextUtil {

	private static ThreadLocal<SOAPMessageContext> threadLocal = new ThreadLocal<SOAPMessageContext>();

	public static void setContext(SOAPMessageContext context) {
		threadLocal.set(context);
	}
	
	public static SOAPMessageContext getContext() {
		return threadLocal.get();
	}
	
}
