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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;


/**
 * @author demurray
 */
public class JavaObjectFieldTypeValidator extends DefaultFieldContentAnnotationValidationRule {
	
	private static Set invalidPrimitives = null;

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		ITypeBinding type = containerBinding.getType();
		if(Binding.isValidBinding(type)) {
			checkTypeValidInExternalType(errorNode, containerBinding.getDeclaringPart(), type.getBaseType(), false, problemRequestor);			
		}
		checkDataDeclarationValidInExternalType(errorNode, containerBinding, problemRequestor);
	}
	
	public void validateFunctionParameter(FunctionParameter fParameter, FunctionParameterBinding parameterBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		ITypeBinding parameterTypeBinding = parameterBinding.getType();
		if(Binding.isValidBinding(parameterTypeBinding)) {			
			checkTypeValidInExternalType(fParameter.getType().getBaseType(), parameterBinding.getDeclaringPart(), parameterTypeBinding.getBaseType(), true, problemRequestor);
			
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == parameterTypeBinding.getKind() &&
			   !Primitive.isDateTimeType(((PrimitiveTypeBinding) parameterTypeBinding).getPrimitive()) &&
			   !parameterBinding.isInput()) {
				problemRequestor.acceptProblem(
					fParameter,
					IProblemRequestor.IN_MODIFIER_REQUIRED_FOR_PRIMITIVE_JAVAOBJECT_FUNCTION_PARAMETERS);
			}
		}			
	}
	
	public void validateFunctionReturnType(Type typeNode, ITypeBinding typeBinding, IPartBinding declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		checkTypeValidInExternalType(typeNode.getBaseType(), declaringPart, typeBinding.getBaseType(), true, problemRequestor);
	}
	
	public static void checkTypeValidInExternalType(Node errorNode, IPartBinding partBinding, ITypeBinding typeBinding, boolean issueErrorForDelegate, IProblemRequestor problemRequestor) {
		if(!partBinding.isSystemPart()) {
			if(IBinding.NOT_FOUND_BINDING != typeBinding && typeBinding != null) {
				boolean typeValid = true;
				switch(typeBinding.getKind()) {
					case ITypeBinding.FIXED_RECORD_BINDING:
					case ITypeBinding.FLEXIBLE_RECORD_BINDING:
					case ITypeBinding.DICTIONARY_BINDING:
					case ITypeBinding.ARRAYDICTIONARY_BINDING:
					case ITypeBinding.HANDLER_BINDING:
						typeValid = false;
						break;
					case ITypeBinding.DELEGATE_BINDING:
						typeValid = !issueErrorForDelegate;
						break;
					case ITypeBinding.PRIMITIVE_TYPE_BINDING:
						typeValid = !getInvalidPrimitives().contains(typeBinding);
						break;
				}
				if(!typeValid) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.TYPE_INVALID_IN_EXTERNALTYPE,
						new String[] {StatementValidator.getTypeString(typeBinding)});
				}
			}
		}
	}
	
	public static void checkDataDeclarationValidInExternalType(Node errorNode, IDataBinding binding, IProblemRequestor problemRequestor) {
		if(!binding.getDeclaringPart().isSystemPart()) {
			ITypeBinding tBinding = binding.getType();
			if(Binding.isValidBinding(tBinding)) {
				tBinding = tBinding.getBaseType();
				if(IBinding.NOT_FOUND_BINDING != tBinding && tBinding != null) {
					if(ITypeBinding.DELEGATE_BINDING == tBinding.getKind()) {
						IAnnotationBinding aBinding = binding.getAnnotation(new String[] {"eglx", "lang"}, IEGLConstants.PROPERTY_EVENTLISTENER);
						if(aBinding == null) {						
							problemRequestor.acceptProblem(
								errorNode,
								IProblemRequestor.TYPE_INVALID_IN_EXTERNALTYPE_UNLESS_PROPERTY_SPECIFIED,
								new String[] {StatementValidator.getTypeString(tBinding), IEGLConstants.PROPERTY_EVENTLISTENER});
						}
					}
				}
			}
		}
	}
	
	private static Set getInvalidPrimitives() {
		if(invalidPrimitives == null) {
			invalidPrimitives = new HashSet();
			invalidPrimitives.add(PrimitiveTypeBinding.getInstance(Primitive.BLOB));
			invalidPrimitives.add(PrimitiveTypeBinding.getInstance(Primitive.CLOB));
			invalidPrimitives.add(PrimitiveTypeBinding.getInstance(Primitive.ANY));
		}
		return invalidPrimitives ;
	}
}
