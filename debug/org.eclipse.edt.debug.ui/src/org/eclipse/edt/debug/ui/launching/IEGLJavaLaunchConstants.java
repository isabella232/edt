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
package org.eclipse.edt.debug.ui.launching;

import org.eclipse.edt.debug.internal.ui.EDTDebugUIPlugin;

public interface IEGLJavaLaunchConstants
{
	public static final String CONFIG_TYPE_MAIN_PROGRAM = "org.eclipse.edt.debug.ui.launching.EGLJavaMainApplication"; //$NON-NLS-1$
	
	public static final String ATTR_PROJECT_NAME = EDTDebugUIPlugin.PLUGIN_ID + ".attr_project_name"; //$NON-NLS-1$
	
	public static final String ATTR_PROGRAM_FILE = EDTDebugUIPlugin.PLUGIN_ID + ".attr_program_file"; //$NON-NLS-1$
	
	public static final String IMG_LAUNCH_MAIN_TAB = "IMG_LAUNCH_MAIN_TAB"; //$NON-NLS-1$
	
	public static final String HELP_ID_PROGRAM_LAUNCH = EDTDebugUIPlugin.PLUGIN_ID + ".program_launch"; //$NON-NLS-1$
}
