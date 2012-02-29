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

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;
import org.eclipse.edt.debug.core.java.filters.ClasspathEntryFilter;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;

/**
 * Filters out JRE classes, based on the JRE of the debug target's active VM.
 */
public class JREFilter extends ClasspathEntryFilter
{
	@Override
	protected void processClasspath( IClasspathEntry entry, IEGLJavaDebugTarget target, Map<String, Object> classMap ) throws CoreException
	{
		IVMInstall vm = JavaRuntime.computeVMInstall( target.getJavaDebugTarget().getLaunch().getLaunchConfiguration() );
		if ( vm != null )
		{
			LibraryLocation[] libraries = JavaRuntime.getLibraryLocations( vm );
			for ( LibraryLocation library : libraries )
			{
				processJar( library.getSystemLibraryPath().toFile(), library.getPackageRootPath().toString(), classMap );
			}
		}
	}
	
	@Override
	protected IClasspathEntry[] getClasspathEntries( IEGLJavaDebugTarget target ) throws CoreException
	{
		IRuntimeClasspathEntry entry = JavaRuntime.computeJREEntry( target.getJavaDebugTarget().getLaunch().getLaunchConfiguration() );
		if ( entry != null )
		{
			return new IClasspathEntry[] { entry.getClasspathEntry() };
		}
		return null;
	}
	
	@Override
	protected Object getContainerClassMapKey( IEGLJavaDebugTarget target ) throws CoreException
	{
		IVMInstall vm = JavaRuntime.computeVMInstall( target.getJavaDebugTarget().getLaunch().getLaunchConfiguration() );
		if ( vm != null )
		{
			return vm.getId();
		}
		return null;
	}
}
