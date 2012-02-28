/*******************************************************************************
 * Copyright © 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.compiler.workingcopy;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ErrorCorrectingParser;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Lexer;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.ide.core.internal.lookup.AbstractASTManager;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IWorkingCopy;

/**
 * The WorkingCopyASTManager is separate from the single ASTManager for the workspace.  This is due to the fact that the WorkingCopyProjectInfo is updated in Pre-Build, 
 * and the ASTMAnager is updated on Pre-Build.  So that we do not have to worry about what order the events are fired in, the WorkingCopyASTManager contains its own cache of 
 * the parsed files in the workspace.
 * 
 * @author svihovec
 *
 */
public class WorkingCopyASTManager extends AbstractASTManager {

	private static final WorkingCopyASTManager INSTANCE = new WorkingCopyASTManager();
	
	private HashMap fileCache = new HashMap();
	
	private WorkingCopyASTManager(){}
	private IPartASTRequestor fileASTRequestor = null;
	
	public static WorkingCopyASTManager getInstance(){
		return INSTANCE;
	}
	
	public void setPartASTRequestor(IPartASTRequestor fileASTRequestor){
		this.fileASTRequestor = fileASTRequestor;
	}
	
	public void reportNestedFunctions(Node node,final IFile file){
		if (fileASTRequestor != null){
			node.accept(new AbstractASTVisitor(){
				public boolean visit(NestedFunction function){
					fileASTRequestor.addPartAST(file, function);
					return false;
				}
				
				public boolean visit(NestedForm form){
					fileASTRequestor.addPartAST(file, form);
					return false;
				}
			});
		}
	}
	
	public File getFileAST(IFile file) {
    	File cachedFile = (File)fileCache.get(file);
    	if(cachedFile != null){
    		return cachedFile;
    	}else{
    		return super.getFileAST(file);
    	}
    }
	
	public File getFileAST(IWorkingCopy workingCopy){
		try {
			String contents = ((IEGLFile)workingCopy).getBuffer().getContents();
 
        	ErrorCorrectingParser parser = new ErrorCorrectingParser(new Lexer(new BufferedReader(new StringReader(contents))));
           	File cachedFile = (File)parser.parse().value;
           	fileCache.put(((IEGLFile)workingCopy.getOriginalElement()).getResource(), cachedFile);
           	
           	return cachedFile;
        } catch (Exception e) {
           throw new BuildException(e);
        }    	
	}
    
    protected void doGetPartAST(IFile declaringFile, Part result) {
    	if (fileASTRequestor != null){
   			fileASTRequestor.addPartAST(declaringFile, result);
   		}
	}
    
    public void clear(){
    	fileCache.clear();
    	super.clear();
    }

	public void resetWorkingCopies() {
		fileCache.clear();	
		fileASTRequestor = null;
	}
}
