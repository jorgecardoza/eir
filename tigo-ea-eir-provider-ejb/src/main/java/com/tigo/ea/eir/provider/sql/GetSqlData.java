package com.tigo.ea.eir.provider.sql;

import java.util.List;
import java.util.Map;

import com.tigo.ea.util.dto.GenericDto;

public interface GetSqlData {
	
	
	List<GenericDto>listSqlObject (String queryStr, Map<String, Object> parametros);
	boolean update(String query, Map<String, Object> parametros);
	boolean insert(String query, Map<String, Object> parametros);
	boolean delete(String query, Map<String, Object> parametros);

}
