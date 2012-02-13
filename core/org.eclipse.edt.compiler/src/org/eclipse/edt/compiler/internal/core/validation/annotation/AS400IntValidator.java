package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.Primitive;

public abstract class AS400IntValidator extends
		AbstractAS400ParameterAnnotaionValidator {

	protected List<Primitive> getSupportedTypes() {
		List<Primitive> list = new ArrayList<Primitive>();
		list.add(Primitive.INT);
		return list;
	}
}
