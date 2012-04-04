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
package org.eclipse.edt.debug.ui.launching;

import org.eclipse.osgi.util.NLS;

public class EGLLaunchingMessages extends NLS
{
	private static final String BUNDLE_NAME = "org.eclipse.edt.debug.ui.launching.EGLLaunchingMessages"; //$NON-NLS-1$
	
	static
	{
		// load message values from bundle file
		NLS.initializeMessages( BUNDLE_NAME, EGLLaunchingMessages.class );
	}
	
	public static String launch_shortcut_usage;
	public static String launch_shortcut_missing_file_error;
	public static String launch_shortcut_multiple_files_error;
	public static String launch_error_dialog_title;
	public static String launch_config_selection_dialog_title;
	public static String launch_config_selection_dialog_message;
	public static String java_launch_no_files;
	public static String java_launch_file_selection_title;
	public static String java_launch_file_selection_msg;
	
	public static String egl_java_main_tab_name;
	public static String egl_java_main_tab_project_label;
	public static String egl_java_main_tab_browse_button;
	public static String egl_java_main_tab_program_file_label;
	public static String egl_java_main_tab_project_browse_title;
	public static String egl_java_main_tab_project_browse_message;
	public static String egl_java_main_tab_program_file_search_title;
	public static String egl_java_main_tab_program_file_search_message;
	public static String egl_java_main_tab_search_button;
	public static String egl_java_main_tab_program_file_not_in_project;
	
	public static String egl_java_main_launch_configuration_type_not_found;
	public static String egl_java_main_launch_configuration_create_config_failed;
	public static String egl_java_main_launch_configuration_no_program_found;
	public static String egl_java_main_launch_configuration_no_project_specified;
	public static String egl_java_main_launch_configuration_invalid_project;
	public static String egl_java_main_launch_configuration_no_program_file_specified;
	public static String egl_java_main_launch_configuration_invalid_program_file;
	public static String egl_java_main_launch_configuration_file_not_program;
	public static String egl_java_main_launch_configuration_missing_java_type;
}
