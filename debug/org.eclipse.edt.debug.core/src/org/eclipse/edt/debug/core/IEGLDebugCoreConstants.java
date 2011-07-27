/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.core;

public interface IEGLDebugCoreConstants
{
	/**
	 * The ID of the EGL Java model presentation.
	 */
	public static final String EGL_JAVA_MODEL_PRESENTATION_ID = "org.eclipse.edt.debug.ui.presentation.java"; //$NON-NLS-1$
	
	/**
	 * The EGL stratum name (as used in the SMAP).
	 */
	public static final String EGL_STRATUM = "egl"; //$NON-NLS-1$
	
	/**
	 * The SMAP file extension.
	 */
	public static final String SMAP_EXTENSION = "eglsmap"; //$NON-NLS-1$
}
