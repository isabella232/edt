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
package org.eclipse.edt.debug.internal.core.java;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * A variable that contains global variables.
 */
public class EGLJavaFunctionContainerVariable extends EGLJavaVariable
{
	/**
	 * Constructor.
	 * 
	 * @param target The debug target.
	 * @param javaVariable The underlying Java variable.
	 */
	public EGLJavaFunctionContainerVariable( IDebugTarget target, IJavaVariable javaVariable ) throws DebugException
	{
		super( target, javaVariable, new SMAPVariableInfo( javaVariable.getReferenceTypeName(),
				javaVariable.getReferenceTypeName(), "", -1 ) ); //$NON-NLS-1$
	}
	
	@Override
	public IValue getValue() throws DebugException
	{
		IValue javaValue = javaVariable.getValue();
		if ( javaValue instanceof IJavaValue )
		{
			if ( value == null || value.getJavaValue() != javaValue )
			{
				value = new EGLJavaFunctionContainerValue( getDebugTarget(), (IJavaValue)javaValue, this );
			}
			return value;
		}
		return javaValue;
	}
}
