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

import java.util.ArrayList;
import java.util.List;

public class Webservice {
	
	String implementation;
	String style;
	String protocol;
	String transaction;
	String uri;
	String userID;
	String wsdlFile;
	String wsdlService;
	String wsdlPort;
	String soapVersion;
	boolean enableGeneration;
	boolean useExistingWSDL;
	List parameters;
	
	public Webservice(String implementation, String style, String protocol, String transaction, 
			String uri, String userID, String useExistingWSDL, String wsdlFile, String wsdlService,
			String wsdlPort, String soapVersion, String enableGenerationString)
	{
		this.implementation = implementation;
		this.style = style;
		this.protocol = protocol;
		this.transaction = transaction;
		this.uri = uri;
		this.userID = userID;
		this.wsdlFile = wsdlFile;
		this.wsdlService = wsdlService;
		this.wsdlPort = wsdlPort;
		this.soapVersion = soapVersion;
		this.parameters = new ArrayList();
		
		if (useExistingWSDL != null && useExistingWSDL.equalsIgnoreCase("true"))
		{
			this.useExistingWSDL = true;
		}
		else
		{
			this.useExistingWSDL = false;
		}
		if (enableGenerationString != null && enableGenerationString.equalsIgnoreCase("true"))
		{
			enableGeneration = true;
		}
		else
		{
			enableGeneration = false;
		}
	}

	public String getSoapVersion() {
		return soapVersion;
	}

	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public boolean isEnableGeneration() {
		return enableGeneration;
	}

	public void setEnableGeneration(boolean enableGeneration) {
		this.enableGeneration = enableGeneration;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setTransaction(String t) {
		transaction = t;
	}
	
	public String getTransaction() {
		return transaction;
	}

	public String getUri() {
		return uri;
	}

	public void setUserID(String id) {
		userID = id;
	}
	
	public String getUserID() {
		return userID;
	}

	public String getWsdlFile()
	{
		return wsdlFile;
	}

	public String getWsdlPort()
	{
		return wsdlPort;
	}

	public String getWsdlService()
	{
		return wsdlService;
	}

	public boolean useExistingWSDL()
	{
		return useExistingWSDL;
	}
	
	public void addParameter(Parameter param)
	{
		parameters.add(param);
	}
	
	public List getParameters()
	{
		return parameters;
	}
}
