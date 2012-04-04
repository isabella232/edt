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
package org.eclipse.edt.compiler.internal.util;

/**
 * @author svihovec
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 */
public class EGLFileExtensionUtility {

	/**
	 * Valid file extension for EGL Files
	 */
	protected static final String EGL_FILE_EXTENSION = "egl"; //$NON-NLS-1$

	/**
	 * Valid file extension for EGL Generation Input Files
	 */
	protected static final String EGLBLD_FILE_EXTENSION = "eglbld"; //$NON-NLS-1$

	/**
	 * Returns true if the file extension is one of the 3 supported by EGL.
	 */
	public static boolean isValidEGLFileExtension(String extension) {

		boolean result = false;

		if (extension != null) {
			if (extension.equalsIgnoreCase(getEGLFileExtension()) || extension.equalsIgnoreCase(getEGLBuildFileExtension())) {
				result = true;
			}
		}

		return result;
	} 
	

	/**
	 * Returns true if the file extension is eglbld.
	 */
	public static boolean isValidEGLBuildFileExtension(String extension) {

		boolean result = false;

		if (extension != null) {
			if (extension.equalsIgnoreCase(getEGLBuildFileExtension())) {
				result = true;
			}
		}

		return result;
	}


	public static String getEGLFileExtension() {
		return EGL_FILE_EXTENSION;
	}
	public static String getEGLBuildFileExtension() {
		return EGLBLD_FILE_EXTENSION;
	}
	
	public static boolean isEGLBldPartFileExtension(String extension) {
		return extension != null && extension.equalsIgnoreCase(getEGLBuildFileExtension());
	}

}
