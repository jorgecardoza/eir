package com.tigo.ea.eir.provider.loading;

import org.springframework.core.env.Environment;

import com.tigo.ea.eir.provider.logger.Loggers;
import com.tigo.ea.eir.provider.logic.ServiceLogic;
import com.tigo.ea.eir.provider.util.EirUtil;
import com.tigo.ea.esb.provider.service.ServiceException;
import com.tigo.ea.strainer.tsb.StrainerTsb;
import com.tigo.ea.util.dto.GenericDto;

public class GetRoutingValidationEirLoad implements ServiceLogic {
	private EirUtil util;
	private Environment env;
	private Loggers log;
	private StrainerTsb strainerTsb;

	private GenericDto requestData = null;
	private Class<?> typeClazz = this.getClass();

	@Override
	public void setMap(GenericDto getRoutingValidationEirMap) {
		this.util = (EirUtil) getRoutingValidationEirMap.get("util");
		this.env = (Environment) getRoutingValidationEirMap.get("env");
		this.log = (Loggers) getRoutingValidationEirMap.get("log");
		this.strainerTsb = (StrainerTsb) getRoutingValidationEirMap.get("strainerTsb");
		this.requestData = (GenericDto) getRoutingValidationEirMap.get("requestData");
		log.setType(typeClazz);
	}

	@Override
	public GenericDto execute() {
		GenericDto responseData = new GenericDto();

		try {
			long start = System.currentTimeMillis();
			
			log.before(log.getMethodName(), util.json2String(requestData), log.getOriginReferenceId());
			
			if (util.stringValue(requestData.get("imei")).equals(""))
				throw new ServiceException(3, "Parámetro de entrada requerido no está presente");

			log.setEndUser(util.stringValue(requestData.get("imei")));
		
			String imei = util.stringValue(requestData.get("imei"));

			responseData.put("routingValidation", (strainerTsb(imei).equals("success"))?"allow provissioning":"no provissioning");
			
			log.after(log.getMethodName(), util.json2String(responseData),start,log.getOriginReferenceId());
			
			return responseData;
		} catch (ServiceException e) {
			log.error(log.getMethodName(), e);
			responseData.put("message", e.getMessage());
		} catch (Exception e) {
			log.error(log.getMethodName(), e);
			responseData.put("message", e.getMessage());
		}
		
		return responseData;
	}

	private String strainerTsb(String imei) {
		String enviroment = env.getProperty("enviroment","");

		if (enviroment.equals("factory") || (enviroment.equals("development"))) {
			String result = strainerTsb.isIncludedByContext("whitelist", imei, "imeiEirValidation");
			log.info("Info",
					String.format("strainerTsb.isIncludedByContext: type=[%s], value=[%s], context=[%s] -> result=[%s]",
							"whitelist", imei, "imeiEirValidation", result),
					log.getOriginReferenceId());
			return result;
		}

		return "success";
	}
}
