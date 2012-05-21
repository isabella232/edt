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
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author winghong
 */
public class FileScope extends Scope {
    	
	protected FileBinding fileBinding;
	private List<IPackageBinding> importedPackages;
    private Map<String, Part> importedTypes = Collections.emptyMap();
    private IDependencyRequestor dependencyRequestor;
  
    public FileScope(Scope parentScope, FileBinding fileBinding, IDependencyRequestor dependencyRequestor) {
        super(parentScope);
        this.fileBinding = fileBinding;
        this.dependencyRequestor = dependencyRequestor;
    
        
        importedPackages = fileBinding.getPackageBindings();
        
        List<Part> partBindings = fileBinding.getPartBindings();
        if(partBindings.size() > 0) {
            importedTypes = new HashMap<String, Part>();
        }
        for(Part partBinding : partBindings) {
            importedTypes.put(partBinding.getName(), partBinding);
        }
    }

//    public IDataBinding findData(String simpleName) {
//        return null;
//    }

    public IPackageBinding findPackage(String simpleName) {
    	return parentScope.findPackage(simpleName);
    }

    public List<Type> findType(String simpleName) {
        dependencyRequestor.recordSimpleName(simpleName);
        Part result = null;
        
        // First check the single type imports
        result = findTypeUsingSingleTypeImports(simpleName);
        if(result != null){
        	List<Type> list = new ArrayList<Type>();
        	list.add(result);
        	return list;
        };
        
        // Then check the declaring package
        result = findTypeInDeclaringPackage(simpleName);
        if(result != null){
        	List<Type> list = new ArrayList<Type>();
        	list.add(result);
        	return list;
        };
        
        // Then check the on demand imports
        return findTypeInOnDemandImports(simpleName);
        
    }
    
    protected Part findTypeUsingSingleTypeImports(String simpleName){
    	if(importedTypes != null) {
            Part result = importedTypes.get(simpleName);
            if(result != null){
                // If this file scope's file binding is at level 2, its single types may be at level 1.  If it is at level 1, we need 
                // to let the file binding's environment try to replace it with a level two.
                if(!BindingUtil.isValid(result)){
                    Part tempResult = BindingUtil.getPart(fileBinding.getEnvironment().getPartBinding(result.getPackageName(), result.getName()));
                    
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
    
    protected Part findTypeInDeclaringPackage(String simpleName){
    	IPackageBinding declaringPackage = fileBinding.getDeclaringPackage();
        if(declaringPackage != null){
        	return BindingUtil.getPart(declaringPackage.resolveType(simpleName));
       }
        return null;
    }
    
    protected List<Type> findTypeInOnDemandImports(String simpleName){
    	IPackageBinding declaringPackage = fileBinding.getDeclaringPackage();
    	List<Type> result = new ArrayList<Type>();
        for(IPackageBinding pkgBinding : importedPackages) {
            Part temp = BindingUtil.getPart(pkgBinding.resolveType(simpleName));
            if(temp != null) {
                if((declaringPackage != null && NameUtile.equals(temp.getPackageName(), declaringPackage.getPackageName())) || !BindingUtil.isPrivate(temp)){
                	result.add(temp);
                }  
             }
        }
        if (result.isEmpty()) {
        	return null;
        }
        return result;
    }

	@Override
	public List<Member> findMember(String simpleName) {
		return null;
	}
    
 }
