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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.utils.NameUtile;


public class SetMethodAnnotationValueValidator implements IValueValidationRule {

	private static final String setMethod = NameUtile.getAsName("setMethod");
	
	@Override
	public void validate(Node errorNode, Node target, Annotation annotation, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (annotation.getValue(setMethod) instanceof Function){
			validateSetMethod(errorNode, target, (Function)annotation.getValue(setMethod), BindingUtil.getDeclaringPart(target), problemRequestor, compilerOptions);
		}
	}
	
	public void validateSetMethod(Node errorNode, Node target, Function function, Part declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {		
		//function must be declared in the part
		Part valueDeclarer = BindingUtil.getDeclaringPart(function);
		if (valueDeclarer != null && !valueDeclarer.equals(declaringPart)) {
			problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.LIBRARY_FUNCTION_NOT_ALLOWED_FOR_PROPERTY,
				new String[] {
					IEGLConstants.PROPERTY_GETMETHOD,
					declaringPart.getCaseSensitiveName()
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
		if (function.getParameters().size() == 1) {
			FunctionParameter binding =  function.getParameters().get(0);
			if (binding.getParameterKind() != ParameterKind.PARM_IN) {
				problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.FUNCTION_PARM_MUST_BE_IN,
						new String[] {
							function.getCaseSensitiveName(),
							IEGLConstants.PROPERTY_SETMETHOD});
			}
		}
		
		//function must have a single parm defined that matches the type of the field
		
		final Type[] fieldType = new Type[1];
		DefaultASTVisitor visitor = new DefaultASTVisitor() {
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.ClassDataDeclaration classDataDeclaration) {
				fieldType[0] = classDataDeclaration.getType().resolveType();
				return false;
			}
		};
		target.accept(visitor);
		if (fieldType[0] != null) {
			boolean error = false;
			if (function.getParameters().size() != 1) {
				error = true;
			}
			else {
				FunctionParameter binding = function.getParameters().get(0);
				if (!fieldType[0].equals(binding.getType())) {
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
