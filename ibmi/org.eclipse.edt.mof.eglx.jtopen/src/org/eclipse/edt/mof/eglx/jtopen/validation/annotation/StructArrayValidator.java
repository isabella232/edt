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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.compiler.binding.IValidationProxy;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;
import org.eclipse.edt.mof.eglx.jtopen.validation.IBMiFunctionParameterValidator;
import org.eclipse.edt.mof.utils.NameUtile;


public class StructArrayValidator extends AbstractStructParameterAnnotationValidator {

	protected void validateType(Annotation annotation, Node errorNode, Type type) {
		super.validateType(annotation, errorNode, type);
		
		if (type != null && isValidType(type)) {
			
			if(annotation.getValue("elementTypeAnnotation") instanceof Annotation) {
				validateElementType((Annotation)annotation.getValue("elementTypeAnnotation"), type, errorNode);
			}
			else {
				validateElementTypeNotRequired(type, errorNode);
			}
			validateReturnCount(annotation, errorNode);
		}
	}
	
	private boolean isCompatibleWithINT(Type type) {
		Type _int = org.eclipse.edt.mof.egl.utils.IRUtils.getEGLType(TypeUtils.Type_EGLInt);
		return TypeUtils.areCompatible(_int.getClassifier(), type.getClassifier());
	}
	
	protected void validateReturnCount(Annotation ann, Node errorNode) {
		Object obj = ann.getValue("returnCountVariable");
		if (obj == null || !(obj instanceof Member)) {
			return;
		}
		
		//Variable must be move compatible with int
		if (!isCompatibleWithINT(((Member)obj).getType())) {
			problemRequestor.acceptProblem(errorNode, 
					IBMiResourceKeys.RETURN_COUNT_VAR_MUST_BE_INT_COMPAT, 
					IMarker.SEVERITY_ERROR, 
					new String[] {((Member)obj).getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
		}
		
		//Make sure the ReturnCount is defined in the same place as the field that is holding the annoation
		
		//If returnCount is a function parameter, we can assume that it is in the same place
		if (obj instanceof FunctionParameter) {
			return;
		}
		
		Type containerBinding = getContainerBinding(errorNode);
		if (((Member)obj).getContainer().equals(containerBinding)) {
			return;
		}
		
		problemRequestor.acceptProblem(errorNode, 
				IBMiResourceKeys.RETURN_COUNT_VAR_DEFINED_IN_WRONG_PLACE, 
				IMarker.SEVERITY_ERROR, 
				new String[] {((Member)obj).getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
		
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
	
	protected void validateElementTypeNotNullable(Type type, Node errorNode) {
		if (type instanceof ArrayType && ((ArrayType)type).elementsNullable()) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_ANNOTATION_NULLABLE_TYPE_INVALID, IMarker.SEVERITY_ERROR, new String[] {getName(), StatementValidator.getShortTypeString(type, true) + "?"}, IBMiResourceKeys.getResourceBundleForKeys());
		}
	}
	
	protected void validateElementTypeNotRequired(Type type, Node errorNode) {
		if (type instanceof ArrayType && 
				IBMiFunctionParameterValidator.requiresAS400TypeAnnotation(((ArrayType)type).getElementType())) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.AS400_PROPERTY_REQUIRED, IMarker.SEVERITY_ERROR, new String[] {"elementTypeAnnotation", getName()}, IBMiResourceKeys.getResourceBundleForKeys());
		}
	}

	protected void validateElementType(Annotation ann, Type type, Node errorNode) {
		boolean hasStructTypeAnnotation = false;
		IValidationProxy proxy = AnnotationValidator.getValidationProxy((Annotation)ann);
		if (proxy != null) {
			for (org.eclipse.edt.compiler.binding.AnnotationValidationRule rule : proxy.getAnnotationValidators()) {
				hasStructTypeAnnotation = true;
				Map<String, Object> annotations = new HashMap<String, Object>(1);
				annotations.put(NameUtile.getAsName(((Annotation) ann).getEClass().getETypeSignature()), ann);
				rule.validate(errorNode, errorNode, ((ArrayType)type).getElementType(), annotations, problemRequestor, compilerOptions);
			}
		}
		
		if(!hasStructTypeAnnotation && IBMiFunctionParameterValidator.requiresAS400TypeAnnotation(type)) {
			problemRequestor.acceptProblem(errorNode, 
					IBMiResourceKeys.PROGRAM_PARAMETER_ANNOTATION_REQUIRED, 
					IMarker.SEVERITY_ERROR, 
					new String[] {StatementValidator.getShortTypeString(type, true)}, IBMiResourceKeys.getResourceBundleForKeys());
		}
	}

	@Override
	protected Type getSupportedType() {
		return null;
	}

	@Override
	protected String getName() {
		return "StructArray";
	}

	protected boolean isValidType(Type typeBinding) {
		if (typeBinding != null) {
						
			if (typeBinding instanceof ArrayType) {
				return IBMiFunctionParameterValidator.isValidAS400Type(typeBinding);
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
