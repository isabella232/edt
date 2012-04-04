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
package org.eclipse.edt.ide.ui.internal.quickfix.proposals;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.ui.EDTUIPlugin;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.EGLUIStatus;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;


public abstract class ASTRewriteCorrectionProposal extends EGLFileCorrectionProposal {

	private IEGLFile modelFile;

	public ASTRewriteCorrectionProposal(String name, IEGLFile eglFile, int relevance, Image image, IEGLDocument document) {
		super(name, eglFile, relevance, image, document);
		modelFile = eglFile;
		
	}

	protected void addEdits(IDocument document, TextEdit editRoot) throws CoreException {
		super.addEdits(document, editRoot);
		ASTRewrite rewrite= getRewrite();
		if (rewrite != null) {
			try {
				TextEdit edit= rewrite.rewriteAST(document);
				editRoot.addChild(edit);
			} catch (IllegalArgumentException e) {
				throw new CoreException(EGLUIStatus.createError(IStatus.ERROR, e));
			}
		}
	}

	protected void performChange(IEditorPart part, IDocument document) throws CoreException{
		ASTRewrite fRewrite = getRewrite();
		if (fRewrite != null) {
			try {
				IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
				sharedWorkingCopy.open(null);
				sharedWorkingCopy.reconcile(false, null);
				
				try{
					TextEdit edit= fRewrite.rewriteAST(document);
					edit.apply(document);
				} catch(Exception e){
					EDTUIPlugin.log(new Status(Status.ERROR, EDTUIPlugin.PLUGIN_ID, "AST Rewrite for quick fix error ", e));
				}finally{
					sharedWorkingCopy.destroy();					
				}
			
			}catch (Exception e) {
				EDTUIPlugin.log(new Status(Status.ERROR, EDTUIPlugin.PLUGIN_ID, "AST Rewrite for quick fix error ", e));
			}
		}
	}
	
	abstract protected ASTRewrite getRewrite();
	
}
