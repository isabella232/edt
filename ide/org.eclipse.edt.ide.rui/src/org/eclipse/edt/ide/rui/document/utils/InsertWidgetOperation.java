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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;

import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * Insert a new widget into the RUIHandler
 * 
 */
public class InsertWidgetOperation {
	
	private IEGLDocument currentDocument;
	private IFile currentFile;
	
	public InsertWidgetOperation(IEGLDocument currentDocument, IFile currentFile){
		this.currentDocument = currentDocument;		
		this.currentFile = currentFile;
	}
		
	public void insertWidget(final String fieldName, final String widgetPackage, final String widgetType, final String template, final int index){
		try{
			addClassFieldAtIndex(index, fieldName, widgetPackage, widgetType, template);
		}catch(Exception e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Insert Widget: Error creating working copy", e));
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
	
	private void addClassFieldAtIndex(int index, String fieldName, String widgetPackage, String widgetType, String template) throws EGLModelException, MalformedTreeException, BadLocationException{
		if(template == null){
			if(widgetPackage.length() == 0){
				// The widget is in the default package - you cannot import a part from the default package, so the 
				// current RUIHandler must be in the default package
				template = widgetType + "{}";
			}else{
				// The widget is in the non-default package
				TypeNameResolver typeNameResolver = new TypeNameResolver(currentFile);
				typeNameResolver.resolveTypeName(widgetPackage, widgetType);
				template = typeNameResolver.getTypeName() + "{}";
				
				if(typeNameResolver.requiresQualifiedImport()){
					AddImportOperation addImportOp = new AddImportOperation(currentDocument, currentFile);
					addImportOp.addFullyQualifiedImport(widgetPackage + "." + widgetType);
				}
			}
		}else{
			//generate from palette
			if(template.length() >= "${typename}".length() && template.substring(0, "${typeName}".length()).equalsIgnoreCase("${typeName}")){
				if(widgetPackage.length() == 0){
					// The widget is in the default package - you cannot import a part from the default package, so the 
					// current RUIHandler must be in the default package
					template = widgetType + template.substring("${typeName}".length());
				}else{
					TypeNameResolver typeNameResolver = new TypeNameResolver(currentFile);
					typeNameResolver.resolveTypeName(widgetPackage, widgetType);
					template = typeNameResolver.getTypeName() + template.substring("${typeName}".length());
					
					if(typeNameResolver.requiresQualifiedImport()){
						AddImportOperation addImportOp = new AddImportOperation(currentDocument, currentFile);
						addImportOp.addFullyQualifiedImport(widgetPackage + "." + widgetType);
					}
				}
			} else {
				//generate from EGL Data View
				fieldName = "\n\t" + fieldName; //insert a blank line before the inserted codes.
			}
		}
		
		//handle ${typeName:<fullyQualifiedTypeName>}
		List imports = new ArrayList();
		template = DocumentUtil.handleTypeNameVaraibles(currentFile, currentDocument, template, imports);
		DocumentUtil.addQualifiedImports(currentFile, currentDocument, imports);
		
		org.eclipse.edt.compiler.core.ast.File fileAST = currentDocument.getNewModelEGLFile();
		String partName = new Path(currentFile.getName()).removeFileExtension().toString();
		Part part = getPart(fileAST, partName);
		ASTRewrite rewrite = ASTRewrite.create(fileAST);
		rewrite.addClassFieldAtIndex(part, fieldName, template, null, index);
		rewrite.rewriteAST(currentDocument).apply(currentDocument);
	}
	

}
