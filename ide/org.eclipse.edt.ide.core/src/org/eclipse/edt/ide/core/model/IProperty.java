/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.model;

/**
 * @author twilson
 * created	Aug 28, 2003
 */
public interface IProperty extends IEGLElement,ISourceReference, ISourceManipulation {
	public String KEY_ALLOW_UNQUALIFIED_ITEM_REFERENCES = "allowUnqualifiedItemReferences"; //$NON-NLS-1$
	// TODO Handle all keys in common way
	public String KEY_CURSOR = "isCursor"; //$NON-NLS-1$
	public String KEY_NULLABLE = "nullable"; //$NON-NLS-1$
	public String KEY_DISPLAYNAME = "displayName"; //$NON-NLS-1$
	public String KEY_BOOLEAN = "boolean"; //$NON-NLS-1$
	public String KEY_ISDECIMALDIGIT = "isDecimalDigit"; //$NON-NLS-1$

	public int VALUE_TYPE_BOOLEAN = 0;
	public int VALUE_TYPE_INT = 1;
	public int VALUE_TYPE_STRING = 2;
	public int VALUE_TYPE_FLOAT = 3;
	public int VALUE_TYPE_INTARRAY = 4;
	public int VALUE_TYPE_STRINGARRAY = 5;
	public int VALUE_TYPE_FLOATARRAY = 6;
	public int VALUE_TYPE_STRINGARRAYARRAY = 7;
	public int VALUE_TYPE_STRINGPAIRS = 8;
	public int VALUE_TYPE_PARTREFERENCE = 9;
	public int VALUE_TYPE_VARREFERENCE = 10;

	public Object getValue() throws EGLModelException;
	public int getValueType() throws EGLModelException;
}
