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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AmbiguousFunctionBinding;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */
public class FileScope extends Scope {
    
	private static final List systemPackages = new ArrayList();
	
	protected FileBinding fileBinding;
	private IPackageBinding[] importedPackages;
    private Map importedTypes = Collections.EMPTY_MAP;
    private IDependencyRequestor dependencyRequestor;
    private boolean returnTopLevelFunctions = true;
  
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

    public IDataBinding findData(String simpleName) {
        return IBinding.NOT_FOUND_BINDING;
    }

    public IFunctionBinding findFunction(String simpleName) {
    	if(returnTopLevelFunctions) {
	    	ITypeBinding type = findType(simpleName);
	        if(type == IBinding.NOT_FOUND_BINDING){
	            return IBinding.NOT_FOUND_BINDING;
	        }
	        else if(type == ITypeBinding.AMBIGUOUS_TYPE){
	        	return AmbiguousFunctionBinding.getInstance();
	        }
	        else if(type.getKind() != ITypeBinding.FUNCTION_BINDING) {
	            return IBinding.NOT_FOUND_BINDING;
	        }
	        
	        IFunctionBinding functionBinding = (IFunctionBinding) type;
	        dependencyRequestor.recordTopLevelFunctionBinding(functionBinding);
	        return functionBinding;
    	}
    	else {
    		return IBinding.NOT_FOUND_BINDING;
    	}
    }

    public IPackageBinding findPackage(String simpleName) {
    	return parentScope.findPackage(simpleName);
    }

    public ITypeBinding findType(String simpleName) {
        dependencyRequestor.recordSimpleName(simpleName);
        ITypeBinding result = null;
        
        // First check the single type imports
        result = findTypeUsingSingleTypeImports(simpleName);
        if(result != null){ return result; };
        
        // Then check the declaring package
        result = findTypeInDeclaringPackage(simpleName);
        if(result != null){ return result; };
        
        // Then check the on demand imports
        result = findTypeInOnDemandImports(simpleName);
        
        return result == null ? IBinding.NOT_FOUND_BINDING : result;
    }
    
    protected ITypeBinding findTypeUsingSingleTypeImports(String simpleName){
    	if(importedTypes != null) {
            ITypeBinding result = (ITypeBinding) importedTypes.get(simpleName);
            if(result != null){
                // If this file scope's file binding is at level 2, its single types may be at level 1.  If it is at level 1, we need 
                // to let the file binding's environment try to replace it with a level two.
                if(!result.isValid()){
                    ITypeBinding tempResult = fileBinding.getEnvironment().getPartBinding(result.getPackageName(), result.getName());
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
    
    protected ITypeBinding findTypeInDeclaringPackage(String simpleName){
    	IPackageBinding declaringPackage = fileBinding.getDeclaringPackage();
        if(declaringPackage != null){
        	ITypeBinding result = declaringPackage.resolveType(simpleName);
	        if(result != IBinding.NOT_FOUND_BINDING){
	           return result;
	        }
        }
        return null;
    }
    
    protected ITypeBinding findTypeInOnDemandImports(String simpleName){
    	ITypeBinding result = null;
    	IPackageBinding declaringPackage = fileBinding.getDeclaringPackage();
        for(int i = 0; i < importedPackages.length; i++) {
            ITypeBinding temp = importedPackages[i].resolveType(simpleName);
            if(temp != IBinding.NOT_FOUND_BINDING) {
                if(result != null){
                    if((declaringPackage != null && ((IPartBinding)temp).getPackageName() == declaringPackage.getPackageName()) || !((IPartBinding)temp).isPrivate()){
                        if(result == IBinding.NOT_FOUND_BINDING){
                            result = temp;
                        }else if (result.getPackageName() != temp.getPackageName()){
                            return ITypeBinding.AMBIGUOUS_TYPE;
                        }
                    }  
                }else{
                    if((declaringPackage != null && ((IPartBinding)temp).getPackageName() == declaringPackage.getPackageName()) || !((IPartBinding)temp).isPrivate()){
                        result = temp;
                    }else{
                        // invisible private part found
                        result = IBinding.NOT_FOUND_BINDING;
                    }
                }                
            }
        }
        return result;
    }
    
	public void stopReturningTopLevelFunctions() {
		super.stopReturningTopLevelFunctions();
		returnTopLevelFunctions = false;
	}
	
	@Override
	public boolean isReturningTopLevelFunctions() {
		return returnTopLevelFunctions;
	}
	
	public void startReturningTopLevelFunctions() {
		super.startReturningTopLevelFunctions();
		returnTopLevelFunctions = true;
	}
 }
