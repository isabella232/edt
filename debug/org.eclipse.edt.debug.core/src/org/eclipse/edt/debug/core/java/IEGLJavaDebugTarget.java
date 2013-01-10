/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.core.java;

import org.eclipse.debug.core.model.IStepFilters;
import org.eclipse.edt.debug.core.IEGLDebugTarget;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;

/**
 * Represents an EGL debug target in the Java-based debugger. EGL debug targets wrap the original Java debug target.
 */
public interface IEGLJavaDebugTarget extends IEGLDebugTarget, IEGLJavaDebugElement, IStepFilters
{
	/**
	 * @return the underlying debug target.
	 */
	public IJavaDebugTarget getJavaDebugTarget();
	
	/**
	 * @return true if the target VM supports JSR-45.
	 */
	public boolean supportsSourceDebugExtension();
	
	/**
	 * @return the cache to use for SMAPs read off disk, or null if SMAPs should not be read off disk.
	 */
	public SMAPFileCache getSMAPFileCache();
}
