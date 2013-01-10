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
package org.eclipse.edt.ide.core.internal.model.indexing;

import java.util.ArrayList;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.internal.model.EGLProject;
import org.eclipse.edt.ide.core.internal.model.index.IIndex;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLModel;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;


/**
 * Selects the indexes that correspond to projects in a given search scope
 * and that are dependent on a given focus element.
 */
public class IndexSelector {
//	IEGLSearchScope searchXScope;
	IEGLElement focus;
	IndexManager indexManager;
	IPath[] indexKeys; // cache of the keys for looking index up
	boolean isPolymorphicSearch;
	IPath[] projectsAndJars;
public IndexSelector(
	IPath[] enclosingProjects,
//	IEGLSearchScope searchScope,
	IEGLElement focus,
	boolean isPolymorphicSearch,
	IndexManager indexManager) {
//	this.searchScope = searchScope;
	projectsAndJars = enclosingProjects;
	this.focus = focus;
	this.indexManager = indexManager;
	this.isPolymorphicSearch = isPolymorphicSearch;
}
/**
 * Returns whether elements of the given project or jar can see the given focus (an IEGLProject or
 * a JarPackageFragmentRot) either because the focus is part of the project or the jar, or because it is 
 * accessible throught the project's classpath
 */
public static boolean canSeeFocus(IEGLElement focus, boolean isPolymorphicSearch, IPath projectOrJarPath) {
	try {
		IEGLModel model = focus.getEGLModel();
		IEGLProject project = getEGLProject(projectOrJarPath, model);
		if (project == null) {
			// projectOrJarPath is a jar
			// it can see the focus only if it is on the classpath of a project that can see the focus
			IEGLProject[] allProjects = model.getEGLProjects();
			for (int i = 0, length = allProjects.length; i < length; i++) {
				IEGLProject otherProject = allProjects[i];
				IEGLPathEntry[] entries = otherProject.getResolvedEGLPath(true);
				for (int j = 0, length2 = entries.length; j < length2; j++) {
					IEGLPathEntry entry = entries[j];
					if ((entry.getEntryKind() == IEGLPathEntry.CPE_LIBRARY) 
						&& entry.getPath().equals(projectOrJarPath)) {
							if (canSeeFocus(focus, isPolymorphicSearch, otherProject.getPath())) {
								return true;
							}
					}
				}
			}
			return false;
		} else {
			// projectOrJarPath is a project
			// EGLTODO: Support JarPackageFragmentRoot?
			//EGLProject focusProject = focus instanceof JarPackageFragmentRoot ? (EGLProject)focus.getParent() : (EGLProject)focus;
			EGLProject focusProject = (EGLProject)focus;
			if (isPolymorphicSearch) {
				// look for refering project
				IEGLPathEntry[] entries = focusProject.getExpandedEGLPath(true);
				for (int i = 0, length = entries.length; i < length; i++) {
					IEGLPathEntry entry = entries[i];
					if ((entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT) 
						&& entry.getPath().equals(projectOrJarPath)) {
							return true;
					}
				}
			}
// EGLTODO: Support JarPackageFragmentRoot?
//			if (focus instanceof JarPackageFragmentRoot) {
//				// focus is part of a jar
//				IPath focusPath = focus.getPath();
//				IEGLPathEntry[] entries = ((EGLProject)project).getExpandedEGLPath(true);
//				for (int i = 0, length = entries.length; i < length; i++) {
//					IEGLPathEntry entry = entries[i];
//					if ((entry.getEntryKind() == IEGLPathEntry.CPE_LIBRARY) 
//						&& entry.getPath().equals(focusPath)) {
//							return true;
//					}
//				}
//				return false;
//			} else {
				// focus is part of a project
				if (focus.equals(project)) {
					return true;
				} else {
					// look for dependent projects
					IPath focusPath = focusProject.getProject().getFullPath();
					IEGLPathEntry[] entries = ((EGLProject)project).getExpandedEGLPath(true);
					for (int i = 0, length = entries.length; i < length; i++) {
						IEGLPathEntry entry = entries[i];
						if ((entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT) 
							&& entry.getPath().equals(focusPath)) {
								return true;
						}
					}
					return false;
				}
//			}
		}
	} catch (EGLModelException e) {
		return false;
	}
}
/*
 *  Compute the list of paths which are keying index files.
 */
private void initializeIndexKeys() {
	
	ArrayList requiredIndexKeys = new ArrayList();
	
	IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	IEGLElement projectOrJarFocus = this.focus == null ? null : getProjectOrJar(this.focus);
	for (int i = 0; i < projectsAndJars.length; i++) {
		IPath location;
		IPath path = projectsAndJars[i];
		if ((!root.getProject(path.lastSegment()).exists()) // if project does not exist
			&& path.segmentCount() > 1
			&& ((location = root.getFile(path).getLocation()) == null
				|| !new java.io.File(location.toOSString()).exists()) // and internal jar file does not exist
			&& !new java.io.File(path.toOSString()).exists()) { // and external jar file does not exist
				continue;
		}
		if (projectOrJarFocus == null || canSeeFocus(projectOrJarFocus, this.isPolymorphicSearch, path)) {
			if (requiredIndexKeys.indexOf(path) == -1) {
				requiredIndexKeys.add(path);
			}
		}
	}
	this.indexKeys = new IPath[requiredIndexKeys.size()];
	requiredIndexKeys.toArray(this.indexKeys);
}
public IIndex[] getIndexes() {
	if (this.indexKeys == null) {
		this.initializeIndexKeys(); 
	}
	// acquire the in-memory indexes on the fly
	int length = this.indexKeys.length;
	IIndex[] indexes = new IIndex[length];
	int count = 0;
	for (int i = 0; i < length; i++){
		// may trigger some index recreation work
		IIndex index = indexManager.getIndex(indexKeys[i], true /*reuse index file*/, false /*do not create if none*/);
		if (index != null) indexes[count++] = index; // only consider indexes which are ready yet
	}
	if (count != length) {
		System.arraycopy(indexes, 0, indexes=new IIndex[count], 0, count);
	}
	return indexes;
}
/**
 * Returns the java project that corresponds to the given path.
 * Returns null if the path doesn't correspond to a project.
 */
private static IEGLProject getEGLProject(IPath path, IEGLModel model) {
	IEGLProject project = model.getEGLProject(path.lastSegment());
	if (project.exists()) {
		return project;
	} else {
		return null;
	}
}
public static IEGLElement getProjectOrJar(IEGLElement element) {
// 	EGLTODO: Support JarPackageFragmentRoot?
	while (!(element instanceof IEGLProject)){ // && !(element instanceof JarPackageFragmentRoot)) {
		element = element.getParent();
	}
	return element;
}
}
