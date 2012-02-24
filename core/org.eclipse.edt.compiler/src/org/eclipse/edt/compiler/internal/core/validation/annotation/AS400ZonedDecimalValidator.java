package org.eclipse.edt.compiler.internal.core.validation.annotation;

import org.eclipse.edt.compiler.binding.AS400ZonedDecimalAnnotationTypeBinding;

public class AS400ZonedDecimalValidator extends AS400DecimalValidator {

	@Override
	protected String getName() {
		return AS400ZonedDecimalAnnotationTypeBinding.caseSensitiveName;
	}
	
	@Override
	protected String getInternedName() {
		return AS400ZonedDecimalAnnotationTypeBinding.name;
	}

}
