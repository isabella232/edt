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
package org.eclipse.edt.ide.core.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

/**
 * Common protocol for all elements provided by the EGL model.
 * EGL model elements are exposed to clients as handles to the actual underlying element.
 * The EGL model may hand out any number of handles for each element. Handles
 * that refer to the same element are guaranteed to be equal, but not necessarily identical.
 * <p>
 * Methods annotated as "handle-only" do not require underlying elements to exist. 
 * Methods that require underlying elements to exist throw
 * a <code>EGLModelException</code> when an underlying element is missing.
 * <code>EGLModelException.isDoesNotExist</code> can be used to recognize
 * this common special case.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IEGLElement extends IAdaptable {

	/**
	 * Constant representing a EGL model (workspace level object).
	 * A EGL element with this type can be safely cast to <code>IEGLModel</code>.
	 */
	int EGL_MODEL = 1;

	/**
	 * Constant representing a EGL project.
	 * A EGL element with this type can be safely cast to <code>IEGLProject</code>.
	 */
	int EGL_PROJECT = 2;

	/**
	 * Constant representing a package fragment root.
	 * A EGL element with this type can be safely cast to <code>IPackageFragmentRoot</code>.
	 */
	int PACKAGE_FRAGMENT_ROOT = 3;

	/**
	 * Constant representing a package fragment.
	 * A EGL element with this type can be safely cast to <code>IPackageFragment</code>.
	 */
	int PACKAGE_FRAGMENT = 4;

	/**
	 * Constant representing a egl file.
	 * A EGL element with this type can be safely cast to <code>IEGLFile</code>.
	 */
	int EGL_FILE = 6;

	/**
	 * Constant representing a class file created from an egl file.
	 * A EGL element with this type can be safely cast to <code>IEGLFile</code>.
	 */
	int CLASS_FILE = 7;
	/**
	 * Constant representing a part (Program, Record, etc).
	 * A EGL element with this type can be safely cast to <code>IPart</code>.
	 */
	int PART = 8;

	/**
	 * Constant representing a field.
	 * A EGL element with this type can be safely cast to <code>IField</code>.
	 */
	int FIELD = 9;

	/**
	 * Constant representing a method or constructor.
	 * A EGL element with this type can be safely cast to <code>IFunction</code>.
	 */
	int FUNCTION = 10;

	/**
	 * Constant representing a stand-alone instance or class initializer.
	 * A EGL element with this type can be safely cast to <code>IInitializer</code>.
	 */
	int INITIALIZER = 11;

	/**
	 * Constant representing a package declaration within a EGL file.
	 * A EGL element with this type can be safely cast to <code>IPackageDeclaration</code>.
	 */
	int PACKAGE_DECLARATION = 12;

	/**
	 * Constant representing all import declarations within a EGL file.
	 * A EGL element with this type can be safely cast to <code>IImportContainer</code>.
	 */
	int IMPORT_CONTAINER = 13;

	/**
	 * Constant representing an import declaration within a EGL file.
	 * A EGL element with this type can be safely cast to <code>IImportDeclaration</code>.
	 */
	int IMPORT_DECLARATION = 14;
	/**
	 * Constant representing a USE declaration within a EGL file.
	 * A EGL element with this type can be safely cast to <code>IUseDeclaration</code>.
	 */
	int USE_DECLARATION = 15;
	/**
	 * Constant representing a property block declaration within a declaration.
	 * A EGL element with this type can be safely cast to <code>IPropertyBlock</code>.
	 */
	int PROPERTY_BLOCK = 16;
	/**
	 * Constant representing a property declaration within a property block.
	 * A EGL element with this type can be safely cast to <code>IProperty</code>.
	 */
	int PROPERTY = 17;
	
	/**
	 * Returns whether this EGL element exists in the model.
	 * <p>
	 * EGL elements are handle objects that may or may not be backed by an
	 * actual element. EGL elements that are backed by an actual element are
	 * said to "exist", and this method returns <code>true</code>. For EGL
	 * elements that are not working copies, it is always the case that if the
	 * element exists, then its parent also exists (provided it has one) and
	 * includes the element as one of its children. It is therefore possible
	 * to navigated to any existing EGL element from the root of the EGL model
	 * along a chain of existing EGL elements. On the other hand, working
	 * copies are said to exist until they are destroyed (with
	 * <code>IWorkingCopy.destroy</code>). Unlike regular EGL elements, a
	 * working copy never shows up among the children of its parent element
	 * (which may or may not exist).
	 * </p>
	 *
	 * @return <code>true</code> if this element exists in the EGL model, and
	 * <code>false</code> if this element does not exist
	 */
	boolean exists();
	
	/**
	 * Returns the first ancestor of this EGL element that has the given type.
	 * Returns <code>null</code> if no such an ancestor can be found.
	 * This is a handle-only method.
	 * 
	 * @param ancestorType the given type
	 * @return the first ancestor of this EGL element that has the given type, null if no such an ancestor can be found
	 * @since 2.0
	 */
	IEGLElement getAncestor(int ancestorType);

	/**
	 * Returns the resource that corresponds directly to this element,
	 * or <code>null</code> if there is no resource that corresponds to
	 * this element.
	 * <p>
	 * For example, the corresponding resource for an <code>ICompilationUnit</code>
	 * is its underlying <code>IFile</code>. The corresponding resource for
	 * an <code>IPackageFragment</code> that is not contained in an archive 
	 * is its underlying <code>IFolder</code>. An <code>IPackageFragment</code>
	 * contained in an archive has no corresponding resource. Similarly, there
	 * are no corresponding resources for <code>IMethods</code>,
	 * <code>IFields</code>, etc.
	 * <p>
	 *
	 * @return the corresponding resource, or <code>null</code> if none
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	IResource getCorrespondingResource() throws EGLModelException;

	/**
	 * Returns the name of this element. This is a handle-only method.
	 *
	 * @return the element name
	 */
	String getElementName();

	/**
	 * Returns this element's kind encoded as an integer.
	 * This is a handle-only method.
	 *
	 * @return the kind of element; one of the constants declared in
	 *   <code>IEGLElement</code>
	 * @see IEGLElement
	 */
	int getElementType();

	/**
	 * Returns a string representation of this element handle. The format of
	 * the string is not specified; however, the identifier is stable across
	 * workspace sessions, and can be used to recreate this handle via the 
	 * <code>EGLCore.create(String)</code> method.
	 *
	 * @return the string handle identifier
	 * @see EGLCore#create(java.lang.String)
	 */
	String getHandleIdentifier();

	/**
	 * Returns the EGL model.
	 * This is a handle-only method.
	 *
	 * @return the EGL model
	 */
	IEGLModel getEGLModel();

	/**
	 * Returns the EGL project this element is contained in,
	 * or <code>null</code> if this element is not contained in any EGL project
	 * (for instance, the <code>IEGLModel</code> is not contained in any EGL 
	 * project).
	 * This is a handle-only method.
	 *
	 * @return the containing EGL project, or <code>null</code> if this element is
	 *   not contained in a EGL project
	 */
	IEGLProject getEGLProject();

	/**
	 * Returns the first openable parent. If this element is openable, the element
	 * itself is returned. Returns <code>null</code> if this element doesn't have
	 * an openable parent.
	 * This is a handle-only method.
	 * 
	 * @return the first openable parent or <code>null</code> if this element doesn't have
	 * an openable parent.
	 * @since 2.0
	 */
	IOpenable getOpenable();

	/**
	 * Returns the element directly containing this element,
	 * or <code>null</code> if this element has no parent.
	 * This is a handle-only method.
	 *
	 * @return the parent element, or <code>null</code> if this element has no parent
	 */
	IEGLElement getParent();

	/**
	 * Returns the path to the innermost resource enclosing this element. 
	 * If this element is not included in an external archive, 
	 * the path returned is the full, absolute path to the underlying resource, 
	 * relative to the workbench. 
	 * If this element is included in an external archive, 
	 * the path returned is the absolute path to the archive in the file system.
	 * This is a handle-only method.
	 * 
	 * @return the path to the innermost resource enclosing this element
	 * @since 2.0
	 */
	IPath getPath();

	/**
	 * Returns the innermost resource enclosing this element. 
	 * If this element is included in an archive and this archive is not external, 
	 * this is the underlying resource corresponding to the archive. 
	 * If this element is included in an external archive, <code>null</code>
	 * is returned.
	 * If this element is a working copy, <code>null</code> is returned.
	 * This is a handle-only method.
	 * 
	 * @return the innermost resource enclosing this element, <code>null</code> if this 
	 * element is a working copy or is included in an external archive
	 * @since 2.0
	 */
	IResource getResource();

	/**
	 * Returns the smallest underlying resource that contains
	 * this element, or <code>null</code> if this element is not contained
	 * in a resource.
	 *
	 * @return the underlying resource, or <code>null</code> if none
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its underlying resource
	 */
	IResource getUnderlyingResource() throws EGLModelException;

	/**
	 * Returns whether this EGL element is read-only. An element is read-only
	 * if its structure cannot be modified by the EGL model. 
	 * <p>
	 * Note this is different from IResource.isReadOnly(). For example, .jar
	 * files are read-only as the EGL model doesn't know how to add/remove 
	 * elements in this file, but the underlying IFile can be writable.
	 * <p>
	 * This is a handle-only method.
	 *
	 * @return <code>true</code> if this element is read-only
	 */
	boolean isReadOnly();

	/**
	 * Returns whether the structure of this element is known. For example, for a
	 * EGL file that could not be parsed, <code>false</code> is returned.
	 * If the structure of an element is unknown, navigations will return reasonable
	 * defaults. For example, <code>getChildren</code> will return an empty collection.
	 * <p>
	 * Note: This does not imply anything about consistency with the
	 * underlying resource/buffer contents.
	 * </p>
	 *
	 * @return <code>true</code> if the structure of this element is known
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	boolean isStructureKnown() throws EGLModelException;
}
