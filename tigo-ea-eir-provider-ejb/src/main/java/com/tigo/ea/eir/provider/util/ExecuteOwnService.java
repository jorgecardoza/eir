package com.tigo.ea.eir.provider.util;

import java.io.IOException;
import java.io.Serializable;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigo.ea.esb.provider.core.ProviderContext;
import com.tigo.ea.esb.provider.core.ProviderException;
import com.tigo.ea.esb.provider.core.ProviderRequest;
import com.tigo.ea.esb.provider.core.ProviderResponse;
import com.tigo.ea.esb.provider.core.ServiceFactory;
import com.tigo.ea.esb.provider.service.Service;
import com.tigo.ea.esb.provider.service.ServiceException;
import com.tigo.ea.util.configuration.ConfigurationServiceFacade;
import com.tigo.ea.util.log.EventData;
import com.tigo.ea.util.log.EventData.Builder;
import com.tigo.ea.util.log.EventLevel;
import com.tigo.ea.util.log.LoggingServiceFacade;

@Component
public class ExecuteOwnService implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private ServiceFactory serviceFactory;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ConfigurationServiceFacade configurationServiceFacade;

	@Autowired
	private LoggingServiceFacade loggingServiceFacade;

	public <T> T executeService(ProviderRequest originalProviderRequest, ProviderResponse originalProviderResponse,
			String skippedServiceId, String serviceIdToExecute, Object requestData, Class<T> responseType,
			String endUser, String referenceId) throws ServiceException, ProviderException, Exception {
		T responseData = null;

		ProviderResponse res = executeService(originalProviderRequest, originalProviderResponse, skippedServiceId,
				serviceIdToExecute, requestData, endUser, referenceId);

		if (res.getData() != null && !"".equals(res.getData()))
			responseData = (T) convertToObject(res.getData(), responseType);

		return responseData;
	}

	public ProviderResponse executeService(ProviderRequest originalProviderRequest,
			ProviderResponse originalProviderResponse, String skippedServiceId, String serviceIdToExecute,
			Object requestData, String endUser, String referenceId)
			throws ServiceException, ProviderException, Exception {
		ProviderResponse res = new ProviderResponse();

		if (!skippedServiceId.equals(serviceIdToExecute)) {
			Service service = serviceFactory.getService(serviceIdToExecute);

			ProviderRequest req = new ProviderRequest();

			BeanUtils.copyProperties(originalProviderRequest, req);
			BeanUtils.copyProperties(originalProviderResponse, res);

			req.setServiceId(serviceIdToExecute);
			req.setEndUser(endUser);
			req.setDataType("json");
			req.setData(convertToJson(requestData));

			res.setServiceId(serviceIdToExecute);

			logging(serviceIdToExecute, "executeService",
					String.format("Trying to execute the service: [%s], providerRequest = [%s]", serviceIdToExecute,
							req),
					"provider", EventLevel.INFO, service.getClass(), null, Integer.valueOf(0), Long.valueOf(0),
					Boolean.TRUE, endUser, referenceId);

			long start = System.currentTimeMillis();

			service.execute(req, res);

			long end = System.currentTimeMillis() - start;

			res.setProcessTime(Long.valueOf(end));

			logging(serviceIdToExecute, "executeService",
					String.format("Response from the service: [%s], providerResponse = [%s]", serviceIdToExecute, res),
					"provider", EventLevel.INFO, service.getClass(), null, res.getServiceCode(), end, Boolean.TRUE,
					endUser, referenceId);
		}

		return res;
	}

	/* Util */
	public String convertToJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(String.format("Can not convert to JSON: %s", object), e);
		}
	}

	public <T> T convertToObject(String json, Class<T> type) {
		T object = null;
		try {
			if ((json != null) && (!json.isEmpty()))
				object = objectMapper.readValue(json, type);
		} catch (IOException e) {
			throw new RuntimeException(String.format("Can not convert to Object: %s", json), e);
		}
		return object;
	}

	private <T> void logging(String methodName, String message, String detail, String category, EventLevel level,
			Class<T> type, Throwable exception, Integer responseCode, Long duration, Boolean successful, String endUser,
			String referenceId) {
		String originReferenceId = ProviderContext.getServiceContext() != null
				? ProviderContext.getServiceContext().getRequest().getApplicationRefId()
				: "";

		if (level.getValue() >= configurationServiceFacade.getEventLevel()) {
			Builder builder = EventData.builder();
			builder.category(category);
			builder.level(level);
			builder.source(type);
			builder.name(methodName);
			builder.message(message);

			if (exception != null)
				builder.exception(exception);

			builder.detail(detail != null ? detail : null);
			builder.responseCode(responseCode != null ? String.valueOf(responseCode.intValue()) : null);
			builder.duration(duration != null ? duration : null);
			builder.originReferenceId(originReferenceId);
			builder.referenceId(referenceId);
			builder.automatic(true);
			builder.successful(successful);
			builder.endUser(endUser);
			loggingServiceFacade.log(builder.build());
		}
	}
}
