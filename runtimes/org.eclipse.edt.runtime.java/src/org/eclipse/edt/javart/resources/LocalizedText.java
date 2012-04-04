/*******************************************************************************
 * Copyright © 2006, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.resources;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.util.JavartUtil;

/**
 * LocalizedText contains information that varies by Locale.
 */
public class LocalizedText implements Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * The properties of the RunUnit.
	 */
	private JavartProperties properties;
	
	/**
	 * Constants for IBM NLS codes supported by EGL.
	 */
	public static final String NLS_ENU = "ENU";
	public static final String NLS_DEU = "DEU";
	public static final String NLS_DES = "DES";
	public static final String NLS_ESP = "ESP";
	public static final String NLS_FRA = "FRA";
	public static final String NLS_ITA = "ITA";
	public static final String NLS_PTB = "PTB";
	public static final String NLS_KOR = "KOR";
	public static final String NLS_JPN = "JPN";
	public static final String NLS_CHS = "CHS";
	public static final String NLS_CHT = "CHT";
	public static final String NLS_RUS = "RUS";
	public static final String NLS_PLK = "PLK";
	public static final String NLS_CZE = "CZE";
	public static final String NLS_HUN = "HUN";
	public static final String NLS_ARA = "ARA";

	/**
	 * The constant for the CHS (simplified Chinese) environment.
	 */
	public static final int CHS = 1;

	/**
	 * The constant for the CHT (traditional Chinese) environment.
	 */
	public static final int CHT = 2;

	/**
	 * The constant for the DES (Swiss German) environment.
	 */
	public static final int DES = 3;

	/**
	 * The constant for the DEU (German) environment.
	 */
	public static final int DEU = 4;

	/**
	 * The constant for the ENU (US English) environment.
	 */
	public static final int ENU = 5;

	/**
	 * The constant for the ESP (Spanish) environment.
	 */
	public static final int ESP = 6;

	/**
	 * The constant for the FRA (French) environment.
	 */
	public static final int FRA = 7;

	/**
	 * The constant for the ITA (Italian) environment.
	 */
	public static final int ITA = 8;

	/**
	 * The constant for the JPN (Japanese) environment.
	 */
	public static final int JPN = 9;

	/**
	 * The constant for the KOR (Korean) environment.
	 */
	public static final int KOR = 10;

	/**
	 * The constant for the PTB (Brazillian Portugese) environment.
	 */
	public static final int PTB = 11;

	/**
	 * The constant for the RUS (Russian) environment.
	 */
	public static final int RUS = 12;

	/**
	 * The constant for the HUN (Hungarian) environment.
	 */
	public static final int HUN = 13;

	/**
	 * The constant for the CZE (Czech) environment.
	 */
	public static final int CZE = 14;

	/**
	 * The constant for the PLK (Polish) environment.
	 */
	public static final int PLK = 15;

	/**
	 * The constant for the ARA (Arabic) environment.
	 */
	public static final int ARA = 16;

	/**
	 * This means we haven't yet looked up the currency location.
	 */
	private static final int CURRENCY_LOCATION_UNKNOWN = -1;

	/**
	 * This means currencyLocation=NONE.
	 */
	public static final int CURRENCY_LOCATION_NONE = 0;

	/**
	 * This means currencyLocation=FRONT.
	 */
	public static final int CURRENCY_LOCATION_FRONT = 1;

	/**
	 * This means currencyLocation=BACK.
	 */
	public static final int CURRENCY_LOCATION_BACK = 2;
	
	/**
	 * The NLS code being used.
	 */
	private String nlsCode;

	/**
	 * A value representing the language of the NLS code.
	 */
	private int languageID;

	/**
	 * The Locale associated with the NLS code.
	 */
	private Locale locale;

	/**
	 * The actual NLS code that was passed to the constructor. It's kept around
	 * so it can be written to the trace file, which might reveal errors.
	 */
	private String inputNlsCode;

	/**
	 * The actual Locale that was passed to the constructor. It's kept around so
	 * it can be written to the trace file, which might reveal errors.
	 */
	private Locale inputLocale;

	/**
	 * An object used to provide locale-specific text elements.
	 */
	private DecimalFormatSymbols decimalFormatSymbols = null;

	/**
	 * The decimal symbol, gotten from the properties file or the Locale.
	 */
	private char decimalSymbol = '\uFFFF';

	/**
	 * The monetary decimal separator, either the decimalSymbol or gotten from the Locale.
	 */
	private char monetaryDecimalSeparator = '\uFFFF';

	/**
	 * The currency symbol, gotten from the properties file or the Locale.
	 */
	private String currencySymbol = null;

	/**
	 * Where to put the currency symbol (one of the CURRENCY_LOCATION constants).
	 */
	private int currencyLocation = CURRENCY_LOCATION_UNKNOWN;
	
	/**
	 * The separator symbol, gotten from the properties file or the Locale.
	 */
	private char separatorSymbol = '\uFFFF';

	/**
	 * The short Gregorian date mask, gotten from the properties file or the Locale.
	 */
	private String shortGregorianDateMask = null;

	/**
	 * The long Gregorian date mask, gotten from the properties file or the Locale.
	 */
	private String longGregorianDateMask = null;

	/**
	 * The short Julian date mask, gotten from the properties file or the Locale.
	 */
	private String shortJulianDateMask = null;

	/**
	 * The long Julian date mask, gotten from the properties file or the Locale.
	 */
	private String longJulianDateMask = null;

	/**
	 * A date-time formatter for the Locale.
	 */
	private DateFormat dateFormatter;
	
	/**
	 * True if the decimal symbol was set in the properties.
	 */
	private boolean decimalSymbolSetInProperties;
	
	/**
	 * The messages from the file named by the user.messages.file property,
	 * in the Locale associated with the NLS code.
	 */
	private transient ResourceBundle userMessages;
	
	/**
	 * The EGL messages, in the Locale associated with the NLS code.
	 */
	private transient ResourceBundle eglMessages;
	
	/**
	 * A cache of ResourceBundles.  The EGL MessageBundle objects are stored
	 * with their Locale as the key.  User-defined MessageBundle objects are
	 * stored with their name plus their Locale as the key.
	 */
	private static HashMap bundleCache = new HashMap( 32 );

	/**
	 * Constructs a LocalizedText based on a set of properties.  The egl.nls.code
	 * property will determine the NLS code, which determines the Locale.
	 */
	public LocalizedText( JavartProperties properties )
	{
		this.properties = properties;
		inputNlsCode = properties.get( "egl.nls.code" );
		inputLocale = null;
		if ( inputNlsCode == null )
		{
			// Use the default Locale.
			locale = Locale.getDefault();
			setNlsCodeFromLocale();
		}
		else
		{
			// Make sure the NLS code from the properties is valid.
			if ( inputNlsCode.equals( NLS_ENU ) || inputNlsCode.equals( NLS_DEU ) 
					|| inputNlsCode.equals( NLS_DES ) || inputNlsCode.equals( NLS_ESP )
					|| inputNlsCode.equals( NLS_FRA ) || inputNlsCode.equals( NLS_ITA )
					|| inputNlsCode.equals( NLS_PTB ) || inputNlsCode.equals( NLS_KOR )
					|| inputNlsCode.equals( NLS_JPN ) || inputNlsCode.equals( NLS_CHS )
					|| inputNlsCode.equals( NLS_CHT ) || inputNlsCode.equals( NLS_RUS )
					|| inputNlsCode.equals( NLS_CZE ) || inputNlsCode.equals( NLS_PLK ) 
					|| inputNlsCode.equals( NLS_HUN ) || inputNlsCode.equals( NLS_ARA ) )
			{
				nlsCode = inputNlsCode;
			}
			else
			{
				nlsCode = NLS_ENU;
			}
			setLocaleFromNlsCode();
		}
		setLanguageID();
	}

	/**
	 * Switches to the given Locale.  The NLS code will be updated too.
	 * 
	 * @param loc  the Locale to use.
	 */
	public void switchLocale( Locale loc )
	{
		inputNlsCode = null;
		inputLocale = loc;
		
		if ( !locale.equals( loc ) )
		{
			locale = loc;
			setNlsCodeFromLocale();
			setLanguageID();
			
			// Clear all cached values that depend on the old settings.
			decimalFormatSymbols = null;
			decimalSymbol = '\uFFFF';
			monetaryDecimalSeparator = '\uFFFF';
			currencySymbol = null;
			separatorSymbol = '\uFFFF';
			shortGregorianDateMask = null;
			longGregorianDateMask = null;
			shortJulianDateMask = null;
			longJulianDateMask = null;
			dateFormatter = null;
			userMessages = null;
			eglMessages = null;
		}
	}

	/**
	 * Returns trace info for this object.
	 * 
	 * @return trace info for this object.
	 */
	public String getInfo()
	{
		String inputLocaleText = null;
		if ( inputLocale != null )
		{
			inputLocaleText = inputLocale.getLanguage() + "." + inputLocale.getCountry();
		}

		return "NLS code: " + nlsCode + " (" + inputNlsCode + "), Locale: " + locale
				+ " (" + inputLocaleText + ')';
	}

	/**
	 * Returns the Locale being used.
	 * 
	 * @return the Locale being used.
	 */
	public Locale getLocale()
	{
		return locale;
	}

	/**
	 * Returns the NLS code being used.
	 * 
	 * @return the NLS code being used.
	 */
	public String getNlsCode()
	{
		return nlsCode;
	}
	
	/**
	 * Sets the nlsCode to match the Locale.  Sets it to ENU if no match is found.
	 */
	private void setNlsCodeFromLocale()
	{
		String language = locale.getLanguage();
		String country = locale.getCountry();

		if ( language.equals( "en" ) )
			nlsCode = NLS_ENU;
		else if ( language.equals( "de" ) )
		{
			if ( country.equals( "CH" ) )
				nlsCode = NLS_DES;
			else
				nlsCode = NLS_DEU;
		}
		else if ( language.equals( "es" ) )
			nlsCode = NLS_ESP;
		else if ( language.equals( "pt" ) )
			nlsCode = NLS_PTB;
		else if ( language.equals( "ko" ) )
			nlsCode = NLS_KOR;
		else if ( language.equals( "fr" ) )
			nlsCode = NLS_FRA;
		else if ( language.equals( "it" ) )
			nlsCode = NLS_ITA;
		else if ( language.equals( "ja" ) )
			nlsCode = NLS_JPN;
		else if ( language.equals( "zh" ) )
		{
			// Use Traditional Chinese for Taiwan and Hong Kong, and Simplified
			// Chinese for all other Chinese locales. 
			if ( country.equals( "TW" ) || country.equals( "HK" ) )
				nlsCode = NLS_CHT;
			else
				nlsCode = NLS_CHS;
		}
		else if ( language.equals( "ru" ) )
			nlsCode = NLS_RUS;
		else if ( language.equals( "pl" ) )
			nlsCode = NLS_PLK;
		else if ( language.equals( "hu" ) )
			nlsCode = NLS_HUN;
		else if ( language.equals( "cs" ) )
			nlsCode = NLS_CZE;
		else if ( language.equals( "ar" ) )
			nlsCode = NLS_ARA;
		else
			nlsCode = NLS_ENU;
	}

	/**
	 * Sets the locale based on the nlsCode.  If the NLS code is not supported,
	 * use the default Locale.
	 */
	private void setLocaleFromNlsCode()
	{
		locale = getLocaleFromNlsCode( nlsCode );
	}

	public static Locale getLocaleFromNlsCode( String nlsCode )
	{
		Locale locale;
		if ( nlsCode.equals( NLS_ENU ) )
			locale = Locale.US;
		else if ( nlsCode.equals( NLS_DEU ) )
			locale = Locale.GERMAN;
		else if ( nlsCode.equals( NLS_DES ) )
			locale = new Locale( "de", "CH", "" );
		else if ( nlsCode.equals( NLS_ESP ) )
			locale = new Locale( "es", "", "" );
		else if ( nlsCode.equals( NLS_FRA ) )
			locale = Locale.FRENCH;
		else if ( nlsCode.equals( NLS_ITA ) )
			locale = Locale.ITALIAN;
		else if ( nlsCode.equals( NLS_PTB ) )
			locale = new Locale( "pt", "BR", "" );
		else if ( nlsCode.equals( NLS_KOR ) )
			locale = Locale.KOREAN;
		else if ( nlsCode.equals( NLS_JPN ) )
			locale = Locale.JAPANESE;
		else if ( nlsCode.equals( NLS_CHS ) )
			locale = Locale.SIMPLIFIED_CHINESE;
		else if ( nlsCode.equals( NLS_CHT ) )
			locale = Locale.TRADITIONAL_CHINESE;
		else if ( nlsCode.equals( NLS_RUS ) )
			locale = new Locale( "ru", "", "" );
		else if ( nlsCode.equals( NLS_PLK ) )
			locale = new Locale( "pl", "", "" );
		else if ( nlsCode.equals( NLS_HUN ) )
			locale = new Locale( "hu", "", "" );
		else if ( nlsCode.equals( NLS_CZE ) )
			locale = new Locale( "cs", "", "" );
		else if ( nlsCode.equals( NLS_ARA ) )
			locale = new Locale( "ar", "", "" );
		else
			locale = Locale.getDefault();
		return locale;
	}

	/**
	 * Returns the symbol used to designate currency.  ("$" for ENU.)
	 *
	 * @return  the currency symbol used in this NLS environment.
	 */
	public String getCurrencySymbol()
	{
		// See if we've cached the value.
		if ( currencySymbol != null )
		{
			return currencySymbol;
		}

		// Look in the properties file first.
		currencySymbol = properties.get( "egl.nls.currency" );
		if ( currencySymbol != null )
		{
			return currencySymbol;
		}

		// Get the symbol used in this Locale.
		if ( decimalFormatSymbols == null )
		{
			decimalFormatSymbols = new DecimalFormatSymbols( locale );
		}
		currencySymbol = decimalFormatSymbols.getCurrencySymbol();

		return currencySymbol;
	}

	/**
	 * Returns one of the CURRENCY_LOCATION constants.
	 *
	 * @return  the constant that tells us where to put the currency symbol.
	 */
	public int getCurrencyLocation()
	{
		// See if we've cached the value.
		if ( currencyLocation != CURRENCY_LOCATION_UNKNOWN )
		{
			return currencyLocation;
		}

		// Look in the properties file.
		String property = properties.get( "egl.nls.currency.location" );
		if ( property != null )
		{
			if ( property.equals( "FRONT" ) )
			{
				currencyLocation = CURRENCY_LOCATION_FRONT;
			}
			else if ( property.equals( "BACK" ) )
			{
				currencyLocation = CURRENCY_LOCATION_BACK;
			}
			else
			{
				// Default to NONE.
				currencyLocation = CURRENCY_LOCATION_NONE;
			}
		}
		else
		{
			// The property isn't set.  Default to NONE.
			currencyLocation = CURRENCY_LOCATION_NONE;
		}
		
		return currencyLocation;
	}

	/**
	 * Returns the symbol used to separate the whole part of a real number
	 * from the fractional part.  ('.' for ENU.)
	 *
	 * @return  the decimal symbol used in this NLS environment.
	 */
	public char getDecimalSymbol()
	{
		// See if we've cached the value.
		if ( decimalSymbol != '\uFFFF' )
		{
			return decimalSymbol;
		}

		// Look in the properties file first.
		String decimalString = properties.get( "egl.nls.number.decimal" );
		if ( decimalString != null && decimalString.length() > 0 )
		{
			decimalSymbol = decimalString.charAt( 0 );
			decimalSymbolSetInProperties = true;
			return decimalSymbol;
		}
		decimalSymbolSetInProperties = false;

		// Get the symbol used in this Locale.
		if ( decimalFormatSymbols == null )
		{
			decimalFormatSymbols = new DecimalFormatSymbols( locale );
		}
		decimalSymbol = decimalFormatSymbols.getDecimalSeparator();

		return decimalSymbol;
	}
	
	/**
	 * Returns the symbol used to separate the whole part of a real number
	 * from the fractional part in a monetary value.  ('.' for ENU.)
	 *
	 * @return  the monetary decimal separator used in this NLS environment.
	 */
	public char getMonetaryDecimalSeparator()
	{
		// See if we've cached the value.
		if ( monetaryDecimalSeparator != '\uFFFF' )
		{
			return monetaryDecimalSeparator;
		}
		
		// We don't have a property for the monetaryDecimalSeparator.  If the
		// properties included a setting for decimalSymbol, use it.  Otherwise
		// get the value from the Locale.
		getDecimalSymbol();
		if ( decimalSymbolSetInProperties )
		{
			monetaryDecimalSeparator = decimalSymbol;
			return monetaryDecimalSeparator;
		}

		// Get the symbol used in this Locale.
		if ( decimalFormatSymbols == null )
		{
			decimalFormatSymbols = new DecimalFormatSymbols( locale );
		}
		monetaryDecimalSeparator = decimalFormatSymbols.getMonetaryDecimalSeparator();

		return monetaryDecimalSeparator;
	}

	/**
	 * Determines whether <code>mask</code> is a valid Gregorian date mask. A
	 * valid mask contains dd, MM, and yy (short format) or yyyy (long format).
	 * A separator must appear between each part.
	 * 
	 * @param mask
	 *            the mask to examine.
	 * @param shortFormat
	 *            true if the mask uses a 2-digit year.
	 * @return true if the mask is a valid Gregorian date mask.
	 */
	public static boolean isGregorianDateMask( String mask, boolean shortFormat )
	{
		char quoteChar = '\'';
		int count_y = 0, count_m = 0, count_d = 0, count_separator = 0;
		boolean inQuote = false;
		int length = mask.length();
		for ( int i = 0; i < length; )
		{
			char ch = mask.charAt( i++ );
			if ( ch == quoteChar )
			{
				if ( i < length )
				{
					ch = mask.charAt( i );
					if ( ch == quoteChar )
					{
						count_separator++;
						i++;
						continue;
					}
				}

				if ( !inQuote )
				{
					inQuote = true;
				}
				else
				{
					inQuote = false;
				}
				continue;
			}

			if ( inQuote )
			{
				count_separator++;
			}
			else if ( ch == 'y' )
			{
				count_y++;
			}
			else if ( ch == 'M' )
			{
				count_m++;
			}
			else if ( ch == 'd' )
			{
				count_d++;
			}
			else if ( (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') )
			{
				return false;
			}
			else
			{
				count_separator++;
			}
		}

		if ( count_d == 2 && count_m == 2 && count_y == (shortFormat ? 2 : 4) 
				&& count_separator == 2 )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Determines whether <code>mask</code> is a valid Julian date mask. A
	 * valid mask contains DDD and yy (short format) or yyyy (long format). A
	 * separator must appear between the two parts.
	 * 
	 * @param mask
	 *            the mask to examine.
	 * @param shortFormat
	 *            true if the mask uses a 2-digit year.
	 * @return true if the mask is a valid Julian date mask.
	 */
	private static boolean isJulianDateMask( String mask, boolean shortFormat )
	{
		char quoteChar = '\'';
		int count_y = 0, count_d = 0, count_separator = 0;
		boolean inQuote = false;
		int length = mask.length();
		for ( int i = 0; i < length; )
		{
			char ch = mask.charAt( i++ );
			if ( ch == quoteChar )
			{
				if ( i < length )
				{
					ch = mask.charAt( i );
					if ( ch == quoteChar )
					{
						count_separator++;
						i++;
						continue;
					}
				}

				if ( !inQuote )
				{
					inQuote = true;
				}
				else
				{
					inQuote = false;
				}
				continue;
			}

			if ( inQuote )
			{
				count_separator++;
			}
			else if ( ch == 'y' )
			{
				count_y++;
			}
			else if ( ch == 'D' )
			{
				count_d++;
			}
			else if ( (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') )
			{
				return false;
			}
			else
			{
				count_separator++;
			}
		}

		if ( count_d == 3 && count_y == (shortFormat ? 2 : 4)
				&& count_separator == 1 )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns the long Gregorian date mask defined for use in this RunUnit.
	 * If a mask is defined in the properties file, it is verified using 
	 * <code>isGregorianDateMask</code>.  If the mask is invalid, or if it's
	 * short/medium/long, an attempt is made to get the mask based on the Locale.
	 * If that fails, <code>MM-dd-yyyy</code> is returned.
	 *
	 * @return the Gregorian date mask defined for this NLS environment.
	 * @see JavartUtil#isGregorianDateMask
	 */
	public String getLongGregorianDateMask()
	{
		// Look in the cache first.
		if ( longGregorianDateMask != null )
		{
			return longGregorianDateMask;
		}

		// Now try the properties file.
		String mask = properties.get( "egl.datemask.gregorian.long." + nlsCode );
		int style = java.text.DateFormat.MEDIUM;
		if ( mask != null )
		{
			// It might be a mask or short, medium, long.
			if ( mask.equals( "short" ) )
			{
				style = java.text.DateFormat.SHORT;
			}
			else if ( mask.equals( "medium" ) )
			{
				style = java.text.DateFormat.MEDIUM;
			}
			else if ( mask.equals( "long" ) )
			{
				style = java.text.DateFormat.LONG;
			}
			else if ( isGregorianDateMask( mask, false ) )
			{
				// It's a valid mask.
				longGregorianDateMask = mask;
				return mask;
			}
		}

		// No mask was defined in the properties file.  Get a DateFormat for
		// this Locale.
		java.text.DateFormat formatter = 
			java.text.DateFormat.getDateInstance( style, locale );

		// The default value.
		String def = "MM-dd-yyyy";

		// Cast it to a SimpleDateFormat.
		SimpleDateFormat sdf;
		try
		{
			sdf = (SimpleDateFormat)formatter;
		}
		catch ( ClassCastException cce )
		{
			// If we can't get a SimpleDateFormat for our Locale, there's
			// nothing we can do but return the default mask.
			return def;
		}

		// Get the formatter's pattern and turn it into a date mask.
		mask = parseDateFormatPattern( sdf.toPattern(), false );
		if ( isGregorianDateMask( mask, false ) )
		{
			longGregorianDateMask = mask;
			return mask;
		}
		else
		{
			// It's not valid, so return the default value.
			longGregorianDateMask = def;
			return def;
		}
	}

	/**
	 * Returns the long Julian date mask defined for use in this RunUnit.
	 * If a mask is defined in the properties file, it is verified using 
	 * <code>isJulianDateMask</code>.  If the mask is invalid, or if it's
	 * short/medium/long, an attempt is made to get the mask based on the 
	 * Locale.  If that fails, <code>yyyy-DDD</code> is returned.
	 *
	 * @return the Julian date mask defined for this NLS environment.
	 * @see JavartUtil#isJulianDateMask
	 */
	public String getLongJulianDateMask()
	{
		// Look in the cache first.
		if ( longJulianDateMask != null )
		{
			return longJulianDateMask;
		}

		// Now try the properties file.
		String mask = properties.get( "egl.datemask.julian.long." + nlsCode );
		int style = java.text.DateFormat.MEDIUM;
		if ( mask != null )
		{
			// It might be a mask or short, medium, long.
			if ( mask.equals( "short" ) )
			{
				style = java.text.DateFormat.SHORT;
			}
			else if ( mask.equals( "medium" ) )
			{
				style = java.text.DateFormat.MEDIUM;
			}
			else if ( mask.equals( "long" ) )
			{
				style = java.text.DateFormat.LONG;
			}
			else if ( isJulianDateMask( mask, false ) )
			{
				// It's a valid mask.
				longJulianDateMask = mask;
				return mask;
			}
		}

		// No mask was defined in the properties file.  Get a DateFormat for
		// this Locale.
		java.text.DateFormat formatter = 
			java.text.DateFormat.getDateInstance( style, locale );

		// The default value.
		String def = "yyyy-DDD";

		// Cast it to a SimpleDateFormat.
		SimpleDateFormat sdf;
		try
		{
			sdf = (SimpleDateFormat)formatter;
		}
		catch ( ClassCastException cce )
		{
			// If we can't get a SimpleDateFormat for our Locale, there's
			// nothing we can do but return the default mask.
			return def;
		}

		// Get the formatter's pattern and turn it into a date mask.
		mask = parseDateFormatPattern( sdf.toPattern(), false );
		if ( isJulianDateMask( mask, false ) )
		{
			longJulianDateMask = mask;
			return mask;
		}
		else
		{
			// It's not valid, so return the default value.
			longJulianDateMask = def;
			return def;
		}
	}

	/**
	 * Returns the symbol used to separate the thousands place from the hundreds
	 * place in a number.  (',' for ENU.)
	 *
	 * @return  the separator symbol used in this NLS environment.
	 */
	public char getSeparatorSymbol()
	{
		// See if we've cached the value.
		if ( separatorSymbol != '\uFFFF' )
		{
			return separatorSymbol;
		}

		// Look in the properties file first.
		String separatorString = properties.get( "egl.nls.number.separator" );
		if ( separatorString != null && separatorString.length() > 0 )
		{
			separatorSymbol = separatorString.charAt( 0 );
			return separatorSymbol;
		}

		// Get the symbol used in this Locale.
		if ( decimalFormatSymbols == null )
		{
			decimalFormatSymbols = new DecimalFormatSymbols( locale );
		}
		separatorSymbol = decimalFormatSymbols.getGroupingSeparator();
		return separatorSymbol;
	}

	/**
	 * Returns the short Gregorian date mask defined for use in this RunUnit.
	 * If a mask is defined in the properties file, it is verified using 
	 * <code>isGregorianDateMask</code>.  If the mask is invalid, 
	 * an attempt is made to get the mask based on the Locale.  If
	 * that fails, <code>MM-dd-yy</code> is returned.
	 *
	 * @return the Gregorian date mask defined for this NLS environment.
	 * @see JavartUtil#isGregorianDateMask
	 */
	public String getShortGregorianDateMask()
	{
		// Look in the cache first.
		if ( shortGregorianDateMask != null )
		{
			return shortGregorianDateMask;
		}

		String def = "MM-dd-yy";

		// Now try the properties file.
		String mask = properties.get( "egl.datemask.gregorian.short." + nlsCode );
		if ( mask != null )
		{
			// See if the mask is valid.
			if ( isGregorianDateMask( mask, true ) )
			{
				// It's valid.
				shortGregorianDateMask = mask;
				return mask;
			}
		}

		// No mask was defined in the properties file.  Get a DateFormat for
		// this Locale.
		java.text.DateFormat formatter = 
			java.text.DateFormat.getDateInstance( java.text.DateFormat.SHORT, locale );

		// Cast it to a SimpleDateFormat.
		SimpleDateFormat sdf;
		try
		{
			sdf = (SimpleDateFormat)formatter;
		}
		catch ( ClassCastException cce )
		{
			// If we can't get a SimpleDateFormat for our Locale, there's
			// nothing we can do but return the default mask.
			return def;
		}

		// Get the formatter's pattern and turn it into a date mask.
		mask = parseDateFormatPattern( sdf.toPattern(), true );
		if ( isGregorianDateMask( mask, true ) )
		{
			shortGregorianDateMask = mask;
			return mask;
		}
		else
		{
			// It's not valid, so return the default value.
			shortGregorianDateMask = def;
			return def;
		}
	}

	/**
	 * Returns the short Julian date mask defined for use in this RunUnit.
	 * If a mask is defined in the properties file, it is verified using 
	 * <code>isJulianDateMask</code>.  If the mask is invalid, 
	 * an attempt is made to get the mask based on the Locale.  If
	 * that fails, <code>yy-DDD</code> is returned.
	 *
	 * @return the Julian date mask defined for this NLS environment.
	 * @see JavartUtil#isJulianDateMask
	 */
	public String getShortJulianDateMask()
	{
		// Look in the cache first.
		if ( shortJulianDateMask != null )
		{
			return shortJulianDateMask;
		}

		String def = "yy-DDD";

		// Now try the properties file.
		String mask = properties.get( "egl.datemask.julian.short." + nlsCode );
		if ( mask != null )
		{
			// See if the mask is valid.
			if ( isJulianDateMask( mask, true ) )
			{
				// It's valid.
				shortJulianDateMask = mask;
				return mask;
			}
		}

		// No mask was defined in the properties file.  Get a DateFormat for
		// this Locale.
		java.text.DateFormat formatter = 
			java.text.DateFormat.getDateInstance( java.text.DateFormat.SHORT, locale );

		// Cast it to a SimpleDateFormat.
		SimpleDateFormat sdf;
		try
		{
			sdf = (SimpleDateFormat)formatter;
		}
		catch ( ClassCastException cce )
		{
			// If we can't get a SimpleDateFormat for our Locale, there's
			// nothing we can do but return the default mask.
			return def;
		}

		// Get the formatter's pattern and turn it into a date mask.
		mask = parseDateFormatPattern( sdf.toPattern(), true );
		if ( isJulianDateMask( mask, true ) )
		{
			shortJulianDateMask = mask;
			return mask;
		}
		else
		{
			// It's not valid, so return the default value.
			shortJulianDateMask = def;
			return def;
		}
	}
	
	/**
	 * @return true if the language specified by the NLS code always uses
	 * single-byte characters.
	 */
	public boolean isSingleByteLanguage()
	{
		switch ( languageID )
		{
			case CHS:
			case CHT:
			case JPN:
			case KOR:
				return false;
				
			default:
				return true;
		}
	}

	/**
	 * Returns true if <code>b</code> is an SBCS character in the ASCII
	 * codepage that corresponds to this object's NLS code.  
	 * <code>b</code> must be the first byte of the character.
	 *
	 * @param b  the character to examine.
	 * @return   true if <code>b</code> is an SBCS character.
	 */
	public boolean isAsciiSingleByteChar( byte b )
	{
		switch ( languageID )
		{
			case CHS:
			case CHT:
				// The first byte of Simplified Chinese (codepage 1386, GBK)
				// and Traditional Chinese (codepage 950, BIG-5) DBCS 
				// characters on ASCII systems range from 0x81 to 0xFE.  We
				// support another Simplified Chinese codepage (1381, IBM GB),
				// in which DBCS characters range from 0x8C to 0xFE.  This 
				// routine should work correctly for that one as well
				return (b < (byte)0x81 || (byte)0xFE < b);

			case JPN:
				// The first byte of Japanese DBCS characters on ASCII 
				// systems range from 0x81 to 0x9F and 0xE0 to 0xFC.
				return (b < (byte)0x81 || ((byte)0x9F < b && b < (byte)0xE0) || (byte)0xFC < b);

			case KOR:
				// The first byte of Korean DBCS characters on ASCII 
				// systems range from 0x8F to 0xFE.
				return (b < (byte)0x8F || (byte)0xFE < b);

			default:
				// The other environments always use SBCS characters.
				return true;
		}
	}

	/**
	 * Parses <code>pattern</code> into an equivalent EGL date mask.
	 * <code>pattern</code> is expected to be a time pattern string
	 * of the type used by java.text.SimpleDateFormat.  The result is
	 * not garaunteed to be a valid mask and should be validated using
	 * <code>isJulianDateMask</code> or <code>isGregorianDateMask</code>.
	 *
	 * <P> The parsing is done according to these rules:
	 * <UL>
	 * <LI> One or more of M (month) becomes MM.
	 * <LI> One or more of D (day of year) becomes DDD.
	 * <LI> One or more of d (day of month) becomes dd.
	 * <LI> One or more of y (year) becomes yy or yyyy, depending on
	 *      <code>shortFormat</code>.
	 * <LI> Characters between the fields are kept as separators, but there
	 *      must be exactly one between any two fields, and none at the front
	 *      or end of the pattern.
	 * </UL>
	 *
	 * @param pattern      the pattern to be parsed.
	 * @param shortFormat  true if a two-digit year is desired, false
	 *                        for a four-digit year.
	 * @return the VG equivalent of <code>pattern</code>, or 
	 *                        <code>pattern</code> itself if there
	 *                        is no VG equivalent.
	 * @see JavartUtil#isGregorianDateMask
	 * @see JavartUtil#isJulianDateMask
	 * @see java.text.SimpleDateFormat
	 */
	public static String parseDateFormatPattern( String pattern, boolean shortFormat )
	{
		char quoteChar = '\'';
		StringBuilder buf = new StringBuilder();
		int count_y = 0, count_m = 0, count_d = 0, count_D = 0;
		boolean inQuote = false;
		int length = pattern.length();
		for ( int i = 0; i < length; )
		{
			char ch = pattern.charAt( i++ );
			if ( ch == quoteChar )
			{
				buf.append( ch );
				if ( i < length )
				{
					ch = pattern.charAt( i );
					if ( ch == quoteChar )
					{
						buf.append( ch );
						i++;
						continue;
					}
				}

				if ( !inQuote )
				{
					inQuote = true;
				}
				else
				{
					inQuote = false;
				}
				continue;
			}

			if ( inQuote )
			{
				buf.append( ch );
				continue;
			}
			else if ( ch == 'y' )
			{
				if ( count_y == 0 )
				{
					if ( shortFormat )
						buf.append( "yy" );
					else
						buf.append( "yyyy" );
				}
				count_y++;
				continue;
			}
			else if ( ch == 'M' )
			{
				if ( count_m == 0 )
				{
					buf.append( "MM" );
				}
				count_m++;
				continue;
			}
			else if ( ch == 'd' )
			{
				if ( count_d == 0 )
				{
					buf.append( "dd" );
				}
				count_d++;
				continue;
			}
			else if ( ch == 'D' )
			{
				if ( count_D == 0 )
				{
					buf.append( "DDD" );
				}
				count_D++;
				continue;
			}
			else
			{
				buf.append( ch );
			}
		}
		return buf.toString();
	}
	
	/**
	 * Sets the languageID field to the constant that corresponds to the
	 * NLS code.
	 */
	private void setLanguageID()
	{
		if ( nlsCode.equals( NLS_CHS ) )
		{
			languageID = CHS;
		}
		else if ( nlsCode.equals( NLS_CHT ) )
		{
			languageID = CHT;
		}
		else if ( nlsCode.equals( NLS_DES ) )
		{
			languageID = DES;
		}
		else if ( nlsCode.equals( NLS_DEU ) )
		{
			languageID = DEU;
		}
		else if ( nlsCode.equals( NLS_ENU ) )
		{
			languageID = ENU;
		}
		else if ( nlsCode.equals( NLS_ESP ) )
		{
			languageID = ESP;
		}
		else if ( nlsCode.equals( NLS_FRA ) )
		{
			languageID = FRA;
		}
		else if ( nlsCode.equals( NLS_ITA ) )
		{
			languageID = ITA;
		}
		else if ( nlsCode.equals( NLS_JPN ) )
		{
			languageID = JPN;
		}
		else if ( nlsCode.equals( NLS_KOR ) )
		{
			languageID = KOR;
		}
		else if ( nlsCode.equals( NLS_PTB ) )
		{
			languageID = PTB;
		}
		else if ( nlsCode.equals( NLS_RUS ) )
		{
			languageID = RUS;
		}
		else if ( nlsCode.equals( NLS_PLK ) )
		{
			languageID = PLK;
		}
		else if ( nlsCode.equals( NLS_HUN ) )
		{
			languageID = HUN;
		}
		else if ( nlsCode.equals( NLS_CZE ) )
		{
			languageID = CZE;
		}
		else if ( nlsCode.equals( NLS_ARA ) )
		{
			languageID = ARA;
		}
		else
		{
			// Default to ENU.
			languageID = ENU;
		}
	}

	/**
	 * Returns the languageID: the constant that corresponds to the NLS code.
	 */
	public int getLanguageID()
	{
		return languageID;
	}

	/**
	 * Returns the text of the message with the given ID in this object's
	 * Locale.  Checks the user message file first, and then the message bundle.
	 * Returns null if there's no message with the given ID.
	 * 
	 * @param id  the message ID.
	 * @return the text of the message.
	 */
	public String getMessage( String id )
	{
		if ( eglMessages == null )
		{
			// The messages need to be loaded.  Load the user messages first.
			boolean userMessagesMissing = false;
			String userMessagesFile = properties.get( "egl.messages.file" );
			if ( userMessagesFile != null )
			{
				// Check the cache.
				userMessages = 
					(ResourceBundle)bundleCache.get( userMessagesFile + locale );
				if ( userMessages == null )
				{
					// Not found, so load it and store in the cache for next time.
					try
					{
						userMessages = 
							ResourceBundle.getBundle( userMessagesFile, locale );
						bundleCache.put( userMessagesFile + locale, userMessages );
					}
					catch ( MissingResourceException mrx )
					{
						// File not found.  We'll deal with this problem below.
						userMessagesMissing = true;
					}
				}
			}
			
			// Load the EGL messages.
			eglMessages = (ResourceBundle)bundleCache.get( locale );
			if ( eglMessages == null )
			{
				eglMessages = 
					ResourceBundle.getBundle( "org.eclipse.edt.javart.messages.MessageBundle", locale );
				bundleCache.put( locale, eglMessages );
			}
			
			if ( userMessagesMissing )
			{
				// Return the message for "properties file not found".
				String text = getMessageText( Message.PROPERTIES_FILE_MISSING );
				MessageFormat mf = new MessageFormat( text, locale );
				return mf.format( new Object[] { userMessagesFile + ".properties" } );
			}
		}
		
		return getMessageText( id );
	}
	
	/**
	 * Returns the text for the message ID.  This assumes the bundles have
	 * already been loaded.
	 * 
	 * @param id  the message ID.
	 * @return the message text, or null if there's no such message.
	 */
	private String getMessageText( String id )
	{
		// Look in the userMessages then the eglMessages.
		if ( userMessages != null )
		{
			try
			{
				return userMessages.getString( id );
			}
			catch ( MissingResourceException mrx )
			{
				// Ignore.
			}
		}
		try
		{
			return eglMessages.getString( id );
		}
		catch ( MissingResourceException mrx )
		{
			// There's no such message.
			return null;
		}
	}

	/**
	 * Returns a date-time formatter for the Locale.
	 */
	public DateFormat getDateFormatter()
	{
		if ( dateFormatter == null )
		{
			dateFormatter = DateFormat.getDateTimeInstance( DateFormat.DEFAULT, DateFormat.DEFAULT, locale );
		}
		
		return dateFormatter;
	}

	/**
	 * Returns the text of the message with the given ID in this object's
	 * Locale.  Checks the user message file first, and then the message bundle.
	 * Returns null if there's no message with the given ID.
	 * 
	 * @param id       the message ID.
	 * @param inserts  the message inserts.
	 * @return the text of the message.
	 */
	public String getMessage( String id, Object... inserts )
	{
		String text = getMessage( id );
		if ( inserts == null || text == null || inserts.length == 0 )
		{
			return text;
		}
		MessageFormat mf = new MessageFormat( text, locale );
		return mf.format( inserts );
	}
}
