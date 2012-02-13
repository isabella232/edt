package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.AS400TimeAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;

public class AS400TimeValidator extends
		AbstractAS400ParameterAnnotaionValidator {
	
	protected List<Primitive> getSupportedTypes() {
		List<Primitive> list = new ArrayList<Primitive>();
		list.add(Primitive.TIME);
		return list;
	}

	@Override
	protected String getName() {
		return AS400TimeAnnotationTypeBinding.caseSensitiveName;
	}
	
	@Override
	protected String getInternedName() {
		return AS400TimeAnnotationTypeBinding.name;
	}
}
