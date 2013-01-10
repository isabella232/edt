/*******************************************************************************
 * Copyright © 1994, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.nl;

import org.eclipse.osgi.util.NLS;

/**
 * This class holds static strings used for national language translation purposes.
 * Each of these static strings has an equivalent in the messages.properties file.
 */
public class Tooltips extends NLS {

	public static final String	BUNDLE_NAME	= "org.eclipse.edt.ide.rui.visualeditor.internal.nl.tooltips";	//$NON-NLS-1$

	private Tooltips() {
		// Do not instantiate
	}

	static {
		NLS.initializeMessages( BUNDLE_NAME, Tooltips.class );
	}

	// Tool bars
	//----------
	// IBM BIDI Append Start
	public static String	NL_Configure_bidirectional_options;
	// IBM BIDI Append End

	public static String	NL_Configure_preferences;
	public static String	NL_Dotted_transparency_pattern;
	public static String	NL_Fully_transparent;
	public static String	NL_Refresh_palette;
	public static String	NL_Refresh_web_page;
	public static String	NL_Show_browser_size_controls;
	public static String	NL_Show_the_web_page_in_an_external_web_browser;
	public static String	NL_Show_transparency_controls;
	public static String	NL_Variable_transparency;
	public static String	NL_User_agent;

	// Design page
	//------------
	public static String	NL_Browser_width;
	public static String	NL_Browser_height;
	public static String	NL_Reset_browser_to_default_size;
	
	// Properties
	//-----------
	public static String	NL_Create_a_new_event_handling_function;
	
	// Property editors
	//-----------------
	public static String	NL_Press_to_select_a_color;
	public static String	NL_Press_to_modify_the_list;
	
	// Insert Widget Wizard
	//-----------------
	public static String	NL_IWWP_Order_Up;
	public static String	NL_IWWP_Order_Down;
}
