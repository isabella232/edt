package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.internal.core.validation.annotation.AbstractAS400ParameterAnnotaionValidator;


public abstract class AS400ParmeterAnnotationTypeBinding extends AnnotationTypeBinding {

	public AS400ParmeterAnnotationTypeBinding(String caseSensitiveInternedName) {
		super(caseSensitiveInternedName);
	}
	
	public abstract AbstractAS400ParameterAnnotaionValidator getValidator();
	
}
