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
package org.eclipse.edt.compiler.binding.annotationType;

import org.eclipse.edt.compiler.binding.AbstractValidationProxy;

public abstract class ComplexAnnotationProxy extends AbstractValidationProxy {
    
	public ComplexAnnotationProxy(String caseSensitiveInternedName, Object[] fields) {
//        super(caseSensitiveInternedName, fields);
    }
    
    public ComplexAnnotationProxy(String caseSensitiveInternedName, Object[] fields, Object[] defaultValues) {
//    	super(caseSensitiveInternedName, fields, defaultValues);
    }

}
