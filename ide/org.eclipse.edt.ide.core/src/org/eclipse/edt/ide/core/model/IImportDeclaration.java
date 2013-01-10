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

// import com.ibm.etools.egl.internal.pgm.model.IEGLImportStatement;
/**
 * Represents an import declaration in EGL file.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IImportDeclaration extends IEGLElement, ISourceReference, ISourceManipulation {
/**
 * Returns the name that has been imported. 
 * For an on-demand import, this includes the trailing <code>".*"</code>.
 * For example, for the statement <code>"import java.util.*"</code>,
 * this returns <code>"egl.util.*"</code>.
 * For the statement <code>"import java.util.Hashtable"</code>,
 * this returns <code>"egl.util.Hashtable"</code>.
 * 
 * @return the name that has been imported
 */
String getElementName();
/**
 * Returns whether the import is on-demand. An import is on-demand if it ends
 * with <code>".*"</code>.
 * @return true if the import is on-demand, false otherwise
 */
boolean isOnDemand();
}
