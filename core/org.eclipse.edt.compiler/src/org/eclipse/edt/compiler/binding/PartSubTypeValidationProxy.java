/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.annotationType.ComplexAnnotationProxy;
import org.eclipse.edt.mof.egl.Annotation;


public abstract class PartSubTypeValidationProxy extends ComplexAnnotationProxy {
	
	public PartSubTypeValidationProxy(String caseSensitiveInternedName) {
		super(caseSensitiveInternedName, new Object[0]);
	}
	
	public PartSubTypeValidationProxy(String caseSensitiveInternedName, Object[] fields) {
        super(caseSensitiveInternedName, fields);
    }
	
	public PartSubTypeValidationProxy(String caseSensitiveInternedName, Object[] fields, Annotation superType) {
        super(caseSensitiveInternedName, fields);
        
//        if (Collections.EMPTY_MAP == this.fields) {
//        	this.fields = new HashMap();
//        }
//        this.fields.putAll(superType.fields);
    }
	
	public PartSubTypeValidationProxy(String caseSensitiveInternedName, Object[] fields, Object[] defaultValues) {
        super(caseSensitiveInternedName, fields, defaultValues);
    }
}