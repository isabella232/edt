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
package org.eclipse.edt.ide.rui.visualeditor.internal.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

public class EvHelp {

	// Help context identifiers as used in code, and in HelpContexts.xml
	// EvEditorHelp.setHelp( control, EvEditorHelp.DESIGN_AREA );
	//----------------------------------------------------------------------------------
	public static String		COLOR_SELECTION_DIALOG		= "ColorSelectionDialog";
	public static String		DESIGN_AREA					= "DesignArea";
	public static String		DESIGN_TOOLBAR				= "DesignToolbar";
	public static String		PREFERENCES					= "Preferences";
	public static String		PREFERENCES_BIDI			= "PreferencesBidi";
	public static String		PREFERENCES_BROWSER_SIZE	= "PreferencesBrowserSize";
	public static String		PREFERENCES_GENERAL			= "PreferencesGeneral";
	public static String		PREFERENCES_LANGUAGE		= "PreferencesLanguage";
	public static String		PREVIEW_AREA				= "PreviewArea";
	public static String		PREVIEW_TOOLBAR				= "PreviewToolbar";
	public static String		PROPERTY_PAGE				= "PropertyPage";
	public static String		VISUAL_EDITOR				= "VisualEditor";
	public static String		DATA_VIEW					= "DataView";
	public static String		INSERT_WIDGET_WIZARD		= "InsertWidgetWizard";
	public static String		NEW_EGL_VARIABLE_WIZARD		= "NewEGLVariableWizard";
	
	/**
	 * Set the context id for a control where the id is prefixed by the plug-in ID
	 */
	public static void setHelp( Control c, String id ) {
		if( c == null || c.isDisposed() == true )
			return;

		//		System.out.println( "EvHelp.setHelp " + c + " " + id );

		PlatformUI.getWorkbench().getHelpSystem().setHelp( c, EvConstants.HELP_PREFIX + id );
	}

	/**
	 * Set the context id for an action
	 */
	public static void setHelp( IAction c, String id ) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp( c, EvConstants.HELP_PREFIX + id );
	}

	/**
	 * Set the context id for a menu item
	 */
	public static void setHelp( MenuItem c, String id ) {
		if( c == null || c.isDisposed() == true )
			return;

		PlatformUI.getWorkbench().getHelpSystem().setHelp( c, EvConstants.HELP_PREFIX + id );
	}
}
