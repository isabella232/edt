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
package org.eclipse.edt.compiler.internal.core.validation.statement;

import org.eclipse.edt.compiler.binding.FieldAccessValidationRule;
import org.eclipse.edt.compiler.binding.IValidationProxy;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


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
		@Override
		public boolean canAssignToConstantVariables() {
			return false;
		}
		@Override
		public boolean canAssignToReadOnlyVariables() {
			return false;
		}
		@Override
		public boolean canAssignToFunctionReferences() {
			return false;
		}
		@Override
		public boolean canAssignToFunctionParmConst() {
			return false;
		}
		@Override
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
		
		Name constName = findConstName(lValue);
		Member constMember = constName == null ? null : constName.resolveMember();
		if (constMember != null) {
			boolean canAssignToConst = constMember instanceof FunctionParameter ? validationRules.canAssignToFunctionParmConst() : validationRules.canAssignToConstantVariables();
			if (!canAssignToConst) {
				// No part of the field can be modified for value types. For reference types only the declaration itself cannot be modified.
				if (constName == lValue || (constMember.getType() != null && TypeUtils.isValueType(constMember.getType()))) {
					problemRequestor.acceptProblem(
							lValue,
							IProblemRequestor.CANNOT_MODIFY_CONSTANT,
							new String[] {member.getCaseSensitiveName()});
						result = false;
				}
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
	
	public static Name findConstName(Expression e) {
		if (e == null) {
			return null;
		}
		
		final Name[] value = new Name[1];
		e.accept(new AbstractASTVisitor() {
			@Override
			public boolean visit(SimpleName simpleName) {
				Member m = simpleName.resolveMember();
				if (m instanceof ConstantField || (m instanceof FunctionParameter && ((FunctionParameter)m).isConst())) {
					value[0] = simpleName;
				}
				return false;
			};
			@Override
			public boolean visit(QualifiedName qualifiedName) {
				Member m = qualifiedName.resolveMember();
				if (m instanceof ConstantField || (m instanceof FunctionParameter && ((FunctionParameter)m).isConst())) {
					value[0] = qualifiedName;
					return false;
				}
				return true;
			};
		});
		
		return value[0];
	}
}
