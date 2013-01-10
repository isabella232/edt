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

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A package fragment is a portion of the workspace corresponding to an entire package,
 * or to a portion thereof. The distinction between a package fragment and a package
 * is that a package with some name is the union of all package fragments in the EGL path
 * which have the same name.
 * <p>
 * Package fragments elements need to be opened before they can be navigated or manipulated.
 * The children are of type <code>IEGLFile</code> (representing a source file).
 * The children are listed in no particular order.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IPackageFragment extends IParent, IEGLElement, IOpenable, ISourceManipulation {

	/**	
	 * <p>
	 * The name of package fragment for the default package (value: the empty 
	 * string, <code>""</code>).
	 * </p>
 	*/
	public static final String DEFAULT_PACKAGE_NAME = ""; //$NON-NLS-1$
	/**
	 * Returns whether this fragment contains at least one EGL resource.
	 * @return true if this fragment contains at least one EGL resource, false otherwise
	 */
	boolean containsEGLResources() throws EGLModelException;
	/**
	 * Creates and returns a EGL File in this package fragment 
	 * with the specified name and contents. No verification is performed
	 * on the contents.
	 *
	 * <p>It is possible that a compilation unit with the same name already exists in this 
	 * package fragment.
	 * The value of the <code>force</code> parameter effects the resolution of
	 * such a conflict:<ul>
	 * <li> <code>true</code> - in this case the compilation is created with the new contents</li>
	 * <li> <code>false</code> - in this case a <code>EGLModelException</code> is thrown</li>
	 * </ul>
	 *
	 * @param contents the given contents
	 * @param force specify how to handle conflict is the same name already exists
	 * @param monitor the given progress monitor
	 * @param name the given name
	 * @exception EGLModelException if the element could not be created. Reasons include:
	 * <ul>
	 * <li> This EGL element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
	 * <li> A <code>CoreException</code> occurred while creating an underlying resource
	 * <li> The name is not a valid compilation unit name (INVALID_NAME)
	 * <li> The contents are <code>null</code> (INVALID_CONTENTS)
	 * </ul>
	 * @return a compilation unit in this package fragment 
	 * with the specified name and contents
	 */
	IEGLFile createEGLFile(String name, String contents, boolean force, IProgressMonitor monitor) throws EGLModelException;
	/**
	 * Returns the class file with the specified name
	 * in this package (for example, <code>"Object.ir"</code>).
	 * The ".ir" suffix is required.
	 * This is a handle-only method.  The class file may or may not be present.
	 * @param name the given name
	 * @return the class file with the specified name in this package
	 */
	IClassFile getClassFile(String name);
	/**
	 * Returns all of the ir files in this package fragment.
	 *
	 * <p>Note: it is possible that a package fragment contains only
	 * compilation units (in other words, its kind is <code>K_SOURCE</code>), in
	 * which case this method returns an empty collection.
	 *
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return all of the class files in this package fragment
	 */
	IClassFile[] getClassFiles() throws EGLModelException;	
	/**
	 * Returns the EGL file with the specified name
	 * in this package (for example, <code>"CommonRecords.egl"</code>).
	 * The ".egl" suffix is required.
	 * This is a handle-only method.  The EGL file may or may not be present.
	 * @param name the given name
	 * @return the EGL file with the specified name in this package
	 */
	IEGLFile getEGLFile(String name);
	/**
	 * Returns all of the EGL files in this package fragment.
	 *
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return all of the EGL files in this package fragment
	 */
	IEGLFile[] getEGLFiles() throws EGLModelException;
	/**
	 * Returns this package fragment's root kind encoded as an integer.
	 * A package fragment can contain <code>.egl</code> source files,
	 * or <code>.zip</code> files. This is a convenience method.
	 *
	 * @see IPackageFragmentRoot#K_SOURCE
	 * @see IPackageFragmentRoot#K_BINARY
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return this package fragment's root kind encoded as an integer
	 */
	int getKind() throws EGLModelException;
	/**
	 * Returns all IParts contained in this package fragment.
	 */
	IPart getPart(String name);
	IPart[] getParts() throws EGLModelException;
	/**
	 * Returns the dot-separated package name of this fragment, for example
	 * <code>"EGL.lang"</code>, or <code>""</code> (the empty string),
	 * for the default package.
	 * 
	 * @return the dot-separated package name of this fragment
	 */
	String getElementName();
	/**
	 * Returns an array of non-EGL resources contained in this package fragment.
	 * <p>
	 * Non-EGL resources includes other files and folders located in the same
	 * directory as the compilation units or class files for this package 
	 * fragment. Source files excluded from this package by one or more
	 * exclusion patterns on the corresponding source classpath entry are
	 * considered non-EGL resources and will appear in the result
	 * (possibly in a folder).
	 * </p>
	 * 
	 * @return an array of non-EGL resources contained in this package fragment
	 * @see IClasspathEntry#getExclusionPatterns
	 */
	Object[] getNonEGLResources() throws EGLModelException;
	/**
	 * Returns whether this package fragment's name is
	 * a prefix of other package fragments in this package fragment's
	 * root.
	 *
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource.
	 * @return true if this package fragment's name is a prefix of other package fragments in this package fragment's root, false otherwise
	 */
	boolean hasSubpackages() throws EGLModelException;
	/**
	 * Returns whether this package fragment is a default package.
	 * This is a handle-only method.
	 * 
	 * @return true if this package fragment is a default package
	 */
	boolean isDefaultPackage();
}
