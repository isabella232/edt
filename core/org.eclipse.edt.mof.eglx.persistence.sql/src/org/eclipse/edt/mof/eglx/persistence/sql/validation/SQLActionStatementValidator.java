/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import org.eclipse.edt.compiler.internal.core.validation.DefaultStatementValidator;

public class SQLActionStatementValidator extends DefaultStatementValidator {
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.AddStatement addStatement) {
		new AddStatementValidator(addStatement, problemRequestor, compilerOptions).validate();
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.CloseStatement closeStatement) {
		// nothing to validate.
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.DeleteStatement deleteStatement) {
		new DeleteStatementValidator(deleteStatement, problemRequestor, compilerOptions).validate();
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.ExecuteStatement executeStatement) {
		new ExecuteStatementValidator(executeStatement, problemRequestor, compilerOptions).validate();
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement foreachStatement) {
		new ForEachStatementValidator(foreachStatement, problemRequestor, compilerOptions).validate();
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByKeyStatement getStatement) {
		new GetByKeyStatementValidator(getStatement, problemRequestor, compilerOptions).validate();
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByPositionStatement getStatement) {
		new GetByPositionStatementValidator(getStatement, problemRequestor, compilerOptions).validate();
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.OpenStatement openStatement) {
		new OpenStatementValidator(openStatement, problemRequestor, compilerOptions).validate();
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.PrepareStatement prepareStatement) {
		new PrepareStatementValidator( prepareStatement, problemRequestor, compilerOptions ).validate();
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReplaceStatement replaceStatement) {
		new ReplaceStatementValidator(replaceStatement, problemRequestor, compilerOptions).validate();
		return false;
	}
}
