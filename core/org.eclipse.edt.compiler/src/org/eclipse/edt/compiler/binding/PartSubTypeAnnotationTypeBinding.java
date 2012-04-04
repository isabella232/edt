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

import java.util.Collections;
import java.util.HashMap;

import org.eclipse.edt.compiler.binding.annotationType.ComplexAnnotationTypeBinding;


/**
 * @author Harmon
 */
public abstract class PartSubTypeAnnotationTypeBinding extends ComplexAnnotationTypeBinding implements IPartSubTypeAnnotationTypeBinding {
	
	public boolean isPartSubType() {
        return true;
    }
    
	public PartSubTypeAnnotationTypeBinding(String caseSensitiveInternedName) {
		super(caseSensitiveInternedName, new Object[0]);
	}
	
	public PartSubTypeAnnotationTypeBinding(String caseSensitiveInternedName, Object[] fields) {
        super(caseSensitiveInternedName, fields);
    }
	
	public PartSubTypeAnnotationTypeBinding(String caseSensitiveInternedName, Object[] fields, AnnotationTypeBinding superType) {
        super(caseSensitiveInternedName, fields);
        if(Collections.EMPTY_MAP == this.fields) {
        	this.fields = new HashMap();
        }
        this.fields.putAll(superType.fields);
    }
	
	public PartSubTypeAnnotationTypeBinding(String caseSensitiveInternedName, Object[] fields, Object[] defaultValues) {
        super(caseSensitiveInternedName, fields, defaultValues);
    }
	
	public FlexibleRecordBinding getAnnotationRecord() {
		return null;
	}
}
