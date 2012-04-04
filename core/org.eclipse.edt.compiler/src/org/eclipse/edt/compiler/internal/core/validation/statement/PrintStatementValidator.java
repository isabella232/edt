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
	
	import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.PrintStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;

	
	/**
	 * @author Craig Duval
	 */
	public class PrintStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants{
		
		private IProblemRequestor problemRequestor;
		private IPartBinding enclosingPart;
		
		public PrintStatementValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart) {
			this.problemRequestor = problemRequestor;
			this.enclosingPart = enclosingPart;
		}
		
		public boolean visit(final PrintStatement printStatement) {
			StatementValidator.validateIOTargetsContainer(printStatement.getIOObjects(),problemRequestor);
			Expression expr = printStatement.getTarget();
			ITypeBinding typeBinding = expr.resolveTypeBinding();
			if (StatementValidator.isValidBinding(typeBinding)){
				if (typeBinding.getAnnotation(EGLUITEXT, "PrintForm") == null){
					problemRequestor.acceptProblem(expr,
							IProblemRequestor.PRINT_TARGET_MUST_BE_PRINT_FORM,
							new String[] {expr.getCanonicalString()});
				}
			}
			return false;
		}

	}
	
	


