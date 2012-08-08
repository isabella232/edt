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
package org.eclipse.edt.mof.eglx.jtopen.validation.annotation;

import java.util.Map;

import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IAnnotationValidationRule;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;



/**
 * @author svihovec
 *
 */
public class IBMiProgramValidator implements IAnnotationValidationRule {

	@Override
	public void validate(Node errorNode, Node target,
			Member targetTypeBinding, Map allAnnotations,
			IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		
		if (!(targetTypeBinding instanceof Function) || !(target instanceof NestedFunction)) {
			return;
		}
		
		validateContainerIsCorrect((Function)targetTypeBinding, errorNode, problemRequestor);		
		validateFunctionBodyIsEmpty((Function)targetTypeBinding, target, problemRequestor);
		validateReturns((NestedFunction)target, (Function)targetTypeBinding, problemRequestor);
		
		Annotation annotation = targetTypeBinding.getAnnotation("eglx.jtopen.IBMiProgram");
		if (annotation == null) { //sanity check, should never happen
			return;
		}
		
		Object obj = annotation.getValue("parameterAnnotations");
		if (obj == null) {
			//If a paramterAnnotations was specified, the paramters are validated in
			//IBMiProgramParameterAnnotationsValidator
			validateParmsDoNotRequireParameterAnnotations((Function)targetTypeBinding, errorNode, problemRequestor);
		}
		
		validateParameters((Function)targetTypeBinding, obj, errorNode, problemRequestor);
	}
	
	private void validateParameters(Function funcBinding, Object parmAnnValue, Node errorNode, IProblemRequestor problemRequestor) {
		int index = -1;
		
		Object[] parmAnnArr = null;
		if (parmAnnValue instanceof Object[] && ((Object[])parmAnnValue).length == funcBinding.getParameters().size()) {
			parmAnnArr = ((Object[])parmAnnValue);
		}
		
		for(FunctionParameter parm : funcBinding.getParameters()) {
			index = index + 1;
			if (parm.getType() != null) {
				
				//if a parameterAnnotationArray entry exists for the parm, the type will have already been validated
				if (parmAnnArr == null || parmAnnArr[index] == null) {
				
					if (!isValidAS400Type(parm.getType())) {
						problemRequestor.acceptProblem(errorNode, 
								IBMiResourceKeys.IBMIPROGRAM_PARM_TYPE_INVALID, 
								IMarker.SEVERITY_ERROR, 
								new String[] {parm.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
						continue;
					}

					if (parm.isNullable()) {
						problemRequestor.acceptProblem(errorNode, 
								IBMiResourceKeys.IBMIPROGRAM_NULLABLE_PARM_INVALID, 
								IMarker.SEVERITY_ERROR, 
								new String[] {parm.getType().getEClass().getName() + "?", parm.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
						continue;
					}
					
					if (parm.getType() instanceof ArrayType && ((ArrayType)parm.getType()).getElementType() != null){
						if (((ArrayType)parm.getType()).elementsNullable()) {
							problemRequestor.acceptProblem(errorNode, 
									IBMiResourceKeys.IBMIPROGRAM_ARRAY_NULLABLE_PARM_INVALID, 
									IMarker.SEVERITY_ERROR, 
									new String[] {((ArrayType)parm.getType()).getElementType().getEClass().getName() + "?[]", parm.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
							continue;
						}
					}
					
					validateFieldsInStructure(parm, parm.getType(), errorNode, problemRequestor);
				}
			}
		}
	}

	
	private void validateParmsDoNotRequireParameterAnnotations(Function funcBinding, Node errorNode, IProblemRequestor problemRequestor) {
		for(FunctionParameter parm : funcBinding.getParameters()){
			if (requiresAS400TypeAnnotation(parm.getType())) {
				problemRequestor.acceptProblem(errorNode, 
						IBMiResourceKeys.PROGRAM_PARAMETER_ANNOTATION_REQUIRED, 
						IMarker.SEVERITY_ERROR, 
						new String[] {parm.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
				
			}
		}
	}
	
	private void validateFieldsInStructure(FunctionParameter parm, Type type,  Node errorNode, IProblemRequestor problemRequestor) {
		if (type != null) {
			if (type instanceof Handler) {
				validateStructPartFields(parm, (Handler)type, errorNode, problemRequestor);
				return;
			}
			
			if (type instanceof Record) {
				validateStructPartFields(parm, (Record)type, errorNode, problemRequestor);
				return;
			}
			
			if (type instanceof ArrayType && ((ArrayType)type).getElementType() != null) {
				validateFieldsInStructure(parm, ((ArrayType)type).getElementType(), errorNode, problemRequestor);
			}
		}
	}
	
	private void validateStructPartFields(FunctionParameter parm, StructPart binding,  Node errorNode, IProblemRequestor problemRequestor) {
		for(Member member : binding.getMembers()) {
			if (member.getAccessKind() != AccessKind.ACC_PRIVATE && member instanceof Field) {
				validateField(parm, (Field)member, binding.getCaseSensitiveName(), errorNode, problemRequestor);
			}
		}
	}
	
	private AbstractStructParameterAnnotationValidator getAS400ParmValidator(Field binding) {
		for(Annotation annotation : binding.getAnnotations()) {
			AbstractStructParameterAnnotationValidator val = IBMiProgramParameterAnnotationsValidator.getValidator(annotation);
			if (val != null) {
				return val;
			}
		}
		return null;
	}
	
	private void validateField(FunctionParameter parm, Field field, String containerName,  Node errorNode, IProblemRequestor problemRequestor) {
		if (field.getType() == null) {
			return;
		}
		
		//if a AS400 annotation exists for the field, the type will have already been validated
		if (getAS400ParmValidator(field) != null) {
			return;
		}
		
		if (!isValidAS400Type(field.getType())) {
			problemRequestor.acceptProblem(errorNode, 
					IBMiResourceKeys.IBMIPROGRAM_PARM_STRUCT_TYPE_INVALID, 
					IMarker.SEVERITY_ERROR, 
					new String[] {parm.getCaseSensitiveName(), containerName, field.getCaseSensitiveName(), field.getType().getTypeSignature()}, IBMiResourceKeys.getResourceBundleForKeys());
			return;
		}

		if (field.isNullable()) {
			problemRequestor.acceptProblem(errorNode, 
					IBMiResourceKeys.IBMIPROGRAM_NULLABLE_PARM_STRUCT_INVALID, 
					IMarker.SEVERITY_ERROR, 
					new String[] {parm.getCaseSensitiveName(), containerName, field.getCaseSensitiveName(), field.getType().getTypeSignature() + "?"}, IBMiResourceKeys.getResourceBundleForKeys());
			return;
		}
		
		if (field.getType() instanceof ArrayType && ((ArrayType)field.getType()).getElementType() != null){
			if (((ArrayType)field.getType()).elementsNullable()) {
				problemRequestor.acceptProblem(errorNode, 
						IBMiResourceKeys.IBMIPROGRAM_ARRAY_NULLABLE_PARM_STRUCT_INVALID, 
						IMarker.SEVERITY_ERROR, 
						new String[] {parm.getCaseSensitiveName(), containerName, field.getCaseSensitiveName(), ((ArrayType)field.getType()).getElementType() + "?[]"}, IBMiResourceKeys.getResourceBundleForKeys());
				return;
			}
		}
				
		if (requiresAS400TypeAnnotation(field.getType())) {
			problemRequestor.acceptProblem(errorNode, 
					IBMiResourceKeys.IBMIPROGRAM_PARM_STRUCT_REQUIRES_AS400, 
					IMarker.SEVERITY_ERROR, 
					new String[] {parm.getCaseSensitiveName(), containerName, field.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
			
		}
		
		
		validateFieldsInStructure(parm, field.getType(), errorNode, problemRequestor);
	}

	
	private void validateReturns(NestedFunction function, Function funcBinding, IProblemRequestor problemRequestor) {
		if (funcBinding.getReturnType() == null) {
			return;
		}
		
		//Returns is only valid for service programs
		
		Annotation ibmiAnn = funcBinding.getAnnotation("eglx.jtopen.IBMiProgram");
		if (ibmiAnn == null) {  //sanity check, this should never happen
			return;
		}
		
		Boolean srvPgmAnn = (Boolean)ibmiAnn.getValue("isServiceProgram");
		if (srvPgmAnn == null || srvPgmAnn != Boolean.YES) {
			problemRequestor.acceptProblem(function.getReturnDeclaration(), IBMiResourceKeys.IBMIPROGRAM_ONLY_SERVICE_CAN_RETURN, IMarker.SEVERITY_ERROR, new String[] {function.getName().getCaseSensitiveIdentifier()}, IBMiResourceKeys.getResourceBundleForKeys());			
		}
		
		if (!MofConversion.Type_Int.equalsIgnoreCase(funcBinding.getReturnType().getTypeSignature())) {
			problemRequestor.acceptProblem(function.getReturnDeclaration(), IBMiResourceKeys.IBMIPROGRAM_CAN_ONLY_RETURN_INT, IMarker.SEVERITY_ERROR, new String[] {function.getName().getCaseSensitiveIdentifier()}, IBMiResourceKeys.getResourceBundleForKeys());			
		}
	}
	
	private void validateFunctionBodyIsEmpty(Function function, Node node, IProblemRequestor problemRequestor) {
		if (function.getStatementBlock() != null && function.getStatementBlock().getStatements() != null && 
				function.getStatementBlock().getStatements().size() > 0) {
			
			for(Statement stmt : function.getStatementBlock().getStatements()) {
				problemRequestor.acceptProblem((Element)stmt, IBMiResourceKeys.IBMIPROGRAM_CANNOT_HAVE_STMTS, IMarker.SEVERITY_ERROR, new String[] {function.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
			}
		}
	}
	
	private void validateContainerIsCorrect(Function funcBinding, Node errorNode, IProblemRequestor problemRequestor) {
		// Only allowed on function of Programs, Libraries, Services, and Basic Handlers
		Container part = funcBinding.getContainer();
		
		if (part != null) {
			if (part instanceof Program) {
				return;
			}
			if (part instanceof Library) {
				return;
			}
			if (part instanceof Service) {
				return;
			}
			if (part instanceof Handler && ((Handler) part).getStereotype() == null) {
				//must be basic handler!
				return;
			}
		}
		
		//If we got this far, the container is invalid!
		problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.IBMIPROGRAM_CONTAINER_INVALID, IMarker.SEVERITY_ERROR, new String[] {funcBinding.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());


	}
	
	public static boolean isValidAS400Type(Type type) {
		if (type == null) {
			return true; //avoid excess error messages
		}
		
		if( type.equals(TypeUtils.Type_SMALLINT) ||
				type.equals(TypeUtils.Type_INT) ||
				type.equals(TypeUtils.Type_BIGINT) ||
				type.equals(TypeUtils.Type_DECIMAL) ||
				type.equals(TypeUtils.Type_SMALLFLOAT) ||
				type.equals(TypeUtils.Type_FLOAT) ||
				type.equals(TypeUtils.Type_DATE) ||
				type.equals(TypeUtils.Type_TIME) ||
				type.equals(TypeUtils.Type_TIMESTAMP) ||
				type.equals(TypeUtils.Type_STRING)){
			return true;
		}
		if (type instanceof Handler) {
			return true;
		}
		
		if (type instanceof Record) {
			return true;
		}
		
		if(type instanceof ArrayType) {
			if (((ArrayType)type).getElementType() ==null) {
				return true; //avoid excess error messages
			}
			if (((ArrayType)type) instanceof ArrayType) {
				return false;
			}
			return isValidAS400Type(((ArrayType)type).getElementType());
			
		}
		return false;
	}

	public static boolean requiresAS400TypeAnnotation(Type type) {
		if (type == null) {
			return false; //avoid excess error messages
		}
		
		if( type.equals(TypeUtils.Type_SMALLINT) ||
				type.equals(TypeUtils.Type_INT) ||
				type.equals(TypeUtils.Type_BIGINT) ||
				type.equals(TypeUtils.Type_DECIMAL) ||
				type.equals(TypeUtils.Type_SMALLFLOAT) ||
				type.equals(TypeUtils.Type_FLOAT) ||
				type.equals(TypeUtils.Type_DATE) ||
				type.equals(TypeUtils.Type_TIME) ||
				type.equals(TypeUtils.Type_TIMESTAMP) ||
				type.equals(TypeUtils.Type_STRING)){
			return TypeUtils.isReferenceType(type);
		}
		
		if(type instanceof ArrayType) {
			if (((ArrayType)type).getElementType() == null) {
				return false; //avoid excess error messages
			}
			if (((ArrayType)type).getElementType() instanceof ArrayType) {
				return false;
			}
			return requiresAS400TypeAnnotation(((ArrayType)type).getElementType());
			
		}
		return false;
	}

}
