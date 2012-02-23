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
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaThread;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.edt.debug.core.java.variables.VariableUtil;
import org.eclipse.edt.debug.internal.core.java.EGLJavaValue;
import org.eclipse.edt.debug.internal.core.java.EGLJavaVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.logicalstructures.JDIPlaceholderVariable;

/**
 * Represents a java.util.Map.
 */
@SuppressWarnings("restriction")
public class MapVariable extends EGLJavaVariable
{
	public MapVariable( IDebugTarget target, IJavaVariable javaVariable, SMAPVariableInfo variableInfo, IEGLJavaStackFrame frame, IEGLJavaValue parent )
	{
		super( target, javaVariable, variableInfo, frame, parent );
	}
	
	@Override
	protected IEGLJavaValue createEGLValue( IJavaValue javaValue )
	{
		return new MapValue( getDebugTarget(), (IJavaValue)javaValue, this );
	}
	
	private class MapValue extends EGLJavaValue
	{
		public MapValue( IDebugTarget target, IJavaValue value, EGLJavaVariable parent )
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
				IEGLJavaThread eglThread = getEGLStackFrame().getEGLThread();
				IJavaValue entrySetValue = VariableUtil.runSendMessage( eglThread, (IJavaObject)javaValue, null,
						"entrySet", "()Ljava/util/Set;", false ); //$NON-NLS-1$ //$NON-NLS-2$
				if ( entrySetValue instanceof IJavaObject )
				{
					IJavaValue toArrayValue = VariableUtil.runSendMessage( eglThread, (IJavaObject)entrySetValue, null,
							"toArray", "()[Ljava/lang/Object;", false ); //$NON-NLS-1$ //$NON-NLS-2$
					if ( toArrayValue != null )
					{
						IVariable[] vars = toArrayValue.getVariables();
						List<IEGLJavaVariable> list = new ArrayList<IEGLJavaVariable>( vars.length );
						
						SMAPVariableInfo parentInfo = parentVariable.getVariableInfo();
						
						for ( IVariable var : vars )
						{
							IValue value = var.getValue();
							if ( value instanceof IJavaObject )
							{
								IJavaValue nextkey = VariableUtil.runSendMessage( eglThread, (IJavaObject)value, null,
										"getKey", "()Ljava/lang/Object;", false ); //$NON-NLS-1$ //$NON-NLS-2$
								IJavaValue nextvalue = VariableUtil.runSendMessage( eglThread, (IJavaObject)value, null,
										"getValue", "()Ljava/lang/Object;", false ); //$NON-NLS-1$ //$NON-NLS-2$
								
								if ( nextkey != null && nextvalue instanceof IJavaValue )
								{
									SMAPVariableInfo info = new SMAPVariableInfo( nextkey.getValueString(), nextkey.getValueString(),
											getTypeNameForElement( nextvalue ), parentInfo.lineDeclared, parentInfo.smapEntry ); //$NON-NLS-1$
									list.add( VariableUtil.createEGLVariable( new JDIPlaceholderVariable( nextkey.getValueString(), nextvalue ),
											info, parentVariable.getEGLStackFrame(), this ) );
								}
							}
						}
						
						children = list.toArray( new IEGLJavaVariable[ list.size() ] );
					}
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
	
	/**
	 * @return the type name to display for the given element of the map. Subclasses may override this method. By default the Java value's type name
	 *         will be used.
	 * @throws DebugException
	 */
	protected String getTypeNameForElement( IJavaValue value ) throws DebugException
	{
		return value.getReferenceTypeName();
	}
	
	@Override
	protected boolean shouldCheckJavaElementAdapter()
	{
		return false;
	}
}
