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

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.edt.debug.internal.ui.java.EGLJavaMessages;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaApplicationLaunchShortcut;

public class EGLJavaLaunchShortcut extends JavaApplicationLaunchShortcut
{
	@Override
	protected ILaunchConfigurationType getConfigurationType()
	{
		return DebugPlugin.getDefault().getLaunchManager()
				.getLaunchConfigurationType( IEGLDebugUIConstants.EGL_JAVA_LAUNCH_CONFIG_TYPE );
	}
	
	@Override
	protected String getTypeSelectionTitle()
	{
		return EGLJavaMessages.ChooseExistingJavaTitle;
	}
}
