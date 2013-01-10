/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.templates.parts;

public class DataItem extends Part {
	Type type;
	private static String eol = System.getProperty("line.separator", "\n");
	
	public String getPartKind() {
		return "DataItem";
	}
	
	public String getHeader() {
		return getPartKind() + " " + getName() + " " + getType() + "{}";
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		
		buff.append(getHeader());
		buff.append(eol);
		buff.append("end");
		
		return buff.toString();
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
