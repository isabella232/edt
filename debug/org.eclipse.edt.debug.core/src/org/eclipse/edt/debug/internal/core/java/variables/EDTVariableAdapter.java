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
package org.eclipse.edt.debug.internal.core.java.variables;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.edt.debug.core.java.variables.IVariableAdapter;
import org.eclipse.edt.debug.core.java.variables.ToStringVariable;
import org.eclipse.edt.debug.core.java.variables.VariableUtil;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * Adapts some types provided by the core EDT Java Runtime.
 */
public class EDTVariableAdapter implements IVariableAdapter
{
	@Override
	public IEGLJavaVariable adapt( IJavaVariable variable, IEGLJavaStackFrame frame, SMAPVariableInfo info, IEGLJavaValue parent )
	{
		try
		{
			if ( VariableUtil.isInstanceOf( variable, "eglx.lang.EDictionary" ) ) //$NON-NLS-1$
			{
				// Rather than the default map implementation, dictionary values are always of type 'any'
				return new MapVariable( frame.getDebugTarget(), variable, info, frame, parent ) {
					@Override
					protected String getTypeNameForElement( IJavaValue value )
					{
						return "eglx.lang.EAny"; //$NON-NLS-1$
					}
				};
			}
			else if ( VariableUtil.isInstanceOf( variable, "org.eclipse.edt.javart.AnyBoxedObject" ) ) //$NON-NLS-1$
			{
				// Look for the "object" field and wrap that.
				IVariable[] kids = variable.getValue().getVariables();
				for ( IVariable kid : kids )
				{
					if ( kid instanceof IJavaVariable && "object".equals( kid.getName() ) ) //$NON-NLS-1$
					{
						return VariableUtil.createEGLVariable( (IJavaVariable)kid, info, frame, parent );
					}
				}
			}
			else if ( VariableUtil.isInstanceOf( variable, "org.eclipse.edt.javart.Delegate" ) ) //$NON-NLS-1$
			{
				return new ToStringVariable( frame.getDebugTarget(), variable, info, frame, parent );
			}
			
			// If the value isn't null, use the value's type. Otherwise use the variable's.
			String type;
			IValue value = variable.getValue();
			if ( value instanceof IJavaValue && !((IJavaValue)value).isNull() )
			{
				type = value.getReferenceTypeName();
			}
			else
			{
				// The type might not be loaded yet. If that's the case, we can't adapt it since the type is unknown.
				try
				{
					type = variable.getReferenceTypeName();
				}
				catch ( DebugException e )
				{
					return null;
				}
			}
			
			if ( "byte[]".equals( type ) && frame.isEGLStratum() ) //$NON-NLS-1$
			{
				return new BytesVariable( frame.getDebugTarget(), variable, info, frame, parent );
			}
		}
		catch ( DebugException e )
		{
			EDTDebugCorePlugin.log( e );
		}
		return null;
	}
	
	@Override
	public void dispose()
	{
		// Nothing to do.
	}
}
