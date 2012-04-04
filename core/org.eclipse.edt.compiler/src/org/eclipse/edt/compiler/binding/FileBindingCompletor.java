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
package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.PackageDeclaration;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;


/**
 * @author svihovec
 *
 */
public class FileBindingCompletor extends AbstractBinder {

	private FileBinding fileBinding;
	private IProblemRequestor problemRequestor;
	
	private List partImportDeclarations = new ArrayList();
	
	public FileBindingCompletor(Scope currentScope, FileBinding fileBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super(currentScope, fileBinding, dependencyRequestor, compilerOptions);
		this.fileBinding = fileBinding;
		this.problemRequestor = problemRequestor;
	}
	
	public boolean visit(File file) {
		// TODO Is this how we want to set the default package on the file binding?
		if(!file.hasPackageDeclaration()){
			IPackageBinding defaultPackage = currentScope.getEnvironmentScope().getRootPackageBinding();
			fileBinding.setDeclaringPackage(defaultPackage);
		}
		return true;
	}
	
	public void endVisit(File file){
		processPartImportDeclarations();
	    fileBinding.setValid(true); 
	}
	
	private void processPartImportDeclarations(){
		HashMap importedParts = new HashMap();
		for (Iterator iter = partImportDeclarations.iterator(); iter.hasNext();) {
			ImportDeclaration importDeclaration = (ImportDeclaration) iter.next();
			Name name = importDeclaration.getName();
			try {
	            ITypeBinding partBinding = bindTypeName(name);
	            
	            if((fileBinding.getDeclaringPackage() != null && fileBinding.getDeclaringPackage().getPackageName() == partBinding.getPackageName()) || !((IPartBinding)partBinding).isPrivate()){
	            	if(importedParts.containsKey(partBinding.getName())){
	            		ITypeBinding resolvedBinding = (ITypeBinding)importedParts.get(partBinding.getName());
	            		
	            		if(resolvedBinding.getPackageName() != partBinding.getPackageName()){
	    					problemRequestor.acceptProblem(importDeclaration, IProblemRequestor.IMPORT_COLLISION, new String[] {name.getCanonicalName()});
	    				}else{
	    					//TODO: Warning - unnecessary import
	    				}
	            	}else{
	            		fileBinding.getPartBindings().add(partBinding);
	            		importedParts.put(partBinding.getName(), partBinding);
	            	}
	            }else{
	                problemRequestor.acceptProblem(importDeclaration, IProblemRequestor.TYPE_CANNOT_BE_RESOLVED, new String[] {name.getCanonicalName()});
	            }
	        } catch (ResolutionException e) {
	        	problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());                
	        }
		}		
	}
	
	public boolean visit(ImportDeclaration importDeclaration) {
		Name name = importDeclaration.getName();
        if(importDeclaration.isOnDemand()) {
            try {
                IPackageBinding packageBinding = bindPackageName(name);
                
                if(!fileBinding.getPackageBindings().contains(packageBinding)){
                    fileBinding.getPackageBindings().add(packageBinding);
                }
                // TODO log warning here
            } catch (ResolutionException e) {
                problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());                
            }
        }
        else {
            partImportDeclarations.add(importDeclaration);
    	}

        return false;
    }
	
	public boolean visit(PackageDeclaration packageDeclaration) {
    	try {
            IPackageBinding packageBinding = bindPackageName(packageDeclaration.getName());
            fileBinding.setDeclaringPackage(packageBinding);
        } catch (ResolutionException e) {
        	// Do nothing - message will be put out by validation                
        }
        return false;
    }
	
	public boolean visit(Part part){
		return false;
	}
}
