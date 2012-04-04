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
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;

	
	/**
	 * @author Craig Duval
	 */
	public class CloseStatementValidator extends DefaultASTVisitor implements IOStatementValidatorConstants{
		
		private IProblemRequestor problemRequestor;
		
		public CloseStatementValidator(IProblemRequestor problemRequestor) {
			this.problemRequestor = problemRequestor;
		}

		//TODO validate iorecord properties keyitem and length
		public boolean visit(final CloseStatement closeStatement) {
			StatementValidator.validateIOTargetsContainer(closeStatement.getIOObjects(),problemRequestor);
			Expression expr = closeStatement.getExpr();
			ITypeBinding typeBinding = expr.resolveTypeBinding();
			if (!StatementValidator.isValidBinding(typeBinding)){
				return false;
			}
			
			
			if (typeBinding.getAnnotation(EGLIOFILE, "IndexedRecord") != null ||
				typeBinding.getAnnotation(EGLIOMQ, "MQRecord") != null ||
				typeBinding.getAnnotation(EGLUITEXT, "PrintForm") != null ||
				typeBinding.getAnnotation(EGLIOFILE, "RelativeRecord") != null ||
				typeBinding.getAnnotation(EGLIOFILE, "SerialRecord") != null ||
				typeBinding.getAnnotation(EGLIOFILE, "CSVRecord") != null ||
				typeBinding.getAnnotation(EGLIOSQL, "SQLRecord") != null){
					return false;
			}

			problemRequestor.acceptProblem(expr,
					IProblemRequestor.INVALID_CLOSE_TARGET,
					new String[] {expr.getCanonicalString()});				

//TODO need to validate resultset id. test line is commented out in closestatement.egl
			return false;
		}


	}
	
	


