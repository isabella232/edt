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

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.core.java.IEGLJavaDebugTarget;
import org.eclipse.edt.debug.core.java.filters.ClasspathEntryFilter;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;

/**
 * Filters out JRE classes, based on the JRE of the debug target's active VM.
 */
public class JREFilter extends ClasspathEntryFilter
{
	@Override
	public synchronized void initialize( IEGLJavaDebugTarget target )
	{
		Object targetKey = getTargetClassmapKey( target );
		if ( targetKey == null || targetClassMap.containsKey( targetKey ) )
		{
			return;
		}
		
		try
		{
			targetClassMap.put( targetKey, null );
			IVMInstall vm = JavaRuntime.computeVMInstall( target.getJavaDebugTarget().getLaunch().getLaunchConfiguration() );
			if ( vm != null )
			{
				LibraryLocation[] libraries = JavaRuntime.getLibraryLocations( vm );
				if ( libraries.length > 0 )
				{
					HashMap<String, Object> targetClasses = new HashMap<String, Object>( 100 );
					targetClassMap.put( targetKey, targetClasses );
					for ( LibraryLocation library : libraries )
					{
						processJar( library.getSystemLibraryPath().toFile(), library.getPackageRootPath().toString(), targetClasses );
					}
				}
			}
		}
		catch ( Exception e )
		{
			EDTDebugCorePlugin.log( e );
		}
	}
	
	@Override
	protected Object getTargetClassmapKey( IEGLJavaDebugTarget target )
	{
		// Cache based on VM instead of target.
		try
		{
			IVMInstall vm = JavaRuntime.computeVMInstall( target.getJavaDebugTarget().getLaunch().getLaunchConfiguration() );
			if ( vm != null )
			{
				return vm.getId();
			}
		}
		catch ( CoreException ce )
		{
			EDTDebugCorePlugin.log( ce );
		}
		return null;
	}
}
