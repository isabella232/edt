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

import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IValueValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.jtopen.messages.IBMiResourceKeys;


public class IBMiProgramParameterAnnotationsValidator implements IValueValidationRule {

	public void validate(Node errorNode, Node target, Annotation annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		Function functionBinding = getFunctionBinding(target);
		if (functionBinding == null) {
			return;
		}
		Object[] values = (Object[])annotationBinding.getValue();
		if (values == null) {
			return;
		}
		
		if (values.length != functionBinding.getParameters().size()) {
			problemRequestor.acceptProblem(errorNode, IBMiResourceKeys.WRONG_NUMBER_OF_PARAMETER_ANNOTATIONS, IMarker.SEVERITY_ERROR, new String[] {functionBinding.getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
			return;
		}
		
		for (int i = 0; i < values.length; i++) {
			
			if (values[i] == null) {
				//If null was specified, make sure that we dont need a parameter annotation for the parm type
				if (requiresAS400TypeAnnotation(functionBinding.getParameters().get(i).getType())) {
					problemRequestor.acceptProblem(getNodeForArrayEntry(errorNode, i), 
							IBMiResourceKeys.PROGRAM_PARAMETER_ANNOTATION_REQUIRED, 
							IMarker.SEVERITY_ERROR, 
							new String[] {functionBinding.getParameters().get(i).getCaseSensitiveName()}, IBMiResourceKeys.getResourceBundleForKeys());
				}
				
			}
			else {
				AbstractStructParameterAnnotationValidator validator = getValidator(values[i]);
				
				if (validator == null) {
					problemRequestor.acceptProblem(getNodeForArrayEntry(errorNode, i), 
											IBMiResourceKeys.PARAMETER_ANNOTATION_INVALID, 
											IMarker.SEVERITY_ERROR, 
											new String[] {Integer.toString(i)}, IBMiResourceKeys.getResourceBundleForKeys());
				}
				else {
					validator.validate((Annotation)values[i], getNodeForArrayEntry(errorNode, i), functionBinding.getParameters().get(i), problemRequestor);
				}
			}
		}
	}
	
	public static AbstractStructParameterAnnotationValidator getValidator(Object obj) {
		if (!(obj instanceof Annotation)) {
			return null;
		}
		
		String proxy = ((AnnotationType)(((Annotation)obj).getEClass())).getValidationProxy();
		Object proxyInstance = null;
		
		try {
			Class<?> proxyClass = AbstractStructParameterAnnotationValidator.class.forName(proxy);
			proxyInstance = proxyClass.newInstance();
		} catch (Exception e) {
		}
		
		if (proxyInstance instanceof AbstractStructParameterAnnotationValidator) {
			return (AbstractStructParameterAnnotationValidator)proxyInstance;
		}
		
		return null;
	}
	
	private Node getNodeForArrayEntry(Node node, final int index) {
		final Node[] result = new Node[] {node};
		DefaultASTVisitor visitor = new DefaultASTVisitor() {
			public boolean visit(ParenthesizedExpression parenthesizedExpression) {
				return true;
			}
			public boolean visit(ArrayLiteral arrayLiteral) {
				if (index < arrayLiteral.getExpressions().size()) {
					result[0] = (Node)arrayLiteral.getExpressions().get(index);
				}
				return false;
			}
		};
		node.accept(visitor);
		return result[0];
	}
	
	private Function getFunctionBinding(Node node) {
		if (node == null) {
			return null;
		}
		if (node instanceof NestedFunction) {
			NestedFunction function = (NestedFunction) node;
			Member binding = function.getName().resolveMember();
			if (binding instanceof Function) {
				return (Function)binding;
			}
		}
		return getFunctionBinding(node.getParent());
	}
	public static boolean requiresAS400TypeAnnotation(Type type) {
		if (type == null) {
			return false; //avoid excess error messages
		}
		
		if( TypeUtils.isPrimitive(type)){
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
	public static boolean isValidAS400Type(Type type) {
		if (type == null) {
			return true; //avoid excess error messages
		}
		
		if( type.equals(TypeUtils.isPrimitive(type))){
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
}
