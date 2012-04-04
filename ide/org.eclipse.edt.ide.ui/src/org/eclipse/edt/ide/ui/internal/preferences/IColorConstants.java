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
package org.eclipse.edt.ide.ui.internal.preferences;

/**
 * Color keys used for syntax highlighting EGL 
 * code and comments. 
 * A <code>IColorManager</code> is responsible for mapping 
 * concrete colors to these keys.
 * <p>
 * This interface declares static final fields only; it is not intended to be 
 * implemented.
 * </p>
 *
 * @see org.eclipse.jdt.ui.text.IColorManager
 */
public interface IColorConstants {
	
	/** 
	 * Note: This constant is for internal use only. Clients should not use this constant.
	 * The prefix all color constants start with
	 * (value <code>"egl_"</code>).
	 */
	String PREFIX= "egl_"; //$NON-NLS-1$
	
	/** The color key for multi-line comments in EGL code
	 * (value <code>"egl_multi_line_comment"</code>).
	 */
	String EGL_MULTI_LINE_COMMENT= "egl_multi_line_comment"; //$NON-NLS-1$

	/** The color key for single-line comments in EGL code
	 * (value <code>"egl_single_line_comment"</code>).
	 */
	String EGL_SINGLE_LINE_COMMENT= "egl_single_line_comment"; //$NON-NLS-1$

	/** The color key for EGL keywords in EGL code
	 * (value <code>"egl_keyword"</code>).
	 */
	String EGL_KEYWORD= "egl_keyword"; //$NON-NLS-1$

	/** The color key for string and character literals in EGL code
	 * (value <code>"java_string"</code>).
	 */
	String EGL_STRING= "egl_string"; //$NON-NLS-1$
	
	/** The color key for EGL System Words in EGL code
		 * (value <code>"egl_system"</code>).
		 */
		String EGL_SYSTEM= "egl_system"; //$NON-NLS-1$

	/**
	 * The color key for everthing in EGL code for which no other color is specified
	 * (value <code>"egl_default"</code>).
	 */
	String EGL_DEFAULT= "egl_default"; //$NON-NLS-1$		
}


