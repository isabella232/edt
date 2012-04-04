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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.edt.javart.Constants;

/**
 * This class gets properties from properties files.  Properties come from
 * up to three files: rununit.properties, a file named after the first program
 * in the run unit, and user.properties.  Rununit.properties is found by 
 * searching the classpath.  The program.properties file is found in the same
 * place as the program class.  The user.properties file is found in the
 * directory specified by the "user.home" JVM system property.  Settings in
 * user.properties take precendence over settings in program.properties, which
 * take precendence over settings in rununit.properties.
 */
public class JavartPropertiesFile extends JavartProperties
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * Contains the run unit property settings.
	 */
	protected static Properties rununitProperties;

	/**
	 * Name of the file that the run unit properties came from.
	 */
	private static String ruPropFile;

	/**
	 * Contains the user property settings.
	 */
	protected static Properties userProperties;

	/**
	 * Name of the file that the user properties came from.
	 */
	private static String userPropFile;

	/**
	 * Contains the program-specific, run unit, and user property settings.
	 */
	protected Properties pgmProperties;

	/**
	 * Name of the file that the program-specific properties came from.
	 */
	private String pgmPropFile;

	/**
	 * Used by the debugger
	 * Create a program property object
	 *
	 * @param  propFile   program property file
	 */
	protected JavartPropertiesFile()
	{
	}
	
	/**
	 * Create a program property object
	 *
	 * @param  propFile   program property file
	 */
	public JavartPropertiesFile( String propFile )
	{
		if ( rununitProperties == null )
		{
			getRunUnitProps();
		}
		if ( userProperties == null )
		{
			getUserProps();
		}
		getProgramProps( propFile );
	}

	/**
	 * Retrieves the property value associated with given key.
	 *
	 * @param   key  the key for the property.
	 */
	public String get( String key )
	{
		return pgmProperties.getProperty( key );
	}

	/**
	 * Retrieves the property value associated with given key.
	 *
	 * @param   propName  the key for the property.
	 * @param   defVal    default value if the property isn't found.
	 */
	public String get( String propName, String defVal )
	{
		return pgmProperties.getProperty( propName, defVal );
	}

	/**
	 * Add a new properties entry.
	 * 
	 * @param	key		the property key
	 * @param	value	the property value
	 */
	public void put( String key, String value )
	{
		pgmProperties.put( key, value );
	}

	/**
	 * Provides trace info for this object.
	 */
	public String getInfo()
	{
		return "User Properties: " + userPropFile + 
				", Program Properties: " + pgmPropFile +
				", RunUnit Properties: " + ruPropFile;
	}
	
	/**
	 * Returns the settings in a Properties object.
	 */
	public Properties getProperties()
	{
		if ( pgmProperties == null )
		{
			return null;
		}
		return new Properties( pgmProperties );
	}

	/**
	 * Returns the name of the program property file.  If there is none,
	 * the String "null" is returned.
	 *
	 * @return the name of the program property file.
	 */
	public String getProgramPropertiesFile()
	{
		return pgmPropFile;
	}

	/**
	 * Loads the run unit properties. 
	 */
	protected void getRunUnitProps()
	{
		try
		{
			// Find the file.
			URL url = getClass().getClassLoader().getResource( "rununit.properties" );
			if ( url == null )
			{
				// Not found.
				rununitProperties = new Properties();
				initDefaultSettings( rununitProperties );
			}
			else
			{
				// Read from the file.
				InputStream iStream = url.openStream();
				rununitProperties = new Properties();
				rununitProperties.load( iStream );
				ruPropFile = url.toString();
				iStream.close();
			}
		}
		catch ( IOException e )
		{
			// Act like the file wasn't found.
			rununitProperties = new Properties();
			initDefaultSettings( rununitProperties );
		}
	}

	/**
	 * Loads the user properties. 
	 */
	protected void getUserProps()
	{
		try
		{
			// Find the file.
			String fileName = System.getProperty( "user.home" );
			if ( fileName == null )
			{
				// Not found.
				userProperties = new Properties();
			}
			else
			{
				// Read from the file.
				if ( !fileName.endsWith( File.separator ) )
				{
					fileName = fileName + File.separatorChar;
				}
				fileName += "user.properties";
				InputStream iStream = new FileInputStream( fileName );
				userProperties = new Properties();
				userProperties.load( iStream );
				userPropFile = fileName;
				iStream.close();
			}
		}
		catch ( SecurityException sx )
		{
			// Ignore it.
		}
		catch ( IOException iox )
		{
			// Ignore it.
		}
	}

	/**
	 * Loads the program properties file and sets pgmProperties to contain
	 * values from all three properties files. 
	 * 
	 * @param fileName  name of the program properties file.
	 */
	protected void getProgramProps( String fileName )
	{
		pgmProperties = new Properties( rununitProperties );
		
		try
		{
			// Find the file.
			URL url = ( fileName != null
					? getClass().getClassLoader().getResource( fileName ) : null );
			if ( url != null )
			{
				// Read from the file.
				InputStream iStream = url.openStream();
				if ( iStream != null )
				{
					pgmProperties.load( iStream );
					pgmPropFile = url.toString();
					iStream.close();
				}
			}
		}
		catch ( IOException e )
		{
			// Ignore it.
		}
		finally
		{
			if ( userProperties != null )
			{
				pgmProperties.putAll( userProperties );
			}
		}
	}

	/**
	 * Remove a properties entry.
	 * 
	 * @param	key		the property key
	 */
	public void remove( String key )
	{
		pgmProperties.remove( key );
	}
}
