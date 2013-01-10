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
package org.eclipse.edt.compiler.binding;

import java.util.Map;

import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IFieldContentAnnotationValidationRule;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author svihovec
 */
public class UserDefinedFieldContentAnnotationValidationRule extends FieldContentValidationRule {

	private Class validatorClass;

	public UserDefinedFieldContentAnnotationValidationRule(Class validatorClass) {
		super(NameUtile.getAsName("UserDefinedFieldContentAnnotationValidationRule"));
		
		this.validatorClass = validatorClass;
	}
	
	@Override
	public void validate(Node errorNode, Node target, Member containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		try {
			((IFieldContentAnnotationValidationRule)validatorClass.newInstance()).validate(errorNode, target, containerBinding, canonicalContainerName, allAnnotations, problemRequestor, compilerOptions);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void validateFunctionParameter(FunctionParameter fParameter, Member parameterBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		try {
			((IFieldContentAnnotationValidationRule)validatorClass.newInstance()).validateFunctionParameter(fParameter, parameterBinding, problemRequestor, compilerOptions);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void validateFunctionReturnType(Type typeNode, org.eclipse.edt.mof.egl.Type typeBinding, Member declaringMember, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		try {
			((IFieldContentAnnotationValidationRule)validatorClass.newInstance()).validateFunctionReturnType(typeNode, typeBinding, declaringMember, problemRequestor, compilerOptions);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
