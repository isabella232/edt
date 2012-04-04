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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;


public class SetMethodAnnotationValueValidator implements IValueValidationRule {

	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (annotationBinding.getValue() != null && annotationBinding.getValue() != IBinding.NOT_FOUND_BINDING) {
			if (annotationBinding.getValue() instanceof IFunctionBinding){
				validateSetMethod(errorNode, target, (IFunctionBinding)annotationBinding.getValue(), annotationBinding.getDeclaringPart(), problemRequestor, compilerOptions);
			}	
		}
	}
	
	public void validateSetMethod(Node errorNode, Node target, IFunctionBinding function, IPartBinding declarer, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {		
		//function must be declared in the part
		IPartBinding valueDeclarer = function.getDeclarer();
		if(valueDeclarer != null && declarer != valueDeclarer) {
			problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.LIBRARY_FUNCTION_NOT_ALLOWED_FOR_PROPERTY,
				new String[] {
					IEGLConstants.PROPERTY_GETMETHOD,
					declarer.getCaseSensitiveName()
				});
		}
		
		//function must have NO return type
		if (function.getReturnType() != null) {
			problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.FUNCTION_CANT_HAVE_RETURN_TYPE,
					new String[] {
						function.getCaseSensitiveName(),
						IEGLConstants.PROPERTY_SETMETHOD});
		}
		
		//function parm must be IN
		if (function.getParameters().size() == 1 ) {
			FunctionParameterBinding binding =  (FunctionParameterBinding)function.getParameters().get(0);
			if (!binding.isInput()) {
				problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.FUNCTION_PARM_MUST_BE_IN,
						new String[] {
							function.getCaseSensitiveName(),
							IEGLConstants.PROPERTY_SETMETHOD});
			}
		}
		
		//function must have a single parm defined that matches the type of the field
		
		final ITypeBinding[] fieldType = new ITypeBinding[1];
		DefaultASTVisitor visitor = new DefaultASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.ClassDataDeclaration classDataDeclaration) {
				fieldType[0] = classDataDeclaration.getType().resolveTypeBinding();
				return false;
			}
		};
		target.accept(visitor);
		if (Binding.isValidBinding(fieldType[0])) {
			
			boolean error = false;
			if (function.getParameters().size() != 1) {
				error = true;
			}
			else {
				FunctionParameterBinding binding =  (FunctionParameterBinding)function.getParameters().get(0);
				if ( fieldType[0] != binding.getType()) {
					error = true;
				}
			}
			if (error) {

				problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.FUNCTION_MUST_HAVE_ONE_PARM,
						new String[] {
							function.getCaseSensitiveName(),
							IEGLConstants.PROPERTY_SETMETHOD,
							StatementValidator.getShortTypeString(fieldType[0])
						});
			}
		}
	}

}
