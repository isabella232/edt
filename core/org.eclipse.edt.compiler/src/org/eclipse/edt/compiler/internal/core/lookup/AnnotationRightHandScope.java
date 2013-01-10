/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;


public class AnnotationRightHandScope extends Scope{
    private EField field;

    public AnnotationRightHandScope(Scope parentScope, EField field) {
        super(parentScope);
        this.field = field;
    }

    public List<Member> findMember(String simpleName) {
    	Enumeration enumeration = getEnumerationType();
        if (enumeration != null) {
        	List<Member> result = BindingUtil.findMembers(enumeration, simpleName);
            if (result != null) {
                return result;
            }
        }
        return parentScope.findMember(simpleName);
    }
    
    private Enumeration getEnumerationType() {
    	if (field != null) {
    		EType type = field.getEType();
    		return getEnumerationType(type);
    	}
    	return null;
    }
    
    private Enumeration getEnumerationType(EType type) {
    	if (type instanceof Enumeration) {
    		return (Enumeration)type;
    	}
    	if (type instanceof EGenericType) {
    		return getEnumerationType(((EGenericType)type).getETypeArguments().get(0));
    	}
    	return null;
    }

    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }

    public List<Type> findType(String simpleName) {
        return parentScope.findType(simpleName);
    }
}
