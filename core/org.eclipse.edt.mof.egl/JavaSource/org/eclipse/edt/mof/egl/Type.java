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
package org.eclipse.edt.mof.egl;

import org.eclipse.edt.mof.MofSerializable;

/**
 * <code>Type</code> is the abstract super type for all EGL types
 * All Types are either an instance of a <code>Classifier</code> directly or
 * is a type that is a combination of a Classifier and some type parameter.
 */
public interface Type extends Element, MofSerializable {
	public String EGL_KeyScheme = "egl";
	public String KeySchemeDelimiter = ":";
	public String TypeArgDelimiter = ";";
	public String PrimArgDelimiter = ":";
	public String NestedPartDelimiter = "#";
	public String PrimArgsStartDelimiter = "(";
	public String PrimArgsEndDelimiter = ")";
	public String TypeArgsStartDelimiter = "<";
	public String TypeArgsEndDelimiter = ">";
	public char NullableIndicator = '?';
	
	/**
	 * All Types have a base <code>Classifier</code> that defines the API to
	 * instances of the given type. 
	 * 
	 * @see Classifier
	 * @return Classifier
	 */
	public Classifier getClassifier();
	
	public Boolean equals(Type eglType);
	
	/**
	 * All Types have a type signature which is composed of the
	 * package qualified name of the base <code>Classifier</code>
	 * and extensions necessary to describe the given type parameters.
	 * The form of a type signature is governed by implementors of this method
	 * 
	 * @see Classifier.getTypeSignature()
	 * @see GenericType.getTypeSignature()
	 * @see ParameterizedType.getTypeSignature()
	 * 
	 * @return String
	 */
	public String getTypeSignature();
	
	/**
	 * A type that is meant to be implemented in the technology of the a given target platform
	 * is said to be a Native type.  This is as opposed to types that are defined by the user
	 * which itself defines the implementation of the type.
	 * @return
	 */
	public boolean isNativeType();
}
