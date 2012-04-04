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


/**
 * @author Dave Murray
 */
public abstract class VariableBinding extends DataBinding {
	
	protected boolean readOnly;
	protected boolean isPrivate;
	
    public VariableBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding) {
        super(caseSensitiveInternedName, declarer, typeBinding);
    }
    
    public boolean isConstant() {
    	return false;
    }
    
    public Object getConstantValue() {
    	return null;
    }
    
    public boolean isReadOnly() {
		return readOnly;
	}
    
    public void setIsReadOnly(boolean readOnly) {
    	this.readOnly = readOnly;
    }
    
    public boolean isPrivate() {
    	return isPrivate;
    }
    
    public void setIsPrivate(boolean isPrivate) {
    	this.isPrivate = isPrivate;
    }    
}
