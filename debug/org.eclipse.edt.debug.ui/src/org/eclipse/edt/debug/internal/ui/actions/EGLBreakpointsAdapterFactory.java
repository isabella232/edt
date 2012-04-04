/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.internal.ui.actions;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IRunToLineTarget;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;

/**
 * Provides adapters for breakpoints.
 * 
 * @see IToggleBreakpointsTarget
 * @see ToggleBreakpointsTarget
 */
public class EGLBreakpointsAdapterFactory implements IAdapterFactory
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter( Object adaptableObject, Class adapterType )
	{
		if ( adapterType == IToggleBreakpointsTarget.class )
		{
			return new ToggleBreakpointsTarget();
		}
		if ( adapterType == IRunToLineTarget.class )
		{
			return new RunToLineTarget();
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	public Class[] getAdapterList()
	{
		return new Class[] { IToggleBreakpointsTarget.class, IRunToLineTarget.class };
	}
}
