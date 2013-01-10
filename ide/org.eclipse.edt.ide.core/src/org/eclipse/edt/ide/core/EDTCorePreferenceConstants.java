/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Preference constants used in the EDT Core preference store. Clients should only read the
 * EDT Core preference store using these values. Clients are not allowed to modify the
 * preference store programmatically.
 * <p>
 * This class it is not intended to be instantiated or subclassed by clients.
 * 
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
  */
public class EDTCorePreferenceConstants {

	private EDTCorePreferenceConstants() {
	}
	
	/**
	 * A named preference that specifies the name of the EGL source folder
	 * in an EGL project.
	 * <p>
	 * Value is of type <code>String</code>
	 * </p>
	 */
	public static final String EGL_SOURCE_FOLDER = "eglSourceFolder"; //$NON-NLS-1$
	public static final String EGL_SOURCE_FOLDER_VALUE = "EGLSource"; //$NON-NLS-1$
	
	/**
	 * A named preference that specifies the name of the EGL output folder
	 * in an EGL project.
	 * <p>
	 * Value is of type <code>String</code>
	 * </p>
	 */
	public static final String EGL_OUTPUT_FOLDER = "eglOutputFolder"; //$NON-NLS-1$
	public static final String EGL_OUTPUT_FOLDER_VALUE = "EGLBin"; //$NON-NLS-1$
	
	/**
	 * A named preference that specifies the default compiler.
	 * <p>
	 * Value is of type <code>String</code>
	 * </p>
	 */
	public static final String COMPILER_ID = "compilerId";
	
	/**
	 * A named preference that specifies the id(s) of the default
	 * generator(s).  Multiple generators id are separated by commas. 
	 * <p>
	 * Value is of type <code>String</code>
	 * </p>
	 */
	public static final String GENERATOR_IDS = "generatorIds";
	
	/**
	 * A named preference that indicates a rebuild is needed
	 * <p>
	 * Value is of type <code>String</code>
	 * </p>
	 */
	public static final String BUILD_FLAG = "buildFlag";

	/**
	 * Initializes the given preference store with the default values.
	 * 
	 * @param store the preference store to be initialized
	 */
	public static void initializeDefaultValues(IPreferenceStore store) {		
		// BasePreferencePage
		store.setDefault( EGL_SOURCE_FOLDER, EGL_SOURCE_FOLDER_VALUE );
		store.setDefault( EGL_OUTPUT_FOLDER, EGL_OUTPUT_FOLDER_VALUE );
		
		// CompilerPreferencePage
		store.setDefault( COMPILER_ID, "org.eclipse.edt.ide.compiler.edtCompiler" ); 
		store.setDefault( GENERATOR_IDS, "org.eclipse.edt.ide.gen.JavaGenProvider,org.eclipse.edt.ide.gen.JavaScriptGenProvider,org.eclipse.edt.ide.gen.JavaScriptDevGenProvider" );
	}

	/**
	 * Returns the EDT UI preference store.
	 * 
	 * @return the EDT UI preference store
	 */
	public static IPreferenceStore getPreferenceStore() {
		return EDTCoreIDEPlugin.getPlugin().getPreferenceStore();
	}
	
	/**
	 * Returns the value for the given key in the given context.
	 * 
	 * @param key The preference key
	 * @param project The current context or <code>null</code> if no context is available and the
	 * workspace setting should be taken. Note that passing <code>null</code> should
	 * be avoided.
	 * @return Returns the current value for the string.
	 * @since 3.1
	 */
	@SuppressWarnings("deprecation")
	public static String getPreference(String key, IJavaProject project) {
		String val;
		if (project != null) {
			val= new ProjectScope(project.getProject()).getNode(EDTCoreIDEPlugin.PLUGIN_ID).get(key, null);
			if (val != null) {
				return val;
			}
		}
		val= new InstanceScope().getNode(EDTCoreIDEPlugin.PLUGIN_ID).get(key, null);
		if (val != null) {
			return val;
		}
		return new DefaultScope().getNode(EDTCoreIDEPlugin.PLUGIN_ID).get(key, null);
	}
}

