package org.eclipse.edt.compiler.internal.core.validation.annotation;

import org.eclipse.edt.compiler.binding.AS400DecimalZonedAnnotationTypeBinding;

public class AS400DecimalZonedValidator extends AS400DecimalValidator {

	@Override
	protected String getName() {
		return AS400DecimalZonedAnnotationTypeBinding.caseSensitiveName;
	}
	
	@Override
	protected String getInternedName() {
		return AS400DecimalZonedAnnotationTypeBinding.name;
	}

}
