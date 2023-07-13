package com.tigo.ea.eir.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.tigo.ea.eir.provider.logger.Loggers;
import com.tigo.ea.eir.provider.logic.ServiceLogic;
import com.tigo.ea.eir.provider.util.EirUtil;
import com.tigo.ea.esb.provider.core.ProviderRequest;
import com.tigo.ea.esb.provider.core.ProviderResponse;
import com.tigo.ea.esb.provider.service.AbstractConvertibleService;
import com.tigo.ea.strainer.tsb.StrainerTsb;
import com.tigo.ea.util.classloading.ClassFactory;
import com.tigo.ea.util.dto.GenericDto;

@Service
public class GetRoutingValidationEirService extends AbstractConvertibleService<GenericDto, GenericDto> {
	@Autowired
	private StrainerTsb strainerTsb;
	@Autowired
	private ClassFactory classFactory;
	@Autowired
	private EirUtil util;
	@Autowired
	private Environment env;
	@Autowired
	private Loggers log;

	@Override
	protected GenericDto execute(ProviderRequest arg0, ProviderResponse response, GenericDto requestData) {
		log.setMethodName("GetRoutingValidationEirService");

		ServiceLogic serviceLogic = (ServiceLogic) classFactory.loadClass("GetRoutingValidationEirLoad");
		GenericDto getRoutingValidationEirMap = new GenericDto();
		getRoutingValidationEirMap.put("util", util);
		getRoutingValidationEirMap.put("env", env);
		getRoutingValidationEirMap.put("log", log);
		getRoutingValidationEirMap.put("strainerTsb", strainerTsb);
		getRoutingValidationEirMap.put("requestData", requestData);
		serviceLogic.setMap(getRoutingValidationEirMap);
		return serviceLogic.execute();
	}

}
