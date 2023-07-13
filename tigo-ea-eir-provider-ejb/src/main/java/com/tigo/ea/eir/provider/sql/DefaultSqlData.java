package com.tigo.ea.eir.provider.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.tigo.ea.util.dto.GenericDto;

@Component
public class DefaultSqlData implements GetSqlData{
	
	
    @Autowired
    private JdbcTemplate jdbcTemplate;
    

	@Override
	public List<GenericDto> listSqlObject(String query, Map<String, Object> parametros) {
		
		List<GenericDto> resp = new ArrayList<GenericDto>();

		try {
		    NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
		    resp = namedParameterJdbcTemplate.query(query, parametros, new RowMapper<GenericDto>() {

			@Override
			public GenericDto mapRow(ResultSet rs, int rowNum) throws SQLException {

			    GenericDto dto = new GenericDto();

			    if (rs != null) {
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {

				    dto.setProperty(rs.getMetaData().getColumnName(i), rs.getObject(i));
				}
			    }
			    return dto;
			}
		    });

		} catch (Exception e) {
		    throw e;
		}
		return resp;
	}

	@Override
	public boolean update(String query, Map<String, Object> parametros) {

		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
		int isInsert = namedParameterJdbcTemplate.update(query, parametros);
		
		if (isInsert > 0)
		    return true;

		return false;
	}

	@Override
	public boolean insert(String query, Map<String, Object> parametros) {
		
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
		int isInsert = namedParameterJdbcTemplate.update(query, parametros);
		
		if (isInsert > 0)
		    return true;

		return false;
	}

	@Override
	public boolean delete(String query, Map<String, Object> parametros) {
		
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
		int isInsert = namedParameterJdbcTemplate.update(query, parametros);
		
		if (isInsert > 0)
		    return true;

		return false;
	}

}
