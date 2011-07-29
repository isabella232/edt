/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.core.java;

import org.eclipse.edt.debug.core.IEGLValue;
import org.eclipse.jdt.debug.core.IJavaValue;

public interface IEGLJavaValue extends IEGLValue, IEGLJavaDebugElement
{
	/**
	 * @return the underlying value.
	 */
	public IJavaValue getJavaValue();
	
	/**
	 * @return a detail string for this value, or null if JDT's implementation for the underlying Java value should be used.
	 */
	public String computeDetail();
	
	/**
	 * @return the variable to which this value belongs, possibly null.
	 */
	public IEGLJavaVariable getParentVariable();
}
