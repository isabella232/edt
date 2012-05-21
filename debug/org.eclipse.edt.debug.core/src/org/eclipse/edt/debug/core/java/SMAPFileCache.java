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
package org.eclipse.edt.debug.core.java;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Cache of SMAP files that are read off disk. This would typically only be used when JSR-45 is not available.
 */
public class SMAPFileCache
{
	private final Map<String, String> classNameToSMAP;
	private final Map<String, String> workspacePathToClassName;
	
	public SMAPFileCache()
	{
		classNameToSMAP = new HashMap<String, String>();
		workspacePathToClassName = new HashMap<String, String>();
	}
	
	/**
	 * Adds an SMAP entry to the cache.
	 * 
	 * @param className The class name for the SMAP.
	 * @param smap The SMAP file contents.
	 * @param workspacePath The workspace-relative path to the file, possibly null (e.g. when the file is in an archive).
	 */
	public void addEntry( String className, String smap, String workspacePath )
	{
		classNameToSMAP.put( className, smap );
		
		if ( workspacePath != null && workspacePath.length() > 0 && ResourcesPlugin.getWorkspace().getRoot().findMember( workspacePath ) != null )
		{
			workspacePathToClassName.put( workspacePath, className );
		}
	}
	
	/**
	 * @return the class name based on the workspace path entry, possibly null.
	 */
	public String getClassName( String workspacePath )
	{
		return workspacePathToClassName.get( workspacePath );
	}
	
	/**
	 * @return the SMAP data cached for the given class name, possibly null.
	 */
	public String getSMAP( String className )
	{
		return classNameToSMAP.get( className );
	}
	
	/**
	 * @return true if there is a cache entry for the given class name.
	 */
	public boolean containsSMAP( String className )
	{
		return classNameToSMAP.containsKey( className );
	}
	
	/**
	 * Removes an entry from the cache based on the workspace path. If there is no corresponding entry, the cache is unchanged. When className is
	 * specified the SMAP entry removed is based on this value; otherwise when workspacePath is specified the corresponding class name for the
	 * workspace path is used to uncache the SMAP entry.
	 * 
	 * @param workspacePath The path of the file to uncache.
	 * @param className The class name to uncache, overriding a class name corresponding to workspacePath; may be null.
	 * @return the SMAP data that was uncached, or null if there was no corresponding entry.
	 */
	public String removeEntry( String workspacePath, String className )
	{
		if ( workspacePath != null )
		{
			String removed = workspacePathToClassName.remove( workspacePath );
			if ( className == null || className.length() == 0 )
			{
				className = removed;
			}
		}
		
		if ( className != null && className.length() > 0 )
		{
			return classNameToSMAP.remove( className );
		}
		return null;
	}
	
	/**
	 * Clears all cache entries.
	 */
	public void clear()
	{
		workspacePathToClassName.clear();
		classNameToSMAP.clear();
	}
}
