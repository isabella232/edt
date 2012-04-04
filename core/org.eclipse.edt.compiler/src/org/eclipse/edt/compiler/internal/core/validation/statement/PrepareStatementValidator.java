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
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.ForExpressionClause;
import org.eclipse.edt.compiler.core.ast.FromExpressionClause;
import org.eclipse.edt.compiler.core.ast.PrepareStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;

	
	/**
	 * @author Craig Duval
	 */
	public class PrepareStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants{
		
		private IProblemRequestor problemRequestor;
        private ICompilerOptions compilerOptions;
		
		public PrepareStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.compilerOptions = compilerOptions;
		}
		
		public boolean visit(final PrepareStatement aprepareStatement) {
			StatementValidator.validateIOTargetsContainer(aprepareStatement.getIOObjects(),problemRequestor);
			aprepareStatement.accept(new AbstractASTVisitor(){
				boolean hasFrom;
				public boolean visit(FromExpressionClause fromExpressionClause) {
					hasFrom = true;
					ITypeBinding typeBinding = fromExpressionClause.getExpression().resolveTypeBinding();
					if(StatementValidator.isValidBinding(typeBinding)) {
						if ((typeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING &&!StatementValidator.isStringCompatible(typeBinding)) ||
							typeBinding.getKind() != ITypeBinding.PRIMITIVE_TYPE_BINDING){
							problemRequestor.acceptProblem(fromExpressionClause.getExpression(),
									IProblemRequestor.ELEMENT_NOT_VALID_IN_EXPRESSION,
									new String[] {fromExpressionClause.getExpression().getCanonicalString()});
						}
					}

					return false;
				}
				public boolean visit(ForExpressionClause forExpressionClause) {
					ITypeBinding typeBinding = forExpressionClause.getExpression().resolveTypeBinding();
					if (StatementValidator.isValidBinding(typeBinding)){
						if(typeBinding.getAnnotation(EGLIOSQL, "SQLRecord") == null){
							problemRequestor.acceptProblem(forExpressionClause.getExpression(),
									IProblemRequestor.STATEMENT_TARGET_NOT_SQL_RECORD,
									new String[] {forExpressionClause.getExpression().getCanonicalString()});
						}
					}
					return false;
				}
				
				public void endVisit(PrepareStatement prepareStatement) {
					if (!hasFrom){
//						problemRequestor.acceptProblem(prepareStatement,
//								IProblemRequestor.PREPARE_STATEMENT_NO_FROM_CLAUSE);
					}
				}
				});
			
//			EGLNameValidator.validate(aprepareStatement.getPreparedStatementID(), EGLNameValidator.IDENTIFIER, problemRequestor, aprepareStatement, compilerOptions);
			
			return false;
		}

	}
	
	


