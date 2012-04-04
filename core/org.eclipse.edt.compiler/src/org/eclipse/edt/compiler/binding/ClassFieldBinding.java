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
 * Bindings that represent variables declared at the top level of a function
 * container, such as a program or library, or in a flexible record.
 * 
 * @author Dave Murray
 */
public class ClassFieldBinding extends VariableBinding {
	
	boolean isStatic;
	boolean implicit;
	
    public ClassFieldBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding) {
        super(caseSensitiveInternedName, declarer, typeBinding);
    }
    
	public int getKind() {
		return CLASS_FIELD_BINDING;
	}
	
	public void setIsStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	
	public boolean isStatic() {
		return isStatic;
	}
	
	public boolean isTypeReference() {
		return false;
	}
	

	public boolean isImplicit() {
		return implicit;
	}

	public void setImplicit(boolean implicit) {
		this.implicit = implicit;
	}
	
}
