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

import org.eclipse.edt.compiler.binding.StructUnsignedBin8AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;

public class StructUnsignedBin8Validator extends AbstractStructParameterAnnotaionValidator implements IAnnotationValidationRule {
	
	@Override
	protected String getName() {
		return StructUnsignedBin8AnnotationTypeBinding.caseSensitiveName;
	}
	
	@Override
	protected String getInternedName() {
		return StructUnsignedBin8AnnotationTypeBinding.name;
	}
	
	@Override
	protected List<Primitive> getSupportedTypes() {
		return new ArrayList<Primitive>();
	}
	
	protected boolean isValidType(ITypeBinding typeBinding) {
		if (Binding.isValidBinding(typeBinding)) {						
			return typeBinding == PrimitiveTypeBinding.getInstance(Primitive.DECIMAL, 20);
		}
		else {
			return true;  //return true to avoid excess error messages
		}
	}


}
