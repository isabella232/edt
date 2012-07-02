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
	
import org.eclipse.edt.compiler.core.ast.CallStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

	
	/**
	 * @author Craig Duval
	 */
	public class CallStatementValidator extends DefaultASTVisitor {
		
		private IProblemRequestor problemRequestor;
		
		public CallStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
		}
		
		public boolean visit(final CallStatement callStatement) {

			//the basic call statement is currently not supported
			problemRequestor.acceptProblem(callStatement, IProblemRequestor.INVALID_STATMENT);
			
			return false;
		}

	}