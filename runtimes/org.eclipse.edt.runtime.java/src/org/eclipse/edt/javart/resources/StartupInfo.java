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

import org.eclipse.edt.javart.Constants;

/**
 * Used by a Program to describe the kind of RunUnit that it wants. 
 */
public class StartupInfo implements Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	
	/**
	 * The name for the run unit, defaults to null.
	 */
	private String ruName;
	
	/**
	 * The properties for the run unit, defaults to null.
	 */
	private JavartProperties props;
	
	/**
	 * The path for the property file to use, defaults to null.
	 */
	private String propertyFilePath;
	
	/**
	 * Command line arguments for main programs
	 */
	private String[] commandLineArgs;
	
	/**
	 * Creates a StartupInfo.
	 */
	public StartupInfo( String ruName, String propertyFilePath, String[] commandLineArgs )
	{
		this.ruName = ruName;
		this.propertyFilePath = propertyFilePath;
		this.commandLineArgs = commandLineArgs;
	}

	/**
	 * Returns the properties.
	 * 
	 * @return the properties.
	 */
	public JavartProperties getProperties()
	{
		return props;
	}

	/**
	 * Sets the properties.
	 * 
	 * @param props  the properties.
	 */
	public void setProperties( JavartProperties props )
	{
		this.props = props;
	}

	/**
	 * Returns the ruName.
	 * 
	 * @return the ruName.
	 */
	public String getRuName()
	{
		return ruName;
	}

	/**
	 * Returns the propertyFilePath.
	 * 
	 * @return the propertyFilePath.
	 */
	public String getPropertyFilePath()
	{
		return propertyFilePath;
	}
	
	/**
	 * Returns the command line arguments.
	 * 
	 * @return	the array of command line arguments.
	 */
	public String[] getArgs()
	{
		return commandLineArgs;
	}
}
