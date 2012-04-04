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
	
	import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.GotoStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;

	
	/**
	 * @author Craig Duval
	 */
	public class GotoStatementValidator extends DefaultASTVisitor {
		
		private IProblemRequestor problemRequestor;
        private ICompilerOptions compilerOptions;
		
		public GotoStatementValidator(IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
			this.problemRequestor = problemRequestor;
			this.compilerOptions = compilerOptions;
		}
		
		public boolean visit(final GotoStatement gotoStatement) {
			
			problemRequestor.acceptProblem(gotoStatement,
					IProblemRequestor.PART_OR_STATEMENT_NOT_SUPPORTED,
					new String[] {"GOTO"});

			EGLNameValidator.validate(gotoStatement.getLabel(), EGLNameValidator.IDENTIFIER, problemRequestor, gotoStatement, compilerOptions);

			return false;
		}

	}
