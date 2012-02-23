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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.ui.actions.IRunToLineTarget;
import org.eclipse.debug.ui.actions.RunToLineHandler;
import org.eclipse.edt.debug.core.IEGLDebugCoreConstants;
import org.eclipse.edt.debug.core.IEGLDebugTarget;
import org.eclipse.edt.debug.core.breakpoints.EGLLineBreakpoint;
import org.eclipse.edt.debug.internal.ui.EDTDebugUIPlugin;
import org.eclipse.edt.ide.ui.editor.IEGLEditor;
import org.eclipse.jdt.debug.ui.IJavaDebugUIConstants;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Handles the Run to Line command.
 */
public class RunToLineTarget implements IRunToLineTarget
{
	@Override
	public void runToLine( IWorkbenchPart part, ISelection selection, ISuspendResume target ) throws CoreException
	{
		String errorMessage = null;
		ITextEditor editor = BreakpointUtils.getEditor( part );
		IEGLEditor eglEditor = editor == null ? null : (IEGLEditor)editor.getAdapter( IEGLEditor.class );
		
		if ( editor != null )
		{
			ITextSelection textSelection = (ITextSelection)selection;
			if ( BreakpointUtils.isBreakpointValid( eglEditor, textSelection.getStartLine() ) )
			{
				IDocumentProvider provider = editor.getDocumentProvider();
				if ( provider != null )
				{
					IEditorInput input = editor.getEditorInput();
					if ( input instanceof IFileEditorInput )
					{
						IFile file = ((IFileEditorInput)input).getFile();
						IDocument document = provider.getDocument( input );
						if ( document != null )
						{
							errorMessage = "Could not locate debug target"; //$NON-NLS-1$
							if ( target instanceof IAdaptable )
							{
								IDebugTarget debugTarget = (IDebugTarget)((IAdaptable)target).getAdapter( IDebugTarget.class );
								if ( debugTarget != null )
								{
									int line = eglEditor.getLineAtOffset( BreakpointUtils.getStatementNode( eglEditor, textSelection.getStartLine() )
											.getOffset() ) + 1;
									
									// Use workspace root instead of the file so that the marker doesn't show up in the editor during the RTL
									EGLLineBreakpoint bp = new EGLLineBreakpoint( ResourcesPlugin.getWorkspace().getRoot(), line, -1, -1, false, true );
									bp.getMarker().setAttribute( IEGLDebugCoreConstants.RUN_TO_LINE_PATH, file.getFullPath().toString() );
									RunToLineHandler handler = new RunToLineHandler( debugTarget, target, bp );
									handler.run( new NullProgressMonitor() );
									return;
								}
							}
						}
						else
						{
							errorMessage = "Missing document"; //$NON-NLS-1$
						}
					}
					else
					{
						errorMessage = "Editor input not IFileEditorInput"; //$NON-NLS-1$
					}
				}
				else
				{
					errorMessage = "Missing document provider"; //$NON-NLS-1$
				}
			}
			else
			{
				errorMessage = "Selected line is not a valid location to run to"; //$NON-NLS-1$
			}
		}
		else
		{
			errorMessage = "Not IEGLEditor"; //$NON-NLS-1$
		}
		
		throw new CoreException( new Status( IStatus.ERROR, EDTDebugUIPlugin.PLUGIN_ID, IJavaDebugUIConstants.INTERNAL_ERROR, errorMessage, null ) );
	}
	
	@Override
	public boolean canRunToLine( IWorkbenchPart part, ISelection selection, ISuspendResume target )
	{
		if ( target instanceof IDebugElement )
		{
			IDebugElement element = (IDebugElement)target;
			IEGLDebugTarget adapter = (IEGLDebugTarget)element.getDebugTarget().getAdapter( IEGLDebugTarget.class );
			return adapter != null;
		}
		return false;
	}
}
