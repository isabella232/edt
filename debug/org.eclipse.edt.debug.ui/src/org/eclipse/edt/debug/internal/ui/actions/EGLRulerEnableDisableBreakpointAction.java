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
package org.eclipse.edt.debug.internal.ui.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.internal.ui.actions.breakpoints.RulerEnableDisableBreakpointAction;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.editor.IEGLEditor;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

@SuppressWarnings("restriction")
public class EGLRulerEnableDisableBreakpointAction extends RulerEnableDisableBreakpointAction
{
	public EGLRulerEnableDisableBreakpointAction( ITextEditor editor, IVerticalRulerInfo info )
	{
		super( editor, info );
	}
	
	/**
	 * Returns the breakpoint at the last line of mouse activity in the ruler or <code>null</code> if none.
	 * 
	 * @return breakpoint associated with activity in the ruler or <code>null</code>
	 */
	protected IBreakpoint getBreakpoint()
	{
		IBreakpoint bp = super.getBreakpoint();
		
		if ( bp == null )
		{
			ITextEditor editor = getEditor();
			IEGLEditor eglEditor = editor == null
					? null
					: (IEGLEditor)editor.getAdapter( IEGLEditor.class );
			IEditorInput input = editor.getEditorInput();
			if ( eglEditor != null && input instanceof IStorageEditorInput )
			{
				int line = getVerticalRulerInfo().getLineOfLastMouseButtonActivity();
				if ( line != -1 )
				{
					Statement stmt = BreakpointUtils.getStatementNode( eglEditor, line );
					if ( stmt != null )
					{
						line = eglEditor.getLineAtOffset( stmt.getOffset() ) + 1;
						try
						{
							IStorage storage = ((IStorageEditorInput)input).getStorage();
							if ( storage instanceof IResource )
							{
								IEGLElement element = BreakpointUtils.getElement( (IResource)storage );
								if ( element != null )
								{
									String typeName = BreakpointUtils.getTypeName( element );
									if ( typeName.length() > 0 )
									{
										IResource resource = BreakpointUtils.getResource( element );
										bp = BreakpointUtils.eglLineBreakpointExists( resource, typeName, line );
									}
								}
							}
						}
						catch ( CoreException ce )
						{
						}
					}
				}
			}
		}
		
		return bp;
	}
}
