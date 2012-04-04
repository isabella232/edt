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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.edt.compiler.internal.core.lookup.EnumerationManager;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
/**
 * @author Harmon
 */
public class SystemEnumerationDataBinding extends EnumerationDataBinding {

    /**
     * @param caseSensitiveInternedName
     * @param declarer
     * @param typeBinding
     */
    public SystemEnumerationDataBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding, int constantValue) {
        super(caseSensitiveInternedName, declarer, typeBinding, constantValue);        
    }
    
    public boolean isSystemEnumeration() {
        return true;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeUTF(getType().getName());
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String typeName = in.readUTF();
        typeName =  InternUtil.intern(typeName);
        typeBinding = (ITypeBinding)EnumerationManager.getEnumTypes().get(typeName);
    }
    
    protected Object readResolve() {		
		return typeBinding.findData(InternUtil.intern(getName()));
	}

}
