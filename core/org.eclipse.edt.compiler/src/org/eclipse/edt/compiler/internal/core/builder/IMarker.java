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
package org.eclipse.edt.compiler.internal.core.builder;

/**
 * @author svihovec
 *
 */
public interface IMarker {

    /** 
	 * Error severity constant (value 2) indicating an error state.
	 *
	 * @see #getAttribute(String, int)
	 */
	public static final int SEVERITY_ERROR = 2;

	/** 
	 * Warning severity constant (value 1) indicating a warning.
	 *
	 * @see #getAttribute(String, int)
	 */
	public static final int SEVERITY_WARNING = 1;

	/** 
	 * Info severity constant (value 0) indicating information only.
	 *
	 * @see #getAttribute(String, int)
	 */
	public static final int SEVERITY_INFO = 0;
}
