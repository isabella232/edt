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

import java.util.LinkedHashMap;

public abstract class Part extends Element {
	private String name;
	private String subType;
	
	private LinkedHashMap<String,Field> fields = new LinkedHashMap<String,Field>();

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSubType() {
		return subType;
	}
	
	public void setSubType(String subType) {
		this.subType = subType;
	}
	
	public abstract String getPartKind();
	
	public String getHeader() {
		if (subType == null) {
			return getPartKind() + " " + getName() + getAnnotationString();
		} else {
			return getPartKind() + " " + getName() + " type " + getSubType() + getAnnotationString();
		}
	}
	
	public void addField(Field field) {
		String fName = field.getName().toUpperCase().toLowerCase();
		Field oldField = (Field) fields.get(fName);
		
		if (oldField == null) {
			fields.put(field.getName().toUpperCase().toLowerCase(), field);
		} else {
			oldField.setType(oldField.getType().compareTo(field.getType()));
		}
	}
	
	public Field[] getFields() {
		return fields.values().toArray(new Field[fields.size()]);
	}

	public Field getField(String name) {
		return fields.get(name.toUpperCase().toLowerCase());
	}
}
