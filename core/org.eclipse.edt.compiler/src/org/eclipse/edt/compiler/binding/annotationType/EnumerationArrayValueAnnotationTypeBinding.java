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
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;

/**
 * @author Dave Murray
 */
abstract class EnumerationArrayValueAnnotationTypeBinding extends AnnotationTypeBinding {
	
	EnumerationTypeBinding enumerationType;
	
	public EnumerationArrayValueAnnotationTypeBinding(String caseSensitiveInternedName, EnumerationTypeBinding enumerationType) {
        super(caseSensitiveInternedName, ArrayTypeBinding.getInstance(enumerationType));
        this.enumerationType = enumerationType;
    }
	
	public EnumerationTypeBinding getEnumerationType() {
		return enumerationType;
	}
	
	public ITypeBinding getSingleValueType() {
		return ArrayTypeBinding.getInstance(enumerationType);
	}
}
