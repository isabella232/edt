/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.core.java.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.core.PDEClasspathContainer;

/**
 * A filter that uses the classpath of a plug-in from the target platform.
 */
@SuppressWarnings("restriction")
public abstract class ExternalPluginClassFilter extends ClasspathEntryFilter
{
	@Override
	protected IClasspathEntry[] getCommonClasspathEntries()
	{
		String[] pluginIds = getPluginIds();
		if ( pluginIds != null && pluginIds.length > 0 )
		{
			List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
			for ( String pluginId : pluginIds )
			{
				IPluginModelBase model = PluginRegistry.findModel( pluginId );
				if ( model != null )
				{
					list.addAll( Arrays.asList( PDEClasspathContainer.getExternalEntries( model ) ) );
				}
			}
			return list.toArray( new IClasspathEntry[ list.size() ] );
		}
		return null;
	}
	
	/**
	 * @return the IDs of the target platform plug-ins whose classes should be filtered.
	 */
	public abstract String[] getPluginIds();
}
