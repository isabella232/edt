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
	
	import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;

	
	/**
	 * @author Craig Duval
	 */
	public class ForStatementValidator extends DefaultASTVisitor {
		
		private IProblemRequestor problemRequestor;
		private ICompilerOptions compilerOptions;
		
		public ForStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.compilerOptions = compilerOptions;
		}
		
		public boolean visit(final ForStatement forStatement) {
			forStatement.accept(new AbstractASTExpressionVisitor(){
				public boolean visitExpression(Expression expr) {
					ITypeBinding tBinding = expr.resolveTypeBinding();
					if (StatementValidator.isValidBinding(tBinding)){
						if (!TypeCompatibilityUtil.isMoveCompatible(PrimitiveTypeBinding.getInstance(Primitive.INT), tBinding, expr, compilerOptions) &&
							!tBinding.isDynamic()) {
							if (expr == forStatement.getCounterVariable()){
								problemRequestor.acceptProblem(expr,
										IProblemRequestor.FOR_STATEMENT_COUNTER_MUST_BE_INT);
							}else{
								String insert = "";
								if (expr == forStatement.getFromIndex()){
									insert = "start";
								}else if (expr == forStatement.getEndIndex()){
									insert = "end";
								}else if (expr == forStatement.getDeltaExpression()) {
									insert = "delta";
								}
								
								if (insert.length() > 0){
									problemRequestor.acceptProblem(expr,
										IProblemRequestor.FOR_STATEMENT_EXPR_MUST_BE_INT
										,new String[]{insert});
								}
							}
						}
					}
				    return false;
				}
			});
			
			
			return false;
		}

	}
	
	


