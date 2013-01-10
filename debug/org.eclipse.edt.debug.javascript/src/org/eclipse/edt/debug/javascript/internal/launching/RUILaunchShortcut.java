/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.javascript.internal.launching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.debug.core.DebugUtil;
import org.eclipse.edt.debug.ui.launching.AbstractEGLLaunchShortcut;
import org.eclipse.edt.debug.ui.launching.EGLLaunchingMessages;
import org.eclipse.edt.ide.debug.javascript.internal.utils.RUIDebugUtil;
import org.eclipse.jface.dialogs.MessageDialog;

public class RUILaunchShortcut extends AbstractEGLLaunchShortcut
{
	@Override
	protected void doLaunch( IFile eglFile, String mode )
	{
		IProject project = eglFile.getProject();
		try
		{
			RUIDebugUtil.launchRUIHandler( project, eglFile, mode );
		}
		catch ( CoreException e )
		{
			MessageDialog.openError( DebugUtil.getShell(), EGLLaunchingMessages.launch_error_dialog_title, e.getMessage() );
		}
	}
}
