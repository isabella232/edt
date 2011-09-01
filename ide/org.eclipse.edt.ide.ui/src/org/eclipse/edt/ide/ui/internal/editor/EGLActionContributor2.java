/*******************************************************************************
 * Copyright © 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.editor;

import org.eclipse.edt.ide.ui.internal.handlers.OpenOnSelectionHandler;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;

public class EGLActionContributor2 extends BasicTextEditorActionContributor{
	private OpenOnSelectionHandler openOnSelectionHandler;
	private EGLEditor eglEditor;
	
	public EGLActionContributor2(){
		openOnSelectionHandler = new OpenOnSelectionHandler();
	}
	
	@Override
	public void setActiveEditor(IEditorPart editorPart) {
		if(editorPart instanceof EGLEditor){
			eglEditor = (EGLEditor)editorPart;
		}
	}

	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);
		
//		ICommandService commandService = (ICommandService)PlatformUI.getWorkbench().getService(ICommandService.class);
//		Command toggleCommentCommand = commandService.getCommand("org.eclipse.edt.ide.ui.source.commentGroup.toggle");
		if(eglEditor != null){
			menuManager.add(this.getAction(eglEditor, "org.eclipse.edt.ide.ui.source.commentGroup.toggle"));
		}
		
//		commandService.getCategory("");
	}

}
