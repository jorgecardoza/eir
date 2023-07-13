package com.tigo.ea.eir.provider.util;

import java.io.ByteArrayOutputStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class AndaSoapHandler implements SOAPHandler<SOAPMessageContext> {

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		
		Boolean isOutbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (!isOutbound) {
			//Mensajes entrantes ...
			System.setProperty("anda.response", printSOAPMessage(context));

		} else {
			
			//Mensajes salientes ...
			System.setProperty("anda.request", printSOAPMessage(context));

		} 
		System.out.println(printSOAPMessage(context));
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		System.out.println("Server::WebService::handleFault()");
		return true;
	}

	@Override
	public void close(MessageContext context) {
		System.out.println("Server::WebService::close()");		
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	private String printSOAPMessage(SOAPMessageContext messageContext) {
		try {
			SOAPMessage msg = messageContext.getMessage();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			msg.writeTo(out);
			String strMsg = new String(out.toByteArray());
			return strMsg;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}	
}
