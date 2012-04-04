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

public class ArrayType extends Type {
    Type elementType = null;
	
	public Type getElementType() {
		return elementType;
	}

	public void setElementType(Type elementType) {
		this.elementType = elementType;
	}

	public String getName() {
		return ((elementType==null)?"any":elementType.toString()) + "[]";
	}
	
	public boolean isNullable() {
		return false;
	}
	
	public boolean isReferenceType() {
		return true;
	}

	public Object clone() {
		ArrayType type = new ArrayType();
		type.elementType = this.elementType;
		type.name = this.name;
		type.isNullable = this.isNullable;
		type.setAnnotations(this.getAnnotations());
		return type;
	}
}
