package com.tigo.ea.eir.provider.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.tigo.ea.esb.provider.core.ProviderContext;
import com.tigo.ea.esb.provider.core.ProviderRequest;
import com.tigo.ea.esb.provider.core.ProviderResponse;
import com.tigo.ea.esb.provider.core.ServiceContext;
import com.tigo.ea.esb.provider.service.Service;
import com.tigo.ea.esb.provider.service.ServiceException;
import com.tigo.ea.util.TransactionIdUtil;
import com.tigo.ea.util.configuration.ConfigurationServiceFacade;
import com.tigo.ea.util.dto.GenericDto;
import com.tigo.ea.util.log.EventData;
import com.tigo.ea.util.log.EventLevel;
import com.tigo.ea.util.log.LoggingServiceFacade;

@Component
public class EirUtil {

	@Autowired
	private ConfigurationServiceFacade configurationServiceFacade;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private LoggingServiceFacade loggingServiceFacade;


	public GenericDto processService(Service service, GenericDto requestData) throws Exception {

		String json;
		try {

			ProviderRequest request = new ProviderRequest();
			ProviderResponse response = new ProviderResponse();
			request.setDataType("json-pretty");

			json = marshalPretty(objectMapper, requestData);
			request.setData(json);
			service.execute(request, response);
			return unmarshal(objectMapper, response.getData(), GenericDto.class);

		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> configentryValue(String key) {

		try {

			Set<String> applicationList = configurationServiceFacade.getValues(key);
			List<String> list = new ArrayList<String>(applicationList);
			return list;

		} catch (Exception e) {

		}

		return new ArrayList<String>();
	}

	public GenericDto processService(Service service, GenericDto requestData, String refId) throws Exception {

		String json;
		try {

			ProviderRequest request = new ProviderRequest();
			ProviderResponse response = new ProviderResponse();
			request.setDataType("json-pretty");

			json = marshalPretty(objectMapper, requestData);
			request.setData(json);
			request.setApplicationRefId(refId);
			service.execute(request, response);
			return unmarshal(objectMapper, response.getData(), GenericDto.class);

		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}



	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> mapListValue(Object object) {

		try {

			if (object != null)
				return (List<Map<String, Object>>) object;
		} catch (Exception e) {

		}

		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> mapValue(Object object) {

		try {
			if (object != null)
				return (Map<String, Object>) object;
		} catch (Exception e) {

		}
		return new HashMap<>();
	}

	public String stringValue(Object object) {
		try {
			return (object != null) ? object.toString() : "";
		} catch (Exception e) {
			return "";
		}
	}

	public String jsonString(Object object) {

		try {
			return this.marshalPretty(objectMapper, object);
		} catch (Exception e) {
			return String.valueOf(object);
		}
	}

	public String json2String(Object object) {

		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			return String.valueOf(object);
		}
	}

	public GenericDto jsonParse(String json) {

		try {
			return this.unmarshal(objectMapper, json, GenericDto.class);
		} catch (Exception e) {
			return new GenericDto();
		}
	}

	public List<GenericDto> jsonParseList(String json) {

		try {
			return this.unmarshalList(objectMapper, GenericDto.class, json);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public String dateString(String formater) {

		try {
			SimpleDateFormat format = new SimpleDateFormat(formater);
			return format.format(new Date());
		} catch (Exception e) {
			return "";
		}
	}

	public String dateString(String formater, Date dateformat) {

		try {
			SimpleDateFormat format = new SimpleDateFormat(formater);
			return format.format(dateformat);
		} catch (Exception e) {
			return "";
		}
	}

	public String deteString(String oldformater, String newformater, String dateformat) {

		try {

			SimpleDateFormat format = new SimpleDateFormat(oldformater);
			Date olddate = format.parse(dateformat);

			format = new SimpleDateFormat(newformater);
			return format.format(olddate);

		} catch (Exception e) {
			return "";
		}
	}

	public String exceptionTrace(Throwable ex) {

		try {
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
			return sw.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public Date dateFormater(String date, String formater) {

		try {

			SimpleDateFormat format = new SimpleDateFormat(formater);
			return format.parse(date);
		} catch (Exception e) {
			return null;
		}
	}

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
		errorMessages = Collections.unmodifiableMap(tempMap);
	}

	private static String[] formatDate = { "yyyy-MM-dd'T'HH:mm:ss", // 0
			"dd/MM/yyyy", // 1
			"dd-MM-yyyy", // 2
			"yyyyMMddHHmmss", // 3
			"yyyy-MM-dd'T'HH:mm:ss.SSS", // 4
			"yyyyMMddHHmmss", // 5
			"MM/dd/yyyy", // 6
			"yyyy-MM-dd'T'HH:mm:ss'Z'", // 7
			"yyyy-MM-dd", // 8
			"dd/MM/yyyy HH:mm:ss", // 9
			"MM/dd/yyyy HH:mm:ss", // 10
			"yyyy-MM-dd'T'HH:mm:ss", // 11
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", // 12
			"yyyy-MM-dd HH:mm:ss", // 13
			"yy/MM/dd", // 14
			"ddMMyyyyHHmmss" // 15
	};

	public boolean validarCodigoAVON(String avonCode) {

		return Operaciones.validarCodigoAVON(avonCode);
	}

	private ThreadLocal<SimpleDateFormat> cbsFormatter = new ThreadLocal<SimpleDateFormat>() {

		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMddHHmmss");
		}

	};

	public Date toDate(String dateInString) {
		Date date = null;
		try {
			date = cbsFormatter.get().parse(dateInString);
		} catch (Exception e) {
		}
		return date;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<GenericDto> orderDateList(List<GenericDto> data, final String key, final String orderby,
			final String dateFormat) {

		Collections.sort(data, new Comparator() {

			@Override
			public int compare(Object arg0, Object arg1) {

				GenericDto dt1 = (GenericDto) arg0;
				GenericDto dt2 = (GenericDto) arg1;

				SimpleDateFormat format = new SimpleDateFormat(dateFormat);
				Date dateArg0 = null;
				Date dateArg1 = null;

				try {

					dateArg0 = format.parse(dt1.get(key).toString());
					dateArg1 = format.parse(dt2.get(key).toString());

				} catch (ParseException e) {
					e.printStackTrace();
				}

				if (orderby.equals("desc"))
					return new Integer(dateArg0.compareTo(dateArg1));
				else
					return new Integer(dateArg1.compareTo(dateArg0));
			}
		});

		return data;
	}

	public Date toDate(String dateInString, int formatDate) {
		String defaultFormatDate = "dd/MM/yyyy HH:mm:ss";
		int tam = formatDate > (EirUtil.formatDate.length - 1) ? 2 : formatDate;
		defaultFormatDate = EirUtil.formatDate[tam];
		SimpleDateFormat format = new SimpleDateFormat(defaultFormatDate);
		try {
			return format.parse(dateInString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Date toParserDate(String strFecha) {

		SimpleDateFormat formatoPost = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		SimpleDateFormat newFormat = new SimpleDateFormat("yyyyMMddHHmmss.S");
		Date fechaPost = null;
		Date fechaNew = null;
		String Stringfecha = null;
		try {

			fechaPost = formatoPost.parse(strFecha);
			Stringfecha = newFormat.format(fechaPost);
			fechaNew = newFormat.parse(Stringfecha);

		} catch (ParseException ex) {
			ex.printStackTrace();
		}

		return fechaNew;
	}

	public Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.setTimeZone(TimeZone.getDefault());
		return calendar;
	}

	public Calendar toCalendar(String dateInString) {
		Calendar calendar = null;
		Date date = toDate(dateInString);
		if (date != null) {
			calendar = Calendar.getInstance();
			calendar.setTime(date);
		}
		return calendar;
	}

	public Calendar toCalendar(String dateInString, int formatDate) {
		Calendar calendar = null;
		Date date = toDate(dateInString, formatDate);
		if (date != null) {
			calendar = Calendar.getInstance();
			calendar.setTime(date);
		}
		return calendar;
	}

	public Calendar toCalendar(XMLGregorianCalendar xmlgc) {
		Calendar calendar = null;
		try {
			Date date = xmlgc.toGregorianCalendar().getTime();
			calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.setTimeZone(TimeZone.getDefault());
			// calendar.setTimeInMillis(DatatypeConstants.FIELD_UNDEFINED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return calendar;
	}

	public Calendar toCalendarWithLocaleExplicit(XMLGregorianCalendar xmlgc) {
		if (xmlgc == null)
			return null;
		XMLGregorianCalendar gc = xmlgc;
		Calendar calendar = gc.toGregorianCalendar(TimeZone.getDefault(), Locale.getDefault(), xmlgc);
		return calendar;
	}


	public XMLGregorianCalendar toXMLGregorianCalendar(java.util.Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		XMLGregorianCalendar xmlCalendar = null;
		try {
			xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
			return xmlCalendar;
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String toString(XMLGregorianCalendar date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			return format.format(date.toGregorianCalendar().getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String toStringWithLocaleExplicit(XMLGregorianCalendar date, String formatDate) {
		SimpleDateFormat format = new SimpleDateFormat(formatDate);
		if (date == null || (formatDate == null || (formatDate != null && formatDate.isEmpty())))
			return null;
		try {
			Calendar calendar = toCalendarWithLocaleExplicit(date);
			return format.format(calendar.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}




	/*****************
	 * CONVERSIONES XML Convierte Xml to List&lt;Object&gt;. => Ok m�todo
	 * funcional.
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> unmarshalList(Class<?> clazz, String xml) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(WrapperList.class, clazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		// StreamSource xml = new StreamSource(xmlLocation); //Si paso archivo f�sico
		// xml
		Source srcXml = new StreamSource(new StringReader(xml));
		WrapperList<T> objectDataList = (WrapperList<T>) unmarshaller.unmarshal(srcXml, WrapperList.class).getValue();

		return objectDataList.getItems();
	}

	/*****************
	 * CONVERSIONES XML Convierte List&lt;Object&gt; to Xml. => Ok m�todo
	 * funcional.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String marshalList(Class<?> clazz, List<?> list, String qName) throws JAXBException {
		QName QName = new QName(qName);
		WrapperList wrapperList = new WrapperList(list);
		JAXBContext jc = JAXBContext.newInstance(WrapperList.class, clazz);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		JAXBElement<WrapperList> jaxbElement = new JAXBElement<WrapperList>(QName, WrapperList.class, wrapperList);
		StringWriter sWriter = new StringWriter();
		marshaller.marshal(jaxbElement, sWriter);
		String xmlDataList = sWriter.toString();

		return xmlDataList;
	}

	/*****************
	 * CONVERSIONES XML Convierte Xml to Object. => Ok m�todo funcional.
	 */
	public <T> T unmarshal(Class<T> clazz, String xml) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(clazz);
		// StreamSource xml = new StreamSource(xmlLocation); //Si paso archivo f�sico
		// xml
		Source srcXml = new StreamSource(new StringReader(xml));
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		T objectData = unmarshaller.unmarshal(srcXml, clazz).getValue();

		return objectData;
	}

	/*****************
	 * CONVERSIONES XML Convierte Object to Xml. Sin uso clase Wrapper
	 * (Recomendado). => Ok m�todo funcional
	 */
	public <T> String marshal(T obj) throws JAXBException {
		StringWriter sw = new StringWriter();
		JAXBContext ctx = JAXBContext.newInstance(obj.getClass());
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(obj, sw);

		return sw.toString();
	}

	/*****************
	 * CONVERSIONES XML Convierte Object to Xml. Usando clase Wrapper
	 * (NoRecomendado) => Ok m�todo funcional
	 */
	@SuppressWarnings("rawtypes")
	public String marshalOther(Class<?> clazz, Object obj, String qName) throws JAXBException {
		QName QName = new QName(qName);
		Wrapper wrapper = new Wrapper(obj);
		JAXBContext jc = JAXBContext.newInstance(Wrapper.class, clazz);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		JAXBElement<Wrapper> jaxbElement = new JAXBElement<Wrapper>(QName, Wrapper.class, wrapper);
		StringWriter sWriter = new StringWriter();
		marshaller.marshal(jaxbElement, sWriter);
		String xmlData = sWriter.toString();

		return xmlData;
	}

	/*****************
	 * CONVERSIONES XML Convierte Xml to Object. => Ok m�todo funcional usando
	 * jackson-databind
	 */
	public <T> T unmarshalOther(String data, Class<T> clazz) throws IOException {
		XmlMapper mapper = new XmlMapper();
		T objectData = mapper.readValue(data, clazz);

		return objectData;
	}

	/*****************
	 * CONVERSIONES XML Convierte Object to Xml. => Ok m�todo funcional usando
	 * jackson-databind
	 */
	public String marshalOther(Object obj) throws IOException {
		XmlMapper mapper = new XmlMapper();
		String xmlData = mapper.writeValueAsString(obj);

		return xmlData;
	}

	/************************************************************************************************************/
	/*****************
	 * CONVERSIONES JSON Convierte Json to List&lt;Object&gt;. => Ok m�todo
	 * funcional.
	 */
	public <T> List<T> unmarshalList(ObjectMapper objectMapper, Class<?> clazz, String json) throws IOException {
		JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
		List<T> objectDataList = objectMapper.readValue(json, javaType);

		return objectDataList;
	}

	/*****************
	 * CONVERSIONES JSON Convierte List&lt;Object&gt; to Json. => Ok m�todo
	 * funcional
	 */
	public String marshalList(ObjectMapper objectMapper, List<?> list) throws JsonProcessingException {
		String jsonData = objectMapper.writeValueAsString(list);

		return jsonData;
	}

	/*****************
	 * CONVERSIONES JSON Convierte Json to Object. => Ok m�todo funcional
	 * 
	 * @return
	 */
	public <T> T unmarshal(ObjectMapper objectMapper, String json, Class<T> clazz) throws IOException {
		T objectData = objectMapper.readValue(json, clazz);

		return objectData;
	}

	/*****************
	 * CONVERSIONES JSON Convierte Object to Json. => Ok m�todo funcional
	 */
	public String marshal(ObjectMapper objectMapper, Object obj) throws JsonProcessingException {
		String jsonData = objectMapper.writeValueAsString(obj);

		return jsonData;
	}

	/*****************
	 * CONVERSIONES JSON Convierte Object to Json Pretty. => Ok m�todo funcional
	 */
	public String marshalPretty(ObjectMapper objectMapper, Object obj) throws JsonProcessingException {
		String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);

		return jsonData;
	}


	public <T> void loggingError(String methodName, String message, String detail, String category, EventLevel level, Class<T> type, String endUser) {
		String refId = TransactionIdUtil.getId();
		ServiceContext serviceContext = ProviderContext.getServiceContext();
		String originRefId = "";
		if (serviceContext != null) {
			originRefId = serviceContext.getRequest().getApplicationRefId();
		}
		int eventLevel = configurationServiceFacade.getEventLevel();
		if (eventLevel < 6)
			loggingServiceFacade.log(EventData.builder().category(category).level(level).source(type).name(methodName).message(message).detail(detail).responseCode("1").duration(null).originReferenceId(originRefId).referenceId(refId).automatic(true).successful(false).endUser(endUser).build());
	}

	public <T> void loggingError(String methodName, String message, String detail, String category, EventLevel level, Class<T> type, Throwable exception, String endUser) {
		String refId = TransactionIdUtil.getId();
		ServiceContext serviceContext = ProviderContext.getServiceContext();
		String originRefId = "";
		if (serviceContext != null) {
			originRefId = serviceContext.getRequest().getApplicationRefId();
		}
		int eventLevel = configurationServiceFacade.getEventLevel();
		if (eventLevel < 6)
			loggingServiceFacade.log(EventData.builder().category(category).level(level).source(type).name(methodName).message(message).exception(exception).detail(detail).responseCode("1").duration(null).originReferenceId(originRefId).referenceId(refId).automatic(true).successful(false).endUser(endUser).build());
	}
	
	public <T> void loggingError(String methodName, String message, String detail, String category, 
			EventLevel level, Class<T> type, Throwable exception, String endUser,String refId) {

		ServiceContext serviceContext = ProviderContext.getServiceContext();
		String originRefId = "";
		if (serviceContext != null) {
			originRefId = serviceContext.getRequest().getApplicationRefId();
		}
		int eventLevel = configurationServiceFacade.getEventLevel();
		if (eventLevel < 6)
			loggingServiceFacade.log(EventData.builder().category(category).level(level).source(type).name(methodName).message(message).exception(exception).detail(detail).responseCode("1").duration(null).originReferenceId(originRefId).referenceId(refId).automatic(true).successful(false).endUser(endUser).build());
	}

	public <T> void logging(String methodName, String message, String detail, String category, EventLevel level, Class<T> type, Throwable exception, Integer responseCode, Long duration, Boolean successful, String endUser) {
		String refId = TransactionIdUtil.getId();
		ServiceContext serviceContext = ProviderContext.getServiceContext();
		String originRefId = "";
		if (serviceContext != null) {
			originRefId = serviceContext.getRequest().getApplicationRefId();
		}
		int eventLevel = configurationServiceFacade.getEventLevel();
		if (exception == null && eventLevel < 6)
			loggingServiceFacade.log(EventData.builder().category(category).level(level).source(type).name(methodName).message(message).detail(detail != null ? detail : null).responseCode(responseCode != null ? String.valueOf(responseCode.intValue()) : null).duration(duration != null ? duration : null).originReferenceId(originRefId).referenceId(refId).automatic(true).successful(successful).endUser(endUser).build());
		else if (eventLevel < 6)
			loggingServiceFacade.log(EventData.builder().category(category).level(level).source(type).name(methodName).message(message).exception(exception).detail(detail != null ? detail : null).responseCode(responseCode != null ? String.valueOf(responseCode.intValue()) : null).duration(duration != null ? duration : null).originReferenceId(originRefId).referenceId(refId).automatic(true).successful(successful).endUser(endUser).build());
	}
	
	public <T> void logging(String methodName, String message, String detail, String category, EventLevel level, Class<T> type,
			Throwable exception, Integer responseCode, Long duration, Boolean successful, String endUser,String refId) {
		ServiceContext serviceContext = ProviderContext.getServiceContext();
		String originRefId = "";
		if (serviceContext != null) {
			originRefId = serviceContext.getRequest().getApplicationRefId();
		}
		int eventLevel = configurationServiceFacade.getEventLevel();
		if (exception == null && eventLevel < 6)
			loggingServiceFacade.log(EventData.builder().category(category).level(level).source(type).name(methodName).message(message).detail(detail != null ? detail : null).responseCode(responseCode != null ? String.valueOf(responseCode.intValue()) : null).duration(duration != null ? duration : null).originReferenceId(originRefId).referenceId(refId).automatic(true).successful(successful).endUser(endUser).build());
		else if (eventLevel < 6)
			loggingServiceFacade.log(EventData.builder().category(category).level(level).source(type).name(methodName).message(message).exception(exception).detail(detail != null ? detail : null).responseCode(responseCode != null ? String.valueOf(responseCode.intValue()) : null).duration(duration != null ? duration : null).originReferenceId(originRefId).referenceId(refId).automatic(true).successful(successful).endUser(endUser).build());
	}

	public <T> void logging(String methodName, String message, String detail, String category, EventLevel level, Class<T> type, Integer responseCode, Long duration, Boolean successful, String endUser) {
		String refId = TransactionIdUtil.getId();
		ServiceContext serviceContext = ProviderContext.getServiceContext();
		String originRefId = "";
		if (serviceContext != null) {
			originRefId = serviceContext.getRequest().getApplicationRefId();
		}
		int eventLevel = configurationServiceFacade.getEventLevel();
		if (eventLevel < 6)
			loggingServiceFacade.log(EventData.builder().category(category).level(level).source(type).name(methodName).message(message).detail(detail != null ? detail : null).responseCode(responseCode != null ? String.valueOf(responseCode.intValue()) : null).duration(duration != null ? duration : null).originReferenceId(originRefId).referenceId(refId).automatic(true).successful(successful).endUser(endUser).build());
	}
	public String getValueFromMap(Map<String, Object> map, String key){
		return map.containsKey(key) && map.get(key) != null ? map.get(key).toString() : "";
	}
	
	

}
