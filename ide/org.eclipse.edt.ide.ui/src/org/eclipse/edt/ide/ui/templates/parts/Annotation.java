/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import java.util.LinkedHashMap;

public class Annotation extends Element {
	String name;
	LinkedHashMap<String,Object> fields = new LinkedHashMap<String,Object>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void setValue(Object obj) {
		fields.put("value", obj);
	}
	
	public void addField(String fieldName, Object value) {
		fields.put(fieldName, value);
	}
	
	public String toString() {
		if (fields.keySet().size() == 1 && ("value".equalsIgnoreCase(((String)fields.keySet().toArray()[0])))) {
			return name + " = " + fields.values().toArray()[0].toString();
		}
		
		StringBuffer buff = new StringBuffer();
		buff.append("@" + name + "{");
		boolean first = true;
		
		for(String key : fields.keySet()) {
			if (first) {
				first = false;
			} else {
				buff.append(", ");
			}
			Object value = fields.get(key);
			buff.append(key + " = " + value.toString());
		}
		
		buff.append ("}");
		return buff.toString();
	}
}
