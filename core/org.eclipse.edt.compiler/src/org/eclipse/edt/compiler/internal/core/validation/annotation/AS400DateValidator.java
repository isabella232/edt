package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.AS400DateAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;

public class AS400DateValidator extends
		AbstractAS400ParameterAnnotaionValidator {
	
	protected List<Primitive> getSupportedTypes() {
		List<Primitive> list = new ArrayList<Primitive>();
		list.add(Primitive.DATE);
		return list;
	}

	@Override
	protected String getName() {
		return AS400DateAnnotationTypeBinding.caseSensitiveName;
	}
	
	@Override
	protected String getInternedName() {
		return AS400DateAnnotationTypeBinding.name;
	}
}
