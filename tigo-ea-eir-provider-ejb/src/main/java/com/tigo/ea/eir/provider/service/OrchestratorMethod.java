package com.tigo.ea.eir.provider.service;

import java.io.Serializable;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tigo.ea.eir.provider.logger.Loggers;
import com.tigo.ea.eir.provider.util.EirUtil;
import com.tigo.ea.esb.provider.core.ProviderRequest;
import com.tigo.ea.esb.provider.core.ProviderResponse;
import com.tigo.ea.util.dto.GenericDto;

@Component
public class OrchestratorMethod implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String LOG_METHOD_NAME = "OrchestratorMethod";

	@Autowired
	private EirUtil eirUtil;

	@Autowired
	private Loggers loggerMethod;

	@Autowired
	private GetRoutingValidationEirService getRoutingValidationEirService;

	@Autowired
	private QueryImeiListService queryImeiListService;

	@Autowired
	private AddImeiListService addImeiListService;

	@Autowired
	private RemoveImeiListService removeImeiListService;

	public ProviderResponse setService(String service, GenericDto request) throws JsonProcessingException {
		Date date = new Date();
		date.setTime(System.currentTimeMillis());

		ProviderRequest providerRequest = new ProviderRequest();
		providerRequest.setApplicationId("eir-provider");
		providerRequest.setApplicationRefId(loggerMethod.getOriginReferenceId());
		providerRequest.setDataType("json");
		providerRequest.setData(eirUtil.json2String(request));
		providerRequest.setEndUser(loggerMethod.getEndUser());
		providerRequest.setRequestDate(date);

		return setService(service, providerRequest);
	}

	public ProviderResponse setService(String service, ProviderRequest providerRequest) throws JsonProcessingException {
		Long start = System.currentTimeMillis();
		ProviderResponse providerResponse = new ProviderResponse();

		loggerMethod.before(LOG_METHOD_NAME, service, eirUtil.json2String(providerRequest),
				loggerMethod.getOriginReferenceId());

		switch (service) {
		case "getRoutingValidationEirService":
			getRoutingValidationEirService.execute(providerRequest, providerResponse);
			break;
		case "queryImeiListService":
			queryImeiListService.execute(providerRequest, providerResponse);
			break;

		case "addImeiListService":
			addImeiListService.execute(providerRequest, providerResponse);
			break;

		case "removeImeiListService":
			removeImeiListService.execute(providerRequest, providerResponse);
			break;

		// activateMTAService.execute(providerRequest, providerResponse);
		// break;

		default:
			String data = "{\"idError\":\"-1\", \"errorStr\":\"Service not found into OrchestratorMethod configuration\"}";
			providerResponse.setReturnCode(-1);
			providerResponse.setServiceCode(-1);
			providerResponse.setServiceDescription("Service not found into OrchestratorMethod configuration");
			providerResponse.setData(data);
			break;
		}
		loggerMethod.after(LOG_METHOD_NAME, service, eirUtil.json2String(providerResponse), start,
				loggerMethod.getOriginReferenceId());

		return providerResponse;
	}
}