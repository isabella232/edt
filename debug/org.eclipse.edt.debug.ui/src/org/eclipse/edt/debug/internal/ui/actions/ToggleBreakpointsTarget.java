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
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension;
import org.eclipse.edt.compiler.internal.core.utils.Aliaser;
import org.eclipse.edt.debug.core.EDTDebugCorePlugin;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPackageDeclaration;
import org.eclipse.edt.ide.ui.editor.IEGLEditor;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.IJavaStratumLineBreakpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
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
	// Would be nice if JDT made this public so we didn't have to duplicate it.
	private static final String STRATUM_BREAKPOINT = "org.eclipse.jdt.debug.javaStratumLineBreakpointMarker"; //$NON-NLS-1$
	
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
				ITextEditor editor = getEditor( part );
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
							String qualName = getGeneratedClassName( textSelection, file );
							if ( qualName == null )
							{
								return Status.CANCEL_STATUS;
							}
							
							// First check if there's a breakpoint on the actual line selected.
							IJavaLineBreakpoint breakpoint = stratumBreakpointExists( file,
									textSelection.getStartLine() + 1, EDTDebugCorePlugin.EGL_STRATUM );
							if ( breakpoint != null )
							{
								DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint( breakpoint, true );
							}
							else
							{
								int line;
								if ( editor instanceof IEGLEditor )
								{
									IEGLEditor eglEditor = (IEGLEditor)editor;
									line = eglEditor.getLineAtOffset( eglEditor.getStatementNode(
											textSelection.getStartLine() ).getOffset() ) + 1;
								}
								else
								{
									line = textSelection.getStartLine() + 1;
								}
								
								// Could be the statement line is different from the actual line. Check if the statement
								// line contains a breakpoint already.
								if ( line != textSelection.getStartLine() + 1 )
								{
									breakpoint = stratumBreakpointExists( file, line, EDTDebugCorePlugin.EGL_STRATUM );
								}
								if ( breakpoint != null )
								{
									DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint( breakpoint, true );
								}
								else
								{
									JDIDebugModel.createStratumBreakpoint( file, EDTDebugCorePlugin.EGL_STRATUM,
											file.getName(), null, qualName, line, -1, -1, 0, true, null );
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
	
	protected String getGeneratedClassName( ITextSelection selection, IFile file )
	{
		IEGLFile eglFile = (IEGLFile)EGLCore.create( file );
		if ( eglFile != null && eglFile.exists() )
		{
			try
			{
				StringBuilder buf = new StringBuilder( 50 );
				IPackageDeclaration[] pkg = eglFile.getPackageDeclarations();
				if ( pkg != null && pkg.length > 0 )
				{
					buf.append( Aliaser.packageNameAlias( pkg[ 0 ].getElementName() ) );
					buf.append( '.' );
				}
				
				// TODO get alias annotation
				String name = eglFile.getElementName();
				int idx = name.lastIndexOf( '.' );
				if ( idx != -1 )
				{
					name = name.substring( 0, idx );
				}
				
				buf.append( Aliaser.getAlias( name ) );
				return buf.toString();
			}
			catch ( EGLModelException e )
			{
			}
		}
		return null;
	}
	
	@Override
	public boolean canToggleLineBreakpoints( IWorkbenchPart part, ISelection selection )
	{
		if ( selection instanceof ITextSelection )
		{
			ITextSelection textSelection = (ITextSelection)selection;
			ITextEditor textEditor = getEditor( part );
			IEditorInput input = textEditor.getEditorInput();
			
			// If the breakpoint already exists, allow the action to run so it can be removed.
			if ( input instanceof IFileEditorInput )
			{
				try
				{
					if ( stratumBreakpointExists( ((IFileEditorInput)textEditor.getEditorInput()).getFile(),
							textSelection.getStartLine() + 1, EDTDebugCorePlugin.EGL_STRATUM ) != null )
					{
						return true;
					}
				}
				catch ( CoreException ce )
				{
				}
			}
			
			if ( textEditor instanceof IEGLEditor )
			{
				// Otherwise validate it's an okay location to add a breakpoint.
				return ((IEGLEditor)textEditor).isBreakpointValid( textSelection.getStartLine() );
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
	
	protected ITextEditor getEditor( IWorkbenchPart part )
	{
		if ( part instanceof ITextEditor )
		{
			return (ITextEditor)part;
		}
		return (ITextEditor)part.getAdapter( ITextEditor.class );
	}
	
	public static IJavaStratumLineBreakpoint stratumBreakpointExists( IResource resource, int lineNumber, String stratum )
			throws CoreException
	{
		String modelId = JDIDebugModel.getPluginIdentifier();
		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		IBreakpoint[] breakpoints = manager.getBreakpoints( modelId );
		for ( int i = 0; i < breakpoints.length; i++ )
		{
			if ( !(breakpoints[ i ] instanceof IJavaStratumLineBreakpoint) )
			{
				continue;
			}
			IJavaStratumLineBreakpoint breakpoint = (IJavaStratumLineBreakpoint)breakpoints[ i ];
			IMarker marker = breakpoint.getMarker();
			if ( marker != null && marker.exists() && STRATUM_BREAKPOINT.equals( marker.getType() ) )
			{
				if ( breakpoint.getLineNumber() == lineNumber && resource.equals( marker.getResource() )
						&& breakpoint.getStratum().equals( stratum ) )
				{
					return breakpoint;
				}
			}
		}
		return null;
	}
}
