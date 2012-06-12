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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.StructFloat8AnnotationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;

public class StructFloat8Validator extends AbstractStructParameterAnnotaionValidator implements IAnnotationValidationRule {
	
	protected List<Primitive> getSupportedTypes() {
		List<Primitive> list = new ArrayList<Primitive>();
		list.add(Primitive.FLOAT);
		return list;
	}

	@Override
	protected String getName() {
		return StructFloat8AnnotationTypeBinding.caseSensitiveName;
	}
	
	@Override
	protected String getInternedName() {
		return StructFloat8AnnotationTypeBinding.name;
	}
}
