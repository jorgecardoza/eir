package com.tigo.ea.eir.provider.logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tigo.ea.eir.provider.util.EirUtil;
import com.tigo.ea.util.TransactionIdUtil;
import com.tigo.ea.util.log.EventLevel;

@Component
public class Loggers {
	@Autowired
	private EirUtil util;

	private String methodName;
	private String category = "provider";
	private Class<?> type;
	private String endUser;
	
	public void info(String message, String detail,String refId) {
		util.logging(methodName, "Before " + message, detail, category, EventLevel.INFO, type, null, null, null, null,
				endUser, refId);
	}
	
	public void before(String message, String detail,String refId) {
		util.logging(methodName, "Before " + message, detail, category, EventLevel.INFO, type, null, null, null, null,
				endUser, refId);
	}
	
	public void after(String message, String detail, Long duration,String refId) {
		util.logging(methodName, "After " + message, detail, category, EventLevel.INFO, type, null, null, getDuration(duration), true,
				endUser, refId);
	}
	
	public void before(String methodName,String message, String detail,String refId) {
		util.logging(methodName, "Before " + message, detail, category, EventLevel.INFO, type, null, null, null, null,
				endUser, refId);
	}
	
	public void after(String methodName,String message, String detail, Long duration,String refId) {
		util.logging(methodName, "After " + message, detail, category, EventLevel.INFO, type, null, null, getDuration(duration), true,
				endUser, refId);
	}
	
	public void error(String message, Throwable exception) {
		util.loggingError(methodName, message, exception.getMessage(), category, EventLevel.ERROR, type, exception, endUser);
	}

	public void error(String message, String detail, Throwable exception) {
		util.loggingError(methodName, message, detail, category, EventLevel.ERROR, type, exception, endUser);
	}

	public void error(String message, String detail, Throwable exception, String refId) {
		util.loggingError(methodName, message, detail, category, EventLevel.ERROR, type, exception, endUser, refId);
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getMethodName() {
		return methodName;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public String getEndUser() {
		return endUser;
	}

	public void setEndUser(String endUser) {
		this.endUser = endUser;
	}

	public String getOriginReferenceId() {
		return TransactionIdUtil.getId();
	}
	
	public Long getDuration(Long start) {
		return start != null ? new Long(System.currentTimeMillis() - start) : null;
	}
	
	
}
