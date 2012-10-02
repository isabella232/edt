/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation.type;

import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.ASTValidator;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.TernaryExpression;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class TypeValidator {
	
	public static void validate(Type type, final IPartBinding declaringPart, final IProblemRequestor problemRequestor, final ICompilerOptions compilerOptions) {
		if (type == null) {
			return;
		}
		
		// First make sure the type can be used by EGL code (e.g. you cannot declare a field whose type is a program).
		org.eclipse.edt.mof.egl.Type typeBinding = type.getBaseType().resolveType();
		if (typeBinding != null) {
			boolean valid = !TypeUtils.isStaticType(typeBinding);
			if (valid) {
				// Annotations can be fields only inside other annotations.
				valid = !(typeBinding instanceof AnnotationType) || (declaringPart instanceof IRPartBinding && ((IRPartBinding)declaringPart).getIrPart() instanceof AnnotationType);
			}
			
			if (!valid) {
				problemRequestor.acceptProblem(type,
						IProblemRequestor.DATA_DECLARATION_HAS_INCORRECT_TYPE,
						new String[] {typeBinding.getTypeSignature()});
				return;
			}
		}
		
		type.accept(new AbstractASTVisitor() {
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.NameType nameType) {
				List<ASTValidator> validators = declaringPart.getEnvironment().getCompiler().getValidatorsFor(nameType);
				if (validators != null && validators.size() > 0) {
					for (ASTValidator validator : validators) {
						validator.validate(nameType, declaringPart, problemRequestor, compilerOptions);
					}
				}
				return false;
			};
		});
	}
	
	public static void validateInstantiatable(Type type, IPartBinding declaringPart, boolean isNullable, IProblemRequestor problemRequestor) {
		// Non-nullable reference types must have a public default constructor in order to be instantiatable.
		if (!isNullable) {
			org.eclipse.edt.mof.egl.Type typeBinding = type.resolveType();
			if (typeBinding != null && !(typeBinding instanceof ParameterizedType)
					&& TypeUtils.isReferenceType(typeBinding) && !hasPublicDefaultConstructor(typeBinding)) {
				// Don't need to throw error if the field is in an ExternalType, or if the field's type is the same as the declaring part's type.
				if (declaringPart == null || (declaringPart.getKind() != ITypeBinding.EXTERNALTYPE_BINDING
						&& !(declaringPart instanceof IRPartBinding && typeBinding.equals(((IRPartBinding)declaringPart).getIrPart())))) {
					problemRequestor.acceptProblem(type,
							IProblemRequestor.TYPE_NOT_INSTANTIABLE,
							new String[] {type.getCanonicalName()});
				}
			}
		}
	}
	
	public static boolean hasPublicDefaultConstructor(org.eclipse.edt.mof.egl.Type typeBinding) {
		if (typeBinding != null && typeBinding.getClassifier() instanceof StructPart) {
			List<Constructor> constructors = ((StructPart)typeBinding.getClassifier()).getConstructors();
			
			// Lack of explicit constructors means it has a default.
			if (constructors.size() == 0) {
				return true;
			}
			
			for (Constructor con : constructors) {
				if (con.getParameters().size() == 0 && !BindingUtil.isPrivate(con)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static void validateTypeDeclaration(Type type, IPartBinding declaringPart, IProblemRequestor problemRequestor) {
		if (type.isArrayType()) {
			while (type.isArrayType()) {
				if (((ArrayType)type).hasInitialSize()) {
					problemRequestor.acceptProblem(
							type,
							IProblemRequestor.ARRAY_DIMENSION_NOT_ALLOWED);
				}
				type = ((ArrayType)type).getElementType();
			}
		}
	}
	
	/**
	 * Finds all the expressions that should be validated against another type. This is typically just the
	 * expr as-is, except that for ternary expressions we need to check both the second and third exprs against
	 * some other type (including any nested ternary exprs - e.g. second expr is itself another ternary). Any generic
	 * types for the expressions will already be resolved.
	 */
	public static void collectExprsForTypeCompatibility(Expression rootExpr, final Map<Expression, org.eclipse.edt.mof.egl.Type> exprMap) {
		rootExpr.accept(new AbstractASTExpressionVisitor() {
			@Override
			public boolean visit(TernaryExpression ternaryExpression) {
				ternaryExpression.getSecondExpr().accept(this);
				ternaryExpression.getThirdExpr().accept(this);
				return false;
			};
			@Override
			public boolean visit(ParenthesizedExpression parenthesizedExpression) {
				return true;
			};
			@Override
			public boolean visitExpression(Expression expression) {
				exprMap.put(expression, BindingUtil.resolveGenericType(expression.resolveType(), expression));
				return false;
			}
		});
	}
}
