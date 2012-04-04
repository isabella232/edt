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
package org.eclipse.edt.compiler.internal.core.validation.part;

import java.util.Iterator;
import java.util.List;

import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.FunctionParameter.UseType;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.PrimitiveType;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;



/**
 * @author cduval
 *
 */
public class ServiceInterfaceValidatorUtil {

	public static void validateParametersAndReturn(final NestedFunction thisnestedFunction, final boolean isService,final IProblemRequestor problemRequestor ) {
		
		final boolean isOneWay = isOneWay(thisnestedFunction);
		
		thisnestedFunction.accept(new AbstractASTVisitor(){
			Node outparm = null;			
			public boolean visit (FunctionParameter functionParameter){
//				if (isService){
//					if (outparm != null){
//						if(functionParameter.getUseType() == FunctionParameter.UseType.IN ||
//						   functionParameter.getUseType() == FunctionParameter.UseType.INOUT ||
//						   functionParameter.getUseType() == null){
//							problemRequestor.acceptProblem(outparm,
//									IProblemRequestor.OUT_SERVICE_PARAM_DECLARED_BEFORE_IN_OR_INOUT_PARAM);
//							outparm = null;
//						}
//					}else{
//						outparm = functionParameter.getUseType() == FunctionParameter.UseType.OUT? functionParameter: null;
//					}
//				}
				
				if (functionParameter.isParmConst()) {
           			problemRequestor.acceptProblem(functionParameter,
	        				IProblemRequestor.SERVICE_PARM_CANNOT_BE_CONST, 
				              new String[] {
	        				functionParameter.getName().getCanonicalName(),
	        				thisnestedFunction.getName().getCanonicalName()});
				}
				
				if (isOneWay && !isIn(functionParameter)) {
         			problemRequestor.acceptProblem(functionParameter,
	    				IProblemRequestor.ONEWAY_FUNCTION_PARM_MUST_BE_IN, 
			              new String[] {
		  				functionParameter.getName().getCanonicalName(),
		  				thisnestedFunction.getName().getCanonicalName()});
				}
				
				ITypeBinding typeBinding = functionParameter.getType().resolveTypeBinding();
				if (StatementValidator.isValidBinding(typeBinding)){
					boolean typeIsPrimitive = false;
					boolean typeIsLoose = false;
					if (functionParameter.getType().isPrimitiveType()){
						PrimitiveType primType = (PrimitiveType) functionParameter.getType();
						Primitive prim = primType.getPrimitive();
						
						if( Primitive.isLooseType(prim,((PrimitiveTypeBinding)typeBinding).getLength()) ) {
							problemRequestor.acceptProblem(functionParameter.getType(),
									IProblemRequestor.LOOSE_TYPES_NOT_ALLOWED_IN_SERVICE_OR_INTERFACE_FUNC_PARM);
							typeIsLoose = true;
						}
						
						typeIsPrimitive = true;
					}else if (functionParameter.getType().isNameType()){
						if (typeBinding.getKind() == ITypeBinding.DATAITEM_BINDING){
							typeIsPrimitive = true;
						}
					}
					
					if( isUnsupportedTypeInServicesAndInterfaces( typeBinding ) ) {
						problemRequestor.acceptProblem(functionParameter.getType(),
								IProblemRequestor.TYPE_NOT_ALLOWED_IN_SERVICE_OR_INTERFACE_FUNC_PARM,
									new String[] {typeBinding.getCaseSensitiveName()});

					}
					
					if( functionParameter.getAttrType() == FunctionParameter.AttrType.FIELD ) {
						problemRequestor.acceptProblem(functionParameter,
								IProblemRequestor.FIELD_NOT_ALLOWED_IN_SERVICE_OR_INTERFACE_FUNC_PARM);

					}
				}
				
				
				return false;
			}
			
			private boolean isIn(FunctionParameter parm) {
				if (parm.getName().resolveBinding() instanceof FunctionParameterBinding) {
					return ((FunctionParameterBinding)parm.getName().resolveBinding()).isInput();
				}
				
				return parm.getUseType() == UseType.IN;
			}

			public void endVisit(NestedFunction nestedFunction){
				if (nestedFunction.hasReturnType()){
					ITypeBinding typeBinding = nestedFunction.getReturnType().resolveTypeBinding();
					if (StatementValidator.isValidBinding(typeBinding) && isUnsupportedTypeInServicesAndInterfaces( typeBinding )){
						problemRequestor.acceptProblem(nestedFunction.getReturnType(),
								IProblemRequestor.TYPE_NOT_ALLOWED_AS_SERVICE_OR_INTERFACE_FUNC_RETURN,
								new String[] {typeBinding.getCaseSensitiveName()});
					}
					
					if (isOneWay) {
						problemRequestor.acceptProblem(nestedFunction.getReturnType(),
								IProblemRequestor.RETURN_NOT_ALLOWED_FOR_ONEWAY,
								new String[] {});
					}
					
				}
			}
		});		
	}

	public static boolean isUnsupportedTypeInServicesAndInterfaces(ITypeBinding typeBinding){
		switch(typeBinding.getKind()) {
			case ITypeBinding.ARRAYDICTIONARY_BINDING:
			case ITypeBinding.DICTIONARY_BINDING:
			case ITypeBinding.DELEGATE_BINDING:
			
			case ITypeBinding.SERVICE_BINDING:
			case ITypeBinding.INTERFACE_BINDING:
	//		case ITypeBinding.HANDLER_BINDING:
				return true;
				
			case ITypeBinding.PRIMITIVE_TYPE_BINDING:
				PrimitiveTypeBinding primitiveBinding = (PrimitiveTypeBinding)typeBinding;
				Primitive primitive = primitiveBinding.getPrimitive();
				if (primitive == Primitive.ANY ){
					return true;
				}
				return false;
				
			default:
				return false;
		}
	}
	
	private static FunctionBinding getFunctionByName(String name,List list){
		for( Iterator iter = list.iterator(); iter.hasNext(); ) {
			FunctionBinding next = (FunctionBinding) ((NestedFunctionBinding) iter.next()).getType();
			if( InternUtil.intern(next.getName()) == InternUtil.intern( name ) ) {
				return next;
			}
		}
		return null;
	}
	
	private static boolean isOneWay(NestedFunction functionAst) {
		IBinding binding = functionAst.getName().resolveBinding();
		if (StatementValidator.isValidBinding(binding)) {
			IAnnotationBinding ann = binding.getAnnotation(new String[] {"eglx", "services"}, "oneway");
			
			if (ann != null) {
				Object value = ann.getValue();
				
				if (value instanceof Boolean) {
					return ((Boolean)value).booleanValue();
				}
				
				if (value instanceof org.eclipse.edt.compiler.core.Boolean) {
					return value == org.eclipse.edt.compiler.core.Boolean.YES;
				}	
			}
		}
		return false;
	}
}
