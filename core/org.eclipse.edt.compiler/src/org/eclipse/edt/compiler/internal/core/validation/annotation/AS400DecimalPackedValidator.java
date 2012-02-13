package org.eclipse.edt.compiler.internal.core.validation.annotation;

import org.eclipse.edt.compiler.binding.AS400DecimalPackedAnnotationTypeBinding;

public class AS400DecimalPackedValidator extends AS400DecimalValidator {

	@Override
	protected String getName() {
		return AS400DecimalPackedAnnotationTypeBinding.caseSensitiveName;
	}
	
	@Override
	protected String getInternedName() {
		return AS400DecimalPackedAnnotationTypeBinding.name;
	}

}
