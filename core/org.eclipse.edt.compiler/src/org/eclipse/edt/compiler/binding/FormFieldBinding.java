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
public abstract class FormFieldBinding extends DataBinding {
	
	int occurs = 0;
	
    public FormFieldBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding) {
        super(caseSensitiveInternedName, declarer, typeBinding);
    }

	public boolean isConstant() {
		return false;
	}
	
	public boolean isVariable() {
		return false;
	}
	
	public int getKind() {
		return FORM_FIELD;
	}	
	
	public boolean isMultiplyOccuring() {
		return occurs != 0;
	}
	
	public int getOccurs() {
		return occurs;
	}
	
	public void setOccurs(int occurs) {
		this.occurs = occurs;
	}
	
	public boolean isTextFormField() {
	    return !isPrintFormField();
	}
	public boolean isPrintFormField() {
	    return getDeclaringPart() != null && ((FormBinding)getDeclaringPart()).isPrintForm();
	}

	
}
