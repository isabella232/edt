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
package org.eclipse.edt.ide.rui.visualeditor.internal.util;

import java.util.StringTokenizer;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorUtil {
	protected static String[]	HEX_STRINGS		= { "f0f8ff", "faebd7", "00ffff", "7fffd4", "f0ffff", "f5f5dc", "ffe4c4", "000000", "ffebcd", "0000ff", "8a2be2", "a52a2a", "deb887", "5f9ea0", "7fff00", "d2691e", "ff7f50", "6495ed", "fff8dc", "dc143c", "00ffff", "00008b", "008b8b", "b8860b", "a9a9a9", "006400", "bdb76b", "8b008b", "556b2f", "ff8c00", "9932cc", "8b0000",
			"e9967a", "8fbc8f", "483d8b", "2f4f4f", "00ced1", "9400d3", "ff1493", "00bfff", "696969", "1e90ff", "b22222", "fffaf0", "228b22", "ff00ff", "dcdcdc", "f8f8ff", "ffd700", "daa520", "808080", "008000", "adff2f", "f0fff0", "ff69b4", "cd5c5c", "4b0082", "fffff0", "f0e68c", "e6e6fa", "fff0f5", "7cfc00", "fffacd", "add8e6", "f08080", "e0ffff", "fafad2", "d3d3d3",
			"90ee90", "ffb6c1", "ffa07a", "20b2aa", "87cefa", "778899", "b0c4e3", "ffffe0", "00ff00", "32cd32", "faf0e6", "ff00ff", "800000", "66cdaa", "0000cd", "ba55d3", "9370d8", "3cb371", "7b68ee", "00fa9a", "48d1cc", "c71585", "191970", "f5fffa", "ffe4e1", "ffe4b5", "ffdead", "000080", "fdf5e6", "808000", "6b8e23", "ffa500", "ff4500", "da70d6", "eee8aa", "98fb98",
			"afeeee", "d87093", "ffefd5", "ffdab9", "cd853f", "ffc0cb", "dda0dd", "b0e0e6", "800080", "ff0000", "bc8f8f", "4169e1", "8b4513", "fa8072", "f4a460", "2e8b57", "fff5ee", "a0522d", "c0c0c0", "87ceeb", "6a5acd", "708090", "fffafa", "00ff7f", "4682b4", "d2b48c", "008080", "d8bfd8", "ff6347", "40e0d0", "ee82ee", "f5deb3", "ffffff", "f5f5f5", "ffff00", "9acd32" };

	protected static String[]	NAME_STRINGS	= { "AliceBlue", "AntiqueWhite", "Aqua", "Aquamarine", "Azure", "Beige", "Bisque", "Black", "BlanchedAlmond", "Blue", "BlueViolet", "Brown", "BurlyWood", "CadetBlue", "Chartreuse", "Chocolate", "Coral", "CornflowerBlue", "Cornsilk", "Crimson", "Cyan", "DarkBlue", "DarkCyan", "DarkGoldenRod", "DarkGray", "DarkGreen", "DarkKhaki",
			"DarkMagenta", "DarkOliveGreen", "DarkOrange", "DarkOrchid", "DarkRed", "DarkSalmon", "DarkSeaGreen", "DarkSlateBlue", "DarkSlateGray", "DarkTurquoise", "DarkViolet", "DeepPink", "DeepSkyBlue", "DimGray", "DodgerBlue", "FireBrick", "FloralWhite", "ForestGreen", "Fuchsia", "Gainsboro", "GhostWhite", "Gold", "GoldenRod", "Gray", "Green", "GreenYellow",
			"HoneyDew", "HotPink", "IndianRed", "Indigo", "Ivory", "Khaki", "Lavender", "LavenderBlush", "LawnGreen", "LemonChiffon", "LightBlue", "LightCoral", "LightCyan", "LightGoldenRodYellow", "LightGrey", "LightGreen", "LightPink", "LightSalmon", "LightSeaGreen", "LightSkyBlue", "LightSlateGray", "LightSteelBlue", "LightYellow", "Lime", "LimeGreen", "Linen",
			"Magenta", "Maroon", "MediumAquaMarine", "MediumBlue", "MediumOrchid", "MediumPurple", "MediumSeaGreen", "MediumSlateBlue", "MediumSpringGreen", "MediumTurquoise", "MediumVioletRed", "MidnightBlue", "MintCream", "MistyRose", "Moccasin", "NavajoWhite", "Navy", "OldLace", "Olive", "OliveDrab", "Orange", "OrangeRed", "Orchid", "PaleGoldenRod", "PaleGreen", "PaleTurquoise",
			"PaleVioletRed", "PapayaWhip", "PeachPuff", "Peru", "Pink", "Plum", "PowderBlue", "Purple", "Red", "RosyBrown", "RoyalBlue", "SaddleBrown", "Salmon", "SandyBrown", "SeaGreen", "SeaShell", "Sienna", "Silver", "SkyBlue", "SlateBlue", "SlateGray", "Snow", "SpringGreen", "SteelBlue", "Tan", "Teal", "Thistle", "Tomato", "Turquoise", "Violet", "Wheat", "White", "WhiteSmoke",
			"Yellow", "YellowGreen",			};

	/**
	 * Converts an RGB object into a string of the form #aabbcc
	 */
	protected static String convertRgbToHexString( RGB rgb ) {
		StringBuffer strb = new StringBuffer( "#" );

		String strHex = Integer.toHexString( rgb.red );
		if( strHex.length() == 1 )
			strb.append( "0" );
		strb.append( strHex );

		strHex = Integer.toHexString( rgb.green );
		if( strHex.length() == 1 )
			strb.append( "0" );
		strb.append( strHex );

		strHex = Integer.toHexString( rgb.blue );
		if( strHex.length() == 1 )
			strb.append( "0" );

		strb.append( strHex );

		return strb.toString();
	}

	/**
	 * Converts an RGB object into a string of the form RGB(r,g,b); 
	 */
	protected static String convertRgbToRgbString( RGB rgb ) {

		StringBuffer strb = new StringBuffer( "RGB(" );
		strb.append( Integer.toString( rgb.red ) );
		strb.append( "," );
		strb.append( Integer.toString( rgb.green ) );
		strb.append( "," );
		strb.append( Integer.toString( rgb.blue ) );
		strb.append( ")" );

		return strb.toString();
	}

	/**
	 * Returns null if invalid format.  Valid formats is #aabbcc.
	 */
	protected static RGB convertHexStringToRGB( String strColor ) {
		if( strColor == null || strColor.length() == 0 )
			return new RGB( 0, 0, 0 );

		// Remove surrounding quotes
		//--------------------------
		strColor = strColor.replace( "\"", " " );
		strColor = strColor.trim();

		// Hex
		//----
		if( strColor.charAt( 0 ) != '#' )
			return null;

		// Only hex symbol
		//----------------
		if( strColor.length() == 1 )
			return new RGB( 0, 0, 0 );

		// Isolate digits
		//---------------
		strColor = strColor.substring( 1 );

		// Prepend with zeros
		//-------------------
		while( strColor.length() < 6 )
			strColor = "0" + strColor;

		String strHexR = strColor.substring( 0, 2 );
		String strHexG = strColor.substring( 2, 4 );
		String strHexB = strColor.substring( 4 );

		int iR = 0;
		int iG = 0;
		int iB = 0;

		try {
			iR = Integer.parseInt( strHexR, 16 );
			iG = Integer.parseInt( strHexG, 16 );
			iB = Integer.parseInt( strHexB, 16 );
		}
		catch( NumberFormatException ex ) {
			return null;
		}

		return new RGB( iR, iG, iB );
	}

	/**
	 * Returns null if invalid format.  Valid format is RGB( r, g, b ) or r, g, b
	 */
	protected static RGB convertRGBStringToRGB( String strColor ) {
		if( strColor == null || strColor.length() == 0 )
			return new RGB( 0, 0, 0 );

		// Remove surrounding quotes
		//--------------------------
		strColor = strColor.replace( "\"", " " );
		strColor = strColor.trim();

		StringBuffer strb = new StringBuffer();

		for( int i = 0; i < strColor.length(); ++i ) {
			if( Character.isDigit( strColor.charAt( i ) ) == true )
				strb.append( strColor.charAt( i ) );
			else
				strb.append( ' ' );
		}

		String strDigits = strb.toString().trim();
		StringTokenizer tokenizer = new StringTokenizer( strDigits, " " );

		if( tokenizer.countTokens() != 3 )
			return null;

		String strR = tokenizer.nextToken();
		String strG = tokenizer.nextToken();
		String strB = tokenizer.nextToken();

		int iR = 0;
		int iG = 0;
		int iB = 0;

		try {
			iR = Integer.parseInt( strR );
			iG = Integer.parseInt( strG );
			iB = Integer.parseInt( strB );
		}
		catch( NumberFormatException ex ) {
			return null;
		}

		return new RGB( iR, iG, iB );
	}

	/**
	 * Converts RGB(0,0,0) or #aabbcc or a color name to an RGB value. 
	 */
	public static RGB convertStringToRGB( String strColorValue ) {

		// Remove surrounding quotes
		//--------------------------
		strColorValue = strColorValue.replace( "\"", " " );
		strColorValue = strColorValue.trim();

		// RGB string
		//-----------
		RGB rgb = convertRGBStringToRGB( strColorValue );
		if( rgb != null )
			return rgb;

		// Hex string
		//-----------
		rgb = ColorUtil.convertHexStringToRGB( strColorValue );
		if( rgb != null )
			return rgb;

		// Color name
		//-----------
		int iIndex = -1;

		String[] straColors = ColorUtil.getColorNames();

		for( int i = 0; i < straColors.length; ++i ) {
			if( strColorValue.equalsIgnoreCase( straColors[ i ] ) == true ) {
				iIndex = i;
				break;
			}
		}

		if( iIndex >= 0 ) {
			String strHex = "#" + HEX_STRINGS[ iIndex ];
			return convertHexStringToRGB( strHex );
		}

		return null;
	}

	/**
	 * Returns the array of color hexadecimal values.
	 */
	public static String[] getColorHexValues() {
		return HEX_STRINGS;
	}

	/**
	 * Returns the array of color names.
	 */
	public static String[] getColorNames() {
		return NAME_STRINGS;
	}

	/**
	 * Used by the widget painter to convert an RGB string to a color.
	 */
	public static Color getColorFromRGBString( Display display, String strRGB ) {

		StringTokenizer tokenizer = new StringTokenizer( strRGB, " ,:" );

		int iR = 0;
		int iG = 0;
		int iB = 0;

		if( tokenizer.hasMoreElements() == true ) {
			String strR = (String)tokenizer.nextElement();
			try {
				iR = Integer.decode( strR ).intValue();
			}
			catch( NumberFormatException ex ) {
			}
		}

		if( tokenizer.hasMoreElements() == true ) {
			String strG = (String)tokenizer.nextElement();
			try {
				iG = Integer.decode( strG ).intValue();
			}
			catch( NumberFormatException ex ) {
			}
		}

		if( tokenizer.hasMoreElements() == true ) {
			String strB = (String)tokenizer.nextElement();
			try {
				iB = Integer.decode( strB ).intValue();
			}
			catch( NumberFormatException ex ) {
			}
		}

		return new Color( display, iR, iG, iB );
	}

	/**
	 * Used by the general preference page.
	 * Converts an RGB object to a string such as "255, 128, 0"
	 */
	public static String getRGBString( RGB rgb ) {

		StringBuffer strb = new StringBuffer();

		strb.append( new Integer( rgb.red ).toString() );
		strb.append( ' ' );
		strb.append( new Integer( rgb.green ).toString() );
		strb.append( ' ' );
		strb.append( new Integer( rgb.blue ).toString() );

		return strb.toString();
	}
}
