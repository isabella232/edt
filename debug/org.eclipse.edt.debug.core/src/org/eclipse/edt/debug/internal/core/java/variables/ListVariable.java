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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.edt.debug.core.java.variables.VariableUtil;
import org.eclipse.edt.debug.internal.core.java.EGLJavaValue;
import org.eclipse.edt.debug.internal.core.java.EGLJavaVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * Represents a java.lang.List.
 */
public class ListVariable extends EGLJavaVariable
{
	public ListVariable( IDebugTarget target, IJavaVariable javaVariable, SMAPVariableInfo variableInfo, IEGLJavaStackFrame frame,
			IEGLJavaValue parent )
	{
		super( target, javaVariable, variableInfo, frame, parent );
	}
	
	@Override
	protected IEGLJavaValue createEGLValue( IJavaValue javaValue )
	{
		return new ListValue( getDebugTarget(), (IJavaValue)javaValue, this );
	}
	
	private class ListValue extends EGLJavaValue
	{
		public ListValue( IDebugTarget target, IJavaValue value, EGLJavaVariable parent )
		{
			super( target, value, parent );
		}
		
		@Override
		public synchronized IVariable[] getVariables() throws DebugException
		{
			if ( children != null )
			{
				return children;
			}
			
			if ( javaValue instanceof IJavaObject )
			{
				IJavaValue toArrayValue = VariableUtil.runSendMessage( getEGLStackFrame().getEGLThread(), (IJavaObject)javaValue, null, "toArray", //$NON-NLS-1$
						"()[Ljava/lang/Object;", false ); //$NON-NLS-1$
				if ( toArrayValue != null )
				{
					IVariable[] vars = toArrayValue.getVariables();
					List<IEGLJavaVariable> list = new ArrayList<IEGLJavaVariable>( vars.length );
					
					SMAPVariableInfo parentInfo = parentVariable.getVariableInfo();
					
					String elementType = parentInfo.type;
					int idx = elementType.indexOf( '<' );
					if ( idx != -1 )
					{
						int idx2 = elementType.lastIndexOf( '>' );
						if ( idx2 != -1 )
						{
							elementType = elementType.substring( idx + 1, idx2 );
						}
					}
					
					for ( IVariable var : vars )
					{
						if ( var instanceof IJavaVariable )
						{
							String eglName;
							String javaName = var.getName();
							if ( javaName.length() > 2 && javaName.charAt( 0 ) == '[' && javaName.charAt( javaName.length() - 1 ) == ']' )
							{
								// If the array entry name is the form [0] change it to [1] (egl has 1-based indices).
								try
								{
									int index = Integer.parseInt( javaName.substring( 1, javaName.length() - 1 ) ) + 1;
									StringBuilder buf = new StringBuilder( javaName.length() + 1 );
									buf.append( '[' );
									buf.append( index );
									buf.append( ']' );
									eglName = buf.toString();
								}
								catch ( NumberFormatException e )
								{
									eglName = javaName;
								}
							}
							else
							{
								eglName = javaName;
							}
							
							SMAPVariableInfo info = new SMAPVariableInfo( eglName, javaName, elementType, parentInfo.lineDeclared,
									parentInfo.smapEntry );
							list.add( VariableUtil.createEGLVariable( (IJavaVariable)var, info, parentVariable.getEGLStackFrame(), this ) );
						}
					}
					
					children = list.toArray( new IEGLJavaVariable[ list.size() ] );
				}
			}
			
			if ( children == null )
			{
				children = super.getVariables();
			}
			return children;
		}
		
		@Override
		public String getValueString()
		{
			return javaValue.isNull()
					? "null" : ""; //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		@Override
		public String computeDetail()
		{
			return getValueString();
		}
	}
	
	@Override
	protected boolean shouldCheckJavaElementAdapter()
	{
		return false;
	}
}
