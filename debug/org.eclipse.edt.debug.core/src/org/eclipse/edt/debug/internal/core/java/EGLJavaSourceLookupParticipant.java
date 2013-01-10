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
package org.eclipse.edt.debug.internal.core.java;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;

public class EGLJavaSourceLookupParticipant extends AbstractSourceLookupParticipant
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourceLookupParticipant#getSourceName(java.lang.Object)
	 */
	@Override
	public String getSourceName( Object object ) throws CoreException
	{
		if ( object instanceof IEGLJavaStackFrame )
		{
			return ((IEGLJavaStackFrame)object).getSourcePath();
		}
		return null;
	}
	
	@Override
	protected ISourceContainer[] getSourceContainers()
	{
		try
		{
			return new EGLJavaSourcePathComputerDelegate().computeSourceContainers( getDirector().getLaunchConfiguration(), null );
		}
		catch ( CoreException e )
		{
			return new ISourceContainer[ 0 ];
		}
	}
}
