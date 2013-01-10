/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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
import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;
import org.eclipse.edt.debug.core.java.filters.AbstractTypeFilter;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

/**
 * Filters out javax.servlet.* classes.
 */
public class ServletFilter extends AbstractTypeFilter
{
	@Override
	public boolean filter( IJavaStackFrame frame, IEGLJavaDebugTarget target )
	{
		try
		{
			return frame.getDeclaringTypeName().startsWith( "javax.servlet." ); //$NON-NLS-1$
		}
		catch ( DebugException de )
		{
			return false;
		}
	}
}
