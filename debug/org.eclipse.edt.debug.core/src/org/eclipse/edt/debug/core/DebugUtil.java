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
package org.eclipse.edt.debug.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Utility methods used by the debugger.
 */
public class DebugUtil
{
	private DebugUtil()
	{
		// No instances.
	}
	
	public final static char[] SUFFIX_egl = ".egl".toCharArray(); //$NON-NLS-1$
	public final static char[] SUFFIX_EGL = ".EGL".toCharArray(); //$NON-NLS-1$
	
	/**
	 * Returns true iff str.toLowerCase().endsWith(".egl") implementation is not creating extra strings.
	 */
	public final static boolean isEGLFileName( String name )
	{
		int nameLength = name == null
				? 0
				: name.length();
		int suffixLength = SUFFIX_EGL.length;
		if ( nameLength < suffixLength )
		{
			return false;
		}
		
		for ( int i = 0, offset = nameLength - suffixLength; i < suffixLength; i++ )
		{
			char c = name.charAt( offset + i );
			if ( c != SUFFIX_egl[ i ] && c != SUFFIX_EGL[ i ] )
			{
				return false;
			}
		}
		return true;
	}
	
	public static Shell getShell()
	{
		final Shell[] shell = new Shell[ 1 ];
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec( new Runnable() {
			@Override
			public void run()
			{
				shell[ 0 ] = display.getActiveShell();
			}
		} );
		
		return shell[ 0 ];
	}
	
	public static IResource getContext()
	{
		IWorkbenchPage page = EDTUIPlugin.getActivePage();
		if ( page != null )
		{
			ISelection selection = page.getSelection();
			Object element = null;
			if ( selection instanceof IStructuredSelection )
			{
				IStructuredSelection sel = (IStructuredSelection)selection;
				if ( !sel.isEmpty() )
				{
					element = sel.getFirstElement();
				}
			}
			else
			{
				element = selection;
			}
			
			if ( element != null && (element instanceof IAdaptable) )
			{
				element = ((IAdaptable)element).getAdapter( IResource.class );
				return (IResource)element;
			}
			
			IEditorPart part = page.getActiveEditor();
			if ( part != null )
			{
				IEditorInput input = part.getEditorInput();
				return (IResource)input.getAdapter( IResource.class );
			}
		}
		return null;
	}
}
