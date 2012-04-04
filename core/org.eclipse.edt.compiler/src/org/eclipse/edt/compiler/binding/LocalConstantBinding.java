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
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LocalConstantBinding extends LocalVariableBinding {
	
	private Object constantValue;

	public LocalConstantBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding, Object constantValue) {		
        super(caseSensitiveInternedName, declarer, typeBinding);
        
        this.constantValue = constantValue;
    }
	
	public Object getConstantValue() {
		return constantValue;
	}
	
	public boolean isConstant() {
		return true;
	}

}
