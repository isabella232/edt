/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.javascript.internal.model;

import org.eclipse.edt.debug.javascript.EDTJavaScriptDebugPlugin;

public interface IRUILaunchConfigurationConstants
{
	public static final String ATTR_PROJECT_NAME = EDTJavaScriptDebugPlugin.PLUGIN_ID + ".attr_project_name"; //$NON-NLS-1$
	public static final String ATTR_HANDLER_FILE = EDTJavaScriptDebugPlugin.PLUGIN_ID + ".attr_handler_file"; //$NON-NLS-1$
	public static final String ATTR_URL = EDTJavaScriptDebugPlugin.PLUGIN_ID + ".attr_url"; //$NON-NLS-1$
	public static final String ATTR_CONTEXT_KEY = EDTJavaScriptDebugPlugin.PLUGIN_ID + ".attr_key"; //$NON-NLS-1$
}
