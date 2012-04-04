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
package org.eclipse.edt.ide.rui.visualeditor.internal.editor;

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.text.undo.IDocumentUndoManager;
import org.eclipse.ui.PlatformUI;

public class EvEditorUndoManager {

	protected IDocumentUndoManager	_undoManager	= null;

	/**
	 * Handles registering compound changes with the undo manager.
	 */
	public EvEditorUndoManager( IDocument document ) {

		if( document != null )
			_undoManager = DocumentUndoManagerRegistry.getDocumentUndoManager( document );		
	}

	/**
	 * Called by the editor before a UI operation begins
	 */
	public void operationStarting() {
		if( _undoManager == null )
			return;

		_undoManager.beginCompoundChange();
	}

	/**
	 * Called by the editor after a UI operation ends.
	 * The label replaces the existing "Typing" label of "&Undo Typing".
	 */
	public void operationEnded( String strUndoLabel ) {
		if( _undoManager == null )
			return;
		
		_undoManager.endCompoundChange();
		
		// Change the undo menu item label from "Typing"
		//----------------------------------------------
		IUndoContext context = _undoManager.getUndoContext();
		AbstractOperation operation = (AbstractOperation)PlatformUI.getWorkbench().getOperationSupport().getOperationHistory().getUndoOperation( context );
		
		if( operation != null )
			operation.setLabel( strUndoLabel );
	}
}
