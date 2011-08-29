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
package org.eclipse.edt.debug.ui.launching;

import org.eclipse.edt.debug.internal.ui.java.EGLJavaMessages;
import org.eclipse.osgi.util.NLS;

public class EGLLaunchingMessages extends NLS
{
private static final String BUNDLE_NAME = "org.eclipse.edt.debug.ui.launching.EGLLaunchingMessages"; //$NON-NLS-1$
	
	static
	{
		// load message values from bundle file
		NLS.initializeMessages( BUNDLE_NAME, EGLJavaMessages.class );
	}
	
	public static String launch_shortcut_usage;
	public static String launch_shortcut_missing_file_error;
	public static String launch_shortcut_multiple_files_error;
	public static String launch_error_dialog_title;
	public static String java_launch_no_files;
	public static String java_launch_file_selection_title;
	public static String java_launch_file_selection_msg;
}
