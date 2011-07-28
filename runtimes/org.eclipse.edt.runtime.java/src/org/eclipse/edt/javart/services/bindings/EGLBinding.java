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

public class EGLBinding extends Binding {

	private static final long serialVersionUID = 70L;
	
	private String serviceName;
	private String alias;
	private Protocol protocol;
	
	public EGLBinding(String name, String serviceName, String alias)
	{
		super(name);
		this.name = name;
		this.serviceName = serviceName;
		this.alias = alias;
	}

	public EGLBinding(String name, String serviceName, String alias, Protocol protocol)
	{
		super(name);
		this.name = name;
		this.serviceName = serviceName;
		this.alias = alias;
		this.protocol = protocol;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
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
	
}
