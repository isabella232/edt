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
package org.eclipse.edt.debug.core.breakpoints;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;

public abstract class EGLBreakpoint extends Breakpoint
{
	@Override
	public String getModelIdentifier()
	{
		return IEGLDebugCoreConstants.EGL_JAVA_MODEL_PRESENTATION_ID;
	}
	
	/**
	 * Sets the qualified type name of the breakpoint.
	 * 
	 * @param typeName The type name.
	 * @throws CoreException
	 */
	protected void setTypeName( String typeName ) throws CoreException
	{
		setAttribute( IEGLDebugCoreConstants.BREAKPOINT_TYPE_NAME, typeName );
	}
	
	/**
	 * @return the qualified type name of the breakpoint, e.g. "pkg1.Foo".
	 * @throws CoreException
	 */
	public String getTypeName() throws CoreException
	{
		return ensureMarker().getAttribute( IEGLDebugCoreConstants.BREAKPOINT_TYPE_NAME, null );
	}
}
