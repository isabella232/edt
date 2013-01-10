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
package org.eclipse.edt.ide.core.internal.model;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;


/** 
 * Info for IEGLProject.
 * <p>
 * Note: <code>getChildren()</code> returns all of the <code>IPackageFragmentRoots</code>
 * specified on the classpath for the project.  This can include roots external to the
 * project. See <code>EGLProject#getAllPackageFragmentRoots()</code> and 
 * <code>EGLProject#getPackageFragmentRoots()</code>.  To get only the <code>IPackageFragmentRoots</code>
 * that are internal to the project, use <code>EGLProject#getChildren()</code>.
 */

/* package */
class EGLProjectElementInfo extends OpenableElementInfo {

	/**
	 * The name lookup facility to use with this project.
	 */
	protected NameLookup fNameLookup = null;

	/**
	 * The searchable builder environment facility used
	 * with this project (doubles as the builder environment). 
	 */
//	protected SearchableEnvironment fSearchableEnvironment = null;

	/**
	 * A array with all the non-java resources contained by this PackageFragment
	 */
	private Object[] fNonEGLResources;

	/**
	 * Create and initialize a new instance of the receiver
	 */
	public EGLProjectElementInfo() {
		fNonEGLResources = null;
	}
	
	/**
	 * Compute the non-EGL resources contained in this java project.
	 */
	private Object[] computeNonEGLResources(EGLProject project) {
		
		// determine if src == project and/or if bin == project
		IPath projectPath = project.getProject().getFullPath();
		boolean srcIsProject = false;
		boolean binIsProject = false;
		char[][] exclusionPatterns = null;
		IEGLPathEntry[] classpath = null;
		IPath projectOutput = null;
		try {
			classpath = project.getResolvedEGLPath(true/*ignore unresolved variable*/);
			for (int i = 0; i < classpath.length; i++) {
				IEGLPathEntry entry = classpath[i];
				if (projectPath.equals(entry.getPath())) {
					srcIsProject = true;
					exclusionPatterns = ((EGLPathEntry)entry).fullExclusionPatternChars();
					break;
				}
			}
			projectOutput = project.getOutputLocation();
			binIsProject = projectPath.equals(projectOutput);
		} catch (EGLModelException e) {
			// ignore
		}

		Object[] nonEGLResources = new IResource[5];
		int nonEGLResourcesCounter = 0;
		try {
			IResource[] members = ((IContainer) project.getResource()).members();
			for (int i = 0, max = members.length; i < max; i++) {
				IResource res = members[i];
				switch (res.getType()) {
					case IResource.FILE :
						IPath resFullPath = res.getFullPath();
						String resName = res.getName();
						
						// ignore a jar file on the classpath
						if (Util.isEGLFileName(resName) && this.isEGLPathEntryOrOutputLocation(resFullPath, classpath, projectOutput)) {
							break;
						}
						// else add non egl resource
						if (nonEGLResources.length == nonEGLResourcesCounter) {
							// resize
							System.arraycopy(
							nonEGLResources,
								0,
								(nonEGLResources = new IResource[nonEGLResourcesCounter * 2]),
								0,
								nonEGLResourcesCounter);
						}
					nonEGLResources[nonEGLResourcesCounter++] = res;
						break;
					case IResource.FOLDER :
						resFullPath = res.getFullPath();
						
						// ignore non-excluded folders on the eglath or that correspond to an output location
						if ((srcIsProject && !Util.isExcluded(res, exclusionPatterns) && Util.isValidFolderNameForPackage(res.getName()))
								|| this.isEGLPathEntryOrOutputLocation(resFullPath, classpath, projectOutput)
								|| this.isEGLbinOutputLocation(resFullPath)) {
							break;
						}
						// else add non java resource
						if (nonEGLResources.length == nonEGLResourcesCounter) {
							// resize
							System.arraycopy(
								nonEGLResources,
								0,
								(nonEGLResources = new IResource[nonEGLResourcesCounter * 2]),
								0,
								nonEGLResourcesCounter);
						}
						nonEGLResources[nonEGLResourcesCounter++] = res;
				}
			}
			if (nonEGLResources.length != nonEGLResourcesCounter) {
				System.arraycopy(
					nonEGLResources,
					0,
					(nonEGLResources = new IResource[nonEGLResourcesCounter]),
					0,
					nonEGLResourcesCounter);
			}
		} catch (CoreException e) {
			nonEGLResources = NO_NON_EGL_RESOURCES;
			nonEGLResourcesCounter = 0;
		}
		return nonEGLResources;
	}
	
	/**
	 * @see IEGLProject
	 */
	protected NameLookup getNameLookup() {

		return fNameLookup;
	}
	
	/**
	 * Returns an array of non-java resources contained in the receiver.
	 */
	Object[] getNonEGLResources(EGLProject project) {

		Object[] nonEGLResources = fNonEGLResources;
		if (nonEGLResources == null) {
			nonEGLResources = computeNonEGLResources(project);
			fNonEGLResources = nonEGLResources;
		}
		return nonEGLResources;
	}
	
	/**
	 * @see IEGLProject 
	 */
//	protected SearchableEnvironment getSearchableEnvironment() {
//
//		return fSearchableEnvironment;
//	}
	/*
	 * Returns whether the given path is a classpath entry or an output location.
	 */
	private boolean isEGLPathEntryOrOutputLocation(IPath path, IEGLPathEntry[] resolvedClasspath, IPath projectOutput) {
		if (projectOutput.equals(path)) return true;
		for (int i = 0, length = resolvedClasspath.length; i < length; i++) {
			IEGLPathEntry entry = resolvedClasspath[i];
			if (entry.getPath().equals(path)) {
				return true;
			}
			IPath output;
			if ((output = entry.getOutputLocation()) != null && output.equals(path)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isEGLbinOutputLocation(IPath path){
		return path.lastSegment().equalsIgnoreCase("EGLBin");
	}
	protected void setNameLookup(NameLookup newNameLookup) {

		fNameLookup = newNameLookup;

		// Reinitialize the searchable name environment since it caches
		// the name lookup.
//		fSearchableEnvironment = null;
	}
	
	/**
	 * Set the fNonEGLResources to res value
	 */
	synchronized void setNonEGLResources(Object[] resources) {

		fNonEGLResources = resources;
	}
	
//	protected void setSearchableEnvironment(SearchableEnvironment newSearchableEnvironment) {
//
//		fSearchableEnvironment = newSearchableEnvironment;
//	}
}
