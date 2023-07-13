package com.tigo.ea.eir.provider.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.NodeList;

public class DefaultSoapHandler implements SOAPHandler<SOAPMessageContext> {

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		System.out.println("Server::WebService::handleMessage()");
		Boolean isOutbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		// only inbound message from client
		if (!isOutbound) {
			System.out.println("Mensajes entrantes ...");
			AppContextUtil.setContext(context);
		} else {
			System.out.println("Mensajes salientes ...");
			/*
			try {
				createParametrosSalida(context, "99", "Error al actualizar Asset en Siebel", "Post_ActualizarAsset_V.1.0_Output");
			} catch (SOAPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		} 
		System.out.println(printSOAPMessage(context));
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		System.out.println("Server::WebService::handleFault()");
		return true;
	}

	@Override
	public void close(MessageContext context) {
		System.out.println("Server::WebService::close()");		
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	private String printSOAPMessage(SOAPMessageContext messageContext) {
		try {
			Boolean isOutbound = (Boolean) messageContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			System.out.println(isOutbound ? "SOAP Response" : "SOAP Request");
			//Test print trace Xml
			SOAPMessage msg = messageContext.getMessage();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			msg.writeTo(out);
			String strMsg = new String(out.toByteArray());
			return strMsg;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}	
	
	private void alterEnvelope(MessageContext messageContext) {
		SOAPMessage message = ((SOAPMessageContext) messageContext)
				.getMessage();
		SOAPBody body = null;
		try {
			SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
			envelope.removeNamespaceDeclaration("S");
			envelope.addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
			envelope.setPrefix("soap");
			envelope.getHeader().setPrefix("soap");
			envelope.getBody().setPrefix("soap");			
			message.saveChanges();
		} catch (SOAPException e) {
			System.out.println("Error en remover nodo S de enveleope response: ");
			e.printStackTrace();
		}
	}	

	private void alterBody(MessageContext messageContext, String nodeToRemove) {
		SOAPMessage message = ((SOAPMessageContext) messageContext)
				.getMessage();
		String operationName = "";
		SOAPBody body = null;
		try {
			body = message.getSOAPPart().getEnvelope().getBody();

			SOAPElement operation = ((SOAPElement) body.getChildElements()
					.next());
			operationName = operation.getElementName().toString();
			System.out.println("operationName: " + operationName);
			operationName = operation.getLocalName().toString();

			NodeList nl = body.getChildNodes();
			List<Node> nodesToRemove = new ArrayList<Node>();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = (Node) nl.item(i);
				String nodeName = node.getLocalName();
				if (nodeName.equals(operationName)
						&& nodeName.equals(nodeToRemove)) {
					nodesToRemove.add(node);
					System.out.println("Nodo marcado para eliminar: "
							+ nodeName);
				}
			}
			// Remove node
			for (Node node : nodesToRemove) {
				node.getParentNode().removeChild(node);
			}
			body.normalize();
			message.saveChanges();
		} catch (SOAPException e) {
			System.out.println("Error en remover nodo de response: ");
			e.printStackTrace();
		}

	}

	private void attachSoapFault(SOAPMessageContext soapMessageContext,
			String code, String description) {
		try {
			SOAPMessage soapMsg = soapMessageContext.getMessage();
			SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
			SOAPHeader soapHeader = soapEnv.getHeader();

			System.setProperty(
					"com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace",
					"true");
			System.setProperty(
					"com.sun.xml.ws.fault.SOAPFaultBuilder.captureStackTrace",
					"false");
			// System.setProperty("com.sun.xml.ws.fault.SOAPFaultBuilder.enableCaptureStackTrace","false");
			System.out
					.println("disableCaptureStackTrace: "
							+ System.getProperty("com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace"));
			System.out
					.println("captureStackTrace: "
							+ System.getProperty("com.sun.xml.ws.fault.SOAPFaultBuilder.captureStackTrace"));
			System.out
					.println("enableCaptureStackTrace: "
							+ System.getProperty("com.sun.xml.ws.fault.SOAPFaultBuilder.enableCaptureStackTrace"));

			// Add an soapFault
			SOAPBody soapBody = soapEnv.getBody();
			SOAPFault soapFault = soapBody.addFault();

			// QName faultName = new
			// QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Client");
			QName faultName = new QName(SOAPConstants.DYNAMIC_SOAP_PROTOCOL,
					"Client");
			soapFault.setFaultCode(faultName);
			soapFault.setFaultString("Exception SOAPFault generada...");

			Detail detail = soapFault.addDetail();
			// Create items of SoapFaultException
			QName errorCode = new QName("ErrorCode");
			DetailEntry entry = detail.addDetailEntry(errorCode);
			entry.addTextNode(code);
			QName errorDescription = new QName("ErrorDescription");
			DetailEntry entry2 = detail.addDetailEntry(errorDescription);
			entry2.addTextNode(description);

			if (soapBody.hasFault()) {
				SOAPFault newFault = soapBody.getFault();
				QName codigo = newFault.getFaultCodeAsQName();
				String string = newFault.getFaultString();
				String actor = newFault.getFaultActor();

				Detail newDetail = newFault.getDetail();
				if (newDetail != null) {
					@SuppressWarnings("rawtypes")
					Iterator entries = newDetail.getDetailEntries();
					StringBuilder sb = new StringBuilder();
					while (entries.hasNext()) {
						DetailEntry newEntry = (DetailEntry) entries.next();
						sb.append("<" + newEntry.getLocalName() + ">"
								+ newEntry.getValue() + "</"
								+ newEntry.getLocalName() + ">");
						System.out.println("<" + newEntry.getLocalName() + ">"
								+ newEntry.getValue() + "</"
								+ newEntry.getLocalName() + ">");
					}
					soapFault.setFaultString(sb.toString());
				}
			}

			soapMsg.writeTo(System.out);
			throw new SOAPFaultException(soapEnv.getBody().getFault());
		} catch (SOAPException e) {
			System.err.println("Error SOAPException => " + e);
		} catch (IOException e) {
			System.err.println("Error IOException => " + e);
		}
	}

	private void addHeaderSecurity(SOAPMessageContext soapMessageContext){
		System.out.println("Creando proxy en Context...");
		try {
			SOAPMessageContext smc = (SOAPMessageContext) soapMessageContext;
			SOAPMessage msg = smc.getMessage();
			SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
			SOAPHeader header = envelope.getHeader();

			String NAMESPACE_ECB = "wsse";
			String NAMESPACE_ECB_STRING = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

			// authentication
			header.addNamespaceDeclaration(NAMESPACE_ECB, NAMESPACE_ECB_STRING);
			SOAPHeaderElement headerElem = header
					.addHeaderElement(envelope.createName("Security",
							NAMESPACE_ECB, NAMESPACE_ECB_STRING));
			headerElem
					.setAttribute(
							"xmlns:wsu",
							"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

			SOAPElement usernameToken = headerElem.addChildElement(
					"UsernameToken", "wsse");
			usernameToken.setAttribute("wsu:Id", "UsernameToken-2");

			String username = "msuscripcionespv";
			SOAPElement elem = usernameToken
					.addChildElement(envelope.createName("Username",
							NAMESPACE_ECB, NAMESPACE_ECB_STRING));
			elem.addTextNode(username);
			System.out.println("Adding username [" + username
					+ "] to SOAP header authentication");

			String password = "KuYbRW3F7ck@";
			elem = usernameToken.addChildElement(envelope.createName(
					"Password", NAMESPACE_ECB, NAMESPACE_ECB_STRING));
			elem.addTextNode(password);
			System.out.println("Adding password [" + password
					+ "] to SOAP header authentication");

			//msg.writeTo(System.out);
			//System.out.println("");

		} catch (SOAPException e) {
			e.printStackTrace();
		//} catch (IOException e) {
		//	e.printStackTrace();
		}
	}
	
	private SOAPHeader addHeaderSecurityNew(){
		System.out.println("Creando proxy en Context...");
		try {			
			MessageFactory factory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
			SOAPMessage msg = factory.createMessage();
			SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
			SOAPHeader header = envelope.getHeader();

			String NAMESPACE_ECB = "wsse";
			String NAMESPACE_ECB_STRING = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";

			// authentication
			header.addNamespaceDeclaration(NAMESPACE_ECB, NAMESPACE_ECB_STRING);
			SOAPHeaderElement headerElem = header
					.addHeaderElement(envelope.createName("Security",
							NAMESPACE_ECB, NAMESPACE_ECB_STRING));
			headerElem
					.setAttribute(
							"xmlns:wsu",
							"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

			SOAPElement usernameToken = headerElem.addChildElement(
					"UsernameToken", "wsse");
			usernameToken.setAttribute("wsu:Id", "UsernameToken-2");

			String username = "msuscripcionespv";
			SOAPElement elem = usernameToken
					.addChildElement(envelope.createName("Username",
							NAMESPACE_ECB, NAMESPACE_ECB_STRING));
			elem.addTextNode(username);
			System.out.println("Adding username [" + username
					+ "] to SOAP header authentication");

			String password = "KuYbRW3F7ck@";
			elem = usernameToken.addChildElement(envelope.createName(
					"Password", NAMESPACE_ECB, NAMESPACE_ECB_STRING));
			elem.addTextNode(password);
			System.out.println("Adding password [" + password
					+ "] to SOAP header authentication");

			//msg.writeTo(System.out);
			//System.out.println("");
			return header;
		} catch (SOAPException e) {
			e.printStackTrace();
		//} catch (IOException e) {
		//	e.printStackTrace();
		}
		return null;
	}	

	private void attachErrorMessage(SOAPMessage errorMessage, String cause){
		try {
			SOAPBody soapBody = errorMessage.getSOAPPart().getEnvelope().getBody();
			SOAPFault soapFault = soapBody.addFault();
			soapFault.setFaultString(cause);
			throw new SOAPFaultException(soapFault);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
		
	private void createParametrosSalida(SOAPMessageContext soapMessageContext, String codeResponse, String response, String nodeToRemove) throws SOAPException{
		// SOAP 1.1
		SOAPMessageContext smc = (SOAPMessageContext) soapMessageContext;
		SOAPMessage msg = smc.getMessage();
		SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
		SOAPBody body = envelope.getBody();
		
		SOAPElement operation = ((SOAPElement) body.getChildElements().next());
		String operationName = operation.getLocalName().toString();
		System.out.println("operationName (LocalName): " + operationName);

		NodeList listNodeRoot = body.getChildNodes();
		List<Node> nodesToRemove = new ArrayList<Node>();
		int errorCode = 0 ;		
		for (int i = 0; i < listNodeRoot.getLength(); i++) {
			Node nodeRoot = (Node) listNodeRoot.item(i);
			NodeList listNodeChild = nodeRoot.getChildNodes();
			for (int j = 0; j < listNodeChild.getLength(); j++) {
				Node nodeChild = (Node) listNodeChild.item(j);
				String nodeNameChild = nodeChild.getLocalName();
				if (nodeNameChild!=null && nodeNameChild.equals("Error_spcCode")){
					if (nodeChild.getTextContent()!=null && !nodeChild.getTextContent().isEmpty()){
						errorCode = getErrorCode(nodeChild.getTextContent());					
						System.out.println("Valor de " + nodeChild + ":" + errorCode);						
					}
					if (errorCode>0) break;					
				}
			}
			String nodeNameRoot = nodeRoot.getLocalName();
			if (nodeNameRoot.equals(operationName) && nodeNameRoot.equals(nodeToRemove) && errorCode>0) {
				nodesToRemove.add(nodeRoot);
				System.out.println("Nodo marcado para eliminar: "
						+ nodeNameRoot);
			}
		}
		// Remove node Post_ActualizarAsset_V.1.0_Output
		for (Node node : nodesToRemove) {
			node.getParentNode().removeChild(node);
		}
		// Crear estructura ParametrosSalida
		if (errorCode>0){
			SOAPBodyElement bodyElement = body.addBodyElement(envelope.createName("ParametrosSalida"));
			SOAPElement elem = bodyElement.addChildElement(envelope.createName("codRespuesta"));
			elem.addTextNode(codeResponse);
			elem = bodyElement.addChildElement(envelope.createName("respuesta"));
			elem.addTextNode(response);	
			elem = bodyElement.addChildElement(envelope.createName("anexo"));
			elem.addTextNode("");	
			body.normalize();
			msg.saveChanges();			
		}else{
			alterResponseBody(soapMessageContext);
		}
	}
	
	private int getErrorCode(String number){
		int result = 0;
		try {
			result = Integer.parseInt(number);			
		} catch (NumberFormatException e) {
			System.out.println("Error parsing serviceCode ActualizarAsset: ");
			e.printStackTrace();
		}
		return result;
	}
	
	private void alterResponseBody(SOAPMessageContext soapMessageContext) throws SOAPException{
		String nodeName1 = "Post_ActualizarAsset_V.1.0_Output";
		String nameSpaceRemoveNode1 = "http://siebel.com/CustomUI";
		String nameSpace2RemoveNode1 = "http://www.siebel.com/xml/Post_CrearAsset_V1";
				
		SOAPMessageContext smc = (SOAPMessageContext) soapMessageContext;
		SOAPMessage msg = smc.getMessage();
		SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
		SOAPBody body = envelope.getBody();
				
		SOAPElement objectResponse = ((SOAPElement) body.getChildElements().next());
		String nameObjectResponse = objectResponse.getLocalName().toString();
		System.out.println("objectResponse (LocalName): " + nameObjectResponse);		
		if (nodeName1.equals(nameObjectResponse)){
			//Elimina nameSpace <Post_ActualizarAsset_V.1.0_Output xmlns:ns0="http://www.siebel.com/xml/Post_CrearAsset_V1">
			@SuppressWarnings("rawtypes")
			Iterator iterator = objectResponse.getAllAttributesAsQNames();
			while (iterator.hasNext()){
				QName attributeName = (QName) iterator.next();
				if (objectResponse.getAttributeValue(attributeName).equals(nameSpace2RemoveNode1)){										                                            
					System.out.println("Attribute name is " + attributeName.toString());
					System.out.println("Attribute value is " + objectResponse.getAttributeValue(attributeName));
					boolean ok = objectResponse.removeAttribute(attributeName);
					System.out.println("Attributo eliminado: " + ok);
					
				}
			}			
		}		
		body.normalize();
		msg.saveChanges();		
	}	
}
