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
public class QueryImeiListService extends AbstractConvertibleService<GenericDto, GenericDto> {
	@Autowired
	private ClassFactory classFactory;
	@Autowired
	private EirUtil util;
	@Autowired
	private Environment env;
	@Autowired
	private Loggers log;

	@Autowired
	private OrchestratorMethod orchestratorMethod;

	@Override
	protected GenericDto execute(ProviderRequest arg0, ProviderResponse request, GenericDto requestData) {
		log.setMethodName("QueryImeiListService");

		ServiceLogic serviceLogic = (ServiceLogic) classFactory.loadClass("QueryImeiListLoad");
		GenericDto queryImeiListMap = new GenericDto();
		queryImeiListMap.put("util", util);
		queryImeiListMap.put("env", env);
		queryImeiListMap.put("log", log);
		queryImeiListMap.put("orchestratorMethod", orchestratorMethod);
		queryImeiListMap.put("requestData", requestData);
		serviceLogic.setMap(queryImeiListMap);
		return serviceLogic.execute();
	}

}
