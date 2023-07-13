package com.tigo.ea.eir.provider.loading;

import java.net.URI;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.tigo.ea.eir.provider.logger.Loggers;
import com.tigo.ea.eir.provider.logic.ServiceLogic;
import com.tigo.ea.eir.provider.service.OrchestratorMethod;
import com.tigo.ea.eir.provider.util.EirUtil;
import com.tigo.ea.esb.provider.core.ProviderResponse;
import com.tigo.ea.esb.provider.service.ServiceException;
import com.tigo.ea.util.dto.GenericDto;

public class QueryImeiListLoad implements ServiceLogic {
	private EirUtil eirUtil;
	private Environment env;
	private Loggers log;// queryListRest
	private HttpHeaders httpHeaders;
	private OrchestratorMethod orchestratorMethod;
	private GenericDto requestData;
	private Class<?> typeClazz = this.getClass();

	@Override
	public void setMap(GenericDto queryImeiListMap) {
		this.eirUtil = (EirUtil) queryImeiListMap.get("util");
		this.env = (Environment) queryImeiListMap.get("env");
		this.log = (Loggers) queryImeiListMap.get("log");
		this.orchestratorMethod = (OrchestratorMethod) queryImeiListMap.get("orchestratorMethod");
		this.requestData = (GenericDto) queryImeiListMap.get("requestData");
		log.setType(typeClazz);
	}

	@Override
	public GenericDto execute() {
		GenericDto responseData = new GenericDto();

		try {
			long start = System.currentTimeMillis();

			log.before(log.getMethodName(), eirUtil.json2String(requestData), log.getOriginReferenceId());

			if (eirUtil.stringValue(requestData.get("imei")).equals(""))
				throw new ServiceException(3, "Parametro de entrada requerido imei no esta presente");

			// if (eirUtil.stringValue(requestData.get("imsi")).equals(""))
			// throw new ServiceException(3, "Parametro de entrada requerido imsi no
			// presente");

			GenericDto requestDataGetRoutingValidationEir = new GenericDto();
			requestDataGetRoutingValidationEir.setProperty("imei", requestData.get("imei"));

			ProviderResponse providerResponseGetRoutingValidationEir = orchestratorMethod
					.setService("getRoutingValidationEirService", requestDataGetRoutingValidationEir);

			GenericDto responseDataGetRoutingValidationEir = eirUtil
					.jsonParse(providerResponseGetRoutingValidationEir.getData());
			if (eirUtil.stringValue(responseDataGetRoutingValidationEir.get("routingValidation"))
					.equals("no provissioning"))
				throw new ServiceException(providerResponseGetRoutingValidationEir.getServiceCode(),
						"imei no existe en routing");

			if (providerResponseGetRoutingValidationEir == null
					|| providerResponseGetRoutingValidationEir.getData() == null
					|| providerResponseGetRoutingValidationEir.getData().equals(""))
				throw new ServiceException(4, "No se obtuvo respuesta de getRoutingValidationEirService");

			HttpHeaders headers = new HttpHeaders();
			// headers.add("X-Authorization", token);
			headers.add("X-Organization-Code", env.getProperty("X-Organization-Code"));
			headers.add("Content-Type", env.getProperty("Content-Type"));
			headers.add(HttpHeaders.CONNECTION, "Keep-Alive");
			headers.add(HttpHeaders.CACHE_CONTROL, "no-cache");
			headers.add(HttpHeaders.ACCEPT_ENCODING, MediaType.APPLICATION_JSON_VALUE);

			boolean isFirst = true;
			StringBuilder builder = new StringBuilder(env.getProperty("eir.url.query"));
			for (Map.Entry<String, Object> entry : requestData.entrySet()) {
				String key = entry.getKey();
				String value = String.valueOf(entry.getValue());
				if (!"null".equalsIgnoreCase(value) && !"".equals(value)) {
					if (isFirst)
						isFirst = false;
					else
						builder.append("&");
					builder.append(key).append("=").append(value);
				}
			}

			URI url = new URI(builder.toString());

			String logDetail = String.format("REST request: headers=[%s], url=[%s]", eirUtil.json2String(headers), url);
			log.info("Info", logDetail, log.getOriginReferenceId());

			RequestEntity<String> requestEntity = new RequestEntity<String>("", headers, HttpMethod.POST, url);

			RestTemplate restTemplate = new RestTemplate();

			ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

			logDetail = String.format("REST response: responseEntity=[%s]", eirUtil.json2String(responseEntity));
			log.info("Info", logDetail, log.getOriginReferenceId());

			HttpStatus statusCode = responseEntity.getStatusCode();

//			responseData.setProperty("code", statusCode.value());
//			responseData.setProperty("message", statusCode.getReasonPhrase());
			Map<String, Object> responseBody = eirUtil.jsonParse(responseEntity.getBody());
			responseData.putAll(responseBody);

			log.after(log.getMethodName(), eirUtil.json2String(responseData), start, log.getOriginReferenceId());

		} catch (ServiceException e) {
			log.error(log.getMethodName(), e);
			responseData.setProperty("code", 98);
			responseData.setProperty("message", e.getMessage());
		} catch (Exception e) {
			log.error(log.getMethodName(), e);
			responseData.setProperty("code", 99);
			responseData.setProperty("message", e.getMessage());
		}

		return responseData;
	}

}
