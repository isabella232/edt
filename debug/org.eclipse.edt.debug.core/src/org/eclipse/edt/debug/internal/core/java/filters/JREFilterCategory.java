/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.core.java.filters;

import org.eclipse.debug.core.DebugException;
import org.eclipse.edt.debug.core.java.filters.FilterStepType;
import org.eclipse.edt.debug.core.java.variables.VariableUtil;
import org.eclipse.jdt.debug.core.IJavaClassType;
import org.eclipse.jdt.debug.core.IJavaReferenceType;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

/**
 * Category for the JRE filter.
 */
public class JREFilterCategory extends DefaultTypeFilterCategory
{
	@Override
	public FilterStepType getStepType( IJavaStackFrame frame )
	{
		// For method reflection classes we always run a step-into otherwise you couldn't step into certain pieces of EGL code without making the
		// entire JRE filter behave this way, which is HIGHLY undesirable (performance would be terrible).
		if ( frame != null )
		{
			try
			{
				IJavaReferenceType type = frame.getReferenceType();
				String typeName = frame.getReferenceType().getName();
				if ( typeName.equals( "java.lang.reflect.Method" ) ) //$NON-NLS-1$
				{
					return FilterStepType.STEP_INTO;
				}
				else if ( typeName.equals( "java.lang.Class" ) || typeName.equals( "java.lang.reflect.Constructor" ) ) //$NON-NLS-1$ //$NON-NLS-2$
				{
					if ( frame.getMethodName().startsWith( "newInstance" ) ) //$NON-NLS-1$
					{
						return FilterStepType.STEP_INTO;
					}
				}
				else if ( type instanceof IJavaClassType && typeName.startsWith( "sun.reflect." ) ) //$NON-NLS-1$
				{
					if ( VariableUtil.isInstanceOf( (IJavaClassType)type, "sun.reflect.MethodAccessor", false ) //$NON-NLS-1$
							|| VariableUtil.isInstanceOf( (IJavaClassType)type, "sun.reflect.ConstructorAccessor", false ) ) //$NON-NLS-1$
					{
						return FilterStepType.STEP_INTO;
					}
				}
			}
			catch ( DebugException de )
			{
			}
		}
		return super.getStepType( frame );
	}
}
