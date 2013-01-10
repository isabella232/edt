/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.utils.NameUtile;


public class GetMethodAnnotationValueValidator implements IValueValidationRule {
	
	private static final String getMethod = NameUtile.getAsName("getMethod");

	@Override
	public void validate(Node errorNode, Node target, Annotation annotation, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (annotation.getValue(getMethod) instanceof Function) {
			validateGetMethod(errorNode, target, (Function)annotation.getValue(getMethod), BindingUtil.getDeclaringPart(target), problemRequestor, compilerOptions);
		}
	}
	
	public void validateGetMethod(Node errorNode, Node target, Function function, Part declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		//function must be declared in the part
		Part valueDeclarer = BindingUtil.getDeclaringPart(function);
		if (valueDeclarer != null && !valueDeclarer.equals(declaringPart)) {
			problemRequestor.acceptProblem(
				errorNode,
				IProblemRequestor.EXTERNAL_FUNCTION_NOT_ALLOWED_FOR_PROPERTY,
				new String[] {
					IEGLConstants.PROPERTY_GETMETHOD,
					declaringPart.getCaseSensitiveName()
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
			if (!fieldType[0].equals(function.getReturnType())) {
				problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.FUNCTION_REQUIRES_RETURN_TYPE,
						new String[] {
							function.getCaseSensitiveName(),
							IEGLConstants.PROPERTY_GETMETHOD,
							BindingUtil.getShortTypeString(fieldType[0])
						});
			}
		}
	}
}
