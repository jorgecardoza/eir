package com.tigo.ea.eir.provider.ejb;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import com.tigo.ea.esb.provider.core.ServiceProvider;
import com.tigo.ea.esb.provider.core.ServiceProviderBean;

@Stateless(mappedName = "ejb/eirProvider")
@WebService(endpointInterface = ServiceProvider.ENDPOINT_INTERFACE, 
		    name = ServiceProvider.DEFAULT_NAME, 
		    serviceName = ServiceProvider.DEFAULT_SERVICE_NAME, 
		    portName = ServiceProvider.DEFAULT_PORT_NAME)
@Remote(ServiceProvider.class)
public class EirBean extends ServiceProviderBean {

}
