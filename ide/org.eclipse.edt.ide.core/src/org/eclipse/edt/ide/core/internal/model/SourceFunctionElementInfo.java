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
package org.eclipse.edt.ide.core.internal.model;

import org.eclipse.edt.ide.core.model.Signature;


/** 
 * Element info for IFunction elements. 
 */
public class SourceFunctionElementInfo extends SourcePartElementInfo {
	
	public static final int UNKNOWN_TYPE = 0;
	public static final int PRIMITIVE_TYPE = 1;
	public static final int DATAITEM_TYPE = 2;
	public static final int RECORD_TYPE = 3;

	/**
	 * For a source function (that is, a function contained in a compilation unit)
	 * this is a collection of the names of the parameters for this function,
	 * in the order the parameters are delcared. For a binary function (that is, 
	 * a function declared in a binary type), these names are invented as
	 * "arg"i where i starts at 1. This is an empty array if this function
	 * has no parameters.
	 */
	protected char[][] argumentNames;

	/**
	 * Collection of type names for the arguments in this
	 * function, in the order they are declared. This is an empty
	 * array for a function with no arguments. (A name is a simple
	 * name or a qualified, dot separated name. ???)
	 * For example, Hashtable or java.util.Hashtable.
	 */
	protected char[][] argumentTypeNames;
	
	/**
	 * Collection of type identifier for the arguments in this
	 * function, in the order they are declared. This is an empty
	 * array for a function with no arguments. 
	 * e.g., PRIMITIVE, RECORD, DATAITEM, 
	 */
	protected int[] argumentTypeIndentify;

	/**
	 * Return type name for this function. The return type of
	 * constructors is equivalent to void.
	 */
	protected char[] returnType;
	
	/**
	 * Return the package for where the return type is defined.
	 * If primitive type, equals to null; if the default package, equals to empty string; otherwise, equals to the
	 * package name 
	 */
	protected char[] returnTypePkg;

	/**
	 * A collection of type names of the exceptions this
	 * function throws, or an empty collection if this function
	 * does not declare to throw any exceptions. A name is a simple
	 * name or a qualified, dot separated name.
	 * For example, Hashtable or java.util.Hashtable.
	 */
	protected char[][] exceptionTypes;
	
	/*
	 * in, out, inout
	 */
	protected char[][] useTypes;
	
	protected boolean[] areNullable;
	
	/*
	 * for non-primitive parameters
	 */
	protected char[][] argumentPackages;

	public char[][] getArgumentNames() {
		return this.argumentNames;
	}
	public char[][] getArgumentTypeNames() {
		return this.argumentTypeNames;
	}
	public char[] getReturnTypeName() {
		return this.returnType;
	}
	public char[] getSelector() {
		return this.name;
	}
	protected String getSignature() {
	
		String[] paramSignatures = new String[this.argumentTypeNames.length];
		for (int i = 0; i < this.argumentTypeNames.length; ++i) {
			paramSignatures[i] = Signature.createTypeSignature(this.argumentTypeNames[i], false);
		}
		return Signature.createFunctionSignature(paramSignatures, Signature.createTypeSignature(this.returnType, false));
	}
	protected void setArgumentNames(char[][] names) {
		this.argumentNames = names;
	}
	protected void setArgumentTypeNames(char[][] types) {
		this.argumentTypeNames = types;
	}
	protected void setReturnType(char[] type) {
		this.returnType = type;
	}
	public char[][] getUseTypes() {
		return useTypes;
	}
	public void setUseTypes(char[][] useTypes) {
		this.useTypes = useTypes;
	}
	public boolean[] getAreNullable() {
		return areNullable;
	}
	public void setNullable(boolean[] areNullable) {
		this.areNullable = areNullable;
	}
	public char[][] getArgumentPackages() {
		return argumentPackages;
	}
	public void setArgumentPackages(char[][] argumentPackages) {
		this.argumentPackages = argumentPackages;
	}
	public int[] getArgumentTypeIndentify() {
		return argumentTypeIndentify;
	}
	public void setArgumentTypeIndentify(int[] argumentTypeIndentify) {
		this.argumentTypeIndentify = argumentTypeIndentify;
	}
	public char[] getReturnTypePkg() {
		return returnTypePkg;
	}
	public void setReturnTypePkg(char[] returnTypePkg) {
		this.returnTypePkg = returnTypePkg;
	}
	
	
}
