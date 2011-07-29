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
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.IVariableAdapter;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.jdt.debug.core.IJavaVariable;

public class EDTVariableAdapter implements IVariableAdapter
{
	@Override
	public IEGLJavaVariable adapt( IJavaVariable variable, IEGLJavaStackFrame frame, SMAPVariableInfo info, IEGLJavaValue parent )
	{
		try
		{
			String type = variable.getReferenceTypeName();
			if ( type != null )
			{
				if ( type.startsWith( "egl.lang.EList<" ) ) //$NON-NLS-1$
				{
					return new ListVariable( frame.getDebugTarget(), variable, info, frame, parent );
				}
			}
		}
		catch ( DebugException e )
		{
		}
		return null;
	}
}
