package org.eclipse.edt.mof.eglx.jtopen.validation;

import org.eclipse.edt.compiler.FunctionValidator;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;
import org.eclipse.edt.mof.eglx.jtopen.validation.annotation.AbstractStructParameterAnnotationValidator;
import org.eclipse.edt.mof.eglx.jtopen.validation.annotation.IBMiProgramParameterAnnotationsValidator;

public class IBMiFunctionValidator implements FunctionValidator{

	@Override
	public void validate(Node node, IPartBinding declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		
	}

	@Override
	public void validateFunction(NestedFunction nestedFunction, IPartBinding declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (!(nestedFunction.getName().resolveMember() instanceof Function)) {
			return;
		}
		
		validateContainerIsCorrect(((IRPartBinding)declaringPart).getIrPart(), nestedFunction, problemRequestor);		
		validateFunctionBodyIsEmpty((Function)nestedFunction.getName().resolveMember(), nestedFunction, problemRequestor);
		validateReturns(nestedFunction, (Function)nestedFunction.getName().resolveMember(), problemRequestor);
		
		Annotation annotation = nestedFunction.getName().resolveMember().getAnnotation("eglx.jtopen.annotations.IBMiProgram");
		
		Object obj = annotation.getValue("parameterAnnotations");
		if (obj == null) {
			//If a paramterAnnotations was specified, the paramters are validated in
			//IBMiProgramParameterAnnotationsValidator
			validateParmsDoNotRequireParameterAnnotations((Function)nestedFunction.getName().resolveMember(), nestedFunction, problemRequestor);
		}
		
		validateParameters((Function)nestedFunction.getName().resolveMember(), obj, nestedFunction, problemRequestor);
	}

	@Override
	public void validateFunction(Constructor constructor, IPartBinding declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		
	}

	private void validateFunctionBodyIsEmpty(Function function, Node node, IProblemRequestor problemRequestor) {
		if (function.getStatementBlock() != null && function.getStatementBlock().getStatements() != null && 
				function.getStatementBlock().getStatements().size() > 0) {
			
			for(Statement stmt : function.getStatementBlock().getStatements()) {
				problemRequestor.acceptProblem(stmt, IProblemRequestor.PROXY_FUNCTIONS_CANNOT_HAVE_STMTS, IMarker.SEVERITY_ERROR, new String[] {function.getCaseSensitiveName()});
			}
		}
	}
	private void validateContainerIsCorrect(Part part, NestedFunction errorNode, IProblemRequestor problemRequestor) {
		// Only allowed on function of Programs, Libraries, Services, and Basic Handlers
		
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
		problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.IBMIPROGRAM_CONTAINER_INVALID, IMarker.SEVERITY_ERROR, new String[] {errorNode.getName().getCaseSensitiveIdentifier()}, IBMiResourceKeys.getResourceBundleForKeys());


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
				
					if (!IBMiProgramParameterAnnotationsValidator.isValidAS400Type(parm.getType())) {
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
	
	private void validateField(FunctionParameter parm, Field field, String containerName,  Node errorNode, IProblemRequestor problemRequestor) {
		if (field.getType() == null) {
			return;
		}
		
		//if a AS400 annotation exists for the field, the type will have already been validated
		if (getAS400ParmValidator(field) != null) {
			return;
		}
		
		if (!IBMiProgramParameterAnnotationsValidator.isValidAS400Type(field.getType())) {
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
				
		if (IBMiProgramParameterAnnotationsValidator.requiresAS400TypeAnnotation(field.getType())) {
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
	
	private void validateParmsDoNotRequireParameterAnnotations(Function funcBinding, Node errorNode, IProblemRequestor problemRequestor) {
		for(FunctionParameter parm : funcBinding.getParameters()){
			if (IBMiProgramParameterAnnotationsValidator.requiresAS400TypeAnnotation(parm.getType())) {
				problemRequestor.acceptProblem(errorNode, 
						IBMiResourceKeys.PROGRAM_PARAMETER_ANNOTATION_REQUIRED, 
						IMarker.SEVERITY_ERROR, 
						new String[] {parm.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
				
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
	
}
