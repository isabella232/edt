package org.eclipse.edt.mof.eglx.jtopen.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.binding.IValidationProxy;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.ReturnsDeclaration;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.AbstractFunctionValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.jtopen.Utils;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;
import org.eclipse.edt.mof.impl.AbstractVisitor;
import org.eclipse.edt.mof.utils.EList;
import org.eclipse.edt.mof.utils.NameUtile;

public class IBMiFunctionValidator extends AbstractFunctionValidator{
	
	private Annotation annotation;
	private NestedFunction nestedFunction;
	private Map<Object, Object> parameterAnnotations;
	
	public boolean visit(NestedFunction nestedFunction){
		if (nestedFunction.getName().resolveMember() instanceof Function) {
		
			validateContainerIsCorrect(((IRPartBinding)declaringPart).getIrPart(), nestedFunction);		
			validateFunctionBodyIsEmpty((Function)nestedFunction.getName().resolveMember(), nestedFunction);

			this.nestedFunction = nestedFunction;
			annotation = nestedFunction.getName().resolveMember().getAnnotation("eglx.jtopen.annotations.IBMiProgram");
			
			Object obj = annotation.getValue("parameterAnnotations");
			if(((EList<?>)obj).size() > 0){
				if (((EList<?>)obj).size() != nestedFunction.getFunctionParameters().size()) {
					problemRequestor.acceptProblem(nestedFunction, IBMiResourceKeys.WRONG_NUMBER_OF_PARAMETER_ANNOTATIONS, IMarker.SEVERITY_ERROR, new String[] {nestedFunction.getName().getCaseSensitiveIdentifier()}, IBMiResourceKeys.getResourceBundleForKeys());
				}
			}
			else{
				for(Object o : nestedFunction.getFunctionParameters()){
					((EList)obj).add(IrFactory.INSTANCE.createNullLiteral());
				}
			}
			parameterAnnotations = new HashMap<Object, Object>(((EList<?>)obj).size());
			for(int idx = 0; idx < ((EList<?>)obj).size(); idx++){
				parameterAnnotations.put(nestedFunction.getFunctionParameters().get(idx), ((EList<?>)obj).get(idx));
			}
			
		}
		return true;
	}

	@Override
	public boolean visit(final org.eclipse.edt.compiler.core.ast.FunctionParameter functionParameter) {
		Member parm = functionParameter.getName().resolveMember();
		if (parm.getType() != null) {
			Object parmAnn = parameterAnnotations.get(functionParameter);
			//if a parameterAnnotationArray entry exists for the parm, the type will have already been validated
			boolean requiresAS400TypeAnnotationTest = true;
			if (parmAnn != null && !(parmAnn instanceof NullLiteral)) {
				if(parmAnn instanceof Annotation){
					IValidationProxy proxy = AnnotationValidator.getValidationProxy((Annotation)parmAnn);
					if (proxy != null) {
						for (org.eclipse.edt.compiler.binding.AnnotationValidationRule rule : proxy.getAnnotationValidators()) {
							requiresAS400TypeAnnotationTest = false;
							Map<String, Object> annotations = new HashMap<String, Object>(1);
							annotations.put(NameUtile.getAsName(((Annotation) parmAnn).getEClass().getETypeSignature()), parmAnn);
							rule.validate(functionParameter, functionParameter, parm, annotations, problemRequestor, compilerOptions);
						}
					}
				}
				else{
					requiresAS400TypeAnnotationTest = false;
					problemRequestor.acceptProblem(functionParameter, 
							IBMiResourceKeys.PARAMETER_ANNOTATION_INVALID, 
							IMarker.SEVERITY_ERROR, 
							new String[] {parmAnn.toString(), functionParameter.getName().getCaseSensitiveIdentifier()}, IBMiResourceKeys.getResourceBundleForKeys());
				}
			}
			
			if(requiresAS400TypeAnnotationTest && Utils.requiresAS400TypeAnnotation(parm.getType())) {
				problemRequestor.acceptProblem(functionParameter, 
						IBMiResourceKeys.PROGRAM_PARAMETER_ANNOTATION_REQUIRED, 
						IMarker.SEVERITY_ERROR, 
						new String[] {parm.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
			}

			if (!Utils.isValidAS400Type(parm.getType())) {
				problemRequestor.acceptProblem(functionParameter, 
						IBMiResourceKeys.IBMIPROGRAM_PARM_TYPE_INVALID, 
						IMarker.SEVERITY_ERROR, 
						new String[] {parm.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
			}

			if (parm.isNullable()) {
				problemRequestor.acceptProblem(functionParameter, 
						IBMiResourceKeys.IBMIPROGRAM_NULLABLE_PARM_INVALID, 
						IMarker.SEVERITY_ERROR, 
						new String[] {BindingUtil.getTypeName(parm), parm.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
			}
				
			if (parm.getType() instanceof ArrayType && ((ArrayType)parm.getType()).getElementType() != null){
				if (((ArrayType)parm.getType()).elementsNullable()) {
					problemRequestor.acceptProblem(functionParameter, 
							IBMiResourceKeys.IBMIPROGRAM_ARRAY_NULLABLE_PARM_INVALID, 
							IMarker.SEVERITY_ERROR, 
							new String[] {BindingUtil.getShortTypeString(((ArrayType)parm.getType()).getElementType(), true), parm.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
				}
			}
			
			if(parm.getType() instanceof Record || parm.getType() instanceof Handler || parm.getType() instanceof ArrayType){
				parm.getType().accept(new ComplexTypes((FunctionParameter)parm, functionParameter));
			}
		}
		return false;
	}

	protected class ComplexTypes extends AbstractVisitor{
		private FunctionParameter parameter;
		private org.eclipse.edt.compiler.core.ast.FunctionParameter functionParameter;
		private List<String> analyzedTypes = new ArrayList<String>();
		
		
		public ComplexTypes(FunctionParameter parameter, org.eclipse.edt.compiler.core.ast.FunctionParameter functionParameter) {
			this.parameter = parameter;
			this.functionParameter = functionParameter;
		}
		public boolean visit(Type type) {
			if(!analyzedTypes.contains(type.getTypeSignature()) &&
					(type instanceof Record || type instanceof Handler)){
				analyzedTypes.add(type.getTypeSignature());
				return true;
			}
			return false;
		}
		public boolean visit(ArrayType arrayType) {
			arrayType.getElementType().accept(this);
			return false;
		}
		public boolean visit(Field field){
			validateField(parameter, field);
			return true;
		}				
		private void validateField(FunctionParameter parm, Field field) {
			if (field.getType() == null) {
				return;
			}
			String containerName = "";
			if(field.getContainer() instanceof NamedElement){
				containerName = ((NamedElement)field.getContainer()).getCaseSensitiveName();
			}
			
			if (!Utils.isValidAS400Type(field.getType())) {
				problemRequestor.acceptProblem(functionParameter, 
						IBMiResourceKeys.IBMIPROGRAM_PARM_STRUCT_TYPE_INVALID, 
						IMarker.SEVERITY_ERROR, 
						new String[] {parm.getCaseSensitiveName(), containerName, field.getCaseSensitiveName(), BindingUtil.getTypeName(field)}, IBMiResourceKeys.getResourceBundleForKeys());
				return;
			}

			if (field.isNullable()) {
				problemRequestor.acceptProblem(functionParameter, 
						IBMiResourceKeys.IBMIPROGRAM_NULLABLE_PARM_STRUCT_INVALID, 
						IMarker.SEVERITY_ERROR, 
						new String[] {parm.getCaseSensitiveName(), containerName, field.getCaseSensitiveName(), BindingUtil.getTypeName(field)}, IBMiResourceKeys.getResourceBundleForKeys());
				return;
			}
			
			if (field.getType() instanceof ArrayType && ((ArrayType)field.getType()).getElementType() != null){
				if (((ArrayType)field.getType()).elementsNullable()) {
					problemRequestor.acceptProblem(functionParameter, 
							IBMiResourceKeys.IBMIPROGRAM_ARRAY_NULLABLE_PARM_STRUCT_INVALID, 
							IMarker.SEVERITY_ERROR, 
							new String[] {parm.getCaseSensitiveName(), containerName, field.getCaseSensitiveName(), BindingUtil.getTypeName(field)}, IBMiResourceKeys.getResourceBundleForKeys());
					return;
				}
			}
					
			if (!hasStructAnnotation(field) && Utils.requiresAS400TypeAnnotation(field.getType())) {
				problemRequestor.acceptProblem(functionParameter, 
						IBMiResourceKeys.IBMIPROGRAM_PARM_STRUCT_REQUIRES_AS400, 
						IMarker.SEVERITY_ERROR, 
						new String[] {parm.getCaseSensitiveName(), containerName, field.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
				
			}
		}
		private boolean hasStructAnnotation(Field binding) {
			for(Annotation annotation : binding.getAnnotations()) {
				if(NameUtile.equals("eglx.jtopen.annotations", annotation.getEClass().getPackageName())){
					return true;
				}
			}
			return false;
		}
	}
	@Override
	public boolean visit(ReturnsDeclaration returnsDeclaration) {
		Type returnType = returnsDeclaration.getType() != null ? returnsDeclaration.getType().resolveType() : null;
		if (returnType != null) {
		
			//Returns is only valid for service programs
			Object isServiceProgram = annotation.getValue("isServiceProgram");
			if (isServiceProgram == null || 
					(isServiceProgram instanceof Boolean && !(Boolean)isServiceProgram) ||
					(isServiceProgram instanceof org.eclipse.edt.compiler.core.Boolean && !((org.eclipse.edt.compiler.core.Boolean)isServiceProgram).booleanValue())) {
				problemRequestor.acceptProblem(returnsDeclaration, IBMiResourceKeys.IBMIPROGRAM_ONLY_SERVICE_CAN_RETURN, IMarker.SEVERITY_ERROR, new String[] {nestedFunction.getName().getCaseSensitiveIdentifier()}, IBMiResourceKeys.getResourceBundleForKeys());			
			}
			
			if (!TypeUtils.Type_INT.equals(returnType)) {
				problemRequestor.acceptProblem(returnsDeclaration, IBMiResourceKeys.IBMIPROGRAM_CAN_ONLY_RETURN_INT, IMarker.SEVERITY_ERROR, new String[] {nestedFunction.getName().getCaseSensitiveIdentifier()}, IBMiResourceKeys.getResourceBundleForKeys());			
			}
		}
		return false;
	}
	private void validateFunctionBodyIsEmpty(Function function, NestedFunction nestedFunction) {
		if (nestedFunction != null && nestedFunction.getStmts() != null) {
			for(Object stmt : nestedFunction.getStmts()) {
				if(stmt instanceof Statement){
					problemRequestor.acceptProblem((Statement)stmt, IProblemRequestor.PROXY_FUNCTIONS_CANNOT_HAVE_STMTS, IMarker.SEVERITY_ERROR, new String[] {function.getCaseSensitiveName()});
				}
			}
		}
	}
	private void validateContainerIsCorrect(Part part, NestedFunction errorNode) {
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
	
}
