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
public class SystemConstantBinding extends SystemClassFieldBinding {
	
	public SystemConstantBinding(String caseSensitiveInternedName, ITypeBinding typeBinding, int systemVariableType) {
        super(caseSensitiveInternedName, null, typeBinding, systemVariableType);
    }
	
	public boolean isConstant() {
		return true;
	}
	
	public boolean isReadOnly() {
		return true;
	}
}
