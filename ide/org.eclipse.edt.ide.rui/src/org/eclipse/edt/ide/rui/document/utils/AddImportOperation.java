/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
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
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;

import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;

/**
 * Insert a new widget into the RUIHandler
 */
public class AddImportOperation {

	private IEGLDocument currentDocument;
	private IFile currentFile;
	
	public AddImportOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
	
	public void addFullyQualifiedImport(String importName){
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.open(null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				final org.eclipse.edt.compiler.core.ast.File fileAST = currentDocument.getNewModelEGLFile();
				
				addImport(importName, fileAST);
			}catch(Exception e){
				// TODO
				e.printStackTrace();
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void addImport(String importName, org.eclipse.edt.compiler.core.ast.File fileAST) throws EGLModelException, MalformedTreeException, BadLocationException{
		ASTRewrite rewrite = ASTRewrite.create(fileAST);
		rewrite.addImport(fileAST, importName, false);
		rewrite.rewriteAST(currentDocument).apply(currentDocument);
	}
}
