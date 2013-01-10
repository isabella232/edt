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


/**
 * Common protocol for EGL elements that can be members of types.
 * This set consists of <code>IType</code>, <code>IMethod</code>, 
 * <code>IField</code>, and <code>IInitializer</code>.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IMember extends IEGLElement, ISourceReference, ISourceManipulation {
/**
 * Returns the egl file in which this member is declared, or <code>null</code>
 * if this member is not declared in a class file (for example, a source type).
 * This is a handle-only method.
 * 
 * @return the class file in which this member is declared, or <code>null</code>
 * if this member is not declared in a class file (for example, a source type)
 */
IEGLFile getEGLFile();
/**
 * Returns the type in which this member is declared, or <code>null</code>
 * if this member is not declared in a part (for example, a top-level part).
 * This is a handle-only method.
 * 
 * @return the type in which this member is declared, or <code>null</code>
 * if this member is not declared in a part (for example, a top-level part)
 */
IPart getDeclaringPart();
/**
 * Returns the modifier flags for this member. The flags can be examined using class
 * <code>Flags</code>.
 * <p>
 * Note that only flags as indicated in the source are returned. Thus if an interface
 * defines a method <code>void myMethod();</code> the flags don't include the
 * 'public' flag.
 *
 * @exception EGLModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @return the modifier flags for this member
 * @see Flags
 */
int getFlags() throws EGLModelException;
/**
 * Returns the source range of this member's simple name,
 * or <code>null</code> if this member does not have a name
 * (for example, a property block), or if this member does not have
 * associated source code (for example, a binary type).
 *
 * @exception EGLModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource.
 * @return the source range of this member's simple name,
 * or <code>null</code> if this member does not have a name
 * (for example, an initializer), or if this member does not have
 * associated source code (for example, a binary type)
 */
ISourceRange getNameRange() throws EGLModelException;

/**
 * Returns the class file in which this member is declared, or <code>null</code>
 * if this member is not declared in a class file (for example, a source type).
 * This is a handle-only method.
 *
 * @return the class file in which this member is declared, or <code>null</code>
 * if this member is not declared in a class file (for example, a source type)
 */
IClassFile getClassFile();
/**
 * Returns whether this member is from a class file.
 * This is a handle-only method.
 *
 * @return <code>true</code> if from a class file, and <code>false</code> if
 *   from a compilation unit
 */
boolean isBinary();
}
