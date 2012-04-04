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
package org.eclipse.edt.ide.rui.document.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.text.edits.TextEdit;

import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IPart;

public class DeleteWidgetDefinitionOperation {
	public static final String DELETE_WIDGET_NAME_SAPERATOR = ":";
	private IEGLDocument currentDocument;
	private IFile currentFile;
	private TextEdit textEdit;
	
	public DeleteWidgetDefinitionOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;
		this.currentFile = currentFile;
	}
	public void deleteWidgetDefinition(final String fieldName){
		
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				final org.eclipse.edt.compiler.core.ast.File fileAST = currentDocument.getNewModelEGLFile();
				String partName = new Path(currentFile.getName()).removeFileExtension().toString();
				final IPart modelPart = sharedWorkingCopy.getPart(partName);
				final ASTRewrite rewrite = ASTRewrite.create(fileAST);
				EGLWidgetDefinitionDeleteStragegy deleteStrategy = new EGLWidgetDefinitionDeleteStragegy( currentDocument, modelPart, rewrite );
				deleteStrategy.deleteWidgetDefinition(fieldName);
				deleteStrategy.modifyFormManagerEntities();
				textEdit = deleteStrategy.getTextEdit();
				if ( textEdit != null ) {
					textEdit.apply(currentDocument);
				}
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Delete Widget Reference: Error deleting reference", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(EGLModelException e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Delete Widget Reference: Error creating working copy", e));
		}

	}

}
