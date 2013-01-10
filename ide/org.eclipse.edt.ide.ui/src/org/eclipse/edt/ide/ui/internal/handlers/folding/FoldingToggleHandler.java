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
package org.eclipse.edt.ide.ui.internal.handlers.folding;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.core.resources.IResource;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.EDTUIPreferenceConstants;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;

public class FoldingToggleHandler extends FoldingHandler {
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Initialize editor 
		IEditorPart editor = getCurrentActiveEditor(event);
		
		if( editor instanceof EGLEditor ) {
			fEditor = (EGLEditor)editor;
			if(editor != null)
			{	
				IEditorInput editorInput = editor.getEditorInput();
				if (editorInput instanceof IFileEditorInput) {
					IResource resource = ((IFileEditorInput) editorInput).getFile();
					IEGLElement element = EGLCore.create(resource);
					fSite = editor.getSite();
					fSelection = new StructuredSelection( element );
				}			
		    }			
		}
		
		if( fSelection != null )
		{
			IPreferenceStore store = EDTUIPlugin.getDefault().getPreferenceStore();
			boolean current= store.getBoolean(EDTUIPreferenceConstants.EDITOR_FOLDING_ENABLED);
			store.setValue(EDTUIPreferenceConstants.EDITOR_FOLDING_ENABLED, !current);
			
			Command command = event.getCommand();
			HandlerUtil.toggleCommandState(command);
		}
		return null;
	}
	
	public boolean isEnabled(){
		ICommandService service = (ICommandService)PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = service.getCommand("org.eclipse.edt.ide.ui.ruler.folding.toggle");
		State state = command.getState("org.eclipse.ui.commands.toggleState");
		IPreferenceStore store = EDTUIPlugin.getDefault().getPreferenceStore();
		state.setValue(store.getBoolean(EDTUIPreferenceConstants.EDITOR_FOLDING_ENABLED));
		
		return(true);
	}
	
}
