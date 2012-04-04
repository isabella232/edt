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
package org.eclipse.edt.compiler.internal;

/**
 * @author svihovec
 *
 */
public interface IEGLMarker {

    /**
	 * EGL problem marker type (value <code>"com.ibm.etools.egl.problem"</code>).
	 * This can be used to recognize those markers in the workspace that flag problems 
	 * detected by the EGL tooling during validation.
	 */
	public static final String EGL_PROBLEM_MARKER = EGLBasePlugin.EGL_BASE_PLUGIN_ID + ".problem"; //$NON-NLS-1$
}
