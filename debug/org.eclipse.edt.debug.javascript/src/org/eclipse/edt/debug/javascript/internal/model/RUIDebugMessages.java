/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.javascript.internal.model;

import org.eclipse.osgi.util.NLS;

public class RUIDebugMessages extends NLS
{
	private RUIDebugMessages()
	{
		// No instances.
	}
	
	static
	{
		NLS.initializeMessages( "org.eclipse.edt.debug.javascript.internal.model.RUIDebugMessages", RUIDebugMessages.class ); //$NON-NLS-1$
	}
	public static String rui_debug_terminated_msg;
	public static String rui_debug_refreshed_msg;
	public static String rui_stack_frame_label_basic;
	public static String rui_stack_frame_watch_exprs_unsupported;
	public static String rui_thread_thread_name;
	public static String rui_thread_label_running;
	public static String rui_thread_label_suspended;
	public static String rui_thread_label_suspendedAtBreakpoint;
	public static String rui_thread_label_stepping;
	public static String rui_thread_label_terminated;
	public static String rui_debug_target_label;
	public static String rui_debug_utils_missing_program_file;
	public static String rui_debug_utils_config_type_not_found;
	public static String rui_debug_utils_create_config_failed;
	public static String rui_debug_utils_no_handler_found;
	public static String rui_load_launch_configuration_no_project_specified;
	public static String rui_load_launch_configuration_invalid_project;
	public static String rui_load_launch_configuration_no_handler_file_specified;
	public static String rui_load_launch_configuration_invalid_handler_file;
	public static String rui_load_launch_configuration_file_not_handler;
	public static String rui_load_main_tab_name;
	public static String rui_load_main_tab_project_label;
	public static String rui_load_main_tab_browse_button;
	public static String rui_load_main_tab_search_button;
	public static String rui_load_main_tab_handler_file_label;
	public static String rui_load_main_tab_handler_file_search_title;
	public static String rui_load_main_tab_handler_file_search_message;
	public static String rui_load_main_tab_handler_file_not_in_project;
	public static String DEBUG_REMOTECLIENTNOTRESPONDING_TITLE;
	public static String DEBUG_REMOTECLIENTNOTRESPONDING_MSG;
	public static String rui_launch_error_title;
}
