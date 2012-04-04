/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.util;


/**
 * @deprecated
 * This class is not needed anymore since we will no longer have the iSeries package version 
 * 
 * @author svihovec
 *
 * This utility can be used to find out if we are currently running as part of 
 * the iSeries or WSED packaging.
 */
public class EGLPackageVersionUtility {
	
	/**
	 * The following ID's are to be used when defining the packageVersion extension
	 * point.  The ID's are used to describe which package version the fragment has been
	 * created for.
	 */
	public static final String WSED_PACKAGE_VERSION_ID = "WSED"; //$NON-NLS-1$
	public static final String ISERIES_PACKAGE_VERSION_ID = "ISERIES"; //$NON-NLS-1$
	
	/**
	 * The following are the public keys for the 2 EGL DTDs that we have
	 * They are the same as those used in the fragments to register with the 
	 * XML DTD catalogue
	 */
	public static final String WSED_DTD_ID = "-//IBM//DTD EGL 5.1//EN"; //$NON-NLS-1$
	public static final String ISERIES_DTD_ID = "-//IBM//DTD EGL ISERIES 5.1//EN"; //$NON-NLS-1$
	
	/**
	 * Check to see if we are running in the WSED package
	 */
	public static final boolean isWSEDPackageVersion()
	{
		return true;
	}
	
	/**
	 * Check to see if we are running in the iSeries package
	 */
	public static final boolean isiSeriesPackageVersion()
	{
		return false;
	}
	
	/**
	 * Get a list of all registered package versions and see if this verison
	 * is in the list.
	 */
//	private static final boolean workbenchContainsPackageVersion(String version)
//	{
//		boolean result = false;
//		String[] versions = getPackageVersions();
//		
//		for(int i=0; i<versions.length; i++)
//		{
//			if(versions[i].equalsIgnoreCase(version))
//			{
//				result = true;
//				break;
//			}
//		}
//		
//		return result;
//	}
		
	
	/**
	 * Attempt to get the extensions for the packageVersion extension point
	 */
//	private static final String[] getPackageVersions()
//	{
//		ArrayList result = new ArrayList();
//		
//		IConfigurationElement[] elements = Platform.getPluginRegistry().getConfigurationElementsFor(EGLBasePlugin.EGL_PACKAGE_VERSION_EXTENSION_POINT_ID);
//		IConfigurationElement currentElement;
//		  
//		for (int i=0; i < elements.length; i++) 
//		{
//			currentElement = elements[i];
//			
//			// if this is a packageVersion id
//			if (currentElement.getName().equalsIgnoreCase(EGLBasePlugin.EGL_PACKAGE_VERSION_ELEMENT_ID)) 
//			{
//				// get the version id
//				result.add(currentElement.getAttribute(EGLBasePlugin.EGL_PACKAGE_VERSION_ATTRIBUTE_ID));
//			}
//		}
//			
//		return (String[])result.toArray(new String[result.size()]);
//	}

}
