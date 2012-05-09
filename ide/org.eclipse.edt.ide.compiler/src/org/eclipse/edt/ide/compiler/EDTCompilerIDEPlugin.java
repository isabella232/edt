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
package org.eclipse.edt.ide.compiler;

import org.eclipse.edt.ide.core.EDTRuntimeContainer;
import org.eclipse.edt.ide.core.EDTRuntimeContainerEntry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class EDTCompilerIDEPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.edt.ide.compiler"; //$NON-NLS-1$
	
	/**
	 * Preference key for the default Java generation directory.
	 */
	public static final String PREFERENCE_DEFAULT_JAVAGEN_DIRECTORY = PLUGIN_ID + ".defaultJavaGenDirectory"; //$NON-NLS-1$
	public static final String PREFERENCE_DEFAULT_JAVASCRIPTGEN_DIRECTORY = PLUGIN_ID + ".defaultJavaScriptGenDirectory"; //$NON-NLS-1$
	
	/**
	 * Project-level property key indicating the directory to use for the Java generator.
	 */
	public static final String PROPERTY_JAVAGEN_DIR = "javaGenDirectory"; //$NON-NLS-1$
	public static final String PROPERTY_JAVAGEN_ARGUMENTS = "javaGenArguments"; //$NON-NLS-1$
	public static final String PROPERTY_JAVASCRIPTGEN_DIR = "jsGenDirectory"; //$NON-NLS-1$
	public static final String PROPERTY_JAVASCRIPTGEN_ARGUMENTS = "jsGenArguments"; //$NON-NLS-1$

	// The shared instance
	private static EDTCompilerIDEPlugin plugin;
	
	public static final EDTRuntimeContainer JAVA_RUNTIME_CONTAINER = new EDTRuntimeContainer("javagen", EDTCompilerStrings.javaRuntimeName, //$NON-NLS-1$
			EDTCompilerStrings.javaRuntimeDescription, new EDTRuntimeContainerEntry[] {
					new EDTRuntimeContainerEntry("org.eclipse.edt.runtime.java.jee", "bin", null, "org.eclipse.edt.runtime.java.jee.source", "src", null), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					new EDTRuntimeContainerEntry("org.eclipse.edt.runtime.java.sql", "bin", null, "org.eclipse.edt.runtime.java.sql.source", "src", null), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					new EDTRuntimeContainerEntry("org.eclipse.edt.runtime.java", "bin", null, "org.eclipse.edt.runtime.java.source", "src", null), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					new EDTRuntimeContainerEntry("com.ibm.icu", "bin", null, "com.ibm.icu.source", "src", null)
			});
	
	public static final EDTRuntimeContainer JAVACORE_RUNTIME_CONTAINER = new EDTRuntimeContainer("javagen", EDTCompilerStrings.javaRuntimeName, //$NON-NLS-1$
		EDTCompilerStrings.javaRuntimeDescription, new EDTRuntimeContainerEntry[] {
				new EDTRuntimeContainerEntry("org.eclipse.edt.runtime.java", "bin", null, "org.eclipse.edt.runtime.java.source", "src", null), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				new EDTRuntimeContainerEntry("com.ibm.icu", "bin", null, "com.ibm.icu.source", "src", null)
		});

	/**
	 * The constructor
	 */
	public EDTCompilerIDEPlugin() {
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
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EDTCompilerIDEPlugin getDefault() {
		return plugin;
	}
	
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(PREFERENCE_DEFAULT_JAVAGEN_DIRECTORY, "generatedJava"); //$NON-NLS-1$
		store.setDefault(PREFERENCE_DEFAULT_JAVASCRIPTGEN_DIRECTORY, "generatedJavaScript"); //$NON-NLS-1$
	}
}
