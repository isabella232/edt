/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
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

/**
 * Used by a Program to describes the kind of RunUnit that it wants. 
 */
public class StartupInfo implements Serializable
{
	private static final long serialVersionUID = 70L;

	/**
	 * The J2EE flag, defaults to false.
	 */
	private boolean isJ2EE;
	
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
	 * The EJB flag, defaults to false.
	 */
	private boolean isEJB;
	
	/**
	 * Command line arguments for main programs
	 */
	private String[] commandLineArgs;
	
	/**
	 * LWI flag, default to false
	 */
	private boolean isLWI = false;
	
	/**
	 * Creates a StartupInfo.
	 */
	public StartupInfo( String ruName, String propertyFilePath, boolean isJ2EE )
	{
		this.ruName = ruName;
		this.propertyFilePath = propertyFilePath;
		this.isJ2EE = isJ2EE;
	}
	
	/**
	 * Creates a StartupInfo.
	 */
	public StartupInfo( String ruName, String propertyFilePath, boolean isJ2EE, boolean isLWI )
	{
		this( ruName, propertyFilePath, isJ2EE );
		this.isLWI = isLWI;
	}

	/**
	 * Returns the isJ2EE flag.
	 * 
	 * @return the isJ2EE flag.
	 */
	public boolean isJ2EE()
	{
		return isJ2EE;
	}
	
	/**
	 * Returns the isLWI flag.
	 * 
	 * @return the isLWI flag.
	 */
	public boolean isLWI()
	{
		return isLWI;
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
	 * Returns the isEJB flag.
	 * 
	 * @return the isEJB flag.
	 */
	public boolean isEJB()
	{
		return isEJB;
	}

	/**
	 * Sets the isEJB flag.
	 * 
	 * @param isEJB  the new isEJB flag. 
	 */
	public void setEJB( boolean isEJB )
	{
		this.isEJB = isEJB;
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
	
	/**
	 * Sets the command line arguments for main programs.
	 * 
	 * @param commandLineArgs the commandLineArgs to set.
	 */
	public void setArgs(String[] commandLineArgs)
	{
		this.commandLineArgs = commandLineArgs;
	}
}
