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
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;


public class LValueValidator {
	private IProblemRequestor problemRequestor;
	private ICompilerOptions compilerOptions;
	private Member member;
	private Expression lValue;
	private ILValueValidationRules validationRules;
	
	public static interface ILValueValidationRules {
		boolean canAssignToConstantVariables();
		boolean canAssignToReadOnlyVariables();
		boolean canAssignToFunctionReferences();
		boolean canAssignToFunctionParmConst();
		boolean shouldRunAccessRules();
		
	}
	
	public static class DefaultLValueValidationRules implements ILValueValidationRules {
		public boolean canAssignToConstantVariables() {
			return false;
		}
		
		public boolean canAssignToReadOnlyVariables() {
			return false;
		}
		
		public boolean canAssignToFunctionReferences() {
			return false;
		}
		
		public boolean canAssignToFunctionParmConst() {
			return false;
		}

		public boolean shouldRunAccessRules() {
			return true;
		}

	}
	
	public LValueValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, Member member, Expression lValue) {
		this(problemRequestor, compilerOptions, member, lValue, new DefaultLValueValidationRules());
	}
	
	public LValueValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions, Member member, Expression lValue, ILValueValidationRules validationRules) {
		this.problemRequestor = problemRequestor;
		this.member = member;
		this.lValue = lValue;
		this.validationRules = validationRules;
		this.compilerOptions = compilerOptions;
	}
	
	private boolean invokeFieldAccessValidators() {
		boolean result = true;

		if (member == null) {
			return result;
		}
		for (Annotation annot : member.getAnnotations()) {
			IValidationProxy proxy = AnnotationValidator.getValidationProxy(annot);
			if (proxy != null) {
				for (FieldAccessValidationRule rule : proxy.getFieldAccessValidators()) {
					result = rule.validateLValue(lValue, member, problemRequestor, compilerOptions) && result;
				}
			}
		}
		return result;
	}
	
	public boolean validate() {
		boolean result = true;
		
		if (validationRules.shouldRunAccessRules()) {
			//Run field access rules defined by annotations on the field
			result = invokeFieldAccessValidators();
		}
		
		if (member instanceof FunctionParameter) {
			if(!validationRules.canAssignToFunctionParmConst() && ((FunctionParameter)member).isConst()) {
				boolean settingValueOfConstantArrayElement = false;
				Type memberType = member.getType();
				Type exprType = lValue.resolveType();
				if (memberType != null && exprType != null) {
					settingValueOfConstantArrayElement = memberType instanceof ArrayType && !memberType.equals(exprType);
				}
				
				if (!settingValueOfConstantArrayElement) {
					problemRequestor.acceptProblem(
						lValue,
						IProblemRequestor.CANNOT_MODIFY_CONSTANT,
						new String[] {member.getCaseSensitiveName()});
					result = false;
				}
			}
		}
		
		if (!validationRules.canAssignToConstantVariables() && member instanceof ConstantField) {
			boolean settingValueOfConstantArrayElement = false;
			Type memberType = member.getType();
			Type exprType = lValue.resolveType();
			if (memberType != null && exprType != null) {
				settingValueOfConstantArrayElement = memberType instanceof ArrayType && !memberType.equals(exprType);
			}
			
			if (!settingValueOfConstantArrayElement) {
				problemRequestor.acceptProblem(
					lValue,
					IProblemRequestor.CANNOT_MODIFY_CONSTANT,
					new String[] {member.getCaseSensitiveName()});
				result = false;
			}
		}
		
		if (member instanceof FunctionMember) {
			if (!validationRules.canAssignToFunctionReferences()) {
				problemRequestor.acceptProblem(
					lValue,
					IProblemRequestor.FUNCTION_NOT_VALID_AS_LVALUE,
					new String[] {member.getCaseSensitiveName()});
				result = false;
			}
		}
		
		return result;
	}
}
