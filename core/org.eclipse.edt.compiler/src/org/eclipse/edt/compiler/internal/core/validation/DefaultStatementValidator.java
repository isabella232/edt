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

import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;


public class DefaultStatementValidator extends AbstractStatementValidator {
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.AddStatement addStatement) {
		problemRequestor.acceptProblem(addStatement, IProblemRequestor.STATEMENT_NOT_EXTENDED, new String[]{IEGLConstants.KEYWORD_ADD});
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.CallStatement callStatement) {
		problemRequestor.acceptProblem(callStatement, IProblemRequestor.STATEMENT_NOT_EXTENDED, new String[]{IEGLConstants.KEYWORD_CALL});
		return false;		
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.CloseStatement closeStatement) {
		problemRequestor.acceptProblem(closeStatement, IProblemRequestor.STATEMENT_NOT_EXTENDED, new String[]{IEGLConstants.KEYWORD_CLOSE});
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.DeleteStatement deleteStatement) {
		problemRequestor.acceptProblem(deleteStatement, IProblemRequestor.STATEMENT_NOT_EXTENDED, new String[]{IEGLConstants.KEYWORD_DELETE});
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ExecuteStatement executeStatement) {
		problemRequestor.acceptProblem(executeStatement, IProblemRequestor.STATEMENT_NOT_EXTENDED, new String[]{IEGLConstants.KEYWORD_EXECUTE});
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement foreachStatement) {
		//TODO port to new binding model
//		foreachStatement.accept(new ForEachStatementValidator(problemRequestor, compilerOptions));
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByKeyStatement getStatement) {
		problemRequestor.acceptProblem(getStatement, IProblemRequestor.STATEMENT_NOT_EXTENDED, new String[]{IEGLConstants.KEYWORD_GET});
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.OpenStatement openStatement) {
		problemRequestor.acceptProblem(openStatement, IProblemRequestor.STATEMENT_NOT_EXTENDED, new String[]{IEGLConstants.KEYWORD_OPEN});
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.PrepareStatement prepareStatement) {
		problemRequestor.acceptProblem(prepareStatement, IProblemRequestor.STATEMENT_NOT_EXTENDED, new String[]{IEGLConstants.KEYWORD_PREPARE});
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReplaceStatement replaceStatement) {
		problemRequestor.acceptProblem(replaceStatement, IProblemRequestor.STATEMENT_NOT_EXTENDED, new String[]{IEGLConstants.KEYWORD_REPLACE});
		return false;
	}
}
