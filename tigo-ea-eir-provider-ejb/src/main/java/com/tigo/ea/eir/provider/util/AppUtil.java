package com.tigo.ea.eir.provider.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigo.ea.util.configuration.ConfigurationServiceFacade;
import com.tigo.ea.util.dto.GenericDto;

@Component
public class AppUtil {
	@Autowired
	private ConfigurationServiceFacade configurationServiceFacade;

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private Environment env;

	@SuppressWarnings("unused")
	private static final long FACTOR = 1000000;

	public final int UNKNOWN_ERROR = 201;
	public final int REQUEST_EMPTY_OR_NULL = 202;
	public final int QUERY_NOT_FOUND = 203;
	public final int ASSET_NOT_CREATED = 204;
	public final int ATTRIBUTES_UNMODIFIED = 205;
	public final int OPTION_INVALID = 206;
	public final int WEB_SERVICE_INVOKE_ERROR = 207;
	public final int ACTIVITY_NOT_REGISTRY = 208;
	public final int ASSET_TYPE_NOT_FOUND = 209;
	public final int PROPERTIES_ACTIVITY_NOT_FOUND = 210;
	public final int REQUEST_INVALID_LENGTH_FIELD = 211;
	public final int RECORD_UNMODIFIED = 212;
	public final int PROPERTY_NOT_FOUND = 213;
	public final int ASSET_UNMODIFIED = 214;
	public final int RESPONSE_EMPTY_OR_NULL = 215;
	public final int WEB_SERVICE_EXCEPTION = 216;
	public final int REQUEST_VALIDATIONS = 217;
	public final int WEB_SERVICE_REST_EXCEPTION = 216;

	public final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; // 12
	public final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss"; // 13
	public final String DATE_FORMAT = "yyyy-MM-dd"; // 8
	public final int TIMESTAMP_FORMAT_ID = 13;

	public Map<Integer, String> errorMessages;
	{
		Map<Integer, String> tempMap = new HashMap<Integer, String>();
		tempMap.put(201, "Unknown Error");
		tempMap.put(202, "Request emtpy or null");
		tempMap.put(203, "Query not found");
		tempMap.put(204, "Asset not created");
		tempMap.put(205, "Attributes not created or modified");
		tempMap.put(206, "Invalid option requested");
		tempMap.put(207, "WebService CRM-Siebel Error");
		tempMap.put(208, "Activity not registry");
		tempMap.put(209, "Asset Type not found");
		tempMap.put(210, "Properties UpsertActivity not found");
		tempMap.put(211, "Invalid length field requested");
		tempMap.put(212, "Record unmodified");
		tempMap.put(213, "Property TSB not found");
		tempMap.put(214, "Asset not modified");
		tempMap.put(215, "Response emtpy or null");
		tempMap.put(216, "Service exception error");
		tempMap.put(217, "Request validations");
		tempMap.put(218, "Service REST exception error");
		errorMessages = Collections.unmodifiableMap(tempMap);
	}

	public Environment getEnv() {
		return env;
	}

	public ConfigurationServiceFacade getConfigurationServiceFacade() {
		return configurationServiceFacade;
	}

	/**********************************************************************
	 * 
	 * CONVERSIONES JSON Descripci�n: Convierte Object to Json. => Ok m�todo
	 * funcional in: ObjectMapper, Object out: String
	 * 
	 */
	public String marshal(ObjectMapper objectMapper, Object obj) throws JsonProcessingException {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**********************************************************************
	 * 
	 * CONVERSIONES XML Descripci�: Convierte Object to Json. => Ok m�todo
	 * funcional in: ObjectMapper, Object out: String
	 * 
	 **/
	public String marshalPretty(ObjectMapper objectMapper, Object obj) throws JsonProcessingException {
		String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		return jsonData;
	}

	/**********************************************************************
	 * 
	 * CONVERSIONES JSON Descripcion: Convierte Json to Object. => Ok metodo
	 * funcional in: String, Class<T> out: <T> T
	 * 
	 */
	public <T> T unmarshal(String json, Class<T> clazz) throws IOException {
		T objectData = objectMapper.readValue(json, clazz);
		return objectData;
	}

	/**********************************************************************
	 * 
	 * CONVERSIONES JSON Descripci�n: Convierte Json to Object. => Ok m�todo
	 * funcional in: ObjectMapper, String, Class<T> out: <T> T
	 * 
	 */
	public <T> T unmarshal(ObjectMapper objectMapper, String json, Class<T> clazz) throws IOException {
		T objectData = objectMapper.readValue(json, clazz);
		return objectData;
	}

	/**********************************************************************
	 * 
	 * CONVERSIONES JSON Descripcion: Convierte Object to Json. => Ok metodo
	 * funcional in: Object out: String
	 * 
	 */
	public String marshal(Object obj) throws JsonProcessingException {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**********************************************************************
	 * 
	 * CONVERSIONES XML Descripcion: Convierte Object to Json. => Ok metodo
	 * funcional in: Object out: String
	 * 
	 **/
	public String marshalPretty(Object obj) throws JsonProcessingException {
		String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		return jsonData;
	}

	/**********************************************************************
	 * 
	 * GET DATA FROM OBJECT Descripci�n: Obtiene data indicada (values) de una
	 * configentryvalue (value) indicando las llaves que se desean obtener, las
	 * configentryvalue deben tener un formato key|value in: String, String[] out:
	 * GenericDto
	 * 
	 */
	public GenericDto getValue(String value, String[] keys) {
		GenericDto parameters = new GenericDto();
		Set<String> parametersList = configurationServiceFacade.getValues(value);
		if (keys == null || keys.length == 0) {
			for (String parameter : parametersList) {
				String[] item = parameter.split("[|]");
				parameters.put(item[0], (item.length == 2) ? (item[1]) : (""));
			}
		} else {
			for (String parameter : parametersList) {
				String[] item = parameter.split("[|]");
				if (!Arrays.asList(keys).isEmpty() && Arrays.asList(keys).contains(item[0]))
					parameters.put(item[0], (item.length == 2) ? (item[1]) : (""));
			}
		}
		return parameters;
	}

	/**********************************************************************
	 * 
	 * GET DATE Descripci�n: Obtiene la fecha en String en base al formato de
	 * fecha indicado in: String out: String
	 * 
	 */
	public String getDate(String formatDate) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(formatDate);
			return format.format(new Date());
		} catch (Exception e) {
			return null;
		}
	}

	/**********************************************************************
	 * 
	 * GET DATE Descripci�n: Obtiene la fecha en actual out: Date
	 * 
	 */
	public Date getDate() {
		return new Date();
	}

	/**********************************************************************
	 * 
	 * GET DATE Descripci�n: Obtiene la fecha en Date en base al formato de fecha
	 * configurado in: String, String out: String
	 * 
	 */
	public Date getDate(String date, String formatDate) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(formatDate);
			return format.parse(date);
		} catch (Exception e) {
			return new Date();
		}
	}

	/**********************************************************************
	 * 
	 * GET DATE Descripci�n: Suma/resta los meses indicados a la fecha en y la
	 * devuelve en String según el formato indicado. in: String, String, int out:
	 * String
	 * 
	 */
	public String getDateChangeMonth(String date, String formatDate, int month) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(formatDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(format.parse(date));
			cal.add(Calendar.MONTH, month);
			return format.format(cal.getTime());
		} catch (Exception e) {
			return null;
		}
	}

	/**********************************************************************
	 * 
	 * GET DATE Descripci�n: Suma/resta los días indicados a la fecha en y la
	 * devuelve en String según el formato indicado. in: String, String, int out:
	 * String
	 * 
	 */
	public String getDateChanceDay(String date, String formatDate, int day) {
		try {
			SimpleDateFormat format = new SimpleDateFormat(formatDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(format.parse(date));
			cal.add(Calendar.DATE, day);
			return format.format(cal.getTime());
		} catch (Exception e) {
			return null;
		}
	}

	/**********************************************************************
	 * 
	 * GET DATE Descripci�n: Obtiene el objecto de un json compuesto de objetos y
	 * listas in: Map<String, Object>, String[] out: String
	 * 
	 */
	@SuppressWarnings("unchecked")
	public String getValueFromDto(Map<String, Object> genericDto, String[] labels) {
		String returnStr = "";

		if (labels.length > 0) {
			String[] labelsArray = new String[labels.length - 1];

			for (int i = 0; i < labelsArray.length; i++)
				labelsArray[i] = labels[i + 1];

			if (genericDto.containsKey(labels[0])) {
				if (genericDto.get(labels[0]) instanceof HashMap)
					returnStr = getValueFromDto((Map<String, Object>) genericDto.get(labels[0]), labelsArray);
				else
					returnStr = (genericDto.get(labels[0]) != null) ? genericDto.get(labels[0]).toString() : "";
			}
		}

		return returnStr;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map> getListFromDto(Map<String, Object> genericDto, String[] labels) {
		List<Map> returnList = new ArrayList<Map>();

		if (labels.length > 0) {
			String[] labelsArray = new String[labels.length - 1];

			for (int i = 0; i < labelsArray.length; i++)
				labelsArray[i] = labels[i + 1];

			if (genericDto.containsKey(labels[0])) {
				if (genericDto.get(labels[0]) instanceof HashMap)
					returnList = getListFromDto((Map<String, Object>) genericDto.get(labels[0]), labelsArray);
				else
					returnList = (genericDto.get(labels[0]) != null) ? (List<Map>) genericDto.get(labels[0])
							: new ArrayList<Map>();
			}
		}
		return returnList;
	}
}