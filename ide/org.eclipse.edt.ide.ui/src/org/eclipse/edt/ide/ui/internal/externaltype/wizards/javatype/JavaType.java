/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.externaltype.wizards.javatype;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JavaType {
	List<Constructor<?>> constructors;
	List<Field>  fields;
	List<Method> methods;
	int source;
	
	public static final int SelectedType = 0;
	public static final int SuperType = 1;
	public static final int ReferencedType = 2;
	
	public static final JavaType DUMMY_SUPER_JAVATYPE = new JavaType(SuperType);
	public static final JavaType DUMMY_REFERENCED_JAVATYPE = new JavaType(ReferencedType);

	public JavaType() {
		this(SelectedType);
	}
	
	public JavaType(int src) {
		constructors = new ArrayList<Constructor<?>>();
		fields = new ArrayList<Field>();
		methods = new ArrayList<Method>();
		source = src;
	}

	public List<Constructor<?>> getConstructors() {
		return constructors;
	}

	public void setConstructors(List<Constructor<?>> constructors) {
		this.constructors = constructors;
	}
	
	public void addConstructor(Constructor<?> ctr) {
		constructors.add(ctr);
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public void addField(Field fld) {
		fields.add(fld);
	}

	public List<Method> getMethods() {
		return methods;
	}

	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}
	
	public void addMehod(Method method) {
		methods.add(method);
	}
	
	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}
}
