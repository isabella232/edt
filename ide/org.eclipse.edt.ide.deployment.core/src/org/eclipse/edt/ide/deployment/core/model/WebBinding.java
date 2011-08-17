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
package org.eclipse.edt.ide.deployment.core.model;

public class WebBinding extends Binding {

    private String _interface;
    private String wsdlLocation;
    private String wsdlPort;
    private String wsdlService;
    private String uri;
	private String soapVersion;
	private boolean enableGeneration;
    
    public WebBinding (String name,
    		           String _interface,
    		           String wsdlLocation,
    		           String wsdlPort,
    		           String wsdlService,
    		           String uri,
    		           String soapVersion,
    		           String enableGenerationString)
    {
    	super(name);
    	this.name = name;
    	this._interface = _interface;
    	this.wsdlLocation = wsdlLocation;
    	this.wsdlPort = wsdlPort;
    	this.wsdlService = wsdlService;
    	this.uri = uri;
		if (enableGenerationString != null && enableGenerationString.equalsIgnoreCase("true"))
		{
			enableGeneration = true;
		}
		else
		{
			enableGeneration = false;
		}
		if (soapVersion != null )
		{
			this.soapVersion = soapVersion;
		}
		else
		{
			this.soapVersion = SOAP11VERSION;
		}
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
	
	public int getBindingType()
	{
		return WEBBINDING;
	}
	
    public String getSoapVersion() {
		return soapVersion;
	}
	
	public String toBindXML(String indent){
		StringBuffer buf = new StringBuffer();
		buf.append(indent + "<webBinding"); 
		if (name != null)
		{
			buf.append(" name=\"" + name + "\"");
		}
		if (_interface != null)
		{
			buf.append(" interface=\"" + _interface + "\"");
		}
		if (wsdlLocation != null)
		{
			buf.append(" wsdlLocation=\"" + wsdlLocation + "\"");
		}
		if (wsdlPort != null)
		{
			buf.append(" wsdlPort=\"" + wsdlPort + "\"");
		}
		if (wsdlService != null)
		{
			buf.append(" wsdlService=\"" + wsdlService + "\"");
		}
		if (uri != null)
		{
			buf.append(" uri=\"" + uri + "\"");
		}
		if (soapVersion != null)
		{
			buf.append(" soapVersion=\"" + soapVersion + "\"");
		}
		buf.append("/>\n");

		return buf.toString();
	}

	public boolean isEnableGeneration() {
		return enableGeneration;
	}
}
