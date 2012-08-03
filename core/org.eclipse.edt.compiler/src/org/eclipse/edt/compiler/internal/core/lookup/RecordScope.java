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

import java.util.List;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Type;

/**
 * @author winghong
 */
public class RecordScope extends Scope {
    
    private Record record;
    
    public RecordScope(Scope parentScope, Record record) {
        super(parentScope);
        this.record = record;
    }

    public List<Member> findMember(String simpleName) {
    	List<Member> result = BindingUtil.findMembers(record, simpleName);
        if(result != null) return result;
        
        return parentScope.findMember(simpleName);
    }

    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }

    public List<Type> findType(String simpleName) {
        return parentScope.findType(simpleName);
    }
    
    public Part getPart() {
    	return record;
    }

}
