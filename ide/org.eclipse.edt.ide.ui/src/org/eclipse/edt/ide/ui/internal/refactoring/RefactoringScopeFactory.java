/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.refactoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;

public class RefactoringScopeFactory {

	/*
	 * Adds to <code> projects </code> IEGLProject objects for all projects directly or indirectly referencing focus. @param projects IEGLProjects will be added to this set
	 */
	private static void addReferencingProjects(IEGLProject focus, Set projects) throws EGLModelException {
		IProject[] referencingProjects= focus.getProject().getReferencingProjects();
		for (int i= 0; i < referencingProjects.length; i++) {
			IEGLProject candidate= EGLCore.create(referencingProjects[i]);
			if (candidate == null || projects.contains(candidate) || !candidate.exists())
				continue; // break cycle
			IEGLPathEntry entry= getReferencingClassPathEntry(candidate, focus);
			if (entry != null) {
				projects.add(candidate);
				if (entry.isExported())
					addReferencingProjects(candidate, projects);
			}
		}
	}

	private static void addRelatedReferencing(IEGLProject focus, Set projects) throws CoreException {
		IProject[] referencingProjects= focus.getProject().getReferencingProjects();
		for (int i= 0; i < referencingProjects.length; i++) {
			IEGLProject candidate= EGLCore.create(referencingProjects[i]);
			if (candidate == null || projects.contains(candidate) || !candidate.exists())
				continue; // break cycle
			IEGLPathEntry entry= getReferencingClassPathEntry(candidate, focus);
			if (entry != null) {
				projects.add(candidate);
				if (entry.isExported()) {
					addRelatedReferencing(candidate, projects);
					addRelatedReferenced(candidate, projects);
				}
			}
		}
	}

	private static void addRelatedReferenced(IEGLProject focus, Set projects) throws CoreException {
		IProject[] referencedProjects= focus.getProject().getReferencedProjects();
		for (int i= 0; i < referencedProjects.length; i++) {
			IEGLProject candidate= EGLCore.create(referencedProjects[i]);
			if (candidate == null || projects.contains(candidate) || !candidate.exists())
				continue; // break cycle
			IEGLPathEntry entry= getReferencingClassPathEntry(focus, candidate);
			if (entry != null) {
				projects.add(candidate);
				if (entry.isExported()) {
					addRelatedReferenced(candidate, projects);
					addRelatedReferencing(candidate, projects);
				}
			}
		}
	}

	/**
	 * Creates a new search scope with all compilation units possibly referencing <code>eglElement</code>,
	 * considering the visibility of the element.
	 * 
	 * @param eglElement the java element
	 * @return the search scope
	 * @throws EGLModelException if an error occurs
	 */
	public static IEGLSearchScope create(IEGLElement eglElement) throws EGLModelException {
		return RefactoringScopeFactory.create(eglElement, true);
	}
	
	/**
	 * Creates a new search scope with all compilation units possibly referencing <code>eglElement</code>.
	 * 
	 * @param eglElement the java element
	 * @param considerVisibility consider visibility of eglElement iff <code>true</code>
	 * @return the search scope
	 * @throws EGLModelException if an error occurs
	 */
	public static IEGLSearchScope create(IEGLElement eglElement, boolean considerVisibility) throws EGLModelException {
		return create(eglElement.getEGLProject());
	}

	private static IEGLSearchScope create(IEGLProject javaProject) throws EGLModelException {
		return SearchEngine.createEGLSearchScope(getAllScopeElements(javaProject), false);
	}

	/**
	 * Creates a new search scope with all projects possibly referenced
	 * from the given <code>eglElements</code>.
	 * 
	 * @param eglElements the java elements
	 * @return the search scope
	 */
	public static IEGLSearchScope createReferencedScope(IEGLElement[] eglElements) {
		Set projects= new HashSet();
		for (int i= 0; i < eglElements.length; i++) {
			projects.add(eglElements[i].getEGLProject());
		}
		IEGLProject[] prj= (IEGLProject[]) projects.toArray(new IEGLProject[projects.size()]);
		return SearchEngine.createEGLSearchScope(prj, true);
	}

	/**
	 * Creates a new search scope with all projects possibly referenced
	 * from the given <code>eglElements</code>.
	 * 
	 * @param eglElements the java elements
	 * @param includeMask the include mask
	 * @return the search scope
	 */
	public static IEGLSearchScope createReferencedScope(IEGLElement[] eglElements, boolean includeReferencedFunctions) {
		Set projects= new HashSet();
		for (int i= 0; i < eglElements.length; i++) {
			projects.add(eglElements[i].getEGLProject());
		}
		IEGLProject[] prj= (IEGLProject[]) projects.toArray(new IEGLProject[projects.size()]);
		return SearchEngine.createEGLSearchScope(prj, includeReferencedFunctions);
	}

	/**
	 * Creates a new search scope containing all projects which reference or are referenced by the specified project.
	 * 
	 * @param project the project
	 * @param includeMask the include mask
	 * @return the search scope
	 * @throws CoreException if a referenced project could not be determined
	 */
	public static IEGLSearchScope createRelatedProjectsScope(IEGLProject project, boolean includeReferencedFunctions) throws CoreException {
		IEGLProject[] projects= getRelatedProjects(project);
		return SearchEngine.createEGLSearchScope(projects, includeReferencedFunctions);
	}

	private static IEGLElement[] getAllScopeElements(IEGLProject project) throws EGLModelException {
		Collection sourceRoots= getAllSourceRootsInProjects(getReferencingProjects(project));
		return (IPackageFragmentRoot[]) sourceRoots.toArray(new IPackageFragmentRoot[sourceRoots.size()]);
	}

	/*
	 * @param projects a collection of IEGLProject @return Collection a collection of IPackageFragmentRoot, one element for each packageFragmentRoot which lies within a project in <code> projects </code> .
	 */
	private static Collection getAllSourceRootsInProjects(Collection projects) throws EGLModelException {
		List result= new ArrayList();
		for (Iterator it= projects.iterator(); it.hasNext();)
			result.addAll(getSourceRoots((IEGLProject) it.next()));
		return result;
	}

	/*
	 * Finds, if possible, a classpathEntry in one given project such that this classpath entry references another given project. If more than one entry exists for the referenced project and at least one is exported, then an exported entry will be returned.
	 */
	private static IEGLPathEntry getReferencingClassPathEntry(IEGLProject referencingProject, IEGLProject referencedProject) throws EGLModelException {
		IEGLPathEntry result= null;
		IPath path= referencedProject.getProject().getFullPath();
		IEGLPathEntry[] classpath= referencingProject.getResolvedEGLPath(true);
		for (int i= 0; i < classpath.length; i++) {
			IEGLPathEntry entry= classpath[i];
			if (entry.getEntryKind() == IEGLPathEntry.CPE_PROJECT && path.equals(entry.getPath())) {
				if (entry.isExported())
					return entry;
				// Consider it as a candidate. May be there is another entry that is
				// exported.
				result= entry;
			}
		}
		return result;
	}

	private static IEGLProject[] getRelatedProjects(IEGLProject focus) throws CoreException {
		final Set projects= new HashSet();

		addRelatedReferencing(focus, projects);
		addRelatedReferenced(focus, projects);

		projects.add(focus);
		return (IEGLProject[]) projects.toArray(new IEGLProject[projects.size()]);
	}

	private static Collection getReferencingProjects(IEGLProject focus) throws EGLModelException {
		Set projects= new HashSet();

		addReferencingProjects(focus, projects);
		projects.add(focus);
		return projects;
	}

	private static List getSourceRoots(IEGLProject javaProject) throws EGLModelException {
		List elements= new ArrayList();
		IPackageFragmentRoot[] roots= javaProject.getPackageFragmentRoots();
		// Add all package fragment roots except archives
		for (int i= 0; i < roots.length; i++) {
			IPackageFragmentRoot root= roots[i];
			if (!root.isArchive())
				elements.add(root);
		}
		return elements;
	}

	private RefactoringScopeFactory() {
		// no instances
	}
}
