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
	
	import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.ConverseStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator.ITargetsContainerChecker;

	
	/**
	 * @author Craig Duval
	 */
	public class ConverseStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants{
		
		private IProblemRequestor problemRequestor;
		private IPartBinding enclosingPart;
		
		public ConverseStatementValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart) {
			this.problemRequestor = problemRequestor;
			this.enclosingPart = enclosingPart;
		}

		
		static ITargetsContainerChecker getTargetsContainerChecker() {
			ITargetsContainerChecker checker = new ITargetsContainerChecker() {
				
				public boolean isLibraryDataAllowed(IDataBinding dataBinding) {
					
					if (!Binding.isValidBinding(dataBinding)) {
						return false;
					}
					
					ITypeBinding targetBinding = dataBinding.getType();
					if (StatementValidator.isValidBinding(targetBinding)){
						//must be a flexible record that is basic or sql
						return StatementValidator.isFlexibleBasicOrSQL(targetBinding);
					}

					return false;
				}
			};
			return checker;
		}

		private void validateStatementAllowedInContainer(final ConverseStatement converseStatement) {
			//Converse is only legal in a library, if the target is a flexible record
		}
		
		public boolean visit(final ConverseStatement converseStatement) {
			StatementValidator.validateIOTargetsContainer(converseStatement.getIOObjects(),problemRequestor, ConverseStatementValidator.getTargetsContainerChecker());
			validateStatementAllowedInContainer(converseStatement);
			
			if(enclosingPart != null) {
				if(ITypeBinding.PROGRAM_BINDING == enclosingPart.getKind()) {
					if (enclosingPart.getAnnotation(EGLCORE, "BasicProgram") != null){
						problemRequestor.acceptProblem(converseStatement,
								IProblemRequestor.STATEMENT_CANNOT_BE_IN_BASIC_PROGRAM,
								new String[] { IEGLConstants.KEYWORD_CONVERSE });
					}else if (enclosingPart.getAnnotation(EGLUITEXT, "TextUIProgram") != null){
						Expression expr = converseStatement.getTarget();
						ITypeBinding targetBinding = expr.resolveTypeBinding();
						if (StatementValidator.isValidBinding(targetBinding)){
							if (targetBinding.getAnnotation(EGLUITEXT, "TextForm") == null){
								problemRequestor.acceptProblem(expr,
										IProblemRequestor.INVALID_CONVERSE_TARGET_FOR_TEXTUI_PROGRAM,
										new String[] { expr.getCanonicalString() });
							}
						}
					}else if (enclosingPart.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") != null){
						Expression expr = converseStatement.getTarget();
						ITypeBinding targetBinding = expr.resolveTypeBinding();
						if (StatementValidator.isValidBinding(targetBinding)){
							if (targetBinding.getAnnotation(EGLUIWEBTRANSACTION, "VGUIRecord") == null){
								problemRequestor.acceptProblem(expr,
										IProblemRequestor.INVALID_CONVERSE_TARGET_FOR_ACTION_PROGRAM,
										new String[] { expr.getCanonicalString() });
							}
						}
					}else if (enclosingPart.getAnnotation(EGLUIGATEWAY, "UIProgram") != null){
						validateTargetIsFlexibleRecord(converseStatement);
					}
				}
				else {
					if(ITypeBinding.LIBRARY_BINDING == enclosingPart.getKind()) {
						validateTargetIsFlexibleRecord(converseStatement);
					}
				}
			}

			return false;
		}
		
		private void validateTargetIsFlexibleRecord(final ConverseStatement converseStatement) { 
			Expression expr = converseStatement.getTarget();
			ITypeBinding targetBinding = expr.resolveTypeBinding();
			if (StatementValidator.isValidBinding(targetBinding)){
				if (!StatementValidator.isFlexibleBasicOrSQL(targetBinding)){
					problemRequestor.acceptProblem(expr,
							IProblemRequestor.INVALID_CONVERSE_TARGET_FOR_UI_PROGRAM,
							new String[] { expr.getCanonicalString() });
				}
			}
		}
	}
	
	


