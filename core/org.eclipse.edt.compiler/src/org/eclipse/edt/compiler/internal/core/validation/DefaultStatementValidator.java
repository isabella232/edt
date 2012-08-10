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
package org.eclipse.edt.compiler.internal.core.validation;


public class DefaultStatementValidator extends AbstractStatementValidator {
	
	//TODO uncomment as validators are ported to the new binding framework
	public boolean visit(org.eclipse.edt.compiler.core.ast.AddStatement addStatement) {
//		addStatement.accept(new AddStatementValidator(problemRequestor));
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.CloseStatement closeStatement) {
//		closeStatement.accept(new CloseStatementValidator(problemRequestor));
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.DeleteStatement deleteStatement) {
//		deleteStatement.accept(new DeleteStatementValidator(problemRequestor, compilerOptions));
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.ExecuteStatement executeStatement) {
//		executeStatement.accept(new ExecuteStatementValidator(problemRequestor, compilerOptions));
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement foreachStatement) {
//		foreachStatement.accept(new ForEachStatementValidator(problemRequestor, compilerOptions));
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByKeyStatement getStatement) {
//		getStatement.accept(new GetByKeyStatementValidator(problemRequestor, compilerOptions));
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.OpenStatement openStatement) {
//		openStatement.accept(new OpenStatementValidator(problemRequestor, compilerOptions));
		return false;
	}

	public boolean visit(org.eclipse.edt.compiler.core.ast.PrepareStatement prepareStatement) {
//		prepareStatement.accept(new PrepareStatementValidator(problemRequestor, compilerOptions));
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReplaceStatement replaceStatement) {
//		replaceStatement.accept(new ReplaceStatementValidator(problemRequestor, compilerOptions));
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.CallStatement callStatement) {
//		callStatement.accept(new CallStatementValidator(problemRequestor, compilerOptions));
		return false;		
	}

}
