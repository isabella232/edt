/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.eglarpackager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.j2ee.project.JavaEEProjectUtilities;

public class EglarUtility {
	
	public static IResource[] getGeneratedJavaClassFolder(IProject project) {
		List<IPath> outputLocations = new ArrayList<IPath>(getOutputLocation(project));
		
		if ((outputLocations != null)) {
			IResource[] resources = new IResource[outputLocations.size()];
			for ( int i = 0; i < outputLocations.size(); i ++ ) {
				resources[i] = project.getFolder(outputLocations.get(i).removeFirstSegments(1));
			}
			return resources;
		} 
		return null;
	}
	
	public static Set<IPath> getOutputLocation(IProject project) {
		Set<IPath> paths = new HashSet<IPath>();

		try {
			IJavaProject javaProject = null;
			if(JavaEEProjectUtilities.isDynamicWebProject(project) || project.hasNature("org.eclipse.jdt.core.javanature")) {
				javaProject = JavaCore.create(project);
				paths.add( javaProject.getOutputLocation() );
			}
			if ( javaProject != null ) {
				IClasspathEntry[] entries = javaProject.getRawClasspath();
				for ( int i = 0; i < entries.length; i ++ ) {
					if ( entries[i].getOutputLocation() != null ) {
						paths.add( entries[i].getOutputLocation() );
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return paths;
	}
}
