package com.tigo.ea.eir.provider.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

@SuppressWarnings("rawtypes")
public class DefaultHandlerResolver implements HandlerResolver {

	private List<Handler> handlerList; 
	
	public void setHandlerList(List<Handler> handlerList) {
		this.handlerList = handlerList;
	}

	@Override
	public List<Handler> getHandlerChain(PortInfo portInfo) {		
		return handlerList;
	}

	public List<Handler> getHandlerList() {
		if (handlerList==null){
			handlerList=new ArrayList<Handler>();
		}
		return handlerList;
	}

}
