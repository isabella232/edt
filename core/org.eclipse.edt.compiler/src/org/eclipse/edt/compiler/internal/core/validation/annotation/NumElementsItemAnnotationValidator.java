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

import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


/**
 * @author svihovec
 *
 */
public class NumElementsItemAnnotationValidator extends AbstractAnnotationValidator {

	
	public NumElementsItemAnnotationValidator(AnnotationExpression annotation, ITypeBinding type, IProblemRequestor problemRequestor) {
		super(annotation, type, problemRequestor);
	}
	
	public void validate(){
		
		if(type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			validateType((PrimitiveTypeBinding)type);
			validateDecimals((PrimitiveTypeBinding)type);
			validateLength((PrimitiveTypeBinding)type);
		}
	}

	private void validateDecimals(PrimitiveTypeBinding primType){
		if(primType.getDecimals() != 0){
			problemRequestor.acceptProblem(annotation, IProblemRequestor.RECORD_ITEM_PROPERTY_VALUE_HAS_DECIMALS);
		}
	}
	
	private void validateLength(PrimitiveTypeBinding primType){
		if(primType.getLength() > 9){
			problemRequestor.acceptProblem(annotation, IProblemRequestor.RECORD_ITEM_PROPERTY_VALUE_TOO_LONG);
		}
	}
	
	private void validateType(PrimitiveTypeBinding primType){
		Primitive primitive = primType.getPrimitive();
		
		if(primitive != Primitive.BIN
				|| primitive != Primitive.NUM
				|| primitive != Primitive.NUMC
				|| primitive != Primitive.PACF
				|| primitive != Primitive.DECIMAL
				|| primitive != Primitive.INT
				|| primitive != Primitive.SMALLINT){
			problemRequestor.acceptProblem(annotation, IProblemRequestor.PROPERTY_INVALID_TYPE_FOR_RECORD_ITEM_PROPERTY);
		}
	}
}
