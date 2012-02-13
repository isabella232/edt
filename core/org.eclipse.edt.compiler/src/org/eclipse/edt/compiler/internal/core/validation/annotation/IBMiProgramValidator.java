/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AnnotationFieldBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBMiProgramAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;



/**
 * @author svihovec
 *
 */
public class IBMiProgramValidator implements IAnnotationValidationRule {

	@Override
	public void validate(Node errorNode, Node target,
			ITypeBinding targetTypeBinding, Map allAnnotations,
			IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		
		if (!Binding.isValidBinding(targetTypeBinding) || !targetTypeBinding.isFunctionBinding() || !(target instanceof NestedFunction)) {
			return;
		}
		
		IFunctionBinding funcBinding = (IFunctionBinding) targetTypeBinding;
		validateContainerIsCorrect(funcBinding, errorNode, problemRequestor);		
		validateFunctionBodyIsEmpty((NestedFunction)target, problemRequestor);
		validateReturns((NestedFunction)target, funcBinding, problemRequestor);
		
		IAnnotationBinding annotation = (IAnnotationBinding)allAnnotations.get(IBMiProgramAnnotationTypeBinding.name);
		if (annotation == null) { //sanity check, should never happen
			return;
		}
		
		Object obj = getValue(annotation, "parameterAnnotations");
		if (obj == null) {
			//If a paramterAnnotations was specified, the paramters are validated in
			//IBMiProgramParameterAnnotationsValidator
			validateParmsDoNotRequireParameterAnnotations(funcBinding, errorNode, problemRequestor);
		}
		
		validateParameters(funcBinding, obj, errorNode, problemRequestor);
	}
	
	private void validateParameters(IFunctionBinding funcBinding, Object parmAnnValue, Node errorNode, IProblemRequestor problemRequestor) {
		int index = -1;
		Iterator i = funcBinding.getParameters().iterator();
		
		Object[] parmAnnArr = null;
		if (parmAnnValue instanceof Object[] && ((Object[])parmAnnValue).length == funcBinding.getParameters().size()) {
			parmAnnArr = ((Object[])parmAnnValue);
		}
		
		while (i.hasNext()) {
			index = index + 1;
			FunctionParameterBinding parm = (FunctionParameterBinding)i.next();
			if (Binding.isValidBinding(parm.getType())) {
				
				//if a parameterAnnotationArray entry exists for the parm, the type will have already been validated
				if (parmAnnArr == null || parmAnnArr[index] == null) {
				
					if (!isValidAS400Type(parm.getType())) {
						problemRequestor.acceptProblem(errorNode, 
								IProblemRequestor.IBMIPROGRAM_PARM_TYPE_INVALID, 
								new String[] {parm.getCaseSensitiveName()});
						continue;
					}

					if (parm.getType().isNullable()) {
						problemRequestor.acceptProblem(errorNode, 
								IProblemRequestor.IBMIPROGRAM_NULLABLE_PARM_INVALID, 
								new String[] {parm.getType().getCaseSensitiveName() + "?", parm.getCaseSensitiveName()});
						continue;
					}
					
					if (ITypeBinding.ARRAY_TYPE_BINDING == parm.getType().getKind() && Binding.isValidBinding(((ArrayTypeBinding)parm.getType()).getElementType())){
						ITypeBinding elemType = ((ArrayTypeBinding)parm.getType()).getElementType();
						if (elemType.isNullable()) {
							problemRequestor.acceptProblem(errorNode, 
									IProblemRequestor.IBMIPROGRAM_ARRAY_NULLABLE_PARM_INVALID, 
									new String[] {elemType.getCaseSensitiveName() + "?[]", parm.getCaseSensitiveName()});
							continue;
						}
					}
					
					validateFieldsInStructure(parm, parm.getType(), errorNode, problemRequestor);
				}
			}
		}
	}

	
	private void validateParmsDoNotRequireParameterAnnotations(IFunctionBinding funcBinding, Node errorNode, IProblemRequestor problemRequestor) {
		int index = 0;
		Iterator i = funcBinding.getParameters().iterator();
		while (i.hasNext()) {
			FunctionParameterBinding parm = (FunctionParameterBinding)i.next();
			if (requiresAS400TypeAnnotation(parm.getType())) {
				problemRequestor.acceptProblem(errorNode, 
						IProblemRequestor.PROGRAM_PARAMETER_ANNOTATION_REQUIRED, 
						new String[] {parm.getCaseSensitiveName()});
				
			}
			index = index + 1;
		}
	}
	
	private void validateFieldsInStructure(FunctionParameterBinding parm, ITypeBinding type,  Node errorNode, IProblemRequestor problemRequestor) {
		if (Binding.isValidBinding(type)) {
			if (ITypeBinding.HANDLER_BINDING == type.getKind()) {
				validateHandlerFields(parm, (HandlerBinding)type, errorNode, problemRequestor);
				return;
			}
			
			if (ITypeBinding.FLEXIBLE_RECORD_BINDING == type.getKind()) {
				validateRecordFields(parm, (FlexibleRecordBinding)type, errorNode, problemRequestor);
				return;
			}
			
			if (ITypeBinding.ARRAY_TYPE_BINDING == type.getKind() && Binding.isValidBinding(((ArrayTypeBinding)type).getElementType())) {
				validateFieldsInStructure(parm, ((ArrayTypeBinding)type).getElementType(), errorNode, problemRequestor);
			}
		}
	}
	
	private void validateHandlerFields(FunctionParameterBinding parm, HandlerBinding binding,  Node errorNode, IProblemRequestor problemRequestor) {
		Iterator i = binding.getClassFields().iterator();
		while (i.hasNext()) {
			ClassFieldBinding field = (ClassFieldBinding)i.next();
			if (!field.isPrivate()) {
				validateField(parm, field, errorNode, problemRequestor);
			}
		}
	}
	
	private void validateRecordFields(FunctionParameterBinding parm, FlexibleRecordBinding binding,  Node errorNode, IProblemRequestor problemRequestor) {
		Iterator i = binding.getDeclaredFields().iterator();
		while (i.hasNext()) {
			FlexibleRecordFieldBinding field = (FlexibleRecordFieldBinding)i.next();
			validateField(parm, field, errorNode, problemRequestor);
		}
	}
	
	private AbstractAS400ParameterAnnotaionValidator getAS400ParmValidator(IDataBinding binding) {
		Iterator i = binding.getAnnotations().iterator();
		while (i.hasNext()) {
			AbstractAS400ParameterAnnotaionValidator val = IBMiProgramParameterAnnotationsValidator.getValidator(i.next());
			if (val != null) {
				return val;
			}
		}
		return null;
	}
	
	private void validateField(FunctionParameterBinding parm, IDataBinding field,  Node errorNode, IProblemRequestor problemRequestor) {
		if (!Binding.isValidBinding(field.getType())) {
			return;
		}
		
		//if a AS400 annotation exists for the field, the type will have already been validated
		if (getAS400ParmValidator(field) != null) {
			return;
		}
		
		if (!isValidAS400Type(field.getType())) {
			problemRequestor.acceptProblem(errorNode, 
					IProblemRequestor.IBMIPROGRAM_PARM_STRUCT_TYPE_INVALID, 
					new String[] {parm.getCaseSensitiveName(), field.getDeclaringPart().getCaseSensitiveName(), field.getCaseSensitiveName(), field.getType().getCaseSensitiveName()});
			return;
		}

		if (field.getType().isNullable()) {
			problemRequestor.acceptProblem(errorNode, 
					IProblemRequestor.IBMIPROGRAM_NULLABLE_PARM_STRUCT_INVALID, 
					new String[] {parm.getCaseSensitiveName(), field.getDeclaringPart().getCaseSensitiveName(), field.getCaseSensitiveName(), field.getType().getCaseSensitiveName() + "?"});
			return;
		}
		
		if (ITypeBinding.ARRAY_TYPE_BINDING == field.getType().getKind() && Binding.isValidBinding(((ArrayTypeBinding)field.getType()).getElementType())){
			ITypeBinding elemType = ((ArrayTypeBinding)field.getType()).getElementType();
			if (elemType.isNullable()) {
				problemRequestor.acceptProblem(errorNode, 
						IProblemRequestor.IBMIPROGRAM_ARRAY_NULLABLE_PARM_STRUCT_INVALID, 
						new String[] {parm.getCaseSensitiveName(), field.getDeclaringPart().getCaseSensitiveName(), field.getCaseSensitiveName(), elemType + "?[]"});
				return;
			}
		}
				
		if (requiresAS400TypeAnnotation(field.getType())) {
			problemRequestor.acceptProblem(errorNode, 
					IProblemRequestor.IBMIPROGRAM_PARM_STRUCT_REQUIRES_AS400, 
					new String[] {parm.getCaseSensitiveName(), field.getDeclaringPart().getCaseSensitiveName(), field.getCaseSensitiveName()});
			
		}
		
		
		validateFieldsInStructure(parm, field.getType(), errorNode, problemRequestor);
	}

	
	private void validateReturns(NestedFunction function, IFunctionBinding funcBinding, IProblemRequestor problemRequestor) {
		if (!Binding.isValidBinding(funcBinding.getReturnType())) {
			return;
		}
		
		//Returns is only valid for service programs
		
		IAnnotationBinding ibmiAnn = funcBinding.getAnnotation(IBMiProgramAnnotationTypeBinding.pkgName, IBMiProgramAnnotationTypeBinding.name);
		if (ibmiAnn == null) {  //sanity check, this should never happen
			return;
		}
		
		IDataBinding srvPgmAnn = (AnnotationFieldBinding)ibmiAnn.findData("isServiceProgram");
		if (!Binding.isValidBinding(srvPgmAnn) || ((AnnotationFieldBinding)srvPgmAnn).getValue() != Boolean.YES) {
			problemRequestor.acceptProblem(function.getReturnDeclaration(), IProblemRequestor.IBMIPROGRAM_ONLY_SERVICE_CAN_RETURN, new String[] {function.getName().getCaseSensitiveIdentifier()});			
		}
		
		if (funcBinding.getReturnType() != PrimitiveTypeBinding.getInstance(Primitive.INT)) {
			problemRequestor.acceptProblem(function.getReturnDeclaration(), IProblemRequestor.IBMIPROGRAM_CAN_ONLY_RETURN_INT, new String[] {function.getName().getCaseSensitiveIdentifier()});			
		}
	}
	
	private void validateFunctionBodyIsEmpty(NestedFunction function, IProblemRequestor problemRequestor) {
		if (function.getStmts() != null && function.getStmts().size() > 0) {
			
			Iterator i = function.getStmts().iterator();
			while (i.hasNext()) {
				Node node = (Node)i.next();
				if (node instanceof Statement) {
					problemRequestor.acceptProblem(node, IProblemRequestor.IBMIPROGRAM_CANNOT_HAVE_STMTS, new String[] {function.getName().getCaseSensitiveIdentifier()});
				}
			}
		}
	}
	
	private void validateContainerIsCorrect(IFunctionBinding funcBinding, Node errorNode, IProblemRequestor problemRequestor) {
		// Only allowed on function of Programs, Libraries, Services, and Basic Handlers
		IPartBinding part = funcBinding.getDeclarer();
		
		if (Binding.isValidBinding(part)) {
			if (ITypeBinding.PROGRAM_BINDING == part.getKind()) {
				return;
			}
			if (ITypeBinding.LIBRARY_BINDING == part.getKind()) {
				return;
			}
			if (ITypeBinding.SERVICE_BINDING == part.getKind()) {
				return;
			}
			if (ITypeBinding.HANDLER_BINDING == part.getKind()) {
				//must be basic handler!
				if (part.getSubType() == null) {
					return;
				}
			}
		}
		
		//If we got this far, the container is invalid!
		problemRequestor.acceptProblem(errorNode, IProblemRequestor.IBMIPROGRAM_CONTAINER_INVALID, new String[] {funcBinding.getCaseSensitiveName()});


	}
	
	public static boolean isValidAS400Type(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return true; //avoid excess error messages
		}
		
		if (type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
			Primitive prim = ((PrimitiveTypeBinding)type).getPrimitive();
			return 
				prim == Primitive.SMALLINT ||
				prim == Primitive.INT ||
				prim == Primitive.BIGINT ||
				prim == Primitive.DECIMAL ||
				prim == Primitive.SMALLFLOAT ||
				prim == Primitive.FLOAT ||
				prim == Primitive.DATE ||
				prim == Primitive.TIME ||
				prim == Primitive.TIMESTAMP ||				
				prim == Primitive.STRING;
		}
		
		if (type.getKind() == ITypeBinding.HANDLER_BINDING) {
			return true;
		}
		
		if (type.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING) {
			return true;
		}
		
		if(type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
			ArrayTypeBinding arr = (ArrayTypeBinding) type;
			if (!Binding.isValidBinding(arr.getElementType())) {
				return true; //avoid excess error messages
			}
			if (arr.getElementType().getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
				return false;
			}
			return isValidAS400Type(arr.getElementType());
			
		}
		return false;
	}

	public static boolean requiresAS400TypeAnnotation(ITypeBinding type) {
		if (!Binding.isValidBinding(type)) {
			return false; //avoid excess error messages
		}
		
		if (type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
			PrimitiveTypeBinding primType = (PrimitiveTypeBinding)type;
			Primitive prim = primType.getPrimitive();
			
			if (prim == Primitive.DECIMAL) {
				return primType.isReference();
			}
			
			if (prim == Primitive.TIMESTAMP) {
				return primType.isReference();
			}
			
			if (prim == Primitive.STRING) {
				return primType.getLength() == 0;
			}			
			return false;
		}
		
		if(type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
			ArrayTypeBinding arr = (ArrayTypeBinding) type;
			if (!Binding.isValidBinding(arr.getElementType())) {
				return false; //avoid excess error messages
			}
			if (arr.getElementType().getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
				return false;
			}
			return requiresAS400TypeAnnotation(arr.getElementType());
			
		}
		return false;
	}

	protected Object getValue(IAnnotationBinding ann, String fieldName) {
		if (!Binding.isValidBinding(ann)) {
			return null;
		}
		IDataBinding db = ann.findData(fieldName);
		if (!Binding.isValidBinding(db)) {
			return null;
		}
		return ((AnnotationFieldBinding)db).getValue();
	}

}
