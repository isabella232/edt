/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.core.java.variables;

import org.eclipse.edt.debug.core.java.IEGLJavaStackFrame;
import org.eclipse.edt.debug.core.java.IEGLJavaValue;
import org.eclipse.edt.debug.core.java.IEGLJavaVariable;
import org.eclipse.edt.debug.core.java.SMAPUtil;
import org.eclipse.edt.debug.core.java.SMAPVariableInfo;
import org.eclipse.jdt.debug.core.IJavaVariable;

/**
 * The Java code used to debug the application can use any number of classes to represent the underlying EGL variables. For example a Java generator
 * might use class com.foo.Array to represent an EGL array variable. It's possible this com.foo.Array type has SMAP information that tells the EDT
 * debug framework which variables to display, what the EGL name and type is, etc. But in the absence of SMAP information you can contribute an
 * IVariableAdapter to the framework that is capable of handling the Java type. The adapter can handle multiple Java types, or a separate adapter can
 * be contributed for each Java type.<br/>
 * <br/>
 * Notes:
 * <ul>
 * <li>Variable adapters will be consulted before checking if there is SMAP variable information.</li>
 * <li>The first adapter that responds with a non-null value will be used, and the order that adapters are called is not guaranteed.
 * <li>
 * <li>If an adapter only wants to handle a variable when there isn't SMAP variable information, there are various utility methods in {@link SMAPUtil}
 * to help out with this.</li>
 * </ul>
 */
public interface IVariableAdapter
{
	/**
	 * Adapts a variable to an IEGLVariable. If this adapter does not support the given variable type, it should return null.
	 * 
	 * @param variable The original Java variable.
	 * @param frame The stack frame.
	 * @param info The SMAP variable information.
	 * @param parent The parent of the variable to be created, possibly null.
	 * @return the adapted variable, or null if it can't adapt the Java variable.
	 */
	public IEGLJavaVariable adapt( IJavaVariable variable, IEGLJavaStackFrame frame, SMAPVariableInfo info, IEGLJavaValue parent );
	
	/**
	 * Performs any required cleanup.
	 */
	public void dispose();
}
