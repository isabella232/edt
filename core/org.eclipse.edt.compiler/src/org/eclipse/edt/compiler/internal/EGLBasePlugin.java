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
package org.eclipse.edt.compiler.internal;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;


public class EGLBasePlugin extends AbstractUIPlugin {

	/**
	 * The following are constants defined for the key elements of the 
	 * Package Version extension point defined in this plugin's plugin.xml file
	 */
	public static final String EGL_BASE_PLUGIN_ID = "org.eclipse.edt.compiler"; //$NON-NLS-1$

	public static final String OUTPUT_CODESET = "outputCodeset";//$NON-NLS-1$
	public static final String INCOMPLETE_BUILD_PATH = "incompleteBuildPath";//$NON-NLS-1$
	
	/**
	 * Keep track of the singleton.
	 */
	protected static EGLBasePlugin plugin;
	/**
	 * EGLBasePlugin constructor comment.
	 * @param descriptor org.eclipse.core.runtime.IPluginDescriptor
	 */
	public EGLBasePlugin() {
		super();
		plugin = this;

	}
	/**
	 * Get the singleton instance.
	 */
	public static EGLBasePlugin getPlugin() {
		return plugin;
	}
	
	public static String getHelpIDPrefix() {
		return "org.eclipse.edt.doc.csh"; //TODO no plug-in for this exists yet
	}
	
	/** 
	 * Sets default preference values. These values will be used
	 * until some preferences are actually set using Preference dialog.
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
//		// These settings are needed for default text preferences (such as
//		// red squiggles)!
//		TextEditorPreferenceConstants.initializeDefaultValues(store);

		// These settings will show up when Preference dialog
		// opens up for the first time.
		store.setDefault(OUTPUT_CODESET, "UTF-8"); //$NON-NLS-1$
		
		//No defaults supplied for destination user id and password
	}	
}
