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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Abstract base implementation of all eglpath variable initializers.
 * EGLPath variable initializers are used in conjunction with the
 * "com.ibm.etools.egl.internal.model.core.eglpathVariableInitializer" extension point.
 * <p>
 * Clients should subclass this class to implement a specific eglpath
 * variable initializer. The subclass must have a public 0-argument
 * constructor and a concrete implementation of <code>initialize</code>.
 * 
 * @see IEGLPathEntry
 * @since 2.0
 */
public abstract class EGLPathVariableInitializer {

    /**
     * Creates a new eglpath variable initializer.
     */
    public EGLPathVariableInitializer() {
    }

    /**
     * Binds a value to the workspace eglpath variable with the given name,
     * or fails silently if this cannot be done. 
     * <p>
     * A variable initializer is automatically activated whenever a variable value
     * is needed and none has been recorded so far. The implementation of
     * the initializer can set the corresponding variable using 
     * <code>JavaCore#setEGLPathVariable</code>.
     * 
     * @param variable the name of the workspace eglpath variable
     *    that requires a binding
     * 
     * @see JavaCore#getEGLPathVariable(String)
     * @see JavaCore#setEGLPathVariable(String, IPath, IProgressMonitor)
     * @see JavaCore#setEGLPathVariables(String[], IPath[], IProgressMonitor)
     */
    public abstract void initialize(String variable);
}
