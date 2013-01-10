/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.persistence.sql.validation;

import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

public class GetByPositionStatementValidator extends AbstractSqlStatementValidator {
	GetByPositionStatement statement;
	IProblemRequestor problemRequestor;
	ICompilerOptions compilerOptions;
	
	
	public GetByPositionStatementValidator(GetByPositionStatement statement, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		super();
		this.statement = statement;
		this.problemRequestor = problemRequestor;
		this.compilerOptions = compilerOptions;
	}
	
	public void validate() {
		problemRequestor.acceptProblem(statement,
				IProblemRequestor.STATEMENT_NOT_SUPPORTED,
				new String[] {"GET"});
	}
		
	
}
