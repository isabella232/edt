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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import org.eclipse.edt.compiler.binding.FieldAccessValidationRule;
import org.eclipse.edt.compiler.binding.IValidationProxy;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.utils.NameUtile;


public class RValueValidator {
	private IProblemRequestor problemRequestor;
	private ICompilerOptions compilerOptions;
	private Member member;
	private Expression nodeForErrors;
	
	public RValueValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, Member member, Expression nodeForErrors) {
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
		this.member = member;
		this.nodeForErrors = nodeForErrors;
	}
	
	public boolean validate() {
		boolean result = true;
		
		if (member == null) {
			return result;
		}

		if(!checkFunctionDelegation()) {
			result = false;
		}
		
		if(!invokeFieldAccessValidators()) {
			result = false;
		}
		
		return result;
	}

	private boolean invokeFieldAccessValidators() {
		//Run field access rules defined by annotations on the field
		boolean result = true;

		for (Annotation annot : member.getAnnotations()) {
			IValidationProxy proxy = AnnotationValidator.getValidationProxy(annot);
			if (proxy != null) {
				for (FieldAccessValidationRule rule : proxy.getFieldAccessValidators()) {
					result = rule.validateRValue(nodeForErrors, member, problemRequestor, compilerOptions) && result;
				}
			}
		}
		return result;
	}

	private boolean checkFunctionDelegation() {
		boolean result = true;
		
		if (member instanceof FunctionMember) {
			Part declaringPart = BindingUtil.getDeclaringPart(member);
			if (declaringPart instanceof Program && NameUtile.equals(member.getName(), NameUtile.getAsName("main"))) {
				problemRequestor.acceptProblem(
					nodeForErrors,
					IProblemRequestor.MAIN_FUNCTION_CANNOT_BE_ASSIGNED_TO_DELEGATE,
					new String[] {
						member.getCaseSensitiveName()	
					});
				result = false;
			}
		}
		
		return result;
	}
}
