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

import java.util.HashMap;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.edt.debug.core.java.variables.ChildlessVariable;
import org.eclipse.edt.debug.core.java.variables.IVariableAdapter;
import org.eclipse.edt.debug.core.java.variables.ToStringVariable;
import org.eclipse.edt.debug.core.java.variables.VariableUtil;
import org.eclipse.edt.debug.internal.core.java.EGLJavaVariable;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaPrimitiveValue;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * Variable adapter for some core JRE types. This will be added at the end of the adapter list so that other adapters will take precedence.
 */
public class DefaultVariableAdapter implements IVariableAdapter
{
	private HashMap<String, Object> simpleTypes;
	
	@Override
	public IEGLJavaVariable adapt( IJavaVariable variable, IEGLJavaStackFrame frame, SMAPVariableInfo info, IEGLJavaValue parent )
	{
		try
		{
			IValue value = variable.getValue();
			if ( value instanceof IJavaPrimitiveValue )
			{
				return new EGLJavaVariable( frame.getDebugTarget(), variable, info, frame, parent ) {
					@Override
					protected boolean shouldCheckJavaElementAdapter()
					{
						return false;
					}
				};
			}
			
			// If the value isn't null, use the value's type. Otherwise use the variable's.
			String type;
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
			
			if ( getSimpleTypeMap().containsKey( type ) )
			{
				if ( "java.lang.String".equals( type ) ) //$NON-NLS-1$
				{
					return new ChildlessVariable( frame.getDebugTarget(), variable, info, frame, parent );
				}
				
				if ( "java.math.BigDecimal".equals( type ) || "java.math.BigInteger".equals( type ) ) //$NON-NLS-1$ //$NON-NLS-2$
				{
					return new ToStringVariable( frame.getDebugTarget(), variable, info, frame, parent );
				}
				
				// The other types have their value inside a 'value' field.
				if ( value instanceof IJavaObject )
				{
					if ( ((IJavaObject)value).isNull() )
					{
						return new EGLJavaVariable( frame.getDebugTarget(), variable, info, frame, parent ) {
							@Override
							protected boolean shouldCheckJavaElementAdapter()
							{
								return false;
							}
						};
					}
					else
					{
						IJavaFieldVariable var = ((IJavaObject)value).getField( "value", false ); //$NON-NLS-1$
						if ( var != null )
						{
							return new EGLJavaVariable( frame.getDebugTarget(), var, info, frame, parent ) {
								@Override
								protected boolean shouldCheckJavaElementAdapter()
								{
									return false;
								}
							};
						}
					}
				}
			}
			else if ( VariableUtil.isInstanceOf( variable, "java.util.List" ) ) //$NON-NLS-1$
			{
				return new ListVariable( frame.getDebugTarget(), variable, info, frame, parent );
			}
			else if ( VariableUtil.isInstanceOf( variable, "java.util.Map" ) ) //$NON-NLS-1$
			{
				return new MapVariable( frame.getDebugTarget(), variable, info, frame, parent );
			}
			else if ( VariableUtil.isInstanceOf( variable, "java.util.Calendar" ) ) //$NON-NLS-1$
			{
				return new CalendarVariable( frame.getDebugTarget(), variable, info, frame, parent );
			}
		}
		catch ( DebugException e )
		{
			EDTDebugCorePlugin.log( e );
		}
		return null;
	}
	
	private synchronized HashMap<String, Object> getSimpleTypeMap()
	{
		if ( simpleTypes == null )
		{
			simpleTypes = new HashMap<String, Object>( 11 );
			simpleTypes.put( "java.lang.Integer", null ); //$NON-NLS-1$
			simpleTypes.put( "java.lang.Long", null ); //$NON-NLS-1$
			simpleTypes.put( "java.lang.Short", null ); //$NON-NLS-1$
			simpleTypes.put( "java.lang.Boolean", null ); //$NON-NLS-1$
			simpleTypes.put( "java.lang.String", null ); //$NON-NLS-1$
			simpleTypes.put( "java.lang.Float", null ); //$NON-NLS-1$
			simpleTypes.put( "java.lang.Double", null ); //$NON-NLS-1$
			simpleTypes.put( "java.lang.Byte", null ); //$NON-NLS-1$
			simpleTypes.put( "java.lang.Character", null ); //$NON-NLS-1$
			simpleTypes.put( "java.math.BigDecimal", null ); //$NON-NLS-1$
			simpleTypes.put( "java.math.BigInteger", null ); //$NON-NLS-1$
		}
		return simpleTypes;
	}
	
	@Override
	public void dispose()
	{
		simpleTypes = null;
	}
}
