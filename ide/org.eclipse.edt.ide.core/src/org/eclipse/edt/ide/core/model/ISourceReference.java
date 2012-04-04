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

/**
 * Common protocol for EGL elements that have associated source code.
 * This set consists of <code>IEGLFile</code>, 
 * <code>IPackageDeclaration</code>, <code>IImportDeclaration</code>,
 * <code>IImportContainer</code>, <code>IPart</code>, <code>IField</code>,
 * <code>IFunction</code>, and <code>IInitializer</code>.
 * </ul>
 * <p>
 * Source reference elements may be working copies if they were created from
 * a file that is a working copy.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 */
public interface ISourceReference {
/**
 * Returns whether this element exists in the model.
 *
 * @return <code>true</code> if this element exists in the Java model
 * @since 2.0
 */
boolean exists();
	
/**
 * Returns the source code associated with this element.
 * This extracts the substring from the source buffer containing this source
 * element. This corresponds to the source range that would be returned by
 * <code>getSourceRange</code>.
 * <p>
 * For class files, this returns the source of the entire compilation unit 
 * associated with the class file (if there is one).
 * </p>
 *
 * @return the source code, or <code>null</code> if this element has no 
 *   associated source code
 * @exception EGLModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource
 */
String getSource() throws EGLModelException;
/**
 * Returns the source range associated with this element.
 * <p>
 * For class files, this returns the range of the entire compilation unit 
 * associated with the class file (if there is one).
 * </p>
 *
 * @return the source range, or <code>null</code> if this element has no 
 *   associated source code
 * @exception EGLModelException if this element does not exist or if an
 *      exception occurs while accessing its corresponding resource
 */
ISourceRange getSourceRange() throws EGLModelException;
}
