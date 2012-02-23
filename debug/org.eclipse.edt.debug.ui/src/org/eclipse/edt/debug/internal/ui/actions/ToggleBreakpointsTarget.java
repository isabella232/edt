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
package org.eclipse.edt.debug.internal.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension;
import org.eclipse.edt.debug.core.breakpoints.EGLLineBreakpoint;
import org.eclipse.edt.ide.ui.editor.IEGLEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Handles adding and removing line breakpoints with the egl stratum.
 */
public class ToggleBreakpointsTarget implements IToggleBreakpointsTargetExtension
{
	@Override
	public void toggleLineBreakpoints( final IWorkbenchPart part, final ISelection selection ) throws CoreException
	{
		if ( !(selection instanceof ITextSelection) )
		{
			return;
		}
		
		Job job = new Job( "Toggle Line Breakpoint" ) { //$NON-NLS-1$
			protected IStatus run( IProgressMonitor monitor )
			{
				ITextEditor editor = BreakpointUtils.getEditor( part );
				if ( editor != null )
				{
					IDocumentProvider provider = editor.getDocumentProvider();
					if ( provider == null )
					{
						return Status.CANCEL_STATUS;
					}
					
					IEditorInput input = editor.getEditorInput();
					if ( !(input instanceof IFileEditorInput) )
					{
						return Status.CANCEL_STATUS;
					}
					
					IDocument document = provider.getDocument( input );
					if ( document != null )
					{
						try
						{
							ITextSelection textSelection = (ITextSelection)selection;
							IFile file = ((IFileEditorInput)input).getFile();
							
							// First check if there's a breakpoint on the actual line selected.
							EGLLineBreakpoint breakpoint = BreakpointUtils.eglLineBreakpointExists( file, textSelection.getStartLine() + 1 );
							if ( breakpoint != null )
							{
								DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint( breakpoint, true );
							}
							else
							{
								int line;
								IEGLEditor eglEditor = (IEGLEditor)editor.getAdapter( IEGLEditor.class );
								if ( eglEditor != null )
								{
									line = eglEditor.getLineAtOffset( BreakpointUtils.getStatementNode( eglEditor, textSelection.getStartLine() )
											.getOffset() ) + 1;
								}
								else
								{
									line = textSelection.getStartLine() + 1;
								}
								
								// Could be the statement line is different from the actual line. Check if the statement
								// line contains a breakpoint already.
								if ( line != textSelection.getStartLine() + 1 )
								{
									breakpoint = BreakpointUtils.eglLineBreakpointExists( file, line );
								}
								if ( breakpoint != null )
								{
									DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint( breakpoint, true );
								}
								else
								{
									new EGLLineBreakpoint( file, line, -1, -1, true, false );
								}
							}
						}
						catch ( CoreException e )
						{
							return Status.CANCEL_STATUS;
						}
					}
				}
				
				return Status.OK_STATUS;
			}
		};
		job.setPriority( Job.INTERACTIVE );
		job.setSystem( true );
		job.schedule();
	}
	
	@Override
	public boolean canToggleLineBreakpoints( IWorkbenchPart part, ISelection selection )
	{
		if ( selection instanceof ITextSelection )
		{
			ITextSelection textSelection = (ITextSelection)selection;
			ITextEditor textEditor = BreakpointUtils.getEditor( part );
			IEditorInput input = textEditor.getEditorInput();
			
			// If the breakpoint already exists, allow the action to run so it can be removed.
			if ( input instanceof IFileEditorInput )
			{
				try
				{
					if ( BreakpointUtils.eglLineBreakpointExists( ((IFileEditorInput)textEditor.getEditorInput()).getFile(),
							textSelection.getStartLine() + 1 ) != null )
					{
						return true;
					}
				}
				catch ( CoreException ce )
				{
				}
			}
			
			IEGLEditor eglEditor = textEditor == null
					? null
					: (IEGLEditor)textEditor.getAdapter( IEGLEditor.class );
			if ( eglEditor != null )
			{
				// Otherwise validate it's an okay location to add a breakpoint.
				return BreakpointUtils.isBreakpointValid( eglEditor, textSelection.getStartLine() );
			}
		}
		return false;
	}
	
	@Override
	public void toggleMethodBreakpoints( IWorkbenchPart part, ISelection selection ) throws CoreException
	{
	}
	
	@Override
	public boolean canToggleMethodBreakpoints( IWorkbenchPart part, ISelection selection )
	{
		return false;
	}
	
	@Override
	public void toggleWatchpoints( IWorkbenchPart part, ISelection selection ) throws CoreException
	{
	}
	
	@Override
	public boolean canToggleWatchpoints( IWorkbenchPart part, ISelection selection )
	{
		return false;
	}
	
	@Override
	public void toggleBreakpoints( IWorkbenchPart part, ISelection selection ) throws CoreException
	{
	}
	
	@Override
	public boolean canToggleBreakpoints( IWorkbenchPart part, ISelection selection )
	{
		return false;
	}
}
