/*******************************************************************************
 * Copyright © 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.model.bde;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public interface ICoreConstants {

	// Target Platform
	String PLATFORM_PATH = "platform_path"; //$NON-NLS-1$
	String SAVED_PLATFORM = "saved_platform"; //$NON-NLS-1$
	String TARGET_MODE = "target_mode"; //$NON-NLS-1$
	String VALUE_USE_THIS = "useThis"; //$NON-NLS-1$
	String VALUE_USE_OTHER = "useOther"; //$NON-NLS-1$
	String CHECKED_PLUGINS = "checkedPlugins"; //$NON-NLS-1$
	String VALUE_SAVED_NONE = "[savedNone]"; //$NON-NLS-1$
	String VALUE_SAVED_ALL = "[savedAll]"; //$NON-NLS-1$
	String VALUE_SAVED_SOME = "savedSome"; //$NON-NLS-1$
	String P_SOURCE_LOCATIONS = "source_locations"; //$NON-NLS-1$
	String PROGRAM_ARGS = "program_args"; //$NON-NLS-1$
	String VM_ARGS = "vm_args"; //$NON-NLS-1$
	String IMPLICIT_DEPENDENCIES = "implicit_dependencies"; //$NON-NLS-1$
	String ADDITIONAL_LOCATIONS = "additional_locations"; //$NON-NLS-1$
	String TARGET_PLATFORM_REALIZATION = "target_platform_realization"; //$NON-NLS-1$
	String POOLED_BUNDLES = "pooled_bundles"; //$NON-NLS-1$
	/**
	 * @since 3.6 - Bug 282708: [target] issues with two versions of the same bundle 
	 */
	String CHECKED_VERSION_PLUGINS = "checkedVersionPlugins"; //$NON-NLS-1$


	/** Constant for the string <code>plugin.xml</code> */
	public final static String PLUGIN_FILENAME_DESCRIPTOR = "plugin.xml"; //$NON-NLS-1$

	/** Constant for the string <code>META-INF/MANIFEST.MF</code> */
	public final static String BUNDLE_FILENAME_DESCRIPTOR = "META-INF/MANIFEST.MF"; //$NON-NLS-1$


	/** Constant for the string <code>.eglpath</code> */
	public final static String EGLPATH_FILENAME = ".eglPath"; //$NON-NLS-1$

	
	public final static String TARGET36 = "3.6"; //$NON-NLS-1$

	// project preferences
	public static final String TARGET_PROFILE = "target.profile"; //$NON-NLS-1$
	/**
	 * Configures launch shortcuts visible in the manifest editor for a project.
	 * Value is a comma separated list of <code>org.eclipse.pde.ui.launchShortcuts</code>
	 * extension identifiers.
	 * 
	 * @since 3.6 
	 */
	public static final String MANIFEST_LAUNCH_SHORTCUTS = "manifest.launchShortcuts"; //$NON-NLS-1$

	/**
	 * Configures the export wizard used in the manifest editor for a project.
	 * Value is an <code>org.eclipse.ui.exportWizards</code> extension identifier.
	 * 
	 * @since 3.6
	 */
	public static final String MANIFEST_EXPORT_WIZARD = "manifest.exportWizard"; //$NON-NLS-1$

	

	// Common paths
	public static IPath PLUGIN_PATH = new Path(PLUGIN_FILENAME_DESCRIPTOR);
	public static IPath EGLPATH_PATH = new Path(EGLPATH_FILENAME);


	/**
	 * File extension for target definitions
	 */
	public static final String TARGET_FILE_EXTENSION = "target"; //$NON-NLS-1$

	/**
	 * Preference key for the active workspace target platform handle memento 
	 */
	public static final String WORKSPACE_TARGET_HANDLE = "workspace_target_handle"; //$NON-NLS-1$

	/**
	 * Explicit setting when the user chooses no target for the workspace.
	 */
	public static final String NO_TARGET = "NO_TARGET"; //$NON-NLS-1$
}

