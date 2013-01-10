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
package org.eclipse.edt.mof.egl;

/**
 * A ParameterizedType is the root abstract class of a set of types
 * used to extend <code>ParameterizableType</code> declarations with
 * arguments specified by subclasses of this abstract type.  Typical example
 * in EGL are types like CHAR(10) and CHAR(25).  The base type of CHAR is an alias
 * for the egl ParameterizableType <code>egl.lang.AnyChar</code>.  The full type CHAR(10)
 * is an instance of SequenceType ( a subclass of ParameterizedType) with a length value of 10
 * and a reference to the parameterizable type <code>egl.lang.AnyChar</code>
 *
 */
public interface ParameterizedType extends Type {
	ParameterizableType getParameterizableType();
	
	void setParameterizableType(ParameterizableType value);
	
	public int getSizeInBytes();
	
	public int getMaxNumberOfParms();
	public int getMinNumberOfParms();
	
}
