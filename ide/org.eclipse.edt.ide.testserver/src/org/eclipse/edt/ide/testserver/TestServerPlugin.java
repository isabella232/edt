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
package org.eclipse.edt.ide.testserver;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class TestServerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.edt.ide.testserver"; //$NON-NLS-1$

	// The shared instance
	private static TestServerPlugin plugin;
	
	public static final String TEST_SERVER_CONTRIBUTION_ID = TestServerPlugin.PLUGIN_ID + ".testServerExtension"; //$NON-NLS-1$
	
	private static final Object syncObj = new Object();
	private static AbstractTestServerContribution[] contributions;
	
	/**
	 * The constructor
	 */
	public TestServerPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		
		synchronized (syncObj) {
			if (contributions != null) {
				for (AbstractTestServerContribution contrib : contributions) {
					contrib.dispose();
				}
				contributions = null;
			}
		}
	}
	
	/**
	 * @return the test server contributions.
	 */
	public static AbstractTestServerContribution[] getContributions() {
		if (plugin == null) {
			return new AbstractTestServerContribution[0];
		}
		
		synchronized (syncObj) {
			if (contributions == null) {
				IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(TEST_SERVER_CONTRIBUTION_ID);
				List<AbstractTestServerContribution> contribs = new ArrayList<AbstractTestServerContribution>(elements.length);
				
				for (IConfigurationElement element : elements) {
					try {
						Object o = element.createExecutableExtension("class"); //$NON-NLS-1$
						if (o instanceof AbstractTestServerContribution) {
							contribs.add((AbstractTestServerContribution)o);
						}
					}
					catch (CoreException e) {
						TestServerPlugin.getDefault().log(e);
					}
				}
				
				contributions = contribs.toArray(new AbstractTestServerContribution[contribs.size()]);
			}
			return contributions;
		}
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static TestServerPlugin getDefault() {
		return plugin;
	}
	
	public static final Shell getShell() {
		Shell shell = null;
		Display display = getDisplay();
		if (display != null) {
			shell = display.getActiveShell();
		}
		
		if (shell == null) {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (workbenchWindow != null) {
				shell = workbenchWindow.getShell();
			}
		}
		
		return shell;
	}
	
	public static final Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}
	
	public void log(Exception e) {
		getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}
	
	public void log(String msg) {
		getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg));
	}
	
	public void log(String msg, Exception e) {
		getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}
	
	public void logWarning(String msg) {
		getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, msg));
	}
}
