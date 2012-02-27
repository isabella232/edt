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
package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.core.ast.Primitive;

/**
 * For data bindings whose type is not known until runtime. For example, the
 * data bindings for "a", "b", "a.b", and "myDict.a.b" in the following:
 * 
 * myDict dictionary;
 * myDict.a.b = 6;
 * 
 * @author Dave Murray
 */
public class DynamicDataBinding extends DataBinding {

    public DynamicDataBinding(String caseSensitiveInternedName, IPartBinding declarer) {
        super(caseSensitiveInternedName, declarer, PrimitiveTypeBinding.getInstance(Primitive.ANY).getNullableInstance());
    }

	public int getKind() {
		return DYNAMIC_BINDING;
	}
}
