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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.AS400ArrayAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.type.PrimitiveTypeValidator.DateTimePattern;

public class AS400ArrayValidator extends
		AbstractAS400ParameterAnnotaionValidator {

	public void validate(IAnnotationBinding annotation, Node errorNode, ITypeBinding targetTypeBinding, IProblemRequestor problemRequestor) {
		super.validate(annotation, errorNode, targetTypeBinding, problemRequestor);
		
		
		
		if (Binding.isValidBinding(targetTypeBinding) && isValidType(targetTypeBinding)) {
			
			if (getElementType(annotation) == null) {
				validateElementTypeNotRequired((ArrayTypeBinding)targetTypeBinding, errorNode, problemRequestor);
				validateElementTypeNotNullable((ArrayTypeBinding)targetTypeBinding, errorNode, problemRequestor);
			}
			else {
				validateElementType(annotation, (ArrayTypeBinding)targetTypeBinding, errorNode, problemRequestor);
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
	
	protected void validateReturnCount(IAnnotationBinding ann, Node errorNode, IProblemRequestor problemRequestor) {
		Object obj = getReturnCount(ann);
		if (obj == null || !(obj instanceof Name)) {
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
		
		IBinding containerBinding = getContainerBinding(errorNode);
		if (containerBinding == db.getDeclaringPart()) {
			return;
		}
		
		problemRequestor.acceptProblem(errorNode, 
				IProblemRequestor.RETURN_COUNT_VAR_DEFINED_IN_WRONG_PLACE, 
				new String[] {db.getCaseSensitiveName()});
		
	}
	
	private IBinding getContainerBinding(Node node) {
		if (node == null) {
			return null;
		}
		if (node instanceof Part) {
			return ((Part)node).getName().resolveBinding();
		}
		
		if (node instanceof NestedFunction) {
			return ((NestedFunction)node).getName().resolveBinding();
		}
		
		return getContainerBinding(node.getParent());
	}
	
	protected void validateElementTypeNotNullable(ArrayTypeBinding targetTypeBinding, Node errorNode, IProblemRequestor problemRequestor) {
		if (Binding.isValidBinding(targetTypeBinding.getElementType()) && targetTypeBinding.getElementType().isNullable()) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_ANNOTATION_NULLABLE_TYPE_INVALID, new String[] {getName(), targetTypeBinding.getElementType().getCaseSensitiveName() + "?"});
		}
	}
	
	protected void validateElementTypeNotRequired(ArrayTypeBinding targetTypeBinding, Node errorNode, IProblemRequestor problemRequestor) {
		if (IBMiProgramValidator.requiresAS400TypeAnnotation(targetTypeBinding.getElementType())) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.AS400_PROPERTY_REQUIRED, new String[] {"elementTypeAS400Annotation", getName()});
		}
	}

	protected void validateElementType(IAnnotationBinding ann, ArrayTypeBinding targetTypeBinding, Node errorNode, IProblemRequestor problemRequestor) {
		Object obj =  getElementType(ann);
		AbstractAS400ParameterAnnotaionValidator validator = IBMiProgramParameterAnnotationsValidator.getValidator(obj);
		if (validator == null) {
			problemRequestor.acceptProblem(errorNode, 
									IProblemRequestor.ELEMENTTYPE_ANNOTATION_INVALID, 
									new String[] {});
		}
		else {
			validator.validate((IAnnotationBinding)obj, errorNode, targetTypeBinding.getElementType(), problemRequestor);
		}
	}
	
	
	protected Object getElementType(IAnnotationBinding annotation) {
		return getValue(annotation, "elementTypeAS400Annotation");
	}
	
	protected Object getReturnCount(IAnnotationBinding annotation) {
		return getValue(annotation, "returnCountVariable");
	}
	
	
	@Override
	protected List<Primitive> getSupportedTypes() {
		return new ArrayList<Primitive>();
	}

	@Override
	protected String getName() {
		return AS400ArrayAnnotationTypeBinding.caseSensitiveName;
	}

	@Override
	protected String getInternedName() {
		return AS400ArrayAnnotationTypeBinding.name;
	}
	
	protected boolean isValidType(ITypeBinding typeBinding) {
		if (Binding.isValidBinding(typeBinding)) {
						
			if (ITypeBinding.ARRAY_TYPE_BINDING == typeBinding.getKind()) {
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
