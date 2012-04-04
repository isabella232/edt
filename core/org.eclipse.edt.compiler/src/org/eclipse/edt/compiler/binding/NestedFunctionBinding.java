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

public class NestedFunctionBinding extends DataBinding {

	public NestedFunctionBinding(String caseSensitiveInternedName, IPartBinding declarer, IFunctionBinding functionBinding) {
		super(caseSensitiveInternedName, declarer, functionBinding);
	}

	public int getKind() {
		return NESTED_FUNCTION_BINDING;
	}
	
	public boolean isPrivate() {
		return ((IFunctionBinding) typeBinding).isPrivate();
	}
}
