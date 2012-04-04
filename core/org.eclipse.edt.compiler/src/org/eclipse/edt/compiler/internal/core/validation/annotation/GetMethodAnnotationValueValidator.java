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


public class GetMethodAnnotationValueValidator implements IValueValidationRule {

	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (annotationBinding.getValue() != null && annotationBinding.getValue() != IBinding.NOT_FOUND_BINDING) {
			if (annotationBinding.getValue() instanceof IFunctionBinding){
				validateGetMethod(errorNode, target, (IFunctionBinding)annotationBinding.getValue(), annotationBinding.getDeclaringPart(), problemRequestor, compilerOptions);
			}	
		}
	}
	
	public void validateGetMethod(Node errorNode, Node target, IFunctionBinding function, IPartBinding declarer, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		

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
		
		//function must have NO parms
		if (function.getParameters().size() > 0) {
			problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.FUNCTION_CANT_HAVE_PARMS,
					new String[] {
						function.getCaseSensitiveName(),
						IEGLConstants.PROPERTY_GETMETHOD});
		}
		
		//return type must match the type of the field the annotation is on
		
		final ITypeBinding[] fieldType = new ITypeBinding[1];
		DefaultASTVisitor visitor = new DefaultASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.ClassDataDeclaration classDataDeclaration) {
				fieldType[0] = classDataDeclaration.getType().resolveTypeBinding();
				return false;
			}
		};
		target.accept(visitor);
		if (Binding.isValidBinding(fieldType[0])) {
			if ( function.getReturnType() == null || fieldType[0] != function.getReturnType()) {

				problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.FUNCTION_REQUIRES_RETURN_TYPE,
						new String[] {
							function.getCaseSensitiveName(),
							IEGLConstants.PROPERTY_GETMETHOD,
							StatementValidator.getShortTypeString(fieldType[0])
						});
			}
		}
			
		
	}

}
