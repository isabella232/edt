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
	
	import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.DisplayStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

	
	/**
	 * @author Craig Duval
	 */
	public class DisplayStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants{
		
		private IProblemRequestor problemRequestor;
        private ICompilerOptions compilerOptions;
		private IPartBinding enclosingPart;
		
		public DisplayStatementValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.enclosingPart = enclosingPart;
			this.compilerOptions = compilerOptions;
		}
		
		public boolean visit(final DisplayStatement displayStatement) {
			StatementValidator.validateIOTargetsContainer(displayStatement.getIOObjects(),problemRequestor);
			
			if(enclosingPart != null) {
				if(ITypeBinding.PROGRAM_BINDING == enclosingPart.getKind()) {			
					if (enclosingPart.getAnnotation(EGLUIWEBTRANSACTION, "VGWebTransaction") != null ||
						enclosingPart.getAnnotation(EGLCORE, "BasicProgram") != null){
						problemRequestor.acceptProblem(displayStatement,
								IProblemRequestor.STATEMENT_CANNOT_BE_IN_ACTION_OR_BASIC_PROGRAM,
								new String[] { IEGLConstants.KEYWORD_DISPLAY });
					}else if (enclosingPart.getAnnotation(EGLUITEXT, "TextUIProgram") != null){
						Expression expr = displayStatement.getExpr();
						ITypeBinding targetBinding = expr.resolveTypeBinding();
						if (StatementValidator.isValidBinding(targetBinding)){
							if (targetBinding.getAnnotation(EGLUITEXT, "TextForm") == null){
								problemRequestor.acceptProblem(expr,
										IProblemRequestor.STATEMENT_TARGET_MUST_BE_TEXT_FORM,
										new String[] { expr.getCanonicalString(),IEGLConstants.KEYWORD_DISPLAY });
							}
						}
					}
				}
			}
			
			return false;
		}

	}
	
	


