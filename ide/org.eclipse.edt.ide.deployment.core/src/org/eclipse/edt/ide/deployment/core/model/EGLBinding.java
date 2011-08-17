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

public class EGLBinding extends Binding {

	private String serviceName;
	private String alias;
	
	public EGLBinding(String name, String serviceName, String alias)
	{
		super(name);
		this.name = name;
		this.serviceName = serviceName;
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public int getBindingType()
	{
		return EGLBINDING;
	}
	
	
	public String toBindXML(String indent){
		StringBuffer buf = new StringBuffer();
		buf.append(indent + "<eglBinding"); 
		if (name != null)
		{
			buf.append(" name=\"" + name + "\"");
		}
		if (serviceName != null)
		{
			buf.append(" serviceName=\"" + serviceName + "\"");
		}
		if (alias != null)
		{
			buf.append(" alias=\"" + alias + "\"");
		}
		buf.append(">\n");
		buf.append(indent + "</eglBinding>\n");
		return buf.toString();
	}
}
