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

import org.eclipse.edt.debug.core.IEGLThread;
import org.eclipse.jdt.debug.core.IJavaThread;

/**
 * Represents an EGL thread in the Java-based debugger. EGL threads wrap the original Java thread.
 */
public interface IEGLJavaThread extends IEGLThread, IEGLJavaDebugElement
{
	/**
	 * @return the underlying thread.
	 */
	public IJavaThread getJavaThread();
	
	/**
	 * @return a lock for running evaluations; only one evaluation per thread may be run.
	 */
	public Object getEvaluationLock();
}
