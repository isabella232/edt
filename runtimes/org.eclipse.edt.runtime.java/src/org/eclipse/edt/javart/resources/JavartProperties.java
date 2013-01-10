/*******************************************************************************
 * Copyright © 2006, 2013 IBM Corporation and others.
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
import java.util.*;

import org.eclipse.edt.javart.Constants;

/**
 * JavartProperties holds the runtime properties.
 */
public abstract class JavartProperties implements Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

	/**
	 * Retrieves the property value associated with given key.
	 *
	 * @param   key  the key for the property.
	 */
	public abstract String get( String key );

	/**
	 * Retrieves the property value associated with given key.
	 *
	 * @param   propName  the key for the property.
	 * @param   defVal    default value if the property isn't found.
	 */
	public abstract String get( String propName, String defVal );

	/**
	 * Add a new properties entry or modify an existing one.
	 * 
	 * @param	key		the property key
	 * @param	value	the property value
	 */
	public abstract void put( String key, String value );

	/**
	 * Remove a properties entry.
	 * 
	 * @param	key		the property key
	 */
	public abstract void remove( String key );

	/**
	 * Provides trace info for this object.
	 */
	public abstract String getInfo();

	/**
	 * Returns the settings in a Properties object.
	 */
	public abstract Properties getProperties();

	/**
	 * Returns the name of the program property file.  If there is none,
	 * the String "null" is returned.
	 *
	 * @return the name of the program property file.
	 */
	public abstract String getProgramPropertiesFile();

	/**
	 * Initialize minimum default properties for this RU.
	 *
	 * @param  settings  property object reference.
	 */
	protected void initDefaultSettings( Properties settings )
	{
		// Set default properties.
		settings.put( "egl.trace.type", "0" ); // no trace is enabled
	}
}
