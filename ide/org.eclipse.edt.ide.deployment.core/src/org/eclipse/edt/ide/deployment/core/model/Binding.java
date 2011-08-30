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

public class Binding {
	
	public static int EGLBINDING = 1;
	public static int WEBBINDING = 2;
	public static int NATIVEBINDING = 3;
	public static int RESTBINDING = 4;
	public static int SQLDATABASEBINDING = 5;
	
	public static String SOAP11VERSION = "SOAP-1.1";
	public static String SOAP12VERSION = "SOAP-1.2";
	
	protected String name;
	
	public Binding(String name)
	{
		this.name = name;
	}
	
	public int getBindingType()
	{
		return 0;
	}
	
	public String toBindXML(String indent)
	{
		return indent + "<binding/>\n";
	}

	public String getName() {
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}
}
