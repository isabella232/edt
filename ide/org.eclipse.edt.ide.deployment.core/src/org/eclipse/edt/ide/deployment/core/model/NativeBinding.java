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

public class NativeBinding extends Binding {

	public NativeBinding(String name)
	{
		super(name);
		this.name = name;
	}

	public int getBindingType()
	{
		return NATIVEBINDING;
	}
	
	
	public String toBindXML(String indent){
		StringBuffer buf = new StringBuffer();
		buf.append(indent + "<nativeBinding"); 
		if (name != null)
		{
			buf.append(" name=\"" + name + "\"");
		}
		buf.append(">\n");
		buf.append(indent + "</nativeBinding>\n");
		return buf.toString();
	}
}
