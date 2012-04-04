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
 * For part-name-qualified data accesses.
 * 
 * For example, in the following the data binding for "lib" in "lib.a" is a
 * static library data binding:
 * 
 * Library lib
 *     a int;
 * end
 * 
 * function func()
 *     lib.a = 10;
 * end
 * 
 * @author Dave Murray
 */
public abstract class StaticPartDataBinding extends DataBinding {

    public StaticPartDataBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding) {
        super(caseSensitiveInternedName, declarer, typeBinding);
    }
    
    public boolean isStaticPartDataBinding() {
		return true;
	}
}
