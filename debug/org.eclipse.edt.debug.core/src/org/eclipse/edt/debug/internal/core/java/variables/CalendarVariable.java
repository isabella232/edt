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
package org.eclipse.edt.debug.internal.core.java.variables;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.EDTDebugCoreMessages;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.edt.debug.internal.core.java.EGLJavaValue;
import org.eclipse.edt.debug.internal.core.java.EGLJavaVariable;
import org.eclipse.edt.debug.internal.core.java.VariableUtil;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * Variable that shows the calendar formatted nicely.
 */
public class CalendarVariable extends EGLJavaVariable
{
	public CalendarVariable( IDebugTarget target, IJavaVariable javaVariable, SMAPVariableInfo variableInfo, IEGLJavaStackFrame frame,
			IEGLJavaValue parent )
	{
		super( target, javaVariable, variableInfo, frame, parent );
	}
	
	@Override
	protected IEGLJavaValue createEGLValue( IJavaValue javaValue )
	{
		return new EGLJavaValue( getDebugTarget(), (IJavaValue)javaValue, this ) {
			@Override
			public String getValueString() throws DebugException
			{
				if ( javaValue.isNull() )
				{
					return "null"; //$NON-NLS-1$
				}
				
				// IJavaStackFrame frame = getEGLStackFrame().getJavaStackFrame();
				if ( javaValue instanceof IJavaObject )
				{
					IJavaValue result = VariableUtil.invokeStaticMethod( getEGLStackFrame().getEGLThread(), getEGLStackFrame().getJavaStackFrame(),
							"org.eclipse.edt.runtime.java.eglx.lang.EString", //$NON-NLS-1$
							"asString", "(Ljava/util/Calendar;)Ljava/lang/String;", new IJavaValue[] { javaValue } ); //$NON-NLS-1$ //$NON-NLS-2$
					
					if ( result == null )
					{
						return EDTDebugCoreMessages.ErrorRetrievingValue;
					}
					return result.getValueString();
				}
				
				return super.getValueString();
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
