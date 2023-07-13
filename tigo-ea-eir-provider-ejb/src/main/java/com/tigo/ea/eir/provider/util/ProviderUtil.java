package com.tigo.ea.eir.provider.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigo.ea.esb.provider.consumer.ProviderHelper;
import com.tigo.ea.esb.provider.core.ProviderRequest;
import com.tigo.ea.esb.provider.core.ProviderResponse;
import com.tigo.ea.esb.provider.service.Service;
import com.tigo.ea.esb.provider.service.ServiceException;
import com.tigo.ea.util.configuration.ConfigurationServiceFacade;
import com.tigo.ea.util.dto.GenericDto;
import com.tigo.ea.util.log.LoggingServiceFacade;

@Component
public class ProviderUtil {

	@Autowired
	public LoggingServiceFacade loggingServiceFacade;
	@Autowired
	public ConfigurationServiceFacade configurationServiceFacade;
	@Autowired
	public Environment environment;
	@Autowired
	public ObjectMapper objectMapper;
	@Autowired
	public ProviderHelper providerHelper;
	@Autowired
	public ExecuteOwnService executeOwnService;

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
			if (object != null)
				return object.toString();
		} catch (Exception e) {

		}
		return "";
	}

	public String jsonString(Object object) {

		try {
			return this.marshalPretty(objectMapper, object);
		} catch (Exception e) {
			return String.valueOf(object);
		}
	}

	public String jsonS2tring(Object object) {

		try {

			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			return String.valueOf(object);
		}
	}

	@SuppressWarnings("unchecked")
	public String jsonListString(Object object) {

		try {

			List<Object> objectList = (List<Object>) object;
			return this.marshalList(objectMapper, objectList);
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

	@SuppressWarnings("rawtypes")
	public SOAPElement getSoapElement(SOAPElement elements, String node) {

		SOAPElement element = null;

		try {

			if (elements == null)
				return null;

			Iterator iter = elements.getChildElements();

			while (iter.hasNext()) {

				Object object = iter.next();

				if (object instanceof SOAPElement) {

					element = (SOAPElement) object;
					if (element.getLocalName().equals(node))
						return element;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	public List<SOAPElement> getSoapListElement(SOAPElement elements, String node) {

		List<SOAPElement> elementList = new ArrayList<SOAPElement>();
		SOAPElement element = null;

		try {

			if (elements == null)
				return null;

			Iterator iter = elements.getChildElements();

			while (iter.hasNext()) {

				Object object = iter.next();

				if (object instanceof SOAPElement) {

					element = (SOAPElement) object;
					if (element.getLocalName().equals(node))
						elementList.add(element);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return elementList;
	}

	@SuppressWarnings("rawtypes")
	public String getSoapText(SOAPElement elements, String node) {

		SOAPElement element = null;

		try {

			if (elements == null)
				return "";

			Iterator iter = elements.getChildElements();

			while (iter.hasNext()) {

				Object object = iter.next();

				if (object instanceof SOAPElement) {

					element = (SOAPElement) object;
					if (element.getLocalName().equals(node))
						return element.getTextContent();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public Date dateFormater(String date, String formater) {

		try {

			SimpleDateFormat format = new SimpleDateFormat(formater);
			return format.parse(date);
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map<String, Object>> orderByIdList(List<Map<String, Object>> data, final String key) {

		Collections.sort(data, new Comparator() {

			@Override
			public int compare(Object arg0, Object arg1) {

				Map<String, Object> dt1 = (Map<String, Object>) arg0;
				Map<String, Object> dt2 = (Map<String, Object>) arg1;
				return new Integer(Integer.valueOf(dt1.get(key).toString()))
						.compareTo(Integer.valueOf(dt2.get(key).toString()));
			}
		});

		return data;
	}

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

	public Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.setTimeZone(TimeZone.getDefault());
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

	public Calendar toCalendar(Date date) {
		Calendar calendar = null;
		if (date != null) {
			calendar = Calendar.getInstance();
			calendar.setTime(date);
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
	 * Validaciones integer. Valida campos corresponden a un valor Integer
	 * [positivo|negativo] dado como un String
	 */
	public boolean isIntegerNumber(String data) {
		String regex = "^-*[0-9]+$"; // uno o m�s d�gitos num�ricos enteros positivos o negativos
		Pattern queryLangPattern = Pattern.compile(regex);
		Matcher matcher = queryLangPattern.matcher(data);
		return matcher.matches();
	}

	/*****************
	 * Validaciones booleanos. Valida campos corresponden a un valor Booleano dado
	 * como un String
	 */
	public boolean isBoolean(String data) {
		String regex = "true|false";// true o false sin importar may�sculas o no
		Pattern queryLangPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = queryLangPattern.matcher(data);
		return matcher.matches();
	}

	/*****************
	 * CONVERSIONES XML Convierte Xml to List&lt;Object&gt;. => Ok m�todo
	 * funcional.
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> unmarshalList(Class<?> clazz, String xml) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(WrapperList.class, clazz);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		// StreamSource xml = new StreamSource(xmlLocation); //Si paso archivo
		// f�sico xml
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
		// StreamSource xml = new StreamSource(xmlLocation); //Si paso archivo
		// f�sico xml
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

	/************************************************************************************************************/
	/*****************
	 * CONVERSIONES JSON Convierte Json to List&lt;Object&gt;. => Ok m�todo
	 * funcional.
	 */
	public <T> List<T> unmarshalList(ObjectMapper objectMapper, Class<?> clazz, String json) throws IOException {
		JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
		List<T> objectDataList = null;
		try {
			objectDataList = objectMapper.readValue(json, javaType);
			return objectDataList;
		} catch (JsonParseException e) {
			throw new RuntimeException(e.getMessage());
		} catch (JsonMappingException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/*****************
	 * CONVERSIONES JSON Convierte List&lt;Object&gt; to Json. => Ok m�todo
	 * funcional
	 */
	public String marshalList(ObjectMapper objectMapper, List<?> list) throws JsonProcessingException {
		String jsonData = null;
		try {
			jsonData = objectMapper.writeValueAsString(list);
			return jsonData;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/*****************
	 * CONVERSIONES JSON Convierte Json to Object. => Ok m�todo funcional
	 * 
	 * @return
	 */
	public <T> T unmarshal(ObjectMapper objectMapper, String json, Class<T> clazz) throws IOException {
		T objectData = null;
		try {
			objectData = objectMapper.readValue(json, clazz);
			return objectData;
		} catch (JsonParseException e) {
			throw new RuntimeException(e.getMessage());
		} catch (JsonMappingException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/*****************
	 * CONVERSIONES JSON Convierte Object to Json. => Ok m�todo funcional
	 */
	public String marshal(ObjectMapper objectMapper, Object obj) throws JsonProcessingException {
		String jsonData = null;
		try {
			jsonData = objectMapper.writeValueAsString(obj);
			return jsonData;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/*****************
	 * CONVERSIONES JSON Convierte Object to Json Pretty. => Ok m�todo funcional
	 */
	public String marshalPretty(ObjectMapper objectMapper, Object obj) throws JsonProcessingException {
		String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);

		return jsonData;
	}

	public int getDateNumberByType(Date date, int typeDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(typeDate);
	}

	public String getFechaActual(String fecha) {
		SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
		Calendar calendar = Calendar.getInstance();

		Date dateObj = calendar.getTime();
		String formattedDate = dtf.format(dateObj);
		System.out.println(formattedDate);
		return formattedDate;
	}

}
