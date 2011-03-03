/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
	
	import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.ReturnStatement;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;

	
	/**
	 * @author Craig Duval
	 */
	public class ReturnStatementValidator extends DefaultASTVisitor {
		
		private IProblemRequestor problemRequestor;
		private ICompilerOptions compilerOptions;
		
		public ReturnStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.compilerOptions = compilerOptions;
		}
		
		public boolean visit(final ReturnStatement returnStatement) {
			Node current = returnStatement.getParent();
			ParentASTVisitor visitor = new ParentASTVisitor(){
				public boolean visit(NestedFunction nFunction) {
					IFunctionBinding fBinding = (IFunctionBinding) ((NestedFunctionBinding) nFunction.getName().resolveBinding()).getType();
					if(fBinding != null) {
						binding = fBinding.getReturnType();
					}
					bcontinue = false;
										
					return false;
				}
				
				public boolean visit(TopLevelFunction tlFunction) {
					bcontinue = false;
					IFunctionBinding fBinding = (IFunctionBinding) tlFunction.getName().resolveBinding();
					if(fBinding != null) {
						binding = fBinding.getReturnType();
					}
					return false;
				}
			};

			while ((current != null) && visitor.canContinue() ) {
				current.accept(visitor);
				current = current.getParent();
			}	
			
			
			if (returnStatement.getParenthesizedExprOpt() != null && !visitor.hasReturnType()){
				problemRequestor.acceptProblem(returnStatement,
						IProblemRequestor.RETURN_VALUE_WO_RETURN_DEF);
			}
			
			validateNoSetValues(returnStatement);
			
			if (visitor.hasReturnType() && returnStatement.getParenthesizedExprOpt()!= null){
				boolean compatible = TypeCompatibilityUtil.isMoveCompatible(visitor.getBinding(), returnStatement.getParenthesizedExprOpt().resolveTypeBinding(), returnStatement.getParenthesizedExprOpt(), compilerOptions);
				if (!compatible ){//|| lhsBinding == null ||rhsBinding == null ){
					problemRequestor.acceptProblem(returnStatement.getParenthesizedExprOpt(),
							IProblemRequestor.RETURN_STATEMENT_TYPE_INCOMPATIBLE,
							new String[] {getTypeName(returnStatement.getParenthesizedExprOpt().resolveTypeBinding()), getTypeName(visitor.getBinding())});
				}
			}
			
			if(returnStatement.getParenthesizedExprOpt() != null) {
				returnStatement.getParenthesizedExprOpt().accept(new DefaultASTVisitor() {
					public boolean visit(AnnotationExpression annotationExpression) {
						problemRequestor.acceptProblem(
							annotationExpression.getOffset(),
							annotationExpression.getOffset()+1,
							IMarker.SEVERITY_ERROR,
							IProblemRequestor.UNEXPECTED_TOKEN,
							new String[] {"@"});
						return false;
					}
					public boolean visit(ParenthesizedExpression parenthesizedExpression) {
						return true;
					}
				});
			}
			
			return false;
		}


		protected String getTypeName(ITypeBinding binding){
			return StatementValidator.getTypeString(binding);
		}
		
		protected void validateNoSetValues(ReturnStatement returnStatement){
			returnStatement.accept(new AbstractASTVisitor(){
				 public boolean visit(SetValuesExpression setValuesExpression) {
					problemRequestor.acceptProblem(setValuesExpression,
							IProblemRequestor.SET_VALUES_BLOCK_NOT_VALID_AS_RETURN_ARG);
					return false;
				 }
			});
	
		}
		
		
		private class ParentASTVisitor extends AbstractASTVisitor{
			ITypeBinding binding = null;
			boolean bcontinue = true;
			public ParentASTVisitor (){
			}
			
			public boolean hasReturnType(){
				return binding != null;
			}

			public ITypeBinding getBinding(){
				return binding;
			}
			public boolean canContinue(){
				return bcontinue;
			}
		}		
		

	}
	
	


