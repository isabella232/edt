/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.debug.javascript.internal.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.edt.debug.core.DebugUtil;
import org.eclipse.edt.debug.ui.launching.EGLLaunchingMessages;
import org.eclipse.edt.ide.debug.javascript.internal.utils.RUIDebugUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

/**
 * Action that launches the RUI debugger for the active editor's file.
 */
public class DebugAction implements IEditorActionDelegate
{
	private IEditorPart activeEditor;
	
	@Override
	public void run( IAction action )
	{
		if ( activeEditor != null && activeEditor.getEditorInput() instanceof IFileEditorInput )
		{
			try
			{
				RUIDebugUtil.launchRUIHandlerInDebugMode( ((IFileEditorInput)activeEditor.getEditorInput()).getFile() );
			}
			catch ( CoreException e )
			{
				MessageDialog.openError( DebugUtil.getShell(), EGLLaunchingMessages.launch_error_dialog_title, e.getMessage() );
			}
		}
	}
	
	@Override
	public void selectionChanged( IAction action, ISelection selection )
	{
	}
	
	@Override
	public void setActiveEditor( IAction action, IEditorPart targetEditor )
	{
		activeEditor = targetEditor;
		
	}
}
