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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.IVariableAdapter;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * Utility class for EGL variables.
 */
public class VariableUtil
{
	public static final IVariable[] EMPTY_VARIABLES = {};
	
	private VariableUtil()
	{
		// No instances.
	}
	
	/**
	 * Given a set of Java variables, and SMAP variable information, this will determine which Java variables should be displayed. Any variables to be
	 * displayed will be wrapped inside an EGLJavaVariable.
	 * 
	 * @param javaVariables The Java variables, not null.
	 * @param currVariables The currently cached EGL variables, not null.
	 * @param prevVariables The EGL variables from the last time we processed variables, possibly null.
	 * @param infos The variable information from the SMAP, not null.
	 * @param frame The active EGL-wrapped stack frame, not null.
	 * @param skipLocals True if local variables should be omitted.
	 * @param parent The parent of the variables being created, possibly null.
	 * @return the filtered, EGL-wrapped variables.
	 * @throws DebugException
	 */
	public static List<IEGLJavaVariable> filterAndWrapVariables( IVariable[] javaVariables, IEGLJavaStackFrame frame, boolean skipLocals,
			IEGLJavaValue parent ) throws DebugException
	{
		List<IEGLJavaVariable> newEGLVariables = new ArrayList<IEGLJavaVariable>( javaVariables.length );
		
		SMAPVariableInfo[] infos = frame.getSMAPVariableInfos();
		String javaFrameSignature = frame.getJavaStackFrame().getMethodName() + ";" + frame.getJavaStackFrame().getSignature(); //$NON-NLS-1$
		int currentLine = frame.getLineNumber();
		int frameStartLine = frame.getSMAPFunctionInfo() == null
				? -1
				: frame.getSMAPFunctionInfo().lineDeclared;
		
		for ( int i = 0; i < javaVariables.length; i++ )
		{
			IJavaVariable javaVar = (IJavaVariable)javaVariables[ i ];
			String javaName = javaVar.getName();
			
			if ( javaVar.isLocal() )
			{
				if ( !skipLocals )
				{
					SMAPVariableInfo matchingInfo = null;
					for ( int j = 0; j < infos.length; j++ )
					{
						SMAPVariableInfo info = infos[ j ];
						if ( info.javaName.equals( javaName ) )
						{
							// Validate the signature and make sure the info is in scope. There could be multiple
							// local variables of different types with the same name, in different scopes within the
							// function.
							if ( info.javaMethodSignature != null && info.javaMethodSignature.equals( javaFrameSignature )
									&& (currentLine >= info.lineDeclared || currentLine == frameStartLine) )
							{
								if ( matchingInfo == null || matchingInfo.lineDeclared < info.lineDeclared )
								{
									matchingInfo = info;
								}
								
								// Don't break - keep looping to see if there's a better match. We want the info
								// with the highest line number that's within scope.
							}
						}
					}
					
					if ( matchingInfo != null )
					{
						IEGLJavaVariable var = frame.getCorrespondingVariable( createEGLVariable( javaVar, matchingInfo, frame, parent ), parent );
						newEGLVariables.add( var );
					}
				}
			}
			else
			{
				if ( "this".equals( javaName ) ) //$NON-NLS-1$
				{
					IEGLJavaVariable var = frame.getCorrespondingVariable( new EGLJavaFunctionContainerVariable( frame.getDebugTarget(), javaVar,
							frame ), parent );
					newEGLVariables.add( var );
				}
				else
				{
					SMAPVariableInfo matchingInfo = null;
					for ( int j = 0; j < infos.length; j++ )
					{
						SMAPVariableInfo info = infos[ j ];
						if ( info.javaName.equals( javaName ) )
						{
							// Make sure it has no signature
							if ( info.javaMethodSignature == null )
							{
								matchingInfo = info;
								break;
							}
						}
					}
					
					if ( matchingInfo != null )
					{
						IEGLJavaVariable var = frame.getCorrespondingVariable( createEGLVariable( javaVar, matchingInfo, frame, parent ), parent );
						newEGLVariables.add( var );
					}
				}
			}
		}
		return newEGLVariables;
	}
	
	/**
	 * Creates an EGL wrapper around the given Java variable. Contributed IVariableAdapters are consulted before doing basic wrapping.
	 * 
	 * @param javaVariable The Java variable.
	 * @param info The variable information from the SMAP.
	 * @param frame The EGL frame.
	 * @param parentVariable The parent of this variable, possibly null.
	 * @return
	 */
	public static IEGLJavaVariable createEGLVariable( IJavaVariable javaVariable, SMAPVariableInfo info, IEGLJavaStackFrame frame,
			IEGLJavaValue parent )
	{
		// Consult the adapters.
		for ( IVariableAdapter adapter : EDTDebugCorePlugin.getDefault().getVariableAdapters() )
		{
			IEGLJavaVariable variable = adapter.adapt( javaVariable, frame, info, parent );
			if ( variable != null )
			{
				return variable;
			}
		}
		
		return new EGLJavaVariable( frame.getDebugTarget(), javaVariable, info, frame, parent );
	}
	
	/**
	 * @return the qualified name of the variable with '|' as the delimeter, e.g. parentVarName|childVarName
	 * @throws DebugException
	 */
	public static String getQualifiedName( IEGLJavaVariable var ) throws DebugException
	{
		if ( var == null )
		{
			return null;
		}
		
		IEGLJavaValue parent = var.getParentValue();
		if ( parent == null || parent.getParentVariable() == null )
		{
			return var.getName();
		}
		
		StringBuilder buf = new StringBuilder( 100 );
		buf.append( getQualifiedName( parent.getParentVariable() ) );
		buf.append( '|' );
		buf.append( var.getName() );
		return buf.toString();
	}
}
