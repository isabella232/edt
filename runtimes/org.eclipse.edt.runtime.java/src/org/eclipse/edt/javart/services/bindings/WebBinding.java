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

public class WebBinding extends Binding {

	private static final long serialVersionUID = 70L;
	
    private String _interface;
    private String wsdlLocation;
    private String wsdlPort;
    private String wsdlService;
    private String uri;
    private String soapLevel;

    public static final String SOAP11 = "SOAP11";
    public static final String SOAP12 = "SOAP12";
    
    public WebBinding (String name,
	           String _interface,
	           String wsdlLocation,
	           String wsdlPort,
	           String wsdlService,
	           String uri)
    {
    	this(name, _interface, wsdlLocation, wsdlPort,  wsdlService, uri, SOAP11);
    }

    public WebBinding (String name,
    		           String _interface,
    		           String wsdlLocation,
    		           String wsdlPort,
    		           String wsdlService,
    		           String uri,
    		           String soapLevel)
    {
    	super(name);
    	this.name = name;
    	this._interface = _interface;
    	this.wsdlLocation = wsdlLocation;
    	this.wsdlPort = wsdlPort;
    	this.wsdlService = wsdlService;
		this.uri = uri;
    	this.soapLevel = soapLevel;
    }

	public String getInterface() {
		return _interface;
	}

	public void setInterface(String _interface) {
		this._interface = _interface;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getWsdlLocation() {
		return wsdlLocation;
	}

	public void setWsdlLocation(String wsdlLocation) {
		this.wsdlLocation = wsdlLocation;
	}

	public String getWsdlPort() {
		return wsdlPort;
	}

	public void setWsdlPort(String wsdlPort) {
		this.wsdlPort = wsdlPort;
	}

	public String getWsdlService() {
		return wsdlService;
	}

	public void setWsdlService(String wsdlService) {
		this.wsdlService = wsdlService;
	}
	
	public String getSOAPLevel() {
		return this. soapLevel;
	}

	public void setSOAPLevel(String soapLevel) {
		this.soapLevel = soapLevel;
	}
	
	public int getBindingType()
	{
		return WEBBINDING;
	}
	}
