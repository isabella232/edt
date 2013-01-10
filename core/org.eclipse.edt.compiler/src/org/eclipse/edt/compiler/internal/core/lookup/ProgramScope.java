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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.ProgramParameter;
import org.eclipse.edt.mof.utils.NameUtile;

/**
 * @author winghong
 */
public class ProgramScope extends FunctionContainerScope {
    
    public ProgramScope(Scope parentScope, Program programBinding) {
        super(parentScope, programBinding);
    }
    
    public boolean isProgramScope() {
        return true;
    }
    
    public Program getProgramBinding() {
        return (Program)functionContainerBinding;
    }
    
    @Override
    protected List<Member> findMemberInPart(String id) {
    	Member parm = findMemberInParms(id);
    	List<Member> result = super.findMemberInPart(id);
    	if (parm == null) {
    		return result;
    	}
    	else {
    		if (result == null) {
    			result = new ArrayList<Member>();
    		}
			result.add(parm);
			return result;
    	}
    }
    
    private Member findMemberInParms(String id) {
    	Program pgm = getProgramBinding();
    	if (pgm == null) {
    		return null;
    	}
    	for (ProgramParameter parm : pgm.getParameters()) {
    		if (NameUtile.equals(id, parm.getName())) {
    			return parm;
    		}
    	}
    	return null;
    		
    }
}
