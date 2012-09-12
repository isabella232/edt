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
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;

/**
 * Insert a new widget into the RUIHandler
 */
public class InsertFieldOperation {

	private IEGLDocument currentDocument;
	private IFile currentFile;
	
	public InsertFieldOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
	
	public void insertField(String fieldTypePackage, String fieldName, String fieldType, String template, int index){
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.open(null);
			sharedWorkingCopy.reconcile(false, null);
		
			try{
				final org.eclipse.edt.compiler.core.ast.File fileAST = currentDocument.getNewModelEGLFile();
				String partName = new Path(currentFile.getName()).removeFileExtension().toString();
				final Part part = getPart(fileAST, partName);
				addField(fieldTypePackage, fieldName, fieldType, template, fileAST, part, index);
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Insert Field: Error inserting field", e));
			}finally{
				sharedWorkingCopy.destroy();					
			}
		}catch(Exception e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Insert Event Field: Error creating working copy", e));
		}
	}
	
	private Part getPart(org.eclipse.edt.compiler.core.ast.File fileAST, String partName) {
		Part part = null;
		List parts = fileAST.getParts();	
		for (Iterator iter = parts.iterator(); iter.hasNext();) {
			Part nextPart = (Part) iter.next();
			if(NameUtile.equals(nextPart.getIdentifier(), NameUtile.getAsName(partName))){
				part = nextPart;
				break;
			}
		}
		return part;
	}
	
	private void addField(String fieldTypePackage, String fieldName, String fieldType, String template, org.eclipse.edt.compiler.core.ast.File fileAST, Part part, int index) throws EGLModelException, MalformedTreeException, BadLocationException{
		boolean isRequireQualifiedImport = false;
		
		if(fieldTypePackage != null && fieldTypePackage.length() > 0){
			TypeNameResolver typeNameResolver = new TypeNameResolver(currentFile);
			typeNameResolver.resolveTypeName(fieldTypePackage, fieldType);
			
			if(typeNameResolver.requiresQualifiedImport()){
				isRequireQualifiedImport = true;
			}
			
			String typeName = typeNameResolver.getTypeName();
			if(typeName != null && typeName.contains(".")){
				template = fieldTypePackage + "." + template;
			}
		}
		
		ASTRewrite rewrite = ASTRewrite.create(fileAST);
		rewrite.addClassFieldAtIndex(part, fieldName, template, null, index);
		rewrite.rewriteAST(currentDocument).apply(currentDocument);
		
		if(isRequireQualifiedImport){
			AddImportOperation addImportOperation = new AddImportOperation(currentDocument, currentFile);
			addImportOperation.addFullyQualifiedImport(fieldTypePackage + "." + fieldType);
		}
	}
}
