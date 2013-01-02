/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.core.java.variables;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.edt.debug.core.java.variables.VariableUtil;
import org.eclipse.edt.debug.internal.core.java.EGLJavaValue;
import org.eclipse.edt.debug.internal.core.java.EGLJavaVariable;
import org.eclipse.jdt.debug.core.IJavaArray;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * Adapts the EGL "bytes" type to the format used by EGL programmers.
 */
public class BytesVariable extends EGLJavaVariable
{
	public BytesVariable( IDebugTarget target, IJavaVariable javaVariable, SMAPVariableInfo variableInfo,
			IEGLJavaStackFrame frame, IEGLJavaValue parent )
	{
		super( target, javaVariable, variableInfo, frame, parent );
	}
	
	@Override
	protected IEGLJavaValue createEGLValue( IJavaValue javaValue )
	{
		return new EGLJavaValue( getDebugTarget(), javaValue, this ) {
			@Override
			public String getValueString() throws DebugException
			{
				if ( javaValue.isNull() )
				{
					return "null"; //$NON-NLS-1$
				}
				
				if ( javaValue instanceof IJavaArray )
				{
					IJavaValue[] elements = ((IJavaArray)javaValue).getValues();
					StringBuilder buf = new StringBuilder(100);
					if ( elements.length > 0 )
					{
						buf.append( "0x" ); //$NON-NLS-1$
						for ( IJavaValue next : elements )
						{
							String nextValue = Long.toHexString( Long.parseLong( next.getValueString() ) & 0xFF ).toUpperCase();
							if ( nextValue.length() % 2 != 0 )
							{
								buf.append( '0' );
							}
							buf.append( nextValue );
						}
					}
					return buf.toString();
				}
				
				// Should never get here.
				return javaValue.getValueString();
			}
			
			@Override
			public IVariable[] getVariables()
			{
				return VariableUtil.EMPTY_VARIABLES;
			}
			
			@Override
			public boolean hasVariables()
			{
				return false;
			}
			
			@Override
			public String computeDetail()
			{
				try
				{
					return getValueString();
				}
				catch ( DebugException e )
				{
					return e.getLocalizedMessage();
				}
			}
		};
	}
	
	@Override
	protected boolean shouldCheckJavaElementAdapter()
	{
		return false;
	}
}
