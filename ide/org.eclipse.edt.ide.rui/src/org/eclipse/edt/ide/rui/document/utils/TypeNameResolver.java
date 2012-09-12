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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.ast.rewrite.ASTRewrite;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.model.EGLFile;
import org.eclipse.edt.ide.core.internal.model.document.EGLDocument;
import org.eclipse.edt.ide.core.internal.utils.Util;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.rui.internal.Activator;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;

/**
 * Insert a new widget into the RUIHandler
 * 
 */
public class TypeNameResolver {

	private class BoundFieldLocator implements IWorkingCopyCompileRequestor {
		private ClassDataDeclaration field = null;
		private String fieldName;
		
		public BoundFieldLocator(String fieldName){
			this.fieldName = fieldName;
		}
		
		public void acceptResult(WorkingCopyCompilationResult result) {
			List contents = ((Handler)result.getBoundPart()).getContents();
			for (Iterator iterator = contents.iterator(); iterator.hasNext();) {
				Node nextNode = (Node) iterator.next();
				nextNode.accept(new DefaultASTVisitor(){
					public boolean visit(ClassDataDeclaration classDataDeclaration) {
						if(classDataDeclaration.getNames().size() == 1){
							if(fieldName.equals(((Name)classDataDeclaration.getNames().get(0)).getIdentifier())){
								field = classDataDeclaration;
							}
						}
						return false;
					}
				});
				if(field != null){
					break;
				}
			}			
		}
		
		public ClassDataDeclaration getField(){
			return field;
		}
	}
	
	private static final String TEMP_FIELD_NAME = "eze$$temp";
	private IFile currentFile;
	private String typeName;
	private boolean qualifiedImportRequired = false;
	
	public TypeNameResolver(IFile currentFile){
		this.currentFile = currentFile;
	}
	
	public void resolveTypeName(final String widgetPackageName, final String widgetTypeName){
		if(widgetPackageName.length() == 0){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Type Name Resolver: Cannot resolve a type name if it is in a default package"));
			return;
		}
		try{
			IEGLFile modelFile = (IEGLFile)EGLCore.create(currentFile);
			IEGLFile sharedWorkingCopy = (IEGLFile)modelFile.getSharedWorkingCopy(null, EGLUI.getBufferFactory(), null);
			sharedWorkingCopy.open(null);
			sharedWorkingCopy.reconcile(false, null);
			
			// Create a temporary working copy that is not tied to the editor
			IEGLFile tempWorkingCopy = (IEGLFile)modelFile.getWorkingCopy();
			
			// Set the contents of the temporary working copy to match the current (possibly unsaved) contents of the editor
			tempWorkingCopy.getBuffer().setContents(sharedWorkingCopy.getBuffer().getContents());
			
			try{
				String partName = new Path(currentFile.getName()).removeFileExtension().toString();
				doResolveTypeName(modelFile.getEGLProject().getProject(), currentFile, tempWorkingCopy, ((EGLFile)modelFile).getPackageName(), partName, widgetPackageName, widgetTypeName);
			}catch(Exception e){
				Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Type Name Resolver: Error resolving type name", e));
			}finally{
				tempWorkingCopy.destroy();
				sharedWorkingCopy.destroy();					
			}
		}catch(Exception e){
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.PLUGIN_ID, "Type Name Resolver: Error resolving type name", e));
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
	
	private void doResolveTypeName(IProject project, IFile file, IEGLFile workingCopy, String[] partPackageName, String partName, String widgetPackageName, String widgetTypeName) throws EGLModelException, MalformedTreeException, BadLocationException{
		// Create a temporary document
		IEGLDocument tempDocument = new EGLDocument(workingCopy.getBuffer().getContents());
		org.eclipse.edt.compiler.core.ast.File fileAST = tempDocument.getNewModelEGLFile();
		Part part = getPart(fileAST, partName);
		
		// add a temp field to the document using just a type name
		ASTRewrite rewrite = ASTRewrite.create(fileAST);
		rewrite.addClassFieldAtIndex(part, TEMP_FIELD_NAME, widgetTypeName, null, 0);
		rewrite.rewriteAST(tempDocument).apply(tempDocument);
		
		// Copy the updated document contents back to the working copy
		workingCopy.getBuffer().setContents(tempDocument.get());
		
		// Check to see if the widget we just inserted can be resolved to the proper type
		BoundFieldLocator locator = new BoundFieldLocator(TEMP_FIELD_NAME);
		WorkingCopyCompiler.getInstance().compilePart(project, Util.stringArrayToQualifiedName(partPackageName), file, new IWorkingCopy[]{workingCopy}, partName, locator);
		if(locator.getField() != null){
			if(locator.getField().getType().resolveType() != null && locator.getField().getType().resolveType().getClassifier() != null){
				if(!NameUtile.equals(locator.getField().getType().resolveType().getClassifier().getPackageName(), NameUtile.getAsName(widgetPackageName))){
					// We resolved a simple type name to the wrong part, which means we need to fully qualify the field
					typeName = getFullyQualifiedTypeName(widgetPackageName, widgetTypeName);
				}else{
					// we resolved the correct type with a simple type name
					typeName = widgetTypeName;
				}
				qualifiedImportRequired = false;
			}else{
				// We didn't resolve the simple type name to any parts, which means we can use a simple type and a fully qualified import
				typeName = widgetTypeName;
				
				// add fully qualified import
				qualifiedImportRequired = true;
			}
		}else{
			// an error occurred, default to a fully qualified type
			typeName = getFullyQualifiedTypeName(widgetPackageName, widgetTypeName);
			qualifiedImportRequired = false;
		}
	}
	
	public String getTypeName(){
		return typeName;
	}
	
	public boolean requiresQualifiedImport(){
		return qualifiedImportRequired;
	}
	
	private String getFullyQualifiedTypeName(String widgetPackageName, String widgetTypeName){
		return widgetPackageName + "." + widgetTypeName;
	}
}
