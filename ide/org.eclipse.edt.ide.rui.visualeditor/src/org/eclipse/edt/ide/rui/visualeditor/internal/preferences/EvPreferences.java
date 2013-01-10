/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.rui.visualeditor.internal.preferences;

import java.util.StringTokenizer;

import org.eclipse.edt.ide.rui.visualeditor.internal.editor.EvConstants;
import org.eclipse.edt.ide.rui.visualeditor.internal.util.BrowserManager;
import org.eclipse.edt.ide.rui.visualeditor.plugin.Activator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;


/**
 * A class that contains static methods to access and modify the editor's preference values.
 */
public class EvPreferences {

	protected static IPreferenceStore	_preferenceStore	= null;

	/**
	 * 
	 */
	protected static String convertRGBToString( RGB rgb ) {
		if( rgb != null ) {
			return rgb.red + "," + rgb.green + "," + rgb.blue; //$NON-NLS-1$ //$NON-NLS-2$
		}
		else {
			return "0,0,0"; //$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	protected static RGB convertStringToRGB( String value ) {
		RGB ret = null;

		if( value != null ) {
			StringTokenizer st = new StringTokenizer( value, "," ); //$NON-NLS-1$
			if( st.hasMoreTokens() ) {
				try {
					int red = Integer.parseInt( st.nextToken() );
					int green = Integer.parseInt( st.nextToken() );
					int blue = Integer.parseInt( st.nextToken() );
					ret = new RGB( red, green, blue );
				}
				catch( Exception ex ) {
				}
			}
		}

		if( ret == null ) {
			ret = new RGB( 0, 0, 0 );
		}

		return ret;
	}

	/**
	 * 
	 */
	public static boolean getBoolean( String name ) {
		initialize();
		return _preferenceStore.getBoolean( name );
	}

	/**
	 * 
	 */
	public static double getDouble( String name ) {
		initialize();
		return _preferenceStore.getDouble( name );
	}

	/**
	 * 
	 */
	public static float getFloat( String name ) {
		initialize();
		return _preferenceStore.getFloat( name );
	}

	/**
	 * 
	 */
	public static int getInt( String name ) {
		initialize();
		return _preferenceStore.getInt( name );
	}

	/**
	 * Returns the preference store
	 */
	public static IPreferenceStore getPreferenceStore() {
		initialize();
		return _preferenceStore;
	}

	/**
	 * 
	 */
	public static RGB getRGB( String name ) {
		initialize();
		RGB rgb = convertStringToRGB( _preferenceStore.getString( name ) );
		return rgb;
	}

	/**
	 * 
	 */
	public static String getString( String name ) {
		initialize();
		return _preferenceStore.getString( name );
	}

	/**
	 * Initializes the preference store with initial values if they have not been set.
	 * Both default values and user defined values are set.
	 */
	protected static void initialize() {
		if( _preferenceStore != null )
			return;

		// Initialize the preference store
		//--------------------------------
		_preferenceStore = Activator.getDefault().getPreferenceStore();

		// Initialize default values
		// Always do this in case they get changed during development
		//-----------------------------------------------------------
		_preferenceStore.setDefault( EvConstants.PREFERENCE_EDITOR_OPENING_TAB, EvConstants.PREFERENCE_DEFAULT_EDITOR_OPENING_TAB );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_BROWSER_SIZE_CONTROLS_VISIBLE, EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_CONTROLS_VISIBLE );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_WIDTH, EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_MINIMUM_WIDTH );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_HEIGHT, EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_MINIMUM_HEIGHT );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_WIDTH, EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_MAXIMUM_WIDTH );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_HEIGHT, EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_MAXIMUM_HEIGHT );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_WIDTH, EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_DEFAULT_WIDTH );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_HEIGHT, EvConstants.PREFERENCE_DEFAULT_BROWSER_SIZE_DEFAULT_HEIGHT );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_TRANSPARENCY_CONTROLS_VISIBLE, EvConstants.PREFERENCE_DEFAULT_TRANSPARENCY_CONTROLS_VISIBLE );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_SEMITRANSPARENCY_WHILE_DRAGGING, EvConstants.PREFERENCE_DEFAULT_SEMITRANSPARENCY_WHILE_DRAGGING );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_SEMITRANSPARENCY_MODE, EvConstants.PREFERENCE_DEFAULT_SEMITRANSPARENCY_MODE );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_SEMITRANSPARENCY_AMOUNT, EvConstants.PREFERENCE_DEFAULT_SEMITRANSPARENCY_AMOUNT );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_POTENTIAL, EvConstants.PREFERENCE_DEFAULT_COLOR_DROP_LOCATION_POTENTIAL );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_SELECTED, EvConstants.PREFERENCE_DEFAULT_COLOR_DROP_LOCATION_SELECTED );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_COLOR_SELECTION, EvConstants.PREFERENCE_DEFAULT_COLOR_SELECTION );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_PATTERN_DROP_LOCATION_SELECTED, EvConstants.PREFERENCE_DEFAULT_PATTERN_DROP_LOCATION_SELECTED );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_PATTERN_SELECTION, EvConstants.PREFERENCE_DEFAULT_PATTERN_SELECTION );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_PERFORMANCE, EvConstants.PREFERENCE_DEFAULT_PERFORMANCE );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_RENDERENGINE, BrowserManager.getInstance().getDefaultRenderEngine() );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_PROMPT_FOR_A_NEW_WIDGET_NAME, EvConstants.PREFERENCE_DEFAULT_PROMPT_FOR_A_NEW_WIDGET_NAME );

		// IBMBIDI Append Start
		_preferenceStore.setDefault( EvConstants.PREFERENCE_BIDI_WIDGET_ORIENTATION, EvConstants.PREFERENCE_DEFAULT_BIDI_WIDGET_ORIENTATION );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_BIDI_TEXT_LAYOUT, EvConstants.PREFERENCE_DEFAULT_BIDI_TEXT_LAYOUT );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_BIDI_REVERSE_TEXT_DIRECTION, EvConstants.PREFERENCE_DEFAULT_BIDI_REVERSE_TEXT_DIRECTION );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_BIDI_SYM_SWAPPING, EvConstants.PREFERENCE_DEFAULT_BIDI_SYM_SWAPPING );
		_preferenceStore.setDefault( EvConstants.PREFERENCE_BIDI_NUM_SWAPPING, EvConstants.PREFERENCE_DEFAULT_BIDI_NUM_SWAPPING );
		// IBMBIDI Append End
		
		// Initialize user preferred values
		//---------------------------------

		// Last preference page viewed
		//----------------------------
		if( _preferenceStore.contains( EvConstants.PREFERENCE_PAGE_TAB ) == false )
			setInt( EvConstants.PREFERENCE_PAGE_TAB, 0 );

		// Tab to show when editor is opened
		//----------------------------------
		if( _preferenceStore.contains( EvConstants.PREFERENCE_EDITOR_OPENING_TAB ) == false )
			setInt( EvConstants.PREFERENCE_EDITOR_OPENING_TAB, 0 );

		// Browser size controls
		//----------------------
		if( _preferenceStore.contains( EvConstants.PREFERENCE_BROWSER_SIZE_CONTROLS_VISIBLE ) == false )
			setBoolean( EvConstants.PREFERENCE_BROWSER_SIZE_CONTROLS_VISIBLE, false );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_WIDTH ) == false )
			setInt( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_WIDTH, 200 );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_HEIGHT ) == false )
			setInt( EvConstants.PREFERENCE_BROWSER_SIZE_MINIMUM_HEIGHT, 200 );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_WIDTH ) == false )
			setInt( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_WIDTH, 3000 );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_HEIGHT ) == false )
			setInt( EvConstants.PREFERENCE_BROWSER_SIZE_MAXIMUM_HEIGHT, 5000 );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_WIDTH ) == false )
			setInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_WIDTH, 1000 );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_HEIGHT ) == false )
			setInt( EvConstants.PREFERENCE_BROWSER_SIZE_DEFAULT_HEIGHT, 800 );

		// Transparency controls
		//----------------------
		if( _preferenceStore.contains( EvConstants.PREFERENCE_TRANSPARENCY_CONTROLS_VISIBLE ) == false )
			setBoolean( EvConstants.PREFERENCE_TRANSPARENCY_CONTROLS_VISIBLE, false );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_SEMITRANSPARENCY_WHILE_DRAGGING ) == false )
			setBoolean( EvConstants.PREFERENCE_SEMITRANSPARENCY_WHILE_DRAGGING, EvConstants.PREFERENCE_DEFAULT_SEMITRANSPARENCY_WHILE_DRAGGING );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_SEMITRANSPARENCY_MODE ) == false )
			setInt( EvConstants.PREFERENCE_SEMITRANSPARENCY_MODE, EvConstants.PREFERENCE_DEFAULT_SEMITRANSPARENCY_MODE );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_SEMITRANSPARENCY_AMOUNT ) == false )
			setInt( EvConstants.PREFERENCE_SEMITRANSPARENCY_AMOUNT, EvConstants.PREFERENCE_DEFAULT_SEMITRANSPARENCY_AMOUNT );

		// Color and pattern
		//------------------
		if( _preferenceStore.contains( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_POTENTIAL ) == false )
			setString( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_POTENTIAL, EvConstants.PREFERENCE_DEFAULT_COLOR_DROP_LOCATION_POTENTIAL );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_SELECTED ) == false )
			setString( EvConstants.PREFERENCE_COLOR_DROP_LOCATION_SELECTED, EvConstants.PREFERENCE_DEFAULT_COLOR_DROP_LOCATION_SELECTED );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_COLOR_SELECTION ) == false )
			setString( EvConstants.PREFERENCE_COLOR_SELECTION, EvConstants.PREFERENCE_DEFAULT_COLOR_SELECTION );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_PATTERN_DROP_LOCATION_SELECTED ) == false )
			setBoolean( EvConstants.PREFERENCE_PATTERN_DROP_LOCATION_SELECTED, EvConstants.PREFERENCE_DEFAULT_PATTERN_DROP_LOCATION_SELECTED );

		if( _preferenceStore.contains( EvConstants.PREFERENCE_PATTERN_SELECTION ) == false )
			setBoolean( EvConstants.PREFERENCE_PATTERN_SELECTION, EvConstants.PREFERENCE_DEFAULT_PATTERN_SELECTION );

		// Performance
		//------------
		if( _preferenceStore.contains( EvConstants.PREFERENCE_PERFORMANCE ) == false )
			setInt( EvConstants.PREFERENCE_PERFORMANCE, EvConstants.PREFERENCE_DEFAULT_PERFORMANCE );

		// Render Engine
		//------------
		if( _preferenceStore.contains( EvConstants.PREFERENCE_RENDERENGINE ) == false )
			setInt( EvConstants.PREFERENCE_RENDERENGINE, BrowserManager.getInstance().getDefaultRenderEngine() );

		// Widget creation
		//----------------
		if( _preferenceStore.contains( EvConstants.PREFERENCE_PROMPT_FOR_A_NEW_WIDGET_NAME ) == false )
			setBoolean( EvConstants.PREFERENCE_PROMPT_FOR_A_NEW_WIDGET_NAME, EvConstants.PREFERENCE_DEFAULT_PROMPT_FOR_A_NEW_WIDGET_NAME );

		// Bidirectional
		//--------------
		
		// IBMBIDI Append Start
		if( _preferenceStore.contains( EvConstants.PREFERENCE_BIDI_WIDGET_ORIENTATION ) == false )
			setString( EvConstants.PREFERENCE_BIDI_WIDGET_ORIENTATION, EvConstants.PREFERENCE_DEFAULT_BIDI_WIDGET_ORIENTATION );
		
		if( _preferenceStore.contains( EvConstants.PREFERENCE_BIDI_TEXT_LAYOUT ) == false )
			setString( EvConstants.PREFERENCE_BIDI_TEXT_LAYOUT, EvConstants.PREFERENCE_DEFAULT_BIDI_TEXT_LAYOUT );
		
		if( _preferenceStore.contains( EvConstants.PREFERENCE_BIDI_REVERSE_TEXT_DIRECTION ) == false )
			setString( EvConstants.PREFERENCE_BIDI_REVERSE_TEXT_DIRECTION, EvConstants.PREFERENCE_DEFAULT_BIDI_REVERSE_TEXT_DIRECTION );
		
		if( _preferenceStore.contains( EvConstants.PREFERENCE_BIDI_SYM_SWAPPING ) == false )
			setString( EvConstants.PREFERENCE_BIDI_SYM_SWAPPING, EvConstants.PREFERENCE_DEFAULT_BIDI_SYM_SWAPPING );
		
		if( _preferenceStore.contains( EvConstants.PREFERENCE_BIDI_NUM_SWAPPING ) == false )
			setString( EvConstants.PREFERENCE_BIDI_NUM_SWAPPING, EvConstants.PREFERENCE_DEFAULT_BIDI_NUM_SWAPPING );

		// IBMBIDI Append End
	}

	/**
	 * 
	 */
	public static void setBoolean( String name, boolean value ) {
		initialize();
		_preferenceStore.setValue( name, value );
	}

	/**
	 * 
	 */
	public static void setDouble( String name, double value ) {
		initialize();
		_preferenceStore.setValue( name, value );
	}

	/**
	 * 
	 */
	public static void setFloat( String name, float value ) {
		initialize();
		_preferenceStore.setValue( name, value );
	}

	/**
	 * 
	 */
	public static void setInt( String name, int value ) {
		initialize();
		_preferenceStore.setValue( name, value );
	}

	/**
	 * 
	 */
	public static void setRGB( String name, RGB value ) {
		initialize();
		_preferenceStore.setValue( name, convertRGBToString( value ) );
	}

	/**
	 * 
	 */
	public static void setString( String name, String value ) {
		initialize();
		_preferenceStore.setValue( name, value );
	}

	/**
	 * Do not publicly instantiate
	 */
	private EvPreferences() {
	}
}
