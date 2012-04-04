/*******************************************************************************
 * Copyright © 1994, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.actions;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.rui.internal.nls.RUINlsStrings;
import org.eclipse.edt.ide.rui.utils.Util;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;


public class ActionLaunchDefaultBrowser extends Action
{
	protected String _strUrl;

	public ActionLaunchDefaultBrowser( String strUrl )
	{
		super( "" );
		_strUrl = strUrl.replaceAll( " ", "%20" );
	}

	/**
	 * Method declared in Action.
	 * Attempts to open an external browser.  A message is displayed if no URL is available.
	 */
	public void run()
	{
		if ( _strUrl == null || _strUrl.length() == 0 )
		{
			MessageDialog.openInformation( Util.getShell(), RUINlsStrings.NoWebContent_Title, RUINlsStrings.NoPreviewContent_Msg );
			return;
		}

		// Try using the default browser specified in preferences
		// Otherwise, use the system browser
		//--------------------------------------------------------

		// Must be run in UI thread when trying to create the internal browser.
		Display.getDefault().asyncExec( new Runnable() {
			public void run()
			{
				boolean launched;
				try
				{
					final IWebBrowser browser = WorkbenchBrowserSupport.getInstance().createBrowser( IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.NAVIGATION_BAR, null, null, null );
					browser.openURL( new URL( _strUrl ) );
					launched = true;
				}
				catch ( Exception e )
				{
					launched = launchSystemBrowser(_strUrl);
				}
				
				if ( !launched )
				{
					ErrorDialog.openError( null, RUINlsStrings.NoBrowserDialog_Title, RUINlsStrings.NoDefaultBrowserDialog_Msg, new Status( IStatus.ERROR, Activator.PLUGIN_ID, RUINlsStrings.NoDefaultBrowserDialog_Msg ) );
				}
			}
		} );
	}
	
	private boolean launchSystemBrowser( final String url )
	{
		return org.eclipse.swt.program.Program.launch( url );
	}
}
