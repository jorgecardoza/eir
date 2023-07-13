package com.tigo.ea.eir.provider.util;

public interface Property {

	//JNDI WebLogic y DataSource
	public static String JDBC_DRIVER = "jdbc.driver";
	public static String JDBC_JNDI = "jdbc.jndi";
	public static String JDBC_PASSWORD = "jdbc.password";
	public static String JDBC_URL = "jdbc.url";
	public static String JDBC_USERNAME = "jdbc.username";	
	public static String SQL_ORDENBASICA_FINDONE = "osmpvs.sql.ordenbasica.findone";
	public static String SQL_ORDENBASICA_FINDBYPLATAFORMA = "osmpvs.sql.ordenbasica.findbyplataforma";
	public static String SQL_ORDENBASICA_FINDBYORDENCODIGO = "osmpvs.sql.ordenbasica.findbyordencodigo";
	public static String SQL_ORDENBASICA_SAVE = "osmpvs.sql.ordenbasica.save";
}

