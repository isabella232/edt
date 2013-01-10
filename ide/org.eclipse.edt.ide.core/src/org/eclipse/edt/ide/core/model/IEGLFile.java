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
 * Represents an entire EGL (<code>.egl</code> source file).
 * EGL file elements need to be opened before they can be navigated or manipulated.
 * The children are of type <code>IPackageDeclaration</code>,
 * <code>IImportContainer</code>, and <code>IPart</code>,
 * and appear in the order in which they are declared in the source.
 * If a <code>.egl</code> file cannot be parsed, its structure remains unknown.
 * Use <code>IEGLElement.isStructureKnown</code> to determine whether this is 
 * the case.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IEGLFile extends IEGLElement, ISourceReference, IParent, IOpenable, ISourceManipulation, IWorkingCopy {
/**
 * Creates and returns an import declaration in this egl file
 * with the given name.
 * <p>
 * Optionally, the new element can be positioned before the specified
 * sibling. If no sibling is specified, the element will be inserted
 * as the last import declaration in this egl file.
 * <p>
 * If the egl file already includes the specified import declaration,
 * the import is not generated (it does not generate duplicates).
 * Note that it is valid to specify both a single-type import and an on-demand import
 * for the same package, for example <code>"java.io.File"</code> and
 * <code>"java.io.*"</code>, in which case both are preserved since the semantics
 * of this are not the same as just importing <code>"java.io.*"</code>.
 * Importing <code>"java.lang.*"</code>, or the package in which the egl file
 * is defined, are not treated as special cases.  If they are specified, they are
 * included in the result.
 *
 * @param name the name of the import declaration to add as defined by JLS2 7.5. (For example: <code>"java.io.File"</code> or
 *  <code>"java.awt.*"</code>)
 * @param sibling the existing element which the import declaration will be inserted immediately before (if
 *	<code> null </code>, then this import will be inserted as the last import declaration.
 * @param monitor the progress monitor to notify
 * @return the newly inserted import declaration (or the previously existing one in case attempting to create a duplicate)
 *
 * @exception EGLModelException if the element could not be created. Reasons include:
 * <ul>
 * <li> This EGL element does not exist or the specified sibling does not exist (ELEMENT_DOES_NOT_EXIST)</li>
 * <li> A <code>CoreException</code> occurred while updating an underlying resource
 * <li> The specified sibling is not a child of this egl file (INVALID_SIBLING)
 * <li> The name is not a valid import name (INVALID_NAME)
 * </ul>
 */
IImportDeclaration createImport(String name, IEGLElement sibling, IProgressMonitor monitor) throws EGLModelException;
/**
 * Creates and returns a package declaration in this egl file
 * with the given package name.
 *
 * <p>If the egl file already includes the specified package declaration,
 * it is not generated (it does not generate duplicates).
 *
 * @param name the name of the package declaration to add as defined by JLS2 7.4. (For example, <code>"java.lang"</code>)
 * @param monitor the progress monitor to notify
 * @return the newly inserted package declaration (or the previously existing one in case attempting to create a duplicate)
 *
 * @exception EGLModelException if the element could not be created. Reasons include:
 * <ul>
 * <li>This EGL element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
 * <li> A <code>CoreException</code> occurred while updating an underlying resource
 * <li> The name is not a valid package name (INVALID_NAME)
 * </ul>
 */
 IPackageDeclaration createPackageDeclaration(String name, IProgressMonitor monitor) throws EGLModelException;   
/**
 * Creates and returns a type in this egl file with the
 * given contents. If this egl file does not exist, one
 * will be created with an appropriate package declaration.
 * <p>
 * Optionally, the new type can be positioned before the specified
 * sibling. If <code>sibling</code> is <code>null</code>, the type will be appended
 * to the end of this egl file.
 *
 * <p>It is possible that a type with the same name already exists in this egl file.
 * The value of the <code>force</code> parameter effects the resolution of
 * such a conflict:<ul>
 * <li> <code>true</code> - in this case the type is created with the new contents</li>
 * <li> <code>false</code> - in this case a <code>EGLModelException</code> is thrown</li>
 * </ul>
 *
 * @param contents the source contents of the type declaration to add.
 * @param sibling the existing element which the type will be inserted immediately before (if
 *	<code> null </code>, then this type will be inserted as the last type declaration.
 * @param force a <code> boolean </code> flag indicating how to deal with duplicates
 * @param monitor the progress monitor to notify
 * @return the newly inserted type
 *
 * @exception EGLModelException if the element could not be created. Reasons include:
 * <ul>
 * <li>The specified sibling element does not exist (ELEMENT_DOES_NOT_EXIST)</li>
 * <li> A <code>CoreException</code> occurred while updating an underlying resource
 * <li> The specified sibling is not a child of this egl file (INVALID_SIBLING)
 * <li> The contents could not be recognized as a type declaration (INVALID_CONTENTS)
 * <li> There was a naming collision with an existing type (NAME_COLLISION)
 * </ul>
 */
IPart createPart(String contents, IEGLElement sibling, boolean force, IProgressMonitor monitor) throws EGLModelException;
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
 * Returns the first import declaration in this egl file with the given name.
 * This is a handle-only method. The import declaration may or may not exist. This
 * is a convenience method - imports can also be accessed from a egl file's
 * import container.
 *
 * @param name the name of the import to find as defined by JLS2 7.5. (For example: <code>"java.io.File"</code> 
 * 	or <code>"java.awt.*"</code>)
 * @return a handle onto the corresponding import declaration. The import declaration may or may not exist.
 */
IImportDeclaration getImport(String name) ;
/**
 * Returns the import container for this egl file.
 * This is a handle-only method. The import container may or
 * may not exist. The import container can used to access the 
 * imports.
 * @return a handle onto the corresponding import container. The 
 *		import contain may or may not exist.
 */
IImportContainer getImportContainer();
/**
 * Returns the import declarations in this egl file
 * in the order in which they appear in the source. This is
 * a convenience method - import declarations can also be
 * accessed from a egl file's import container.
 *
 * @exception EGLModelException if this element does not exist or if an
 *		exception occurs while accessing its corresponding resource
 */
IImportDeclaration[] getImports() throws EGLModelException;
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
