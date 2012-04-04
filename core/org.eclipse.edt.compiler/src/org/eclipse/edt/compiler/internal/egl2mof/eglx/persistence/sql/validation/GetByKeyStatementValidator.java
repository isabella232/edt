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
package org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation;

import java.util.List;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ConstructorBinding;
import org.eclipse.edt.compiler.binding.DictionaryBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.IntoClause;
import org.eclipse.edt.compiler.core.ast.UsingClause;
import org.eclipse.edt.compiler.core.ast.UsingKeysClause;
import org.eclipse.edt.compiler.core.ast.WithExpressionClause;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

public class GetByKeyStatementValidator extends AbstractSqlStatementValidator {
	GetByKeyStatement statement;
	IProblemRequestor problemRequestor;
	ICompilerOptions compilerOptions;
	
	UsingClause using;
	UsingKeysClause usingKeys;
	FromOrToExpressionClause from;
	WithInlineSQLClause withInline;
	WithExpressionClause withExpression;
	IntoClause into;
	
	public GetByKeyStatementValidator(GetByKeyStatement statement, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super();
		this.statement = statement;
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public void validate() {
		initialize();
		
		validateTargets();
		validateFrom();
		validateInto();
		
		//TODO when associations are supported, add the following validation:
		// "If the action target is an association_expr no WITH clause is allowed and the data source must be of type SQLDataSource, i.e. it cannot be an SQLResultSet."
	}
	
	private void validateTargets() {
		// target can be a data expression (record, dictionary, etc), or a scalar list of primitives that map to table columns.
		boolean isDataExpr = false;
		List targets = statement.getTargets();
		if (targets.size() == 1) {
			Object o = targets.get(0);
			if (o instanceof Expression) {
				Expression expr = (Expression)o;
				if (isDataExpr(expr)) {
					// Associations are not yet supported.
					if (isAssociationExpression(expr)) {
						problemRequestor.acceptProblem(expr,
								IProblemRequestor.SQL_ENTITY_ASSOCIATIONS_NOT_SUPPORTED,
								new String[] {});
						return;
					}
					
					// If it's a nullable entity, it must have a public default constructor.
					if (expr.resolveTypeBinding().isNullable() && isEntity(expr.resolveTypeBinding()) && !hasDefaultConstructor(expr.resolveTypeBinding())) {
						problemRequestor.acceptProblem(expr,
								IProblemRequestor.SQL_NULLABLE_TARGET_MISSING_DEFAULT_CONSTRUCTOR,
								new String[] {expr.getCanonicalString(), expr.resolveTypeBinding().getPackageQualifiedName()});
						return;
					}
					
					isDataExpr = true;
				}
			}
		}
		
		if (!isDataExpr && !mapsToColumns(targets)) {
			int[] offsets = getOffsets(targets);
			problemRequestor.acceptProblem(offsets[0], offsets[1],
					IProblemRequestor.SQL_TARGET_MUST_BE_DATA_EXPR_OR_COLUMNS,
					new String[] {});
			return;
		}
		else if (!isDataExpr && withExpression == null && withInline == null && !(mapsToSingleTable(targets) || isFromResultSet()) ) {
			// WITH required when the columns do not map to a single table.
			int[] offsets = getOffsets(targets);
			problemRequestor.acceptProblem(offsets[0], offsets[1],
					IProblemRequestor.SQL_STMT_REQUIRED_FOR_NON_SINGLE_TABLE,
					new String[] {IEGLConstants.KEYWORD_WITH});
			return;
		}
		
		if (using == null && withExpression == null && withInline == null) {
			// When no USING or WITH, a field in the type of the target must have @Id.
			ITypeBinding targetType = getTargetType(isDataExpr);
			if (Binding.isValidBinding(targetType) && !hasID(targetType)) {
				int[] offsets = getOffsets(targets);
				problemRequestor.acceptProblem(offsets[0], offsets[1],
						IProblemRequestor.SQL_NO_ID_IN_TARGET_TYPE,
						new String[] {targetType.getPackageQualifiedName()});
				return;
			}
		}
	}
	
	private boolean hasDefaultConstructor(ITypeBinding binding) {
		List<ConstructorBinding> constructors = null;
		switch (binding.getKind()) {
			case ITypeBinding.EXTERNALTYPE_BINDING:
				constructors = ((ExternalTypeBinding)binding).getConstructors();
				break;
			case ITypeBinding.HANDLER_BINDING:
				constructors = ((HandlerBinding)binding).getConstructors();
				break;
			case ITypeBinding.DICTIONARY_BINDING:
				constructors = ((DictionaryBinding)binding).getConstructors();
				break;
			default:
				return true;
		}
		
		if (constructors != null) {
			for (ConstructorBinding con : constructors) {
				if (!con.isPrivate() && con.getParameters().size() == 0) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private ITypeBinding getTargetType(boolean isDataExpr) {
		ITypeBinding type = null;
		List targets = statement.getTargets();
		if (isDataExpr || mapsToSingleTable(targets)) {
			Expression e = (Expression)targets.get(0);
			type = e.resolveTypeBinding();
			if (Binding.isValidBinding(type) && type.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
				IDataBinding data = e.resolveDataBinding();
				if (Binding.isValidBinding(data)) {
					type = data.getDeclaringPart();
				}
			}
		}
		
		if (Binding.isValidBinding(type) && type.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
			type = ((ArrayTypeBinding)type).getElementType();
		}
		
		return type;
	}
	
	private boolean isFromResultSet() {
		if (from != null) {
			// When WITH is specified, FROM must be SQLDataSource.
			ITypeBinding type = from.getExpression().resolveTypeBinding();
			return Binding.isValidBinding(type) && isResultSet(type);
		}
		return false;
	}
	
	private void validateFrom() {
		if (from != null && (withExpression != null || withInline != null)) {
			// When WITH is specified, FROM must be SQLDataSource.
			ITypeBinding type = from.getExpression().resolveTypeBinding();
			if (Binding.isValidBinding(type) && !isDataSource(type)) {
				problemRequestor.acceptProblem(from.getExpression(),
						IProblemRequestor.SQL_EXPR_HAS_WRONG_TYPE,
						new String[] {from.getExpression().getCanonicalString(), "eglx.persistence.sql.SQLDataSource"});
				return;
			}
		}
	}
	
	private void validateInto() {
		if (into != null) {
			// INTO not currently part of the spec.
			problemRequestor.acceptProblem(into, IProblemRequestor.SQL_INTO_NOT_ALLOWED, new String[] {});
		}
	}
	
	private void initialize() {
		statement.accept(new AbstractASTVisitor() {
			public boolean visit(WithInlineSQLClause withInlineSQLClause) {
				if (withInline == null && withExpression == null) {
					withInline = withInlineSQLClause;
				}
				else {
					problemRequestor.acceptProblem(withInlineSQLClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(), IEGLConstants.KEYWORD_WITH.toUpperCase()});

				}
				return false;
			}
			
			public boolean visit(WithExpressionClause withExpressionClause) {
				if (withInline == null && withExpression == null) {
					withExpression = withExpressionClause;
				}
				else {
					problemRequestor.acceptProblem(withExpressionClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(), IEGLConstants.KEYWORD_WITH.toUpperCase()});
				}
				return false;
				
			}
			
			public boolean visit(UsingClause usingClause) {
				if (using == null) {
					using = usingClause;
				}
				else {
					problemRequestor.acceptProblem(usingClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(), IEGLConstants.KEYWORD_USING.toUpperCase()});
				}
				return false;
			}
			
			public boolean visit(UsingKeysClause usingKeysClause) {
				if (usingKeys == null) {
					usingKeys = usingKeysClause;
				}
				else {
					problemRequestor.acceptProblem(usingKeysClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(), IEGLConstants.KEYWORD_USINGKEYS.toUpperCase()});
				}
				return false;
			}
			
			public boolean visit(FromOrToExpressionClause clause) {
				if (from == null) {
					from = clause;
				}
				else {
					problemRequestor.acceptProblem(clause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(), IEGLConstants.KEYWORD_FROM.toUpperCase()});
				}
				return false;
			}
			
			public boolean visit(IntoClause intoClause) {
				if (into == null) {
					into = intoClause;
				}
				else {
					problemRequestor.acceptProblem(intoClause,
							IProblemRequestor.DUPE_OPTION,
							new String[] { IEGLConstants.KEYWORD_GET.toUpperCase(), IEGLConstants.KEYWORD_INTO.toUpperCase()});
				}
				return false;
			}
		});
	}
}
