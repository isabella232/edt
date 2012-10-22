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
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.internal.core.dependency.NullDependencyRequestor;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author winghong
 */
public class FileASTScope extends FileScope {
    
	private List importedTypeNames = new ArrayList();
	
	public FileASTScope(Scope parentScope, FileBinding fileBinding, File fileAST) {
        super(parentScope, fileBinding, NullDependencyRequestor.getInstance());
        
        List importDeclarations = fileAST.getImportDeclarations();
    	
    	for (Iterator iter = importDeclarations.iterator(); iter.hasNext();) {
			ImportDeclaration importDeclaration = (ImportDeclaration) iter.next();
			
			if(!importDeclaration.isOnDemand()) {
				importedTypeNames.add(importDeclaration);
			}
		}
    }

    public List<Type> findType(String simpleName) {
        // First check the single part imports from the AST
        if(importedTypeNames != null){
        	for (Iterator iter = importedTypeNames.iterator(); iter.hasNext();) {
				ImportDeclaration importDeclaration = (ImportDeclaration) iter.next();
				if(NameUtile.equals(importDeclaration.getName().getIdentifier(), simpleName)){
					IPartBinding temp = null;
	            	if(importDeclaration.getName().isQualifiedName()){
	            		temp = fileBinding.getEnvironment().getPartBinding(((QualifiedName)importDeclaration.getName()).getQualifier().getNameComponents(), importDeclaration.getName().getIdentifier());            	
	            	}else{
	            		temp = fileBinding.getEnvironment().getPartBinding(NameUtile.getAsName(""), importDeclaration.getName().getIdentifier());
	            	}
		            
	            	if(temp != null){
	    	            if((fileBinding.getDeclaringPackage() != null && NameUtile.equals(fileBinding.getDeclaringPackage().getPackageName(), temp.getPackageName())) || !((IPartBinding)temp).isPrivate()){
	    	            	List<Type> list = new ArrayList<Type>();
	    	            	list.add(BindingUtil.getPart(temp));
	    	            	return list;
	    	            }else{
	    	                // error - unreachable import
	    	            }
	            	}
				}
			}
        }
        
        // Then check the declaring package
        Part part = findTypeInDeclaringPackage(simpleName);
        if(part != null){
        	List<Type> list = new ArrayList<Type>();
        	list.add(part);
        	return list;
        };

        // Then check the on demand imports
        List<Type> results = findTypeInOnDemandImports(simpleName);
        if (results != null) {
        	return results;
        }
        
        //Check the implicit system package(s)
        return findTypeInImplicitSystemPackages(simpleName);
    }
 }
