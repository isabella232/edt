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
package org.eclipse.edt.debug.internal.core.java;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jdt.debug.core.IJavaValue;

/**
 * The value of a function container variable.
 */
public class EGLJavaFunctionContainerValue extends EGLJavaValue
{
	/**
	 * Constructor.
	 * 
	 * @param target The debug target.
	 * @param value The underlying Java value.
	 * @param parent The variable to which this value belongs.
	 */
	public EGLJavaFunctionContainerValue( IDebugTarget target, IJavaValue value, EGLJavaVariable parent )
	{
		super( target, value, parent );
	}
	
	@Override
	public String getValueString() throws DebugException
	{
		return ""; //$NON-NLS-1$
	}
	
	@Override
	public String computeDetail()
	{
		return ""; //$NON-NLS-1$
	}
}
