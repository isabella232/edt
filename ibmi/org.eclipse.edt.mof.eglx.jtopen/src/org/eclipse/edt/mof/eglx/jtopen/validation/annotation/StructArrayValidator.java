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
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.jtopen.validation.IBMiProgramParameterAnnotationsValidator;


public class StructArrayValidator extends AbstractStructParameterAnnotationValidator {

	public void validate(Annotation annotation, Node errorNode, Member targetBinding, IProblemRequestor problemRequestor) {
		super.validate(annotation, errorNode, targetBinding, problemRequestor);
		
		if (targetBinding != null && isValidType(targetBinding.getType())) {
			
			if (annotation.getValue("elementTypeAnnotation") == null) {
				validateElementTypeNotRequired(targetBinding, errorNode, problemRequestor);
				validateElementTypeNotNullable(targetBinding, errorNode, problemRequestor);
			}
			else {
				validateElementType(annotation, targetBinding, errorNode, problemRequestor);
			}
			
			validateReturnCount(annotation, errorNode, problemRequestor);
		}
	}
	
	private boolean isCompatibleWithINT(Type type) {
		Type _int = org.eclipse.edt.mof.egl.utils.IRUtils.getEGLType(TypeUtils.Type_EGLInt);
		return TypeUtils.areCompatible(_int.getClassifier(), type.getClassifier());
	}
	
	protected void validateReturnCount(Annotation ann, Node errorNode, IProblemRequestor problemRequestor) {
		Object obj = ann.getValue("returnCountVariable");
		if (obj == null || !(obj instanceof Member)) {
			return;
		}
		
		//Variable must be move compatible with int
		if (!isCompatibleWithINT(((Member)obj).getType())) {
			problemRequestor.acceptProblem(errorNode, 
					IProblemRequestor.RETURN_COUNT_VAR_MUST_BE_INT_COMPAT, 
					new String[] {((Member)obj).getCaseSensitiveName()});
		}
		
		//Make sure the ReturnCount is defined in the same place as the field that is holding the annoation
		
		//If returnCount is a function parameter, we can assume that it is in the same place
		if (obj instanceof FunctionParameter) {
			return;
		}
		
		Type containerBinding = getContainerBinding(errorNode);
		if (((Member)obj).getType().equals(containerBinding)) {
			return;
		}
		
		problemRequestor.acceptProblem(errorNode, 
				IProblemRequestor.RETURN_COUNT_VAR_DEFINED_IN_WRONG_PLACE, 
				new String[] {((Member)obj).getCaseSensitiveName()});
		
	}
	
	private Type getContainerBinding(Node node) {
		if (node == null) {
			return null;
		}
		if (node instanceof Part) {
			return ((Part)node).getName().resolveType();
		}
		
		return getContainerBinding(node.getParent());
	}
	
	protected void validateElementTypeNotNullable(Member targetBinding, Node errorNode, IProblemRequestor problemRequestor) {
		if (targetBinding.getType() instanceof ArrayType && ((ArrayType)targetBinding.getType()).elementsNullable()) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_ANNOTATION_NULLABLE_TYPE_INVALID, new String[] {getName(), targetBinding.getCaseSensitiveName() + "?"});
		}
	}
	
	protected void validateElementTypeNotRequired(Member targetBinding, Node errorNode, IProblemRequestor problemRequestor) {
		if (targetBinding.getType() instanceof ArrayType && 
				IBMiProgramValidator.requiresAS400TypeAnnotation(((ArrayType)targetBinding.getType()).getElementType())) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_REQUIRED, new String[] {"elementTypeAnnotation", getName()});
		}
	}

	protected void validateElementType(Annotation ann, Member targetBinding, Node errorNode, IProblemRequestor problemRequestor) {
		Object obj =  ann.getValue("elementTypeAnnotation");
		AbstractStructParameterAnnotationValidator validator = IBMiProgramParameterAnnotationsValidator.getValidator(obj);
		if (validator == null) {
			problemRequestor.acceptProblem(errorNode, 
									IProblemRequestor.ELEMENTTYPE_ANNOTATION_INVALID, 
									new String[] {});
		}
		else {
			Member elementMember = (Member)targetBinding.clone();
			elementMember.setType(((ArrayType)targetBinding.getType()).getElementType());
			validator.validate((Annotation)obj, errorNode, elementMember, problemRequestor);
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

	protected boolean isValidType(Type typeBinding) {
		if (typeBinding != null) {
						
			if (typeBinding instanceof ArrayType) {
				return IBMiProgramValidator.isValidAS400Type(typeBinding);
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
