/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.services.bindings;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;


public class BindXMLParser extends DefaultHandler implements Serializable {
	
	private static final long serialVersionUID = 70L;

	private RuntimeBindings bindings;

	private EGLBinding currentEGLBinding;

	private NativeBinding currentNativeBinding;

	public RuntimeBindings parse(RuntimeBindings bindings, String path)
			throws Exception {
		this.bindings = bindings;
		return parse(new InputSource(new FileReader(path)));
	}
	
	public RuntimeBindings parse(RuntimeBindings bindings, InputStream is)
	throws Exception {
		this.bindings = bindings;
		return parse(new InputSource(is));
	}

	private RuntimeBindings parse(InputSource source) throws IOException,
			SAXException {
		currentEGLBinding = null;
		XMLReader xr = XMLReaderFactory.createXMLReader();
		xr.setContentHandler(this);
		xr.setErrorHandler(this);
		xr.parse(source);

		return bindings;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
	    if (localName.equals("eglBinding")) {
			currentEGLBinding = new EGLBinding(attributes.getValue("name"), 
					                           attributes.getValue("serviceName"), 
					                           attributes.getValue("alias"));
			bindings.addBinding(currentEGLBinding);
		} 
		else if (localName.equals("nativeBinding")) {
			currentNativeBinding = new NativeBinding(attributes.getValue("name"));
			bindings.addBinding(currentNativeBinding);
		} 
		else if (localName.equals("webBinding")) {
			bindings.addBinding(new WebBinding(attributes.getValue("name"),
											  attributes.getValue("interface"),
											  attributes.getValue("wsdlLocation"),
											  attributes.getValue("wsdlPort"),
											  attributes.getValue("wsdlService"),
											  attributes.getValue("uri"),
											  attributes.getValue("SOAPLevel")));
		}
		else if (localName.equals("restBinding")) {
			bindings.addBinding(new RestBinding(attributes.getValue("name"),
											  attributes.getValue("baseURI"),
											  attributes.getValue("sessionCookieId")));
		}
		else if (localName.equals("protocol.ref")) {
			ProtocolRef protocol = new ProtocolRef(attributes.getValue("ref"));
			if (currentEGLBinding != null) {
				currentEGLBinding.setProtocol(protocol);
			}
			else if( currentNativeBinding != null )
			{
				currentNativeBinding.setProtocol(protocol);
			}
		}
		else if (localName.equals("protocol.local")) {
			ProtocolLOCAL protocol = new ProtocolLOCAL(attributes.getValue("name"));
			if (currentEGLBinding != null) {
				currentEGLBinding.setProtocol(protocol);
			}
			else {
				bindings.addProtocol(protocol);
			}
		}
		else if (localName.equals("protocol.cicseci")) {
			ProtocolCICSECI protocol = new ProtocolCICSECI(attributes.getValue("name"),
					                                       attributes.getValue("conversionTable"),
					                                       attributes.getValue("ctgLocation"),
					                                       attributes.getValue("ctgPort"),
					                                       attributes.getValue("location"),
					                                       attributes.getValue("serverID"));
			if (currentEGLBinding != null) {
				currentEGLBinding.setProtocol(protocol);
			}
			else {
				bindings.addProtocol(protocol);
			}
		}
		else if (localName.equals("protocol.cicsssl")) {
			ProtocolCICSSSL protocol = new ProtocolCICSSSL(attributes.getValue("name"),
					                                       attributes.getValue("conversionTable"),
					                                       attributes.getValue("ctgLocation"),
					                                       attributes.getValue("ctgPort"),
					                                       attributes.getValue("location"),
					                                       attributes.getValue("serverID"),
					                                       attributes.getValue("ctgKeyStore"),
					                                       attributes.getValue("ctgKeyStorePassword"));
			if (currentEGLBinding != null) {
				currentEGLBinding.setProtocol(protocol);
			}
			else {
				bindings.addProtocol(protocol);
			}
		}
		else if (localName.equals("protocol.cicsj2c")) {
			ProtocolCICSJ2C protocol = new ProtocolCICSJ2C(attributes.getValue("name"),
					                                       attributes.getValue("conversionTable"),
					                                       attributes.getValue("location"));
			if (currentEGLBinding != null) {
				currentEGLBinding.setProtocol(protocol);
			}
			else {
				bindings.addProtocol(protocol);
			}
		}
		else if (localName.equals("protocol.java400")) {
			ProtocolJAVA400 protocol = new ProtocolJAVA400(attributes.getValue("name"),
						attributes.getValue("library"),
						attributes.getValue("location"),
						attributes.getValue("conversionTable"),
						attributes.getValue("password"),
						attributes.getValue("userID") );
			if (currentEGLBinding != null) {
				currentEGLBinding.setProtocol(protocol);
			}
			else if(currentNativeBinding != null) {
				currentNativeBinding.setProtocol(protocol);
			}
			else {
				bindings.addProtocol(protocol);
			}
		}
		else if (localName.equals("protocol.tcpip")) {
			ProtocolTCPIP protocol = new ProtocolTCPIP(attributes.getValue("name"),
					                                       attributes.getValue("location"),
					                                       attributes.getValue("serverID"));
			if (currentEGLBinding != null) {
				currentEGLBinding.setProtocol(protocol);
			}
			else {
				bindings.addProtocol(protocol);
			}
		}
		else if (localName.equals("protocol.java400j2c")) {
			ProtocolJAVA400J2C protocol = new ProtocolJAVA400J2C(attributes.getValue("name"),
						attributes.getValue("libraries"),
						attributes.getValue("currentLibrary"),
						attributes.getValue("location"),
						attributes.getValue("conversionTable"),
						attributes.getValue("password"),
						attributes.getValue("userID") );
			if (currentEGLBinding != null) {
				currentEGLBinding.setProtocol(protocol);
			}
			else if(currentNativeBinding != null) {
				currentNativeBinding.setProtocol(protocol);
			}
			else {
				bindings.addProtocol(protocol);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (localName.equals("eglBinding")) {
			currentEGLBinding = null;
		}
		else if (localName.equals("nativeBinding")) {
			currentNativeBinding = null;
		}
	}
	
}
