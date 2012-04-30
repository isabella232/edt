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

import org.eclipse.edt.compiler.binding.DataBindingWithImplicitQualifier;
import org.eclipse.edt.compiler.binding.DynamicDataBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public class TypeBindingScope extends Scope {
    
    private ITypeBinding typeBinding;
    private IDataBinding dataBinding;
    private boolean addImplicitQualifier;
    
    public TypeBindingScope(Scope parentScope, ITypeBinding typeBinding, IDataBinding dataBinding) {
        super(parentScope);
        this.typeBinding = typeBinding;
        this.dataBinding = dataBinding;
    }

    public TypeBindingScope(Scope parentScope, ITypeBinding typeBinding, IDataBinding dataBinding, boolean addImplicitQualifier) {
        this(parentScope, typeBinding, dataBinding);
        this.addImplicitQualifier = addImplicitQualifier;
    }

    public IDataBinding findData(String simpleName) {
        IDataBinding result = findDataInType(simpleName);
        
        if (result != null && result != IBinding.NOT_FOUND_BINDING) {
        	if (addImplicitQualifier && typeBinding.isPartBinding()) {
        		IPartBinding partBinding = (IPartBinding) typeBinding;
        		if (partBinding.getStaticPartDataBinding() != null) {
            		return new DataBindingWithImplicitQualifier(result, partBinding.getStaticPartDataBinding());
        		} 		
        	}
        	return result;
        }
        	
        
        return parentScope.findData(simpleName);
    }
    
    private IDataBinding findDataInType(String simpleName) {
    	if(typeBinding != null) {
	        if (typeBinding.isDynamicallyAccessible()) {
	            IPartBinding declaringPart = null;
	            if (dataBinding != null) {
	                declaringPart = dataBinding.getDeclaringPart();
	            }
	            return new DynamicDataBinding(InternUtil.internCaseSensitive(simpleName), declaringPart);
	        }
	        IDataBinding result = typeBinding.getBaseType().findData(simpleName);
	        if(result != IBinding.NOT_FOUND_BINDING) return result;
	        if (typeBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING) {
	            result = (IDataBinding)typeBinding.getSimpleNamesToDataBindingsMap().get(simpleName);
	            if (result != null) {
	                return result;
	            }
	        }
    	}
        return null;
     }

    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }

    public ITypeBinding findType(String simpleName) {
        return parentScope.findType(simpleName);
    }

    public ITypeBinding getTypeBinding() {
        return typeBinding;
    }
}
