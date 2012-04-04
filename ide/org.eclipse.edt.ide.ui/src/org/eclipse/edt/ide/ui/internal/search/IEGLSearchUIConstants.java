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
package org.eclipse.edt.ide.ui.internal.search;

/**
 * Constants used by the EGL search
 */
public interface IEGLSearchUIConstants {
	/** Attribute name of the EGL Element handle ID in a marker */
	static final String ATT_GE_HANDLE_ID= "org.eclipse.edt.ide.ui.search.handleID"; //$NON-NLS-1$

	/** Attribute name of EGL Element handle ID changed flag in a marker */
	static final String ATT_GE_HANDLE_ID_CHANGED= "org.eclipse.edt.ide.ui.search.handleIdChanged"; //$NON-NLS-1$

	/** Attribute name for isWorkingCopy property */
	static final String ATT_IS_WORKING_COPY= "org.eclipse.edt.ide.ui.search.isWorkingCopy"; //$NON-NLS-1$
}
