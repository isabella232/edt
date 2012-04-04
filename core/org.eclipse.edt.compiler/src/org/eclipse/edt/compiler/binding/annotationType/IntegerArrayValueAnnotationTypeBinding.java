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
package org.eclipse.edt.compiler.binding.annotationType;

import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;


/**
 * @author Harmon
 */
abstract class IntegerArrayValueAnnotationTypeBinding extends AnnotationTypeBinding {

    public IntegerArrayValueAnnotationTypeBinding(String caseSensitiveInternedName) {
        super(caseSensitiveInternedName, ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.INT)));
    }
    
    public ITypeBinding getSingleValueType() {
    	return ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.INT));
    }
}
