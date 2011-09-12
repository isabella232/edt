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

import java.util.HashMap;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.IVariableAdapter;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.edt.debug.internal.core.java.EGLJavaVariable;
import org.eclipse.edt.debug.internal.core.java.VariableUtil;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaInterfaceType;
import org.eclipse.jdt.debug.core.IJavaType;
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
			IJavaType javaType = variable.getJavaType();
			if ( javaType instanceof IJavaClassType || javaType instanceof IJavaInterfaceType )
			{
				IValue value = variable.getValue();
				String type = value.getReferenceTypeName(); // Use the value's type which will be the actual type. The variable would report the
															// declared type.
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
					
					// The other types have the value inside a 'value' field.
					IVariable[] vars = value.getVariables();
					for ( IVariable var : vars )
					{
						if ( var instanceof IJavaVariable && !((IJavaVariable)var).isStatic() && var.getName().equals( "value" ) ) //$NON-NLS-1$
						{
							return new EGLJavaVariable( frame.getDebugTarget(), (IJavaVariable)var, info, frame, parent );
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
			}
		}
		catch ( DebugException e )
		{
		}
		return null;
	}
	
	private HashMap<String, Object> getSimpleTypeMap()
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
}
