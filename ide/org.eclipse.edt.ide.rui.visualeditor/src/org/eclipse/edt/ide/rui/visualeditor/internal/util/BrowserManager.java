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
package org.eclipse.edt.ide.rui.visualeditor.internal.util;

import org.eclipse.core.internal.runtime.PlatformActivator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.preferences.EvPreferences;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class BrowserManager {
	
	public final static byte IE = 0X1;
	public final static byte WEBKIT = 0X2;
	public final static byte XULRUNNER = 0X4;
	
	public final static int SWT_WEBKIT = 0x10000; // SWT.WEBKIT
	
	public boolean ECLIPSE_37 = false;
	
	private static final boolean LINUX = Platform.OS_LINUX.equals(Platform.getOS());

	private byte BRWOSERS = 0X0;
	
	private byte defaultBrowser;
	
	
	private static BrowserManager INSTANCE;
	
	private BrowserManager() {
		
	}
	
	public static BrowserManager getInstance() {
		if ( INSTANCE == null ) {
			INSTANCE = new BrowserManager();
			INSTANCE.initializeBrowser();
		}
		return INSTANCE;
	}
	
	private void initializeBrowser() {
		try {
			ECLIPSE_37 = ((String)PlatformActivator.getContext().getBundle().getHeaders().get( "Bundle-Version")).indexOf( "3.7" ) >= 0;
			if ( ECLIPSE_37 ) {
				Browser b = new Browser( Display.getCurrent().getShells()[0], SWT_WEBKIT );
				BRWOSERS = (byte)(BRWOSERS | WEBKIT);
				b.dispose();
			}
		} catch ( Throwable e ) {
			
		}

		try {
			Browser b = new Browser( Display.getCurrent().getShells()[0], SWT.MOZILLA );
			BRWOSERS = (byte)(BRWOSERS | XULRUNNER);
			b.dispose();
		} catch ( Throwable e ) {
			
		}

	}

	public Browser createBrowser( Composite compositeParent ) {
		int _iRenderEngine = EvPreferences.getInt( EvConstants.PREFERENCE_RENDERENGINE );
		Browser browser = null;
		if ( _iRenderEngine == EvConstants.PREFERENCE_RENDERENGINE_DEFAULT ) {
			// For Linux there are known issues with older versions of webkit that come with some distros, so default to XULRunner. Otherwise default to webkit.
			if ( LINUX && (BRWOSERS & XULRUNNER) != 0 ) {
				return createXULRunner( compositeParent );
			}
			
			if ( (BRWOSERS & WEBKIT) != 0 &&  ECLIPSE_37 ) {
				return createWebKit( compositeParent );
			}
			
			if ( !LINUX && (BRWOSERS & XULRUNNER) != 0 ) {
				return createXULRunner( compositeParent );
			}
			
			return createNONE( compositeParent );
		} else if ( _iRenderEngine == EvConstants.PREFERENCE_RENDERENGINE_WEBKIT ) {
			return createWebKit( compositeParent );
		//TODO the following should not be checking the OS at all. too close to a release to remove it, so for now it has been widened to add linux.
		} else if ( _iRenderEngine == EvConstants.PREFERENCE_RENDERENGINE_XULRUNNER && (Platform.getOS().equals(Platform.OS_WIN32) || LINUX) ) {
			return createXULRunner( compositeParent );
		} else {
			return createNONE( compositeParent );
		}
	}
	
	private Browser createWebKit( Composite compositeParent ) {
		try {
			Browser browser = new Browser( compositeParent, SWT_WEBKIT);
			initializeBrowser(compositeParent.getDisplay(), browser, SWT_WEBKIT);
			return browser;
		} catch( SWTError ex ) {
			showOutOfResourcesMessage(Messages.NL_WEBKIT_Out_of_resources_message);
		}
		return null;
	}
	
	private Browser createXULRunner( Composite compositeParent ) {
		try {
			Browser browser = new Browser( compositeParent, SWT.MOZILLA);
			initializeBrowser(compositeParent.getDisplay(), browser, SWT.MOZILLA);
			return browser;
		} catch( SWTError ex ) {
			showOutOfResourcesMessage(Messages.NL_XULRunner_Out_of_resources_message);
		}
		return null;
	}
	
	private Browser createNONE( Composite compositeParent ) {
		try {
			Browser browser = new Browser( compositeParent, SWT.NONE);
			initializeBrowser(compositeParent.getDisplay(), browser, SWT.NONE);
			return browser;
		} catch( SWTError ex ) {
			showOutOfResourcesMessage(Messages.NL_IE_Out_of_resources_message);
		}
		return null;
	}
	
	private static void initializeBrowser(final Display display, Browser browser, final int style) {
		browser.addOpenWindowListener(new OpenWindowListener() {
			public void open(WindowEvent event) {
				if (!event.required) return;
				Shell shell = new Shell(display);
				shell.setLayout(new FillLayout());
				Browser browser = new Browser(shell, style);
				initializeBrowser(display, browser, style);
				event.browser = browser;
			}
		});
		browser.addVisibilityWindowListener(new VisibilityWindowListener() {
			public void hide(WindowEvent event) {
				Browser browser = (Browser)event.widget;
				Shell shell = browser.getShell();
				shell.setVisible(false);
			}
			public void show(WindowEvent event) {
				Browser browser = (Browser)event.widget;
				final Shell shell = browser.getShell();
				if (event.location != null) shell.setLocation(event.location);
				if (event.size != null) {
					Point size = event.size;
					shell.setSize(shell.computeSize(size.x, size.y));
				}
				shell.open();
			}
		});
		browser.addCloseWindowListener(new CloseWindowListener() {
			public void close(WindowEvent event) {
				Browser browser = (Browser)event.widget;
				Shell shell = browser.getShell();
				shell.close();
			}
		});
		if ( style == SWT.MOZILLA ) {
			browser.execute( "try { var ioService = Components.classes['@mozilla.org/network/io-service;1'].getService(Components.interfaces.nsIIOService2); ioService.offline = false; } catch ( e ) {}" );
		}
	}
	
	
	/**
	 * Displays a messages box with an ok button recommending that the editor be closed. 
	 */
	public void showOutOfResourcesMessage(String message) {
		String[] straButtons = new String[] { IDialogConstants.OK_LABEL };
		MessageDialog dialog = new MessageDialog( Display.getDefault().getActiveShell(), Messages.NL_EGL_Rich_UI_Editor, null, message, MessageDialog.WARNING, straButtons, 0 );
		dialog.open();
	}
}
