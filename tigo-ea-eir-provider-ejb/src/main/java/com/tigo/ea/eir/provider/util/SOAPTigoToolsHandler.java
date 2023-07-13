package com.tigo.ea.eir.provider.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class SOAPTigoToolsHandler implements SOAPHandler<SOAPMessageContext> {

    private String authorization;

    public SOAPTigoToolsHandler(String authorization) {
     this.authorization =authorization;
    }

    public boolean handleMessage(SOAPMessageContext context) {

	final Boolean outInd = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

	if (outInd.booleanValue()) {
	    try {

		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		headers.put("Content-Type", Collections.singletonList("application/xml"));
		headers.put("authorization", Collections.singletonList(getAuthorization()));
		context.put(MessageContext.HTTP_REQUEST_HEADERS, headers);

	    } catch (final Exception e) {
		return false;
	    }
	}

	return true;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Set getHeaders() {
	return null;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {

	return true;
    }

    public void close(MessageContext context) {

    }

    public String getAuthorization() {
	return authorization;
    }

    public void setAuthorization(String authorization) {
	this.authorization = authorization;
    }
}