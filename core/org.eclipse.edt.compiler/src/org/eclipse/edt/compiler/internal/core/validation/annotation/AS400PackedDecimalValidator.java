package org.eclipse.edt.compiler.internal.core.validation.annotation;

import org.eclipse.edt.compiler.binding.AS400PackedDecimalAnnotationTypeBinding;

public class AS400PackedDecimalValidator extends AS400DecimalValidator {

	@Override
	protected String getName() {
		return AS400PackedDecimalAnnotationTypeBinding.caseSensitiveName;
	}
	
	@Override
	protected String getInternedName() {
		return AS400PackedDecimalAnnotationTypeBinding.name;
	}

}
