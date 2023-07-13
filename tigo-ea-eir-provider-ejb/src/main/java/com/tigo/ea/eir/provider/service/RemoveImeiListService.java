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
import com.tigo.ea.util.classloading.ClassFactory;
import com.tigo.ea.util.dto.GenericDto;

@Service
public class RemoveImeiListService extends AbstractConvertibleService<GenericDto, GenericDto> {
	@Autowired
	private ClassFactory classFactory;
	@Autowired
	private EirUtil util;
	@Autowired
	private Environment env;
	@Autowired
	private Loggers log;
	
	private OrchestratorMethod orchestratorMethod;

	@Override
	protected GenericDto execute(ProviderRequest arg0, ProviderResponse response, GenericDto requestData) {
		log.setMethodName("RemoveImeiListService");

		ServiceLogic serviceLogic = (ServiceLogic) classFactory.loadClass("RemoveImeiListLoad");
		GenericDto removeImeiListMap = new GenericDto();
		removeImeiListMap.put("util", util);
		removeImeiListMap.put("env", env);
		removeImeiListMap.put("log", log);
		removeImeiListMap.put("orchestratorMethod", orchestratorMethod);
		removeImeiListMap.put("requestData", requestData);
		serviceLogic.setMap(removeImeiListMap);
		return serviceLogic.execute();

	}

}
