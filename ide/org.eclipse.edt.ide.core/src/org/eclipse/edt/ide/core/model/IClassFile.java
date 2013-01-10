/*******************************************************************************
 * Copyright © 2010, 2013 IBM Corporation and others.
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

public interface IClassFile extends IEGLElement, ISourceReference, IParent, IOpenable, ISourceManipulation, IWorkingCopy {

	/**
	 * Returns the bytes contained in this class file.
	 *
	 * @return the bytes contained in this class file
	 *
	 * @exception EGLModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource
	 * @since 3.3
	 */
	byte[] getBytes() throws EGLModelException;
	/**
	 * Returns the type contained in this class file.
	 * This is a handle-only method. The type may or may not exist.
	 *
	 * @return the type contained in this class file
	 */
	IPart getPart();

	/**
	 * Returns whether this type represents a class. This is not guaranteed to be
	 * instantaneous, as it may require parsing the underlying file.
	 *
	 * @return <code>true</code> if the class file represents a class.
	 *
	 * @exception JavaModelException if this element does not exist or if an
	 *      exception occurs while accessing its corresponding resource
	 */
	boolean isClass() throws EGLModelException;
	/**
	 * Returns all types declared in this egl file in the order
	 * in which they appear in the source. 
	 * This includes all top-level types and nested member types.
	 * It does NOT include local types (types defined in methods).
	 *
	 * @return the array of top-level and member types defined in a egl file, in declaration order.
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	IPart[] getAllParts() throws EGLModelException;
	/**
	 * Returns the smallest element within this egl file that 
	 * includes the given source position (that is, a method, field, etc.), or
	 * <code>null</code> if there is no element other than the compilation
	 * unit itself at the given position, or if the given position is not
	 * within the source range of this egl file.
	 *
	 * @param position a source position inside the egl file
	 * @return the innermost EGL element enclosing a given source position or <code>null</code>
	 *	if none (excluding the egl file).
	 * @exception EGLModelException if the egl file does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	IEGLElement getElementAt(int position) throws EGLModelException;
	/**
	 * Returns the first package declaration in this egl file with the given package name
	 * (there normally is at most one package declaration).
	 * This is a handle-only method. The package declaration may or may not exist.
	 *
	 * @param name the name of the package declaration as defined by JLS2 7.4. (For example, <code>"java.lang"</code>)
	 */
	IPackageDeclaration getPackageDeclaration(String name);
	/**
	 * Returns the package declarations in this egl file
	 * in the order in which they appear in the source.
	 * There normally is at most one package declaration.
	 *
	 * @return an array of package declaration (normally of size one)
	 *
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	IPackageDeclaration[] getPackageDeclarations() throws EGLModelException;
	/**
	 * Returns the top-level type declared in this egl file with the given simple type name.
	 * The type name has to be a valid egl file name.
	 * This is a handle-only method. The type may or may not exist.
	 *
	 * @param name the simple name of the requested type in the egl file
	 * @return a handle onto the corresponding type. The type may or may not exist.
	 * @see EGLConventions#validateCompilationUnitName(String name)
	 */
	IPart getPart(String name);
	/**
	 * Returns the top-level types declared in this egl file
	 * in the order in which they appear in the source.
	 *
	 * @exception EGLModelException if this element does not exist or if an
	 *		exception occurs while accessing its corresponding resource
	 */
	IPart[] getParts() throws EGLModelException;
}
