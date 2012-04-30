/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author winghong
 */
public class FileScope extends Scope {
    
	private static final List systemPackages = new ArrayList();
	
	protected FileBinding fileBinding;
	private IPackageBinding[] importedPackages;
    private Map importedTypes = Collections.EMPTY_MAP;
    private IDependencyRequestor dependencyRequestor;
  
    public FileScope(Scope parentScope, FileBinding fileBinding, IDependencyRequestor dependencyRequestor) {
        super(parentScope);
        this.fileBinding = fileBinding;
        this.dependencyRequestor = dependencyRequestor;
    
        List allPackages = new ArrayList();
        allPackages.addAll(fileBinding.getPackageBindings());
        allPackages.addAll(systemPackages);
        
        importedPackages = (IPackageBinding[])allPackages.toArray(new IPackageBinding[fileBinding.getPackageBindings().size()]);
        
        List partBindings = fileBinding.getPartBindings();
        if(partBindings.size() > 0) {
            importedTypes = new HashMap();
        }
        for(Iterator iter = partBindings.iterator(); iter.hasNext();) {
            IPartBinding partBinding = (IPartBinding) iter.next();
            importedTypes.put(partBinding.getName(), partBinding);
        }
    }

//    public IDataBinding findData(String simpleName) {
//        return null;
//    }

    public IPackageBinding findPackage(String simpleName) {
    	return parentScope.findPackage(simpleName);
    }

    public IPartBinding findType(String simpleName) {
        dependencyRequestor.recordSimpleName(simpleName);
        IPartBinding result = null;
        
        // First check the single type imports
        result = findTypeUsingSingleTypeImports(simpleName);
        if(result != null){ return result; };
        
        // Then check the declaring package
        result = findTypeInDeclaringPackage(simpleName);
        if(result != null){ return result; };
        
        // Then check the on demand imports
        return findTypeInOnDemandImports(simpleName);
        
    }
    
    protected IPartBinding findTypeUsingSingleTypeImports(String simpleName){
    	if(importedTypes != null) {
            IPartBinding result = (IPartBinding) importedTypes.get(simpleName);
            if(result != null){
                // If this file scope's file binding is at level 2, its single types may be at level 1.  If it is at level 1, we need 
                // to let the file binding's environment try to replace it with a level two.
                if(!result.isValid()){
                    IPartBinding tempResult = fileBinding.getEnvironment().getPartBinding(result.getPackageName(), result.getName());
                    if(tempResult != result){
                        importedTypes.remove(simpleName);
                        importedTypes.put(simpleName, tempResult);
                        result = tempResult;
                    }
                }
                return result;
            }
        }
        return null;
    }
    
    protected IPartBinding findTypeInDeclaringPackage(String simpleName){
    	IPackageBinding declaringPackage = fileBinding.getDeclaringPackage();
        if(declaringPackage != null){
        	return declaringPackage.resolveType(simpleName);
       }
        return null;
    }
    
    protected IPartBinding findTypeInOnDemandImports(String simpleName){
    	IPartBinding foundType = null;
    	IPackageBinding declaringPackage = fileBinding.getDeclaringPackage();
        for(int i = 0; i < importedPackages.length; i++) {
            IPartBinding temp = importedPackages[i].resolveType(simpleName);
            if(temp != null) {
                if(foundType != null){
                    if((declaringPackage != null && NameUtile.equals(((IPartBinding)temp).getPackageName(), declaringPackage.getPackageName())) || !((IPartBinding)temp).isPrivate()){
                    	throw AmbiguousPartError;
                    }  
                }else{
                    if((declaringPackage != null && NameUtile.equals(((IPartBinding)temp).getPackageName(), declaringPackage.getPackageName())) || !((IPartBinding)temp).isPrivate()){
                        foundType = temp;
                    }
                }                
            }
        }
        return foundType;
    }
    
 }
