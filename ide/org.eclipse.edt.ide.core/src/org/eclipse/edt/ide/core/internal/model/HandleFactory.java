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
package org.eclipse.edt.ide.core.internal.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.edt.compiler.internal.eglar.FileInEglar;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IClassFile;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IEGLPathEntry;
import org.eclipse.edt.ide.core.model.IEGLProject;
import org.eclipse.edt.ide.core.model.IPackageFragment;
import org.eclipse.edt.ide.core.model.IPackageFragmentRoot;

/**
 * Creates egl element handles.
 */
public class HandleFactory {

	/**
	 * Cache package fragment root information to optimize speed performance.
	 */
	private String lastPkgFragmentRootPath;
	private IPackageFragmentRoot lastPkgFragmentRoot;

	/**
	 * Cache package handles to optimize memory.
	 */
	private Map packageHandles;

	private IWorkspace workspace;
	private EGLModel eglModel;


	public HandleFactory(IWorkspace workspace) {
		this.workspace = workspace;
		this.eglModel = EGLModelManager.getEGLModelManager().getEGLModel();
	}
	/**
	 * Creates an Openable handle from the given resource path.
	 * The resource path can be a path to a file in the workbench (eg. /Proj/com/ibm/jdt/core/HandleFactory.java)
	 * or a path to a file in a jar file - it then contains the path to the jar file and the path to the file in the jar
	 * (eg. c:/jdk1.2.2/jre/lib/rt.jar|java/lang/Object.class or /Proj/rt.jar|java/lang/Object.class)
	 * NOTE: This assumes that the resource path is the toString() of an IPath, 
	 *       in other words, it uses the IPath.SEPARATOR for file path
	 *            and it uses '/' for entries in a zip file.
	 * If not null, uses the given scope as a hint for getting EGL project handles.
	 */
	public Openable createOpenable(String resourcePath){//, IEGLSearchScope scope) {
		int separatorIndex;
		if ( resourcePath.startsWith( FileInEglar.EGLAR_PREFIX ) ) {
			resourcePath = resourcePath.substring( FileInEglar.EGLAR_PREFIX.length() );
		}
		if ((separatorIndex= resourcePath.indexOf(FileInEglar.EGLAR_SEPARATOR)) > -1) {
			// path to a class file inside a jar
			// Optimization: cache package fragment root handle and package handles
			int rootPathLength;
			if (this.lastPkgFragmentRootPath == null
					|| (rootPathLength = this.lastPkgFragmentRootPath.length()) != resourcePath.length()
					|| !resourcePath.regionMatches(0, this.lastPkgFragmentRootPath, 0, rootPathLength)) {
				String jarPath= resourcePath.substring(0, separatorIndex);
				PackageFragmentRoot root= getJarPkgFragmentRoot(resourcePath, separatorIndex, jarPath);
				if (root == null)
					return null; // match is outside classpath
				this.lastPkgFragmentRootPath = jarPath;
				this.lastPkgFragmentRoot = root;
				this.packageHandles = new HashMap(5);
			}
			// create handle
			String classFilePath = resourcePath.substring(separatorIndex + 1);
			String[] simpleNames = new Path(classFilePath).segments();
			String[] pkgName;
			int length = simpleNames.length-1;
			if (length > 0) {
				pkgName = new String[length];
				System.arraycopy(simpleNames, 0, pkgName, 0, length);
			} else {
				pkgName = new String[0];
			}
			IPackageFragment pkgFragment= (IPackageFragment) this.packageHandles.get(pkgName);
			if (pkgFragment == null) {
				pkgFragment= ((PackageFragmentRoot)this.lastPkgFragmentRoot).getPackageFragment(pkgName);
				this.packageHandles.put(pkgName, pkgFragment);
			}
			IClassFile classFile= pkgFragment.getClassFile(simpleNames[length]);
			return (Openable) classFile;
		} else {
			// path to a file in a directory
			// Optimization: cache package fragment root handle and package handles
			int length = -1;
			if (this.lastPkgFragmentRootPath == null 
				|| !(resourcePath.startsWith(this.lastPkgFragmentRootPath) 
					&& (length = this.lastPkgFragmentRootPath.length()) > 0
					&& resourcePath.charAt(length) == '/')) {
				IPackageFragmentRoot root= this.getPkgFragmentRoot(resourcePath);
				if (root == null)
					return null; // match is outside eglpath
				this.lastPkgFragmentRoot= root;
				this.lastPkgFragmentRootPath= this.lastPkgFragmentRoot.getPath().toString();
				this.packageHandles= new HashMap(5);
			}
			// create handle
			int lastSlash= resourcePath.lastIndexOf(IPath.SEPARATOR);
			String packageName= lastSlash > (length= this.lastPkgFragmentRootPath.length()) ? resourcePath.substring(length + 1, lastSlash).replace(IPath.SEPARATOR, '.') : IPackageFragment.DEFAULT_PACKAGE_NAME;
			IPackageFragment pkgFragment= (IPackageFragment) this.packageHandles.get(packageName);
			if (pkgFragment == null) {
				pkgFragment= this.lastPkgFragmentRoot.getPackageFragment(packageName);
				this.packageHandles.put(packageName, pkgFragment);
			}
			String simpleName= resourcePath.substring(lastSlash + 1);
			IEGLFile unit= pkgFragment.getEGLFile(simpleName);
			return (Openable) unit;
		}

		/* TODO handle multiple types later
		if (Util.isEGLFileName(simpleName)) {
			ICompilationUnit unit= pkgFragment.getCompilationUnit(simpleName);
			return (Openable) unit;
		} else {
			IClassFile classFile= pkgFragment.getClassFile(simpleName);
			return (Openable) classFile;
		}
		*/
	}
	/**
	 * Returns the package fragment root that contains the given resource path.
	 */
	private IPackageFragmentRoot getPkgFragmentRoot(String pathString) {

		IPath path= new Path(pathString);
		IProject[] projects= this.workspace.getRoot().getProjects();
		for (int i= 0, max= projects.length; i < max; i++) {
			try {
				IProject project = projects[i];
				if (!project.isAccessible() 
					|| !project.hasNature(EGLCore.NATURE_ID)) continue;
				IEGLProject eglProject= this.eglModel.getEGLProject(project);
				IPackageFragmentRoot[] roots= eglProject.getPackageFragmentRoots();
				for (int j= 0, rootCount= roots.length; j < rootCount; j++) {
					PackageFragmentRoot root= (PackageFragmentRoot)roots[j];
					if (root.getPath().isPrefixOf(path) && !Util.isExcluded(path, root.fullExclusionPatternChars())) {
						return root;
					}
				}
			} catch (CoreException e) {
				// CoreException from hasNature - should not happen since we check that the project is accessible
				// EGLModelException from getPackageFragmentRoots - a problem occured while accessing project: nothing we can do, ignore
			}
		}
		return null;
	}
	
	/**
	 * Returns the package fragment root that corresponds to the given jar path.
	 * See createOpenable(...) for the format of the jar path string.
	 * If not null, uses the given scope as a hint for getting Java project handles.
	 */
	private PackageFragmentRoot getJarPkgFragmentRoot(String resourcePathString, int jarSeparatorIndex, String jarPathString) { //TODO, IJavaSearchScope scope) {

		IPath jarPath= new Path(jarPathString);

		Object target = EGLModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), jarPath, false);
		if (target instanceof IFile) {
			// internal jar: is it on the classpath of its project?
			//  e.g. org.eclipse.swt.win32/ws/win32/swt.jar
			//        is NOT on the classpath of org.eclipse.swt.win32
			IFile jarFile = (IFile)target;
			EGLProject javaProject = (EGLProject) this.eglModel.getEGLProject(jarFile);
			try {
				IEGLPathEntry entry = javaProject.getEGLPathEntryFor(jarPath);
				if (entry != null) {
					return (PackageFragmentRoot) javaProject.getPackageFragmentRoot(jarFile);
				}
			} catch (EGLModelException e) {
				// ignore and try to find another project
			}
		}

		// walk projects in the scope and find the first one that has the given jar path in its classpath
		IEGLProject[] projects;
//		if (scope != null) {
//			if (scope instanceof Object) { //TODO AbstractEGLSearchScope
////				PackageFragmentRoot root = (PackageFragmentRoot) ((AbstractJavaSearchScope) scope).packageFragmentRoot(resourcePathString, jarSeparatorIndex, jarPathString);
////				if (root != null)
////					return root;
//			} else {
//				IPath[] enclosingProjectsAndJars = scope.enclosingProjectsAndJars();
//				int length = enclosingProjectsAndJars.length;
//				projects = new IEGLProject[length];
//				int index = 0;
//				for (int i = 0; i < length; i++) {
//					IPath path = enclosingProjectsAndJars[i];
//					if (path.segmentCount() == 1) {
//						projects[index++] = this.eglModel.getEGLProject(path.segment(0));
//					}
//				}
//				if (index < length) {
//					System.arraycopy(projects, 0, projects = new IEGLProject[index], 0, index);
//				}
//				PackageFragmentRoot root = getJarPkgFragmentRoot(jarPath, target, projects);
//				if (root != null) {
//					return root;
//				}
//			}
//		}

		// not found in the scope, walk all projects
		try {
			projects = this.eglModel.getEGLProjects();
		} catch (EGLModelException e) {
			// java model is not accessible
			return null;
		}
		return getJarPkgFragmentRoot(jarPath, target, projects);
	}
	
	private PackageFragmentRoot getJarPkgFragmentRoot(IPath jarPath, Object target, IEGLProject[] projects) {
		for (int i = 0, projectCount = projects.length; i < projectCount; i++) {
			try {
				EGLProject javaProject = (EGLProject) projects[i];
				IEGLPathEntry classpathEnty = javaProject.getEGLPathEntryFor(jarPath);
				if (classpathEnty != null) {
					if (target instanceof IFile) {
						// internal jar
						return (PackageFragmentRoot) javaProject.getPackageFragmentRoot((IFile) target);
					} else {
						// external jar
						return (PackageFragmentRoot) javaProject.getPackageFragmentRoot0(jarPath);
					}
				}
			} catch (EGLModelException e) {
				// EGLModelException from getResolvedClasspath - a problem
				// occurred while accessing project: nothing we can do, ignore
			}
		}
		return null;
	}
}
