/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;

/**
 * @author jshavor
 */
public class ProjectPathUtility {

	/**
	 * Adds the project's location and the source folders of the project's EGL
	 * path to the Collection.  If other projects are referenced, their locations
	 * and source folders are added recusively.
	 * 
	 * @param project  the project.
	 * @param eglPath  the Collection to add to.
	 * @param root     the workspace root.
	 * @param visited  the projects that have already been examined.
	 */
	static public void addEglPathOfProject( 
		IProject project, 
		Collection eglPath,
		IWorkspaceRoot root, 
		Set visited )
	{
		// Break cycles and prevent repetition.
		if ( visited.contains( project ) )
		{
			return;
		}
		else
		{
			visited.add( project );
		}

		// Get the project and its EGL path.
		if ( EGLProject.hasEGLNature( project ) )
		{
			try
			{
				EGLProject eglProject = (EGLProject)EGLCore.create( project );
				IEGLPathEntry[] entries = eglProject.getResolvedEGLPath( true );
				for ( int i = 0; i < entries.length; i++ )
				{
					IEGLPathEntry entry = entries[ i ];

					// Add all the SOURCE entries and recurse on all the 
					// PROJECT entries.
					if ( entry.getEntryKind() == IEGLPathEntry.CPE_SOURCE )
					{
						IResource member = root.findMember( entry.getPath() );
						if ( member != null && member.getLocation() != null) 
						{
							eglPath.add( member.getLocation().toOSString() );
						}						
					}

					else if (entry.getEntryKind() == IEGLPathEntry.CPE_LIBRARY){
	                		eglPath.add(resolvePathString(entry.getPath()));
					}
					else if ( entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT )
					{
						IResource member = root.findMember( entry.getPath() ); 
						if ( member != null && member.getType() == IResource.PROJECT )
						{
							addEglPathOfProject( (IProject)member, eglPath, root, visited );
						}
					}
				}
			}
			catch ( EGLModelException emx )
			{
				// Ignore it.  (Hope that's safe!)
			}
		}

		// Add the location of the project.  This is needed for imports from
		// one .eglbld file to another.
		eglPath.add( project.getLocation().toOSString() );
	}

	
	/**
	 * returns a string representing the project's EGL path.  
	 * See comment for addEglPathOfProject().
	 * 
	 * @param project  the project.
	 */
	static public String getEglPathString(IProject[] projects) {
		
		// Get the workspace root.
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		// Find all the source folders in the project's EGL path.
		Collection eglPathSet = new ArrayList();  //ArrayList because order is important!!!
		for (int i = 0; i < projects.length; i++){
			IProject project = projects[i];
			addEglPathOfProject(project, eglPathSet, root, new HashSet());
		}
		String eglProjectPathString = ""; //$NON-NLS-1$
		HashSet paths = new HashSet(); 
		for (Iterator iter = eglPathSet.iterator(); iter.hasNext();) {
			String s = (String) iter.next();
			if (!paths.contains(s)){
				paths.add(s);
				eglProjectPathString += s;
				eglProjectPathString += File.pathSeparator;
			}
		}
		return eglProjectPathString;
	}

    private static String resolvePathString(IPath path) {   	
    	return AbsolutePathUtility.getAbsolutePathString(path);
    }	
}
