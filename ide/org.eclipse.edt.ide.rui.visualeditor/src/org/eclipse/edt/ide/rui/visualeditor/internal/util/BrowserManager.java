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

import org.eclipse.core.runtime.Platform;
import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;
import org.eclipse.edt.ide.rui.visualeditor.internal.preferences.EvPreferences;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
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
import org.osgi.framework.Version;

public class BrowserManager {
	
	public final static byte IE = 0X1;
	public final static byte WEBKIT = 0X2;
	public final static byte XULRUNNER = 0X4;
	
	public final static int SWT_WEBKIT = 0x10000; // SWT.WEBKIT
	
	public boolean ECLIPSE_36;
	
	private byte BROWSERS = 0X0;
	
	private Integer defaultBrowser;
	
	
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
			Version version = Platform.getBundle("org.eclipse.core.runtime").getVersion();
			ECLIPSE_36 = version.getMajor() == 3 && version.getMinor() == 6;
			if ( !ECLIPSE_36 ) {
				Browser b = new Browser( Display.getCurrent().getShells()[0], SWT_WEBKIT );
				BROWSERS = (byte)(BROWSERS | WEBKIT);
				b.dispose();
			}
		} catch ( Throwable e ) {
			
		}

		try {
			Browser b = new Browser( Display.getCurrent().getShells()[0], SWT.MOZILLA );
			BROWSERS = (byte)(BROWSERS | XULRUNNER);
			b.dispose();
		} catch ( Throwable e ) {
			
		}

	}

	public Browser createBrowser( Composite compositeParent ) {
		switch (EvPreferences.getInt( EvConstants.PREFERENCE_RENDERENGINE )) {
			case EvConstants.PREFERENCE_RENDERENGINE_WEBKIT:
				return createWebKit( compositeParent );
			case EvConstants.PREFERENCE_RENDERENGINE_XULRUNNER:
				return createXULRunner( compositeParent );
			default:
				return createNONE( compositeParent );
		}
	}
	
	private Browser createWebKit( Composite compositeParent ) {
		try {
			Browser browser = new Browser( compositeParent, SWT_WEBKIT);
			initializeBrowser(compositeParent.getDisplay(), browser, SWT_WEBKIT);
			return browser;
		} catch( SWTError ex ) {
			Activator.log(ex);
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
			Activator.log(ex);
			showOutOfResourcesMessage(Messages.NL_XULRunner_Out_of_resources_message);
		}
		return null;
	}
	
	private Browser createNONE( Composite compositeParent ) {
		try {
			Browser browser = new Browser( compositeParent, SWT.NONE );
			initializeBrowser(compositeParent.getDisplay(), browser, SWT.NONE);
			return browser;
		} catch( SWTError ex ) {
			Activator.log(ex);
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
	
	public int getDefaultRenderEngine() {
		if (defaultBrowser == null) {
			if (Platform.getOS().equals(Platform.OS_WIN32)) {
				// Win64 must use IE. For 32bit we'll try to default to webkit on Eclipse 3.7 or later. If webkit's not
				// available we'll try to default to XULRunner. Otherwise we use IE.
				defaultBrowser = EvConstants.PREFERENCE_RENDERENGINE_IE;
				if (!Platform.getOSArch().equals(Platform.ARCH_X86_64)) {
					if ((BROWSERS & WEBKIT) != 0) {
						defaultBrowser = EvConstants.PREFERENCE_RENDERENGINE_WEBKIT;
					}
					else if ((BROWSERS & XULRUNNER) != 0) {
						defaultBrowser = EvConstants.PREFERENCE_RENDERENGINE_XULRUNNER;
					}
				}
			}
			else {
				// If Mozilla is available, that's the default. Otherwise fall back on user-configured.
				if ((BROWSERS & XULRUNNER) != 0) {
					defaultBrowser = EvConstants.PREFERENCE_RENDERENGINE_XULRUNNER;
				}
				else {
					defaultBrowser = EvConstants.PREFERENCE_RENDERENGINE_USER_CONFIGURED;
				}
			}
		}
		return defaultBrowser;
	}
	
	public void displayBrowserInfo(Browser browser) {
		Object agent = null;
		Shell shell = null;
		
		if (browser != null) {
			agent = browser.evaluate("return navigator.userAgent;");
			shell = browser.getDisplay().getActiveShell();
		}
		
		if (!(agent instanceof String)) {
			agent = Messages.NL_User_Agent_Error_Retrieving;
		}
		
		if (shell == null) {
			shell = Display.getDefault().getActiveShell();
		}
		
		new MessageDialog(shell, Messages.NL_User_Agent_Title, null,
				(String)agent, MessageDialog.NONE, new String[] {IDialogConstants.OK_LABEL}, 0).open();
	}
}
