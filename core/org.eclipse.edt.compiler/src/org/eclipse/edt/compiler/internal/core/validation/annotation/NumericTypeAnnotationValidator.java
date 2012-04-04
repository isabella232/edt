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
public abstract class NumericTypeAnnotationValidator extends AbstractAnnotationValidator {

	public NumericTypeAnnotationValidator(AnnotationExpression annotation, ITypeBinding type, IProblemRequestor problemRequestor){
		super(annotation, type, problemRequestor);
	}
	
	public void validate(){
		if(type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
			if(!Primitive.isNumericType(((PrimitiveTypeBinding)type).getPrimitive())){
				// error
			}else{
				doValidate((PrimitiveTypeBinding)type);
			}
		}
	}
	
	protected abstract void doValidate(PrimitiveTypeBinding numericType);
}
