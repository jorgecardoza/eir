package com.tigo.ea.eir.provider.service.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigo.ea.eir.provider.config.EirConfig;
import com.tigo.ea.eir.provider.util.ProviderUtil;
import com.tigo.ea.esb.provider.core.ProviderRequest;
import com.tigo.ea.esb.provider.core.ProviderResponse;
import com.tigo.ea.esb.provider.service.Service;
import com.tigo.ea.util.dto.GenericDto;

@ActiveProfiles("standalone")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EirConfig.class })
public class EirTEST {

    private static final String SERVICE_ID = "getRoutingValidationEirService";
    private ObjectMapper objectMapper;
    private ProviderRequest request;
    private ProviderResponse response;

    @Autowired
    private Service getRoutingValidationEirService;

    @Autowired
    private ProviderUtil providerUtil;

    @Before
    public void setUp() {
	objectMapper = new ObjectMapper();
	request = new ProviderRequest();
	response = new ProviderResponse();

	request.setServiceId(SERVICE_ID);
	request.setDataType("json-pretty");
    }

    @Test
    public void testGetResultSuccessfullyByMsisdn() throws JsonParseException, JsonMappingException {

	GenericDto requestData = new GenericDto();




	
	String json = "";
	try {
			
	    json = providerUtil.marshalPretty(objectMapper, requestData);
	    System.out.println("Data Input ProcessProvisioningBbiService [json]: \n" + json);
	    request.setData(json);
	    getRoutingValidationEirService.execute(request, response);		
	    System.out.println("Data Output getRoutingValidationEirService [Json]: \n" + response.getData());

	} catch (Exception e) {
	    e.printStackTrace();
	}
	Assert.isTrue(response.getServiceCode() == 0, "Busqueda fallida");
    }

    
    

}
