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

public class RestBinding extends Binding {

    private String baseURI;
    private String sessionCookieId;
    private boolean preserveRequestHeaders;
    private boolean enableGeneration;
    
    public RestBinding (String name,
    		           String baseURI,
    		           String sessionCookieId,
    		           String preserveRequestHeadersString,
    		           String enableGenerationString)
    {
    	super(name);
    	this.name = name;
    	this.baseURI = baseURI;
    	this.sessionCookieId = sessionCookieId;
		if (preserveRequestHeadersString != null && preserveRequestHeadersString.equalsIgnoreCase("true"))
		{
			preserveRequestHeaders = true;
		}
		else
		{
			preserveRequestHeaders = false;
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

	public int getBindingType()
	{
		return RESTBINDING;
	}
	
	
	public String toBindXML(String indent){
		StringBuffer buf = new StringBuffer();
		buf.append(indent + "<restBinding"); 
		if (name != null)
		{
			buf.append(" name=\"" + name + "\"");
		}
		if (baseURI != null)
		{
			buf.append(" baseURI=\"" + baseURI + "\"");
		}
		if (sessionCookieId != null)
		{
			buf.append(" sessionCookieId=\"" + sessionCookieId + "\"");
		}
		if (preserveRequestHeaders)
			buf.append(" preserveRequestHeaders=\"true\"");
		else
			buf.append(" preserveRequestHeaders=\"false\"");
		
		buf.append("/>\n");

		return buf.toString();
	}

	public boolean isEnableGeneration() {
		return enableGeneration;
	}

	public String getBaseURI() {
		return baseURI;
	}

	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}

	public String getSessionCookieId() {
		return sessionCookieId;
	}

	public void setSessionCookieId(String sessionCookieId) {
		this.sessionCookieId = sessionCookieId;
	}

	public boolean isPreserveRequestHeaders() {
		return preserveRequestHeaders;
	}

	public void setPreserveRequestHeaders(boolean preserveRequestHeaders) {
		this.preserveRequestHeaders = preserveRequestHeaders;
	}

	public void setEnableGeneration(boolean enableGeneration) {
		this.enableGeneration = enableGeneration;
	}
}
