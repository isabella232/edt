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

public abstract class SystemClassFieldBinding extends ClassFieldBinding {
	
	private int systemVariableType;

	public SystemClassFieldBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding, int systemVariableType) {
        super(caseSensitiveInternedName, declarer, typeBinding);
        this.systemVariableType = systemVariableType;
    }
	
	/**
	 * @return The constant IEGLConstants.Special_Function_* which corresponds
	 *         to this system variable
	 */
	public int getSystemVariableType() {
		return systemVariableType;
	}
	
	public int getKind() {
		return SYSTEM_VARIABLE_BINDING;
	}
}
