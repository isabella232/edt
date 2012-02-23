/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.core;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Utility methods for the core debug preferences.
 */
public class PreferenceUtil
{
	/**
	 * Holds instance preference values.
	 */
	private static IEclipsePreferences instanceNode;
	
	/**
	 * Holds default preference values.
	 */
	private static IEclipsePreferences defaultNode;
	
	private PreferenceUtil()
	{
		// No instances.
	}
	
	public static void setToDefault( String key )
	{
		getInstanceNode().remove( key );
	}
	
	public static void setDefaultString( String key, String value )
	{
		if ( value == null )
		{
			getDefaultNode().remove( key );
		}
		else
		{
			getDefaultNode().put( key, value );
		}
	}
	
	public static void setDefaultBoolean( String key, boolean value )
	{
		getDefaultNode().putBoolean( key, value );
	}
	
	public static String getDefaultString( String key, String defaultValue )
	{
		return getDefaultNode().get( key, defaultValue );
	}
	
	public static boolean getDefaultBoolean( String key, boolean defaultValue )
	{
		return getDefaultNode().getBoolean( key, defaultValue );
	}
	
	public static void setString( String key, String value )
	{
		if ( value == null )
		{
			getInstanceNode().remove( key );
		}
		else
		{
			getInstanceNode().put( key, value );
		}
	}
	
	public static void setBoolean( String key, boolean value )
	{
		getInstanceNode().putBoolean( key, value );
	}
	
	public static String getString( String key, String defaultValue )
	{
		return getInstanceNode().get( key, defaultValue );
	}
	
	public static boolean getBoolean( String key, boolean defaultValue )
	{
		return getInstanceNode().getBoolean( key, defaultValue );
	}
	
	public static synchronized void savePreferences()
	{
		try
		{
			getDefaultNode().flush();
		}
		catch ( BackingStoreException bse )
		{
			EDTDebugCorePlugin.log( bse );
		}
		
		try
		{
			getInstanceNode().flush();
		}
		catch ( BackingStoreException bse )
		{
			EDTDebugCorePlugin.log( bse );
		}
	}
	
	public static void addPreferenceChangeListener( IPreferenceChangeListener listener )
	{
		getInstanceNode().addPreferenceChangeListener( listener );
	}
	
	public static void removePreferenceChangeListener( IPreferenceChangeListener listener )
	{
		getInstanceNode().removePreferenceChangeListener( listener );
	}
	
	@SuppressWarnings("deprecation")
	private static IEclipsePreferences getDefaultNode()
	{
		if ( defaultNode == null )
		{
			// Cannot use InstanceScope.INSTANCE because it doesn't exist in Eclipse 3.6.
			defaultNode = new DefaultScope().getNode( EDTDebugCorePlugin.PLUGIN_ID );
		}
		return defaultNode;
	}
	
	@SuppressWarnings("deprecation")
	private static IEclipsePreferences getInstanceNode()
	{
		if ( instanceNode == null )
		{
			// Cannot use InstanceScope.INSTANCE because it doesn't exist in Eclipse 3.6.
			instanceNode = new InstanceScope().getNode( EDTDebugCorePlugin.PLUGIN_ID );
		}
		return instanceNode;
	}
}
