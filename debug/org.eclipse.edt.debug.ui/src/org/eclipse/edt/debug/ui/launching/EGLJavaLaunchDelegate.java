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
package org.eclipse.edt.debug.ui.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventFilter;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.debug.internal.core.java.EGLJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

/**
 * Launches a Java-based EDT debugger.
 */
public class EGLJavaLaunchDelegate extends AbstractJavaLaunchConfigurationDelegate
{
	@Override
	public void launch( ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor )
			throws CoreException
	{
		// Set up the javaagent for instrumenting the SMAP info. Do this on a working copy so that it's not saved
		// since the jar location can change.
		String transformerPath = EDTDebugCorePlugin.getDefault().getTransformerPath();
		if ( transformerPath != null && transformerPath.length() != 0 )
		{
			String vmArgs = configuration.getAttribute( IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "" ); //$NON-NLS-1$
			if ( !vmArgs.contains( "-javaagent:" ) ) //$NON-NLS-1$
			{
				vmArgs += " -javaagent:\"" + transformerPath + "\""; //$NON-NLS-1$ //$NON-NLS-2$
				
				ILaunchConfigurationWorkingCopy wc = configuration.getWorkingCopy();
				wc.setAttribute( IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs );
				configuration = wc;
			}
		}
		
		DebugTargetWrapper wrapper = new DebugTargetWrapper( launch );
		DebugPlugin.getDefault().addDebugEventFilter( wrapper );
		new JavaLaunchDelegate().launch( configuration, mode, launch, monitor );
		DebugPlugin.getDefault().removeDebugEventFilter( wrapper );
	}
	
	/**
	 * Looks for the creation of the IJavaDebugTarget for the launch. When found, the target is wrapped and we set our
	 * own target on the launch.
	 */
	private class DebugTargetWrapper implements IDebugEventFilter
	{
		private final ILaunch launch;
		
		DebugTargetWrapper( ILaunch launch )
		{
			this.launch = launch;
		}
		
		public DebugEvent[] filterDebugEvents( DebugEvent[] events )
		{
			if ( events != null && events.length != 0 && events[ 0 ].getKind() == DebugEvent.CREATE )
			{
				Object src = events[ 0 ].getSource();
				if ( src instanceof IDebugTarget )
				{
					IJavaDebugTarget javaTarget = (IJavaDebugTarget)((IDebugTarget)src)
							.getAdapter( IJavaDebugTarget.class );
					if ( javaTarget != null && javaTarget.getLaunch() == launch )
					{
						EGLJavaDebugTarget edtTarget = new EGLJavaDebugTarget( (IJavaDebugTarget)javaTarget );
						launch.removeDebugTarget( javaTarget );
						launch.addDebugTarget( edtTarget );
						edtTarget.handleDebugEvents( events );
						return null;
					}
				}
			}
			return events;
		}
	}
}
