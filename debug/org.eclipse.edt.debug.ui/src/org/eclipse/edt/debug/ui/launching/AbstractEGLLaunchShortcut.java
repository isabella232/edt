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
package org.eclipse.edt.debug.ui.launching;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.edt.debug.core.DebugUtil;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

public abstract class AbstractEGLLaunchShortcut implements ILaunchShortcut2
{
	/**
	 * Performs the actual launch on the .egl file.
	 * 
	 * @param eglFile The .egl file
	 * @param mode The mode (run, debug)
	 */
	protected abstract void doLaunch( IFile eglFile, String mode );
	
	@Override
	public void launch( ISelection selection, String mode )
	{
		if ( selection == null || !(selection instanceof IStructuredSelection) )
		{
			MessageDialog
					.openError( DebugUtil.getShell(), EGLLaunchingMessages.launch_error_dialog_title, EGLLaunchingMessages.launch_shortcut_usage );
			return;
		}
		
		List<IFile> eglFiles = new ArrayList<IFile>();
		Iterator iterator = ((IStructuredSelection)selection).iterator();
		while ( iterator.hasNext() )
		{
			Object obj = iterator.next();
			if ( obj instanceof IEGLElement )
			{
				IEGLElement element = (IEGLElement)obj;
				try
				{
					IResource file = element.getUnderlyingResource();
					if ( file.getType() == IResource.FILE )
					{
						String name = file.getName();
						if ( DebugUtil.isEGLFileName( name ) )
						{
							eglFiles.add( (IFile)file );
						}
					}
				}
				catch ( EGLModelException e1 )
				{
				}
			}
			else if ( obj instanceof IFile )
			{
				IFile file = (IFile)obj;
				String name = file.getName();
				if ( DebugUtil.isEGLFileName( name ) )
				{
					eglFiles.add( file );
				}
			}
		}
		
		if ( eglFiles.size() == 0 )
		{
			MessageDialog.openError( DebugUtil.getShell(), EGLLaunchingMessages.launch_error_dialog_title,
					EGLLaunchingMessages.launch_shortcut_missing_file_error );
			return;
		}
		
		if ( eglFiles.size() > 1 )
		{
			MessageDialog.openError( DebugUtil.getShell(), EGLLaunchingMessages.launch_error_dialog_title,
					EGLLaunchingMessages.launch_shortcut_multiple_files_error );
			return;
		}
		
		doLaunch( eglFiles.get( 0 ), mode );
	}
	
	@Override
	public void launch( IEditorPart editor, String mode )
	{
		IEditorInput input = editor.getEditorInput();
		IEGLElement ee = (IEGLElement)input.getAdapter( IEGLElement.class );
		if ( ee != null )
		{
			launch( new StructuredSelection( ee ), mode );
		}
		
		IFile ifile = (IFile)input.getAdapter( IFile.class );
		if ( ifile != null )
		{
			launch( new StructuredSelection( ifile ), mode );
		}
	}
	
	@Override
	public ILaunchConfiguration[] getLaunchConfigurations( ISelection selection )
	{
		// Let the framework figure it out.
		return null;
	}
	
	@Override
	public ILaunchConfiguration[] getLaunchConfigurations( IEditorPart editorpart )
	{
		// Let the framework figure it out.
		return null;
	}
	
	@Override
	public IResource getLaunchableResource( ISelection selection )
	{
		if ( selection instanceof IStructuredSelection )
		{
			IStructuredSelection ss = (IStructuredSelection)selection;
			if ( ss.size() == 1 )
			{
				Object element = ss.getFirstElement();
				if ( element instanceof IAdaptable )
				{
					return getLaunchableResource( (IAdaptable)element );
				}
			}
		}
		return null;
	}
	
	@Override
	public IResource getLaunchableResource( IEditorPart editorpart )
	{
		return getLaunchableResource( editorpart.getEditorInput() );
	}
	
	private IResource getLaunchableResource( IAdaptable adaptable )
	{
		IEGLElement ee = (IEGLElement)adaptable.getAdapter( IEGLElement.class );
		if ( ee != null )
		{
			return ee.getResource();
		}
		return null;
	}
}
