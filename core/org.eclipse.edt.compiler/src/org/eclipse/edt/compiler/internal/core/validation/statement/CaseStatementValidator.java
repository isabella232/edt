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
	
	import java.util.Iterator;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.CaseStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.IsNotExpression;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SubstringAccess;
import org.eclipse.edt.compiler.core.ast.WhenClause;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;

	
	/**
	 * @author Craig Duval
	 */
	public class CaseStatementValidator extends DefaultASTVisitor {
		
		private IProblemRequestor problemRequestor;
		private ICompilerOptions compilerOptions;
		
		public CaseStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.compilerOptions = compilerOptions;
		}
		
		public boolean visit(final CaseStatement caseStatement) {			
			caseStatement.accept(new DefaultASTVisitor(){
				boolean visitingWhenClause = false;
				
				public boolean visit(CaseStatement caseStatement) {
					return true;
				}
				public boolean visit(WhenClause whenClause) {
					visitingWhenClause = true;
					return true;
				}
				
				public boolean visit(ParenthesizedExpression parenthesizedExpression) {
					return true;
				}
				
				 public boolean visit(SetValuesExpression setValuesExpression) {
					problemRequestor.acceptProblem(setValuesExpression,
							visitingWhenClause ?
									IProblemRequestor.SET_VALUES_BLOCK_NOT_VALID_AS_WHEN_CLAUSE :
									IProblemRequestor.SET_VALUES_BLOCK_NOT_VALID_AS_CASE_CRITERION);
					return true;
				 }
				 
			});				
			
			for(Iterator iter = caseStatement.getWhenClauses().iterator(); iter.hasNext();) {
				WhenClause whenClause = (WhenClause) iter.next();
				for(Iterator iter2 = whenClause.getExpr_plus().iterator(); iter2.hasNext();) {
					Expression expression = (Expression) iter2.next();
					expression.accept(new AbstractASTExpressionVisitor() {	
						public boolean visit(IsNotExpression isNotExpression) {
					        return false;
					    }
						
					    public boolean visit(SetValuesExpression setValuesExpression) {
					        return false;
					    }
					    
						public boolean visitExpression(Expression expr) {
							boolean criterionExisits = caseStatement.getCriterion() != null && caseStatement.getCriterion().resolveTypeBinding() != IBinding.NOT_FOUND_BINDING;
							if (expr.resolveTypeBinding() != null && expr.resolveTypeBinding() != null) {
									ITypeBinding binding = expr.resolveTypeBinding();
									if (binding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING){
										Primitive type = ((PrimitiveTypeBinding)binding).getPrimitive();
										if (type != Primitive.BOOLEAN  && !criterionExisits){
											problemRequestor.acceptProblem(expr,
													IProblemRequestor.CASE_WHEN_MUST_BE_BOOLEAN_EXPRESSION);
											return false;
										}
									}
									
									if (criterionExisits &&
										StatementValidator.isValidBinding(caseStatement.getCriterion().resolveTypeBinding()) &&
										StatementValidator.isValidBinding(expr.resolveTypeBinding())){
										boolean compatible = TypeCompatibilityUtil.isMoveCompatible(caseStatement.getCriterion().resolveTypeBinding(), binding, expr, compilerOptions);
										
										if(!compatible) {
											problemRequestor.acceptProblem(
												expr,
												IProblemRequestor.TYPE_INCOMPATIBLE_ARITHMETIC_COMPARISON,
												new String[] {													
													expr.getCanonicalString(),
													caseStatement.getCriterion().getCanonicalString()
												});
										}
									}
							}
							
							
							
							return false;
						}
					});
				}
			}
			
			checkNoSubstringWithSpecialCriterion(caseStatement);			
		
			return false;
		}
		
		private void checkNoSubstringWithSpecialCriterion(final CaseStatement caseStatement) {
			if(caseStatement.hasCriterion()) {
				caseStatement.getCriterion().accept(new DefaultASTVisitor() {
					public boolean visit(ParenthesizedExpression parenthesizedExpression) {
						return true;
					}
					public boolean visit(final SubstringAccess substringAccess) {
						boolean criterionIsSpecial = false;
						
						IDataBinding dBinding = substringAccess.getPrimary().resolveDataBinding();
						if(dBinding != null) {
							criterionIsSpecial = AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "core"}, "SysVar", "SYSTEMTYPE") ||
							                     AbstractBinder.dataBindingIs(dBinding, new String[] {"egl", "ui", "text"}, "ConverseVar", "EVENTKEY");
						}
						
						if(criterionIsSpecial) {
							problemRequestor.acceptProblem(
								substringAccess,
								IProblemRequestor.SUBSTRING_EXPRESSION_IN_BAD_LOCATION);
						}
						return false;
					}
				});
			}
		}

	}
	
	


