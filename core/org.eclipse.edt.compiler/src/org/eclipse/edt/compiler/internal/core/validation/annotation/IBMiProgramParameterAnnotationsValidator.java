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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.edt.compiler.binding.AS400ParmeterAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;


public class IBMiProgramParameterAnnotationsValidator implements IValueValidationRule {

	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		IFunctionBinding functionBinding = getFunctionBinding(target);
		if (!Binding.isValidBinding(functionBinding)) {
			return;
		}
		Object[] values = (Object[])annotationBinding.getValue();
		if (values == null) {
			return;
		}
		
		if (values.length != functionBinding.getParameters().size()) {
			problemRequestor.acceptProblem(errorNode, IProblemRequestor.WRONG_NUMBER_OF_PARAMETER_ANNOTATIONS, new String[] {functionBinding.getCaseSensitiveName()});
			return;
		}
		
		for (int i = 0; i < values.length; i++) {
			
			if (values[i] == null) {
				//If null was specified, make sure that we dont need a parameter annotation for the parm type
				if (IBMiProgramValidator.requiresAS400TypeAnnotation(((FunctionParameterBinding)functionBinding.getParameters().get(i)).getType())) {
					problemRequestor.acceptProblem(getNodeForArrayEntry(errorNode, i), 
							IProblemRequestor.PROGRAM_PARAMETER_ANNOTATION_REQUIRED, 
							new String[] {((FunctionParameterBinding)functionBinding.getParameters().get(i)).getCaseSensitiveName()});
				}
				
			}
			else {
				AbstractAS400ParameterAnnotaionValidator validator = getValidator(values[i]);
				
				if (validator == null) {
					problemRequestor.acceptProblem(getNodeForArrayEntry(errorNode, i), 
											IProblemRequestor.PARAMETER_ANNOTATION_INVALID, 
											new String[] {Integer.toString(i)});
				}
				else {
					validator.validate((IAnnotationBinding)values[i], getNodeForArrayEntry(errorNode, i), ((FunctionParameterBinding)functionBinding.getParameters().get(i)).getType(), problemRequestor);
				}
			}
		}
	}
	
	public static AbstractAS400ParameterAnnotaionValidator getValidator(Object obj) {
		if (! (obj instanceof IAnnotationBinding)) {
			return null;
		}
		
		if (!Binding.isValidBinding(((IAnnotationBinding)obj).getAnnotationType())) {
			return null;
		}
		
		IAnnotationTypeBinding proxy = (((IAnnotationBinding)obj).getAnnotationType()).getValidationProxy();
		
		if (proxy instanceof AS400ParmeterAnnotationTypeBinding) {
			return ((AS400ParmeterAnnotationTypeBinding)proxy).getValidator();
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
	
	private IFunctionBinding getFunctionBinding(Node node) {
		if (node == null) {
			return null;
		}
		if (node instanceof NestedFunction) {
			NestedFunction function = (NestedFunction) node;
			IBinding binding = function.getName().resolveBinding();
			if (Binding.isValidBinding(binding) && binding.isDataBinding() && ((IDataBinding)binding).getKind() == IDataBinding.NESTED_FUNCTION_BINDING) {
				return (IFunctionBinding)((NestedFunctionBinding)binding).getType();
			}
		}
		return getFunctionBinding(node.getParent());
	}
	
}
