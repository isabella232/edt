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
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
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
	public EGLJavaFunctionContainerVariable( IDebugTarget target, IJavaVariable javaVariable, IEGLJavaStackFrame frame ) throws DebugException
	{
		super( target, javaVariable,
				new SMAPVariableInfo( getDisplayName( javaVariable ), javaVariable.getReferenceTypeName(), "", -1, null ), frame, null ); //$NON-NLS-1$
	}
	
	private static String getDisplayName( IJavaVariable variable ) throws DebugException
	{
		// The SMAP will have the full file path 'pkg/foo.egl' - turn that into 'pkg.foo'.
		try
		{
			if ( variable.getJavaType() instanceof IJavaReferenceType )
			{
				String[] paths = ((IJavaReferenceType)variable.getJavaType()).getSourcePaths( IEGLDebugCoreConstants.EGL_STRATUM );
				if ( paths.length > 0 )
				{
					String path = paths[ 0 ];
					int lastDot = path.lastIndexOf( '.' );
					if ( lastDot != -1 )
					{
						path = path.substring( 0, lastDot );
					}
					return path.replace( '/', '.' );
				}
			}
		}
		catch ( DebugException e )
		{
		}
		return variable.getReferenceTypeName();
	}
	
	@Override
	protected IEGLJavaValue createEGLValue( IJavaValue javaValue )
	{
		return new EGLJavaFunctionContainerValue( getDebugTarget(), javaValue, this );
	}
	
	@Override
	public boolean isLocal()
	{
		return false;
	}
}
