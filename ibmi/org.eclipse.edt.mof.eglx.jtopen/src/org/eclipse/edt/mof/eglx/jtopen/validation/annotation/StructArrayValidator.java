/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.jtopen.validation.annotation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IBMiProgramValidator;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.eglx.jtopen.validation.IBMiProgramParameterAnnotationsValidator;


public class StructArrayValidator extends AbstractStructParameterAnnotaionValidator {

	public void validate(Annotation annotation, Node errorNode, Member targetBinding, IProblemRequestor problemRequestor) {
		super.validate(annotation, errorNode, targetBinding, problemRequestor);
		
		
		
		if (targetBinding != null && isValidType(targetBinding)) {
			
			if (annotation.getValue("elementTypeAnnotation") == null) {
				validateElementTypeNotRequired((ArrayType)targetBinding, errorNode, problemRequestor);
				validateElementTypeNotNullable((ArrayType)targetBinding, errorNode, problemRequestor);
			}
			else {
				validateElementType(annotation, (ArrayType)targetBinding, errorNode, problemRequestor);
			}
			
			validateReturnCount(annotation, errorNode, problemRequestor);
		}
	}
	
	private boolean isCompatibleWithINT(ITypeBinding type) {
		if (ITypeBinding.PRIMITIVE_TYPE_BINDING != type.getKind()) {
			return false;
		}
		
		return TypeCompatibilityUtil.isMoveCompatible(PrimitiveTypeBinding.getInstance(Primitive.INT), (PrimitiveTypeBinding)type);
	}
	
	protected void validateReturnCount(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		Object obj = ann.getValue("returnCountVariable");
		if (obj == null || !(obj instanceof String)) {
			return;
		}
		
		IDataBinding db = ((Name)obj).resolveDataBinding();
		if (!Binding.isValidBinding(db) || !Binding.isValidBinding(db.getType())) {
			return;
		}
		
		//Variable must be move compatible with int
		if (!isCompatibleWithINT(db.getType())) {
			problemRequestor.acceptProblem(errorNode, 
					IProblemRequestor.RETURN_COUNT_VAR_MUST_BE_INT_COMPAT, 
					new String[] {db.getCaseSensitiveName()});
		}
		
		//Make sure the ReturnCount is defined in the same place as the field that is holding the annoation
		
		//If returnCount is a function parameter, we can assume that it is in the same place
		if (db.getKind() == IDataBinding.FUNCTION_PARAMETER_BINDING) {
			return;
		}
		
		Container containerBinding = getContainerBinding(errorNode);
		if (containerBinding == db.getDeclaringPart()) {
			return;
		}
		
		problemRequestor.acceptProblem(errorNode, 
				IProblemRequestor.RETURN_COUNT_VAR_DEFINED_IN_WRONG_PLACE, 
				new String[] {db.getCaseSensitiveName()});
		
	}
	
	private Element getContainerBinding(Element element) {
		if (element == null) {
			return null;
		}
		if (element instanceof Part) {
			return element;
		}
		
		if (element instanceof Function) {
			return element;
		}
		
		return getContainerBinding(element.getParent());
	}
	
	protected void validateElementTypeNotNullable(ArrayType targetBinding, Node errorNode, IProblemRequestor problemRequestor) {
		if (targetBinding.getElementType() != null && targetBinding.elementsNullable()) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_ANNOTATION_NULLABLE_TYPE_INVALID, new String[] {getName(), targetBinding.getElementType().getCaseSensitiveName() + "?"});
		}
	}
	
	protected void validateElementTypeNotRequired(ArrayType targetBinding, Node errorNode, IProblemRequestor problemRequestor) {
		if (IBMiProgramValidator.requiresAS400TypeAnnotation(targetBinding.getElementType())) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_REQUIRED, new String[] {"elementTypeAnnotation", getName()});
		}
	}

	protected void validateElementType(Annotation ann, ArrayType targetBinding, Node errorNode, IProblemRequestor problemRequestor) {
		Object obj =  ann.getValue("elementTypeAnnotation");
		AbstractStructParameterAnnotaionValidator validator = IBMiProgramParameterAnnotationsValidator.getValidator(obj);
		if (validator == null) {
			problemRequestor.acceptProblem(errorNode, 
									IProblemRequestor.ELEMENTTYPE_ANNOTATION_INVALID, 
									new String[] {});
		}
		else {
			validator.validate((Annotation)obj, errorNode, targetBinding.getElementType(), problemRequestor);
		}
	}

	@Override
	protected List<String> getSupportedTypes() {
		return new ArrayList<String>();
	}

	@Override
	protected String getName() {
		return "StructArray";
	}

	protected boolean isValidType(Member typeBinding) {
		if (typeBinding != null) {
						
			if (typeBinding.getType() instanceof ArrayType) {
				return IBMiProgramValidator.isValidAS400Type(typeBinding.getType());
			}
			else {
				return false;
			}
		}
		else {
			return true;  //return true to avoid excess error messages
		}
	}


}
