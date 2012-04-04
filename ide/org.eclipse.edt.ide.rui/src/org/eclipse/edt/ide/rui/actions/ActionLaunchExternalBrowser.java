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
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;


public class ActionLaunchExternalBrowser extends Action {
	protected String			_strUrl		= null;
	protected boolean           _isDebug;

	public ActionLaunchExternalBrowser( String strUrl, boolean isDebug ) {
		super( "" );

		_strUrl = strUrl.replaceAll( " ", "%20" );
		_isDebug = isDebug;
	}

	/**
	 * Method declared in Action.
	 * Attempts to open an external browser.  A message is displayed if no URL is available.
	 */
	public void run() {
		if( _strUrl == null || _strUrl.length() == 0 ) {
			// Invalid thread access in debugger - must throw a DebugException in RUILaunchDelegate.
			if (_isDebug) {
				throw new RuntimeException(RUINlsStrings.NoPreviewContent_Msg);
			}
			else {
				MessageDialog.openInformation( Util.getShell(), RUINlsStrings.NoPreviewContent_Title, RUINlsStrings.NoPreviewContent_Msg );
			}
			return;
		}

		boolean launched;
		try {
			// Try using the external browser specified in preferences
			// Otherwise, use the system browser
			//--------------------------------------------------------
			IWebBrowser browser = WorkbenchBrowserSupport.getInstance().getExternalBrowser();
			
			if( browser != null ) {
				browser.openURL( new URL( _strUrl ) );
				launched = true;
			}
			else {
				launched = launchSystemBrowser(_strUrl);
			}
		}
		catch( Exception e ) {
			launched = launchSystemBrowser(_strUrl);
		}
		
		if( !launched ) {
			// Invalid thread access in debugger - must throw a DebugException in RUILaunchDelegate.
			if (_isDebug) {
				throw new RuntimeException(RUINlsStrings.NoBrowserDialog_Msg);
			}
			else {
				ErrorDialog.openError(null, RUINlsStrings.NoBrowserDialog_Title, RUINlsStrings.NoBrowserDialog_Msg, new Status(IStatus.ERROR, Activator.PLUGIN_ID, RUINlsStrings.NoBrowserDialog_Msg));
			}
		}
	}
	
	private boolean launchSystemBrowser( final String url ) {
		if (_isDebug) {
			final boolean[] launched = new boolean[]{false};
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					launched[0] = org.eclipse.swt.program.Program.launch( url ); 
				}
			});
			return launched[0];
		}
		else {
			return org.eclipse.swt.program.Program.launch( url );
		}
	}
}
