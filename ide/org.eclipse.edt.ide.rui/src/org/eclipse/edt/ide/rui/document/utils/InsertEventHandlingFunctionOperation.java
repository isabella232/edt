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

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;

import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;

/**
 * Insert a new widget into the RUIHandler
 */
public class InsertEventHandlingFunctionOperation {

	private IEGLDocument currentDocument;
	private IFile currentFile;
	
	public InsertEventHandlingFunctionOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
	
	public void insertFunction(String eventType){
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.open(null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				final org.eclipse.edt.compiler.core.ast.File fileAST = currentDocument.getNewModelEGLFile();
				String partName = new Path(currentFile.getName()).removeFileExtension().toString();
				final Part part = getPart(fileAST, partName);
				addFunction(eventType, fileAST, part);
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Insert Event Handler: Error inserting function", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(Exception e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Insert Event Handler: Error creating working copy", e));
		}
	}
	
	private Part getPart(org.eclipse.edt.compiler.core.ast.File fileAST, String partName) {
		Part part = null;
		List parts = fileAST.getParts();	
		for (Iterator iter = parts.iterator(); iter.hasNext();) {
			Part nextPart = (Part) iter.next();
			if(nextPart.getIdentifier() == InternUtil.intern(partName)){
				part = nextPart;
				break;
			}
		}
		return part;
	}
	
	private void addFunction(String functionName, org.eclipse.edt.compiler.core.ast.File fileAST, Part part) throws EGLModelException, MalformedTreeException, BadLocationException{
		ASTRewrite rewrite = ASTRewrite.create(fileAST);
		String functionText = "function " + functionName + "(event Event in)" + getLineDelimiter(currentDocument) + "\t" + getLineDelimiter(currentDocument) + "end";
		rewrite.addFunction(part, functionText);
		rewrite.rewriteAST(currentDocument).apply(currentDocument);
	}
	
	private static String getLineDelimiter(IDocument document) throws BadLocationException {
		String lineDelimiter = document.getLineDelimiter(0);
		return lineDelimiter == null ? System.getProperty("line.separator") : lineDelimiter;
	}
}
