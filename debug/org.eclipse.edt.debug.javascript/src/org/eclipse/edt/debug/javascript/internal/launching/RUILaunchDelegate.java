/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.javascript.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.edt.debug.javascript.internal.model.IRUILaunchConfigurationConstants;
import org.eclipse.edt.debug.javascript.internal.model.RUIDebugContextResolver;
import org.eclipse.edt.debug.javascript.internal.model.RUIDebugMessages;
import org.eclipse.edt.ide.debug.javascript.internal.server.DebugContext;
import org.eclipse.edt.ide.debug.javascript.internal.utils.RUIDebugUtil;
import org.eclipse.edt.ide.rui.actions.ActionLaunchDefaultBrowser;
import org.eclipse.edt.ide.rui.actions.ActionLaunchExternalBrowser;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;

public class RUILaunchDelegate extends LaunchConfigurationDelegate
{
	
	@Override
	public void launch( ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor ) throws CoreException
	{
		if ( mode.equalsIgnoreCase( ILaunchManager.DEBUG_MODE ) )
		{
			launchInDebugMode( configuration, launch, monitor );
		}
		else if ( mode.equalsIgnoreCase( ILaunchManager.RUN_MODE ) )
		{
			launchInRunMode( configuration, launch, monitor );
		}
	}
	
	private void launchInDebugMode( ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor ) throws CoreException
	{
		DebugContext context = RUIDebugUtil.createContext( configuration, launch );
		RUIDebugContextResolver.getInstance().addContext( context );
		
		try
		{
			if ( !configuration.hasAttribute( IRUILaunchConfigurationConstants.ATTR_URL ) )
			{
				// If this attribute is present then we're restarting a debug session,
				// meaning the browser is already open.
				new ActionLaunchExternalBrowser( context.getUrl(), true ).run();
			}
			launch.addDebugTarget( context.getDebugTarget() );
		}
		catch ( RuntimeException re )
		{
			RUIDebugContextResolver.getInstance().removeContext( context );
			displayError( NLS.bind( RUIDebugMessages.rui_launch_error_title, configuration.getName() ), re.getMessage() );
			monitor.setCanceled( true );
		}
	}
	
	private void launchInRunMode( ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor ) throws CoreException
	{
		// Don't display this launch in the Debug view.
		DebugPlugin.getDefault().getLaunchManager().removeLaunch( launch );
		
		String file = configuration.getAttribute( IRUILaunchConfigurationConstants.ATTR_HANDLER_FILE, "" ); //$NON-NLS-1$
		String project = configuration.getAttribute( IRUILaunchConfigurationConstants.ATTR_PROJECT_NAME, "" ); //$NON-NLS-1$
		new ActionLaunchDefaultBrowser( RUIDebugUtil.getDebugURL( file, project ) ).run();
	}
	
	@Override
	protected boolean saveBeforeLaunch( ILaunchConfiguration configuration, String mode, IProgressMonitor monitor ) throws CoreException
	{
		// don't prompt to save dirty editors
		return true;
	}
	
	/**
	 * Display our own error dialog rather than throwing a DebugException so that the error doesn't show up in the error log.
	 * 
	 * @param title Dialog title.
	 * @param msg Dialog message.
	 */
	private void displayError( final String title, final String msg )
	{
		Display.getDefault().asyncExec( new Runnable() {
			@Override
			public void run()
			{
				MessageDialog.openError( Display.getDefault().getActiveShell(), title, msg );
			}
		} );
	}
}
