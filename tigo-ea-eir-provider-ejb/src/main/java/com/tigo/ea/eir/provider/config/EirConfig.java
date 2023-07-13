package com.tigo.ea.eir.provider.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigo.ea.esb.provider.config.ServiceProviderConfig;
import com.tigo.ea.esb.provider.consumer.config.ProviderConsumerConfig;
import com.tigo.ea.esb.provider.service.EchoService;
import com.tigo.ea.esb.provider.service.Service;
import com.tigo.ea.strainer.tsb.StrainerTsb;
import com.tigo.ea.strainer.tsb.StrainerTsbWrapper;
import com.tigo.ea.util.classloading.ClassFactory;
import com.tigo.ea.util.classloading.DynamicClassFactory;
import com.tigo.ea.util.configuration.ConfigurationServiceConfig;
import com.tigo.ea.util.spring.FactoryBeanUtil;

@Configuration
@ComponentScan({ "com.tigo.ea.eir.provider" })
@Import({ ServiceProviderConfig.class, ProviderConsumerConfig.class })
public class EirConfig {
	@Autowired
	private Environment env;
	@Bean
	RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	
	@Bean
	HttpHeaders httpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}
	
	@Bean
	public ClassFactory classFactory() {
		String url = env.getProperty(ConfigurationServiceConfig.CLASSFILE_URL);
		String basePackage = env.getProperty(ConfigurationServiceConfig.CLASSFILE_PACKAGE);
		DynamicClassFactory dynamicClassFactory = new DynamicClassFactory(url, basePackage);
		return dynamicClassFactory;
	}
	// Sacado del Manual de Jose Reyes

	@Bean(destroyMethod = "")
	public JdbcTemplate jdbcTemplate() {

		// obteniendo conexion
		JdbcTemplate jdbcTemplate = null;
		String jndiName = env.getProperty("jdbc.jndi");// configentry que se debe ingresar a la bd

		try {

			// usando jndi
			JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
			factory.setJndiName(jndiName);
			factory.setResourceRef(true);
			factory.setExpectedType(DataSource.class);
			factory.afterPropertiesSet();

			DataSource dataSou = (DataSource) factory.getObject();
			jdbcTemplate = new JdbcTemplate(dataSou);

		} catch (Exception e) {
			System.out.println("Error, Creating @Bean DataSourceSLDDB with jndiName = " + jndiName + ". Error: " + e);
			e.printStackTrace();
		}

		return jdbcTemplate;
	}
/*
	@Bean
	public StrainerTsb strainerTsb() {
		JaxWsPortProxyFactoryBean jaxWsPortProxyFactoryBean = new JaxWsPortProxyFactoryBean();
		String wsdl = env.getProperty("isIncludedByContext.ws.wsdl"); // http://internaltsbfactprovider.sv.tigo.com:7013/StrainerTsbBean/StrainerTsbWS?wsdl;
		try {
			jaxWsPortProxyFactoryBean.setServiceInterface(StrainerTsb.class);
			jaxWsPortProxyFactoryBean.setWsdlDocumentUrl(new URL(wsdl));
			jaxWsPortProxyFactoryBean.setNamespaceUri("http://tsb.strainer.ea.tigo.com/");
			jaxWsPortProxyFactoryBean.setServiceName("StrainerTsbWS");
			jaxWsPortProxyFactoryBean.setPortName("StrainerTsbPort");
			jaxWsPortProxyFactoryBean.setEndpointAddress(wsdl);
			jaxWsPortProxyFactoryBean.addCustomProperty("com.sun.xml.ws.request.timeout",new Integer(env.getProperty("strainertsb.timeout")));
			jaxWsPortProxyFactoryBean.addCustomProperty("com.sun.xml.ws.connect.timeout",new Integer(env.getProperty("proxy.connect.timeout")));
			jaxWsPortProxyFactoryBean.afterPropertiesSet();
		} catch (Exception e) {
//throw new RuntimeException("Error creating @Bean taskStatusUpdatePort WS impl with WSDL = " + wsdl, e);
		}
		return (StrainerTsb) jaxWsPortProxyFactoryBean.getObject();
	}
	*/
	@Bean(name = "strainerTsb")
	public StrainerTsb strainerTsb()
	{
		String strainerTsbImpl = null;
		StrainerTsb strainerTsb = null;
		StrainerTsbWrapper wrapper = new StrainerTsbWrapper();
		try
		{
			strainerTsbImpl = env.getRequiredProperty("strainerTsb.impl");
			if ("ws".equals(strainerTsbImpl))
				strainerTsb = FactoryBeanUtil.createWsService(env.getRequiredProperty("strainerTsb.ws.wsdl"), StrainerTsb.class);
			else if ("ejb".equals(strainerTsbImpl))
				strainerTsb = FactoryBeanUtil.createJndiService(env.getRequiredProperty("strainerTsb.ejb.jndi"), env.getRequiredProperty(ConfigurationServiceConfig.TOOLBOX_FACTORY_PROPERTY), env.getRequiredProperty("strainerTsb.ejb.provider"), StrainerTsb.class);
			wrapper.setStrainerTsb(strainerTsb);
		} catch (Exception e)
		{
			throw new RuntimeException(String.format("Error creating @Bean strainerTsb, impl = %s", strainerTsbImpl), e);
		}
		return wrapper;
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setTimeZone(TimeZone.getDefault());
		return mapper;
	}

	@Bean(name = "eir.echoService")
	public Service echoService() {
		return new EchoService();
	}

	@Bean
	public List<String> reloadingClasses() {
		List<String> reloadingClasses = new ArrayList<String>();
		return reloadingClasses;
	}

}
