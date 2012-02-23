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
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Utility methods for the core debug preferences.
 */
public class PreferenceUtil
{
	private PreferenceUtil()
	{
		// No instances.
	}
	
	public static void setToDefault( String key )
	{
		InstanceScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).remove( key );
	}
	
	public static void setDefaultString( String key, String value )
	{
		if ( value == null )
		{
			DefaultScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).remove( key );
		}
		else
		{
			DefaultScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).put( key, value );
		}
	}
	
	public static void setDefaultBoolean( String key, boolean value )
	{
		DefaultScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).putBoolean( key, value );
	}
	
	public static String getDefaultString( String key, String defaultValue )
	{
		return DefaultScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).get( key, defaultValue );
	}
	
	public static boolean getDefaultBoolean( String key, boolean defaultValue )
	{
		return DefaultScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).getBoolean( key, defaultValue );
	}
	
	public static void setString( String key, String value )
	{
		if ( value == null )
		{
			InstanceScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).remove( key );
		}
		else
		{
			InstanceScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).put( key, value );
		}
	}
	
	public static void setBoolean( String key, boolean value )
	{
		InstanceScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).putBoolean( key, value );
	}
	
	public static String getString( String key, String defaultValue )
	{
		return InstanceScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).get( key, defaultValue );
	}
	
	public static boolean getBoolean( String key, boolean defaultValue )
	{
		return InstanceScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).getBoolean( key, defaultValue );
	}
	
	public static synchronized void savePreferences()
	{
		try
		{
			DefaultScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).flush();
		}
		catch ( BackingStoreException bse )
		{
			EDTDebugCorePlugin.log( bse );
		}
		
		try
		{
			InstanceScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).flush();
		}
		catch ( BackingStoreException bse )
		{
			EDTDebugCorePlugin.log( bse );
		}
	}
	
	public static void addPreferenceChangeListener( IPreferenceChangeListener listener )
	{
		InstanceScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).addPreferenceChangeListener( listener );
	}
	
	public static void removePreferenceChangeListener( IPreferenceChangeListener listener )
	{
		InstanceScope.INSTANCE.getNode( EDTDebugCorePlugin.PLUGIN_ID ).removePreferenceChangeListener( listener );
	}
}
