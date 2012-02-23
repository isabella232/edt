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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;
import org.eclipse.edt.debug.core.java.filters.ClasspathEntryFilter;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * Filters out JRE classes, based on the JRE of the debug target's active VM.
 */
public class JREFilter extends ClasspathEntryFilter
{
	@Override
	protected IClasspathEntry[] getClasspathEntries( IEGLJavaDebugTarget target ) throws CoreException
	{
		ILaunchConfiguration config = target.getJavaDebugTarget().getLaunch().getLaunchConfiguration();
		IJavaProject javaProject = JavaRuntime.getJavaProject( config );
		if ( javaProject != null )
		{
			return new IClasspathEntry[] { JavaRuntime.computeJREEntry( config ).getClasspathEntry() };
		}
		return null;
	}
	
	@Override
	protected Object getContainerClassMapKey( IEGLJavaDebugTarget target ) throws CoreException
	{
		IJavaProject javaProject = JavaRuntime.getJavaProject( target.getJavaDebugTarget().getLaunch().getLaunchConfiguration() );
		if ( javaProject != null )
		{
			IVMInstall vm = JavaRuntime.getVMInstall( javaProject );
			return vm.getId();
		}
		return null;
	}
}
