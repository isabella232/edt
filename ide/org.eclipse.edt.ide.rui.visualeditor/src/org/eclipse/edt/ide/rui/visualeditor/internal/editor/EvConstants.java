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
package org.eclipse.edt.ide.rui.visualeditor.internal.editor;

import org.eclipse.edt.ide.rui.visualeditor.internal.nl.Messages;

public interface EvConstants {
	
	public static final String	PLUGIN_ID											= "org.eclipse.edt.ide.rui.visualeditor";
	public static final String	PLUGIN_ID_DOT										= PLUGIN_ID + ".";

	public static final String	DESIGN_AREA_CONTEXT_MENU_ID							= PLUGIN_ID_DOT + "designarea.contextmenu";

	public static final String	ICON_FOLDER											= "icons";
	public static final String	ICON_BUSY_INDICATOR									= "elcl16/busyindicator_obj.gif";
	public static final String	ICON_COLOR_SELECTION_DIALOG							= "elcl16/colorselectiondialog_obj.gif";
	public static final String	ICON_CREATE_FUNCTION								= "elcl16/createfunction_obj.gif";
	public static final String	ICON_DEFAULT_WIDGET									= "elcl16/defaultwidget_obj.gif";
	public static final String	ICON_EDITOR											= "obj16/ruivisualeditor.gif";
	public static final String	ICON_LAUNCH_EXTERNAL_BROWSER						= "elcl16/launchexternalbrowser_obj.gif";
	public static final String	ICON_PREFERENCES									= "elcl16/preferences_obj.gif";
	public static final String	ICON_REFRESH_PALETTE								= "elcl16/refreshpalette_obj.gif";
	public static final String	ICON_REFRESH_WEB_PAGE								= "elcl16/refreshwebpage_obj.gif";
	public static final String	ICON_SHOW_BROWSER_SIZE_CONTROLS						= "elcl16/showbrowsersizecontrols_obj.gif";
	public static final String	ICON_SHOW_TRANSPARENCY_CONTROLS						= "elcl16/showtransparencycontrols_obj.gif";
	public static final String	ICON_TRANSPARENCY_NONE								= "elcl16/transparencynone_obj.gif";
	public static final String	ICON_TRANSPARENCY_FIXED								= "elcl16/transparencyfixed_obj.gif";
	public static final String	ICON_TRANSPARENCY_VARIABLE							= "elcl16/transparencyvariable_obj.gif";
	public static final String	ICON_PROPERTY_EVENT_PLUS							= "elcl16/plus_obj.gif";
	public static final String	ICON_INSERT_WIDGET_WIZARD							= "obj16/insertWidgetWizard.gif";
	public static final String	ICON_NEW_EGL_VARIABLE_WIZARD						= "obj16/newEGLVariableWizard.gif";
	public static final String	ICON_DEFAULT_WIDGET_SMALL							= "ctool16/widget.gif";
	public static final String	ICON_DEFAULT_WIDGET_LARGE							= "ctool24/widget.gif";
	public static final String	ICON_ORDER_UP_BUTTON								= "obj16/up.gif";
	public static final String	ICON_ORDER_DOWN_BUTTON								= "obj16/down.gif";
	
	public static final String	IMAGE_ALIGNMENT_TEST								= "alignmenttest.gif";
	public static final String	HTML_ALIGNMENT_TEST									= "alignmenttest.html";
	public static final String	HTML_EMPTY											= "empty.html";

	public static final String	WIDGET_PROVIDER_EXTENSION_POINT						= PLUGIN_ID_DOT + "widgetprovider";

	// Color pattern
	//--------------
	public static final double	LUMINOSITY_WHITE_BLACK_BOUNDARY						= 150.0;

	// Help constants
	//---------------
	public static final String	HELP_PREFIX											= "org.eclipse.edt.ide.cshelp.";

	// Preference constants
	//---------------------
	public static final String	PREFERENCE_PREFIX									= PLUGIN_ID_DOT;
	
	// Last tab folder page which holds the index of the last used preference page tab folder page
	//--------------------------------------------------------------------------------------------
	public static final String	PREFERENCE_PAGE_TAB									= PREFERENCE_PREFIX + "PreferencePageLast";
	
	// Editor tab on opening
	//----------------------
	public static final String	PREFERENCE_EDITOR_OPENING_TAB						= PREFERENCE_PREFIX + "EditorOpeningTab";
	public static final int		PREFERENCE_DEFAULT_EDITOR_OPENING_TAB				= 0;
	
	// Browser size
	//-------------
	public static final String	PREFERENCE_BROWSER_SIZE_CONTROLS_VISIBLE			= PREFERENCE_PREFIX + "BrowserSizeControlsVisible";
	public static final String	PREFERENCE_BROWSER_SIZE_DEFAULT_WIDTH				= PREFERENCE_PREFIX + "BrowserSizeDefaultWidth";
	public static final String	PREFERENCE_BROWSER_SIZE_DEFAULT_HEIGHT				= PREFERENCE_PREFIX + "BrowserSizeDefaultHeight";
	public static final String	PREFERENCE_BROWSER_SIZE_MINIMUM_WIDTH				= PREFERENCE_PREFIX + "BrowserSizeMinimumWidth";
	public static final String	PREFERENCE_BROWSER_SIZE_MINIMUM_HEIGHT				= PREFERENCE_PREFIX + "BrowserSizeMinimumHeight";
	public static final String	PREFERENCE_BROWSER_SIZE_MAXIMUM_WIDTH				= PREFERENCE_PREFIX + "BrowserSizeMaximumWidth";
	public static final String	PREFERENCE_BROWSER_SIZE_MAXIMUM_HEIGHT				= PREFERENCE_PREFIX + "BrowserSizeMaximumHeight";

	public static final boolean	PREFERENCE_DEFAULT_BROWSER_SIZE_CONTROLS_VISIBLE	= false;
	public static final int		PREFERENCE_DEFAULT_BROWSER_SIZE_DEFAULT_WIDTH		= 1000;
	public static final int		PREFERENCE_DEFAULT_BROWSER_SIZE_DEFAULT_HEIGHT		= 1000;
	public static final int		PREFERENCE_DEFAULT_BROWSER_SIZE_MINIMUM_WIDTH		= 200;
	public static final int		PREFERENCE_DEFAULT_BROWSER_SIZE_MINIMUM_HEIGHT		= 200;
	public static final int		PREFERENCE_DEFAULT_BROWSER_SIZE_MAXIMUM_WIDTH		= 3000;
	public static final int		PREFERENCE_DEFAULT_BROWSER_SIZE_MAXIMUM_HEIGHT		= 5000;
	
	// Transparency
	//-------------
	public static final String	PREFERENCE_TRANSPARENCY_CONTROLS_VISIBLE			= PREFERENCE_PREFIX + "TransparencyControlsVisible";
	public static final String	PREFERENCE_SEMITRANSPARENCY_MODE					= PREFERENCE_PREFIX + "SemiTransparencyMode";
	public static final String	PREFERENCE_SEMITRANSPARENCY_AMOUNT					= PREFERENCE_PREFIX + "SemiTransparencyAmount";
	public static final String	PREFERENCE_SEMITRANSPARENCY_WHILE_DRAGGING			= PREFERENCE_PREFIX + "SemiTransparencyWhileDragging";

	public static final int		SEMITRANSPARENCY_NONE								= 0;
	public static final int		SEMITRANSPARENCY_FIXED								= 1;
	public static final int		SEMITRANSPARENCY_VARIABLE							= 2;

	public static final boolean	PREFERENCE_DEFAULT_TRANSPARENCY_CONTROLS_VISIBLE	= false;
	public static final int		PREFERENCE_DEFAULT_SEMITRANSPARENCY_MODE			= SEMITRANSPARENCY_NONE;
	public static final int		PREFERENCE_DEFAULT_SEMITRANSPARENCY_AMOUNT			= 255;
	public static final boolean	PREFERENCE_DEFAULT_SEMITRANSPARENCY_WHILE_DRAGGING	= true;
	
	// Colors and pattern
	//-------------------
	public static final String	PREFERENCE_COLOR_DROP_LOCATION_POTENTIAL			= PREFERENCE_PREFIX + "ColorDropLocationPotential";
	public static final String	PREFERENCE_COLOR_DROP_LOCATION_SELECTED				= PREFERENCE_PREFIX + "ColorDropLocationSelected";
	public static final String	PREFERENCE_COLOR_SELECTION							= PREFERENCE_PREFIX + "ColorSelection";
	public static final String	PREFERENCE_PATTERN_DROP_LOCATION_SELECTED			= PREFERENCE_PREFIX + "PatternDropLocationSelected";
	public static final String	PREFERENCE_PATTERN_SELECTION						= PREFERENCE_PREFIX + "PatternSelection";

	public static final String	PREFERENCE_DEFAULT_COLOR_DROP_LOCATION_POTENTIAL	= "253 253 237";
	public static final String	PREFERENCE_DEFAULT_COLOR_DROP_LOCATION_SELECTED		= "234 248 213";
	public static final String	PREFERENCE_DEFAULT_COLOR_SELECTION					= "0 0 140";
	public static final boolean	PREFERENCE_DEFAULT_PATTERN_DROP_LOCATION_SELECTED	= false;
	public static final boolean	PREFERENCE_DEFAULT_PATTERN_SELECTION				= true;
	
	// Performance
	//------------
	public static final String	PREFERENCE_PERFORMANCE								= PREFERENCE_PREFIX + "Performance";
	public static final int		PREFERENCE_PERFORMANCE_OPTIMIZE_SPEED				= 0;
	public static final int		PREFERENCE_PERFORMANCE_OPTIMIZE_RESOURCES			= 1;
	
	public static final int		PREFERENCE_DEFAULT_PERFORMANCE						= PREFERENCE_PERFORMANCE_OPTIMIZE_SPEED;

	// Performance
	//------------
	public static final String	PREFERENCE_RENDERENGINE								= PREFERENCE_PREFIX + "RenderEngine";
	public static final int		PREFERENCE_RENDERENGINE_DEFAULT						= 0;
	public static final int		PREFERENCE_RENDERENGINE_WEBKIT						= 1;
	public static final int		PREFERENCE_RENDERENGINE_XULRUNNER					= 2;
	public static final int		PREFERENCE_RENDERENGINE_IE							= 3;
	
	public static final int		PREFERENCE_DEFAULT_RENDERENGINE						= PREFERENCE_RENDERENGINE_DEFAULT;

	// Widget creation
	//----------------
	public static final String	PREFERENCE_PROMPT_FOR_A_NEW_WIDGET_NAME				= PREFERENCE_PREFIX + "PromptForNewWidgetName";
	public static final boolean	PREFERENCE_DEFAULT_PROMPT_FOR_A_NEW_WIDGET_NAME		= true;
	
	// IBMBIDI Append Start
	// Bidirectional properties
	//-------------------------
	public static final String	ICON_BIDI_SETTINGS									= "elcl16/bidisettings_obj.gif";
	public static final String	PREFERENCE_BIDI_WIDGET_ORIENTATION					= PREFERENCE_PREFIX + "BidiWidgetOrientation";
	public static final String	PREFERENCE_BIDI_TEXT_LAYOUT							= PREFERENCE_PREFIX + "BidiTextLayout";
	public static final String	PREFERENCE_BIDI_REVERSE_TEXT_DIRECTION				= PREFERENCE_PREFIX + "BidiReverseTextDirection";
	public static final String	PREFERENCE_BIDI_SYM_SWAPPING						= PREFERENCE_PREFIX + "BidiSymSwap";
	public static final String	PREFERENCE_BIDI_NUM_SWAPPING						= PREFERENCE_PREFIX + "BidiNumSwap";
	
	public static final String	FIELD_NAME_BIDI_WIDGET_ORIENTATION					= "widgetOrientation";
	public static final String	FIELD_NAME_BIDI_TEXT_LAYOUT							= "textLayout";
	public static final String	FIELD_NAME_BIDI_REVERSE_TEXT_DIRECTION				= "reverseTextDirection";
	public static final String	FIELD_NAME_BIDI_SYM_SWAPPING						= "symmetricSwap";
	public static final String	FIELD_NAME_BIDI_NUM_SWAPPING						= "numericSwap";

	public static final String	PREFERENCE_DEFAULT_BIDI_WIDGET_ORIENTATION			= Messages.NL_BIDI_LTR;
	public static final String	PREFERENCE_DEFAULT_BIDI_TEXT_LAYOUT					= Messages.NL_BIDI_Logical;
	public static final String	PREFERENCE_DEFAULT_BIDI_REVERSE_TEXT_DIRECTION		= Messages.NL_BIDI_No;
	public static final String	PREFERENCE_DEFAULT_BIDI_SYM_SWAPPING				= Messages.NL_BIDI_Yes;
	public static final String	PREFERENCE_DEFAULT_BIDI_NUM_SWAPPING				= Messages.NL_BIDI_Yes;
	
	//The filter types for filtering the properties and events in widget
	public static final int FILTER_SHOW_ALL = 1;
	public static final int FILTER_HIDE_EXCLUDED = 2;

}
