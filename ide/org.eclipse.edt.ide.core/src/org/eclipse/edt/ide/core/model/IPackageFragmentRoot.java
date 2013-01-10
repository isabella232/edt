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
package org.eclipse.edt.ide.core.model;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A package fragment root contains a set of package fragments.
 * It corresponds to an underlying resource which is either a folder,
 * JAR, or zip.  In the case of a folder, all descendant folders represent
 * package fragments.  For a given child folder representing a package fragment, 
 * the corresponding package name is composed of the folder names between the folder 
 * for this root and the child folder representing the package, separated by '.'.
 * In the case of a JAR or zip, the contents of the archive dictates 
 * the set of package fragments in an analogous manner.
 * Package fragment roots need to be opened before they can be navigated or manipulated.
 * The children are of type <code>IPackageFragment</code>, and are in no particular order.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IPackageFragmentRoot
	extends IParent, IEGLElement, IOpenable {
		/**
		 * Kind constant for a source path root. Indicates this root
		 * only contains source files.
		 */
		int K_SOURCE = 1;
		/**
		 * Kind constant for a binary path root. Indicates this
		 * root only contains binary files.
		 */
		int K_BINARY = 2;
	/**
	 * Empty root path
	 */
	String DEFAULT_PACKAGEROOT_PATH = ""; //$NON-NLS-1$
	/**
	 * Update model flag constant (bit mask value 1) indicating that the operation
	 * is to not copy/move/delete the package fragment root resource.
	 * @since 2.1
	 */
	int NO_RESOURCE_MODIFICATION = 1;
	/**
	 * Update model flag constant (bit mask value 2) indicating that the operation
	 * is to update the eglpath of the originating project.
	 * @since 2.1
	 */
	int ORIGINATING_PROJECT_EGLPATH = 2;
	/**
	 * Update model flag constant (bit mask value 4) indicating that the operation
	 * is to update the eglpath of all referring projects except the originating project.
	 * @since 2.1
	 */
	int OTHER_REFERRING_PROJECTS_EGLPATH = 4;
	/**
	 * Update model flag constant (bit mask value 8) indicating that the operation
	 * is to update the eglpath of the destination project.
	 * @since 2.1
	 */
	int DESTINATION_PROJECT_EGLPATH = 8;	
	/**
	 * Update model flag constant (bit mask value 16) indicating that the operation
	 * is to replace the resource and the destination project's eglpath entry.
	 * @since 2.1
	 */
	int REPLACE = 16;	

	/**
	 * Copies the resource of this package fragment root to the destination path
	 * as specified by <code>IResource.copy(IPath, int, IProgressMonitor)</code>
	 * but excluding nested source folders.
	 * <p>
	 * If <code>NO_RESOURCE_MODIFICATION</code> is specified in 
	 * <code>updateModelFlags</code> or if this package fragment root is external, 
	 * this operation doesn't copy the resource. <code>updateResourceFlags</code> 
	 * is then ignored.
	 * </p><p>
	 * If <code>DESTINATION_PROJECT_EGLPATH</code> is specified in 
	 * <code>updateModelFlags</code>, updates the eglpath of the 
	 * destination's project (if it is a EGL project). If a non-<code>null</code> 
	 * sibling is specified, a copy of this root's eglpath entry is inserted before the 
	 * sibling on the destination project's raw eglpath. If <code>null</code> is 
	 * specified, the eglpath entry is added at the end of the raw eglpath.
	 * </p><p>
	 * If <code>REPLACE</code> is specified in <code>updateModelFlags</code>,
	 * overwrites the resource at the destination path if any.
	 * If the same eglpath entry already exists on the destination project's raw
	 * eglpath, then the sibling is ignored and the new eglpath entry replaces the 
	 * existing one.
	 * </p><p>
	 * If no flags is specified in <code>updateModelFlags</code> (using 
	 * <code>IResource.NONE</code>), the default behavior applies: the
	 * resource is copied (if this package fragment root is not external) and the
	 * eglpath is not updated.
	 * </p>
	 * 
	 * @param destination the destination path
	 * @param updateResourceFlags bit-wise or of update resource flag constants
	 *   (<code>IResource.FORCE</code> and <code>IResource.SHALLOW</code>)
	 * @param updateModelFlags bit-wise or of update resource flag constants
	 *   (<code>DESTINATION_PROJECT_eglpath</code> and 
	 *   <code>NO_RESOURCE_MODIFICATION</code>)
	 * @param sibling the eglpath entry before which a copy of the eglpath
	 * entry should be inserted or <code>null</code> if the eglpath entry should
	 * be inserted at the end
	 * @param monitor a progress monitor
	 * 
	 * @exception EGLModelException if this root could not be copied. Reasons
	 * include:
	 * <ul>
	 * <li> This root does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> A <code>CoreException</code> occurred while copying the
	 * resource or updating a eglpath</li>
	 * <li>
	 * The destination is not inside an existing project and <code>updateModelFlags</code>
	 * has been specified as <code>DESTINATION_PROJECT_eglpath</code> 
	 * (INVALID_DESTINATION)</li>
	 * <li> The sibling is not a eglpath entry on the destination project's
	 * raw eglpath (INVALID_SIBLING)</li>
	 * <li> The same eglpath entry already exists on the destination project's
	 * eglpath (NAME_COLLISION) and <code>updateModelFlags</code>
	 * has not been specified as <code>REPLACE</code></li>
	 * </ul>
	 * @see org.eclipse.core.resources.IResource#copy
	 * @since 2.1
	 */
	void copy(IPath destination, int updateResourceFlags, int updateModelFlags, IEGLPathEntry sibling, IProgressMonitor monitor) throws EGLModelException;
	/**
	 * Creates and returns a package fragment in this root with the 
	 * given dot-separated package name.  An empty string specifies the default package. 
	 * This has the side effect of creating all package
	 * fragments that are a prefix of the new package fragment which
	 * do not exist yet. If the package fragment already exists, this
	 * has no effect.
	 *
	 * For a description of the <code>force</code> flag, see <code>IFolder.create</code>.
	 *
	 * @param name the given dot-separated package name
	 * @param force a flag controlling how to deal with resources that
	 *    are not in sync with the local file system
	 * @param monitor the given progress monitor
	 * @exception EGLModelException if the element could not be created. Reasons include:
	 * <ul>
	 * <li> This EGL element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> A <code>CoreException</code> occurred while creating an underlying resource
	 * <li> This package fragment root is read only (READ_ONLY)
	 * <li> The name is not a valid package name (INVALID_NAME)
	 * </ul>
	 * @return a package fragment in this root with the given dot-separated package name
	 * @see org.eclipse.core.resources.IFolder#create
	 */
	IPackageFragment createPackageFragment(
		String name,
		boolean force,
		IProgressMonitor monitor)
		throws EGLModelException;
	/**
	 * Deletes the resource of this package fragment root as specified by
	 * <code>IResource.delete(int, IProgressMonitor)</code> but excluding nested
	 * source folders.
	 * <p>
	 * If <code>NO_RESOURCE_MODIFICATION</code> is specified in 
	 * <code>updateModelFlags</code> or if this package fragment root is external, 
	 * this operation doesn't delete the resource. <code>updateResourceFlags</code> 
	 * is then ignored.
	 * </p><p>
	 * If <code>ORIGINATING_PROJECT_eglpath</code> is specified in 
	 * <code>updateModelFlags</code>, update the raw eglpath of this package 
	 * fragment root's project by removing the corresponding eglpath entry.
	 * </p><p>
	 * If <code>OTHER_REFERRING_PROJECTS_eglpath</code> is specified in 
	 * <code>updateModelFlags</code>, update the raw eglpaths of all other EGL
	 * projects referring to this root's resource by removing the corresponding eglpath 
	 * entries.
	 * </p><p>
	 * If no flags is specified in <code>updateModelFlags</code> (using 
	 * <code>IResource.NONE</code>), the default behavior applies: the
	 * resource is deleted (if this package fragment root is not external) and no
	 * eglpaths are updated.
	 * </p>
	 * 
	 * @param updateResourceFlags bit-wise or of update resource flag constants
	 *   (<code>IResource.FORCE</code> and <code>IResource.KEEP_HISTORY</code>)
	 * @param updateModelFlags bit-wise or of update resource flag constants
	 *   (<code>ORIGINATING_PROJECT_eglpath</code>,
	 *   <code>OTHER_REFERRING_PROJECTS_eglpath</code> and 
	 *   <code>NO_RESOURCE_MODIFICATION</code>)
	 * @param monitor a progress monitor
	 * 
	 * @exception EGLModelException if this root could not be deleted. Reasons
	 * include:
	 * <ul>
	 * <li> This root does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> A <code>CoreException</code> occurred while deleting the resource 
	 * or updating a eglpath
	 * </li>
	 * </ul>
	 * @see org.eclipse.core.resources.IResource#delete
	 * @since 2.1
	 */
	void delete(int updateResourceFlags, int updateModelFlags, IProgressMonitor monitor) throws EGLModelException;
	/**
	 * Returns this package fragment root's kind encoded as an integer.
	 * A package fragment root can contain <code>.EGL</code> source files,
	 * or <code>.class</code> files, but not both.
	 * If the underlying folder or archive contains other kinds of files, they are ignored.
	 * In particular, <code>.class</code> files are ignored under a source package fragment root,
	 * and <code>.EGL</code> files are ignored under a binary package fragment root.
	 *
	 * @see IPackageFragmentRoot#K_SOURCE
	 * @see IPackageFragmentRoot#K_BINARY
	 *
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return this package fragment root's kind encoded as an integer
	 */
	int getKind() throws EGLModelException;
	
	/**
	 * Returns an array of non-EGL resources contained in this package fragment root.
	 * <p>
	 * Non-EGL resources includes other files and folders located in the same
	 * directories as the compilation units or class files under this package
	 * fragment root. Resources excluded from this package fragment root
	 * by one or more exclusion patterns on the corresponding source eglpath
	 * entry are considered non-EGL resources and will appear in the result
	 * (possibly in a folder). Thus when a nested source folder is excluded, it will appear
	 * in the non-EGL resources of the outer folder.
	 * </p>
	 * @return an array of non-EGL resources contained in this package fragment root
	 * @see IeglpathEntry#getExclusionPatterns
	 */
	Object[] getNonEGLResources() throws EGLModelException;
	
	/**
	 * Returns the package fragment with the given package name.
	 * An empty string indicates the default package.
	 * This is a handle-only operation.  The package fragment
	 * may or may not exist.
	 * 
	 * @param packageName the given package name
	 * @return the package fragment with the given package name
	 */
	IPackageFragment getPackageFragment(String packageName);
	

	/**
	 * Returns the first raw eglpath entry that corresponds to this package
	 * fragment root.
	 * A raw eglpath entry corresponds to a package fragment root if once resolved
	 * this entry's path is equal to the root's path. 
	 * 
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return the first raw eglpath entry that corresponds to this package fragment root
	 * @since 2.0
	 */
	IEGLPathEntry getRawEGLPathEntry() throws EGLModelException;
	
	/**
	 * Returns whether this package fragment root's underlying
	 * resource is a binary archive (a JAR or zip file).
	 * 
	 * @return true if this package fragment root's underlying resource is a binary archive, false otherwise
	 */
	public boolean isArchive();
	
	/**
	 * Returns whether this package fragment root is external
	 * to the workbench (that is, a local file), and has no
	 * underlying resource.
	 * 
	 * @return true if this package fragment root is external
	 * to the workbench (that is, a local file), and has no
	 * underlying resource, false otherwise
	 */
	boolean isExternal();
	/**
	 * Moves the resource of this package fragment root to the destination path
	 * as specified by <code>IResource.move(IPath,int,IProgressMonitor)</code>
	 * but excluding nested source folders.
	 * <p>
	 * If <code>NO_RESOURCE_MODIFICATION</code> is specified in 
	 * <code>updateModelFlags</code> or if this package fragment root is external, 
	 * this operation doesn't move the resource. <code>updateResourceFlags</code> 
	 * is then ignored.
	 * </p><p>
	 * If <code>DESTINATION_PROJECT_eglpath</code> is specified in 
	 * <code>updateModelFlags</code>, updates the eglpath of the 
	 * destination's project (if it is a EGL project). If a non-<code>null</code> 
	 * sibling is specified, a copy of this root's eglpath entry is inserted before the 
	 * sibling on the destination project's raw eglpath. If <code>null</code> is 
	 * specified, the eglpath entry is added at the end of the raw eglpath.
	 * </p><p>
	 * If <code>ORIGINATING_PROJECT_eglpath</code> is specified in 
	 * <code>updateModelFlags</code>, update the raw eglpath of this package 
	 * fragment root's project by removing the corresponding eglpath entry.
	 * </p><p>
	 * If <code>OTHER_REFERRING_PROJECTS_eglpath</code> is specified in 
	 * <code>updateModelFlags</code>, update the raw eglpaths of all other EGL
	 * projects referring to this root's resource by removing the corresponding eglpath 
	 * entries.
	 * </p><p>
	 * If <code>REPLACE</code> is specified in <code>updateModelFlags</code>,
	 * overwrites the resource at the destination path if any.
	 * If the same eglpath entry already exists on the destination project's raw
	 * eglpath, then the sibling is ignored and the new eglpath entry replaces the 
	 * existing one.
	 * </p><p>
	 * If no flags is specified in <code>updateModelFlags</code> (using 
	 * <code>IResource.NONE</code>), the default behavior applies: the
	 * resource is moved (if this package fragment root is not external) and no
	 * eglpaths are updated.
	 * </p>
	 * 
	 * @param destination the destination path
	 * @param updateFlags bit-wise or of update flag constants
	 * (<code>IResource.FORCE</code>, <code>IResource.KEEP_HISTORY</code> 
	 * and <code>IResource.SHALLOW</code>)
	 * @param updateModelFlags bit-wise or of update resource flag constants
	 *   (<code>DESTINATION_PROJECT_eglpath</code>,
	 *   <code>ORIGINATING_PROJECT_eglpath</code>,
	 *   <code>OTHER_REFERRING_PROJECTS_eglpath</code> and 
	 *   <code>NO_RESOURCE_MODIFICATION</code>)
	 * @param sibling the eglpath entry before which a copy of the eglpath
	 * entry should be inserted or <code>null</code> if the eglpath entry should
	 * be inserted at the end
	 * @param monitor a progress monitor
	 * 
	 * @exception EGLModelException if this root could not be moved. Reasons
	 * include:
	 * <ul>
	 * <li> This root does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> A <code>CoreException</code> occurred while copying the
	 * resource or updating a eglpath</li>
	 * <li>
	 * The destination is not inside an existing project and <code>updateModelFlags</code>
	 * has been specified as <code>DESTINATION_PROJECT_eglpath</code> 
	 * (INVALID_DESTINATION)</li>
	 * <li> The sibling is not a eglpath entry on the destination project's
	 * raw eglpath (INVALID_SIBLING)</li>
	 * <li> The same eglpath entry already exists on the destination project's
	 * eglpath (NAME_COLLISION) and <code>updateModelFlags</code>
	 * has not been specified as <code>REPLACE</code></li>
	 * </ul>
	 * @see org.eclipse.core.resources.IResource#move
	 * @since 2.1
	 */
	void move(IPath destination, int updateResourceFlags, int updateModelFlags, IEGLPathEntry sibling, IProgressMonitor monitor) throws EGLModelException;
}
